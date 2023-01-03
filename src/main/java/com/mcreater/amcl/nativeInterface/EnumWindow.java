package com.mcreater.amcl.nativeInterface;

import com.mcreater.amcl.patcher.ClassPathInjector;
import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.ptr.IntByReference;
import oshi.jna.platform.mac.CoreGraphics;

import java.util.HashSet;
import java.util.Set;

public class EnumWindow {
    public static boolean getTaskPidContains(long pid) {
        if (OSInfo.isWin()) {
            User32 user32 = User32.INSTANCE;
            Set<Long> set = new HashSet<>();
            IntByReference i = new IntByReference();
            user32.EnumWindows((h, p) -> {
                user32.GetWindowThreadProcessId(h, i);
                if (user32.IsWindow(h) && user32.IsWindowEnabled(h) && user32.IsWindowVisible(h)) {
                    set.add((long) i.getValue());
                }
                return true;
            }, null);
            return set.contains(pid);
        }
        else if (OSInfo.isMac()){
            CoreFoundation.CFArrayRef windowInfo = CoreGraphics.INSTANCE.CGWindowListCopyWindowInfo(0, 0);
            CoreFoundation.CFStringRef kCGWindowOwnerPID = CoreFoundation.CFStringRef.createCFString("kCGWindowOwnerPID");
            int numWindows = windowInfo.getCount();
            Set<Long> pids = new HashSet<>();
            for (int i = 0; i < numWindows; i++) {
                Pointer result = windowInfo.getValueAtIndex(i);
                CoreFoundation.CFDictionaryRef windowRef = new CoreFoundation.CFDictionaryRef(result);
                result = windowRef.getValue(kCGWindowOwnerPID);
                CoreFoundation.CFNumberRef windowOwnerPID = new CoreFoundation.CFNumberRef(result);
                pids.add(windowOwnerPID.longValue());
            }
            kCGWindowOwnerPID.release();
            windowInfo.release();
            return pids.contains(pid);
        }
        else {
            return false;
        }
    }

    public static boolean enumWindowEnabled() {
        return ClassPathInjector.version > 8 && !OSInfo.isLinux();
    }
}
