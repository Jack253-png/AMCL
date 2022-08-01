package com.mcreater.amcl.util.fileUtils;

import com.mcreater.amcl.nativeInterface.PosixHandler;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;

public class ChangeDir {
    public static String dirs;

    public static void saveNowDir(){
        dirs = System.getProperty("user.dir");
    }
    public static void changeToDefault(){
        changeTo(dirs);
    }
    public static void changeTo(String dir){
        System.setProperty("user.dir", dir);
        PosixHandler handler = new PosixHandler();
        handler.setVerbose(true);
        POSIX posix = POSIXFactory.getPOSIX(handler, true);
        posix.chdir(dir);
    }
}
