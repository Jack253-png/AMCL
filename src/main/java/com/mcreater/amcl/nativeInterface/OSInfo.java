package com.mcreater.amcl.nativeInterface;

public class OSInfo {
    public static boolean isWin(){
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
    public static boolean isMac(){
        return System.getProperty("os.name").toLowerCase().contains("mac") && System.getProperty("os.name").toLowerCase().contains("os");
    }
    public static boolean isLinux(){
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    public static boolean isX86() {
        return System.getProperty("os.arch").equals("x86");
    }
    private static boolean isArm() {
        return System.getProperty("os.arch").contains("arm");
    }
    public static boolean isArm64() {
        return isArm() && System.getProperty("os.arch").contains("64");
    }
    public static boolean isArm32() {
        return isArm() && System.getProperty("os.arch").contains("32");
    }
    public static OSType getOSType() {
        if (isWin()) {
            if (isX86()) return OSType.WINDOWS_X86;
            return OSType.WINDOWS;
        }
        else if (isMac()) {
            if (isArm64()) return OSType.MACOS_ARM64;
            return OSType.MACOS;
        }
        else if (isLinux()) {
            if (isArm32()) return OSType.LINUX_ARM32;
            if (isArm64()) return OSType.LINUX_ARM64;
            return OSType.LINUX;
        }
        else {
            return OSType.WINDOWS;
        }
    }
    public enum OSType {
        WINDOWS,
        WINDOWS_X86,

        MACOS,
        MACOS_ARM64,

        LINUX,
        LINUX_ARM32,
        LINUX_ARM64
    }
}
