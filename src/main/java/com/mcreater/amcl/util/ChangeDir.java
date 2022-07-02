package com.mcreater.amcl.util;

import com.mcreater.amcl.nativeInterface.PosixHandler;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;

public class ChangeDir {
    public static String dirs;
    private static POSIX posix;
    public static void saveNowDir(){
        dirs = System.getProperty("user.dir");
    }
    public static void changeToDefault(){
        changeTo(dirs);
    }
    public static void changeTo(String dir){
        System.setProperty("user.dir", dir);
        posix = POSIXFactory.getPOSIX(new PosixHandler(), true);
        posix.chdir(dir);
    }
}
