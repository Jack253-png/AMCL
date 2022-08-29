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
}
