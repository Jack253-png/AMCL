package com.mcreater.amcl.nativeInterface;

public class OSInfo {
    public static boolean isWin(){
        return System.getProperty("os.name").toLowerCase().indexOf("win") > 0;
    }
    public static boolean isMac(){
        return System.getProperty("os.name").toLowerCase().indexOf("mac") > 0 && System.getProperty("os.name").toLowerCase().indexOf("os") > 0;
    }
    public static boolean isLinux(){
        return System.getProperty("os.name").toLowerCase().indexOf("linux") > 0;
    }
}
