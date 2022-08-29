package com.mcreater.amcl.nativeInterface;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation;

import java.util.HashSet;
import java.util.Set;

public interface CoreGraphics extends Library {
    CoreGraphics INSTANCE = Native.load("CoreGraphics", CoreGraphics.class);
    CoreFoundation.CFArrayRef CGWindowListCopyWindowInfo(int option, int relativeToWindow);
    default Set<Long> get(){
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
        return pids;
    }
}
