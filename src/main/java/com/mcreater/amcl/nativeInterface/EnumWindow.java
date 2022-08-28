package com.mcreater.amcl.nativeInterface;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.ptr.IntByReference;

import java.util.HashSet;
import java.util.Set;

public class EnumWindow {
    public static Set<Long> getTaskPID() {
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
            return set;
        }
        else if (OSInfo.isMac()){
            return CoreGraphics.INSTANCE.get();
        }
        else {
            return new HashSet<>();
        }
    }
}
