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

    private static boolean contain_L(String arch, String... keys) {
        for (String k : keys) {
            if (arch.toLowerCase().contains(k)) return true;
        }
        return false;
    }

    public static boolean isX86() {
        return contain_L(System.getProperty("os.arch"), "x86", "x86_32", "x86-32", "i386", "i486", "i586", "i686", "i86pc", "ia32", "x32");
    }
    public static boolean isArm64() {
        return contain_L(System.getProperty("os.arch"), "arm64", "aarch64", "armv8", "armv9");
    }
    public static boolean isArm32() {
        return contain_L(System.getProperty("os.arch"), "arm32", "aarch32");
    }
    public static boolean isLoongarch() {
        return System.getProperty("os.name").toLowerCase().contains("loong");
    }
    public static OSType getOSType() {
        if (isWin()) {
            if (isX86()) return OSType.WINDOWS_X86;
            if (isArm64()) return OSType.WINDOWS_ARM;
            return OSType.WINDOWS;
        }
        else if (isMac()) {
            if (isArm64()) return OSType.MACOS_ARM64;
            return OSType.MACOS;
        }
        else if (isLinux()) {
            if (isArm64()) return OSType.LINUX_ARM64;
            if (isArm32()) return OSType.LINUX_ARM32;
            if (isLoongarch()) return OSType.LINUX_LOONGARCH64_OW;
            return OSType.LINUX;
        }
        else {
            return OSType.WINDOWS;
        }
    }
    public enum OSType {
        WINDOWS,
        WINDOWS_X86,
        WINDOWS_ARM,

        MACOS,
        MACOS_ARM64,

        LINUX,
        LINUX_ARM64,
        LINUX_ARM32,
        LINUX_LOONGARCH64_OW
    }
}
