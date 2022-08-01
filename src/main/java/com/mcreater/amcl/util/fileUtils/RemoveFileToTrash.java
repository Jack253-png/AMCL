package com.mcreater.amcl.util.fileUtils;

import com.sun.jna.platform.FileUtils;

import java.io.File;
import java.io.IOException;

public class RemoveFileToTrash {
    public static void remove(String path){
        File f = new File(path);
        if (f.exists()) {
            FileUtils fu = FileUtils.getInstance();
            if (fu.hasTrash()) {
                try {
                    fu.moveToTrash(f);
                } catch (IOException e) {
                    f.delete();
                }
            } else {
                f.delete();
            }
        }
    }
}
