package com.mcreater.amcl.game;

import com.mcreater.amcl.util.FileUtils.LinkPath;

import java.io.File;
import java.util.Objects;
import java.util.Vector;

public class getMinecraftVersion {
    public static Vector<String> get(String p){
        Vector<String> result = new Vector<>();
        File f = new File(p);
        if (!f.exists()){
            return null;
        }
        File f1 = new File(LinkPath.link(p,"versions"));
        if (!f1.exists()){
            return null;
        }
        for (File file : Objects.requireNonNull(f1.listFiles())){
            if (file.isDirectory()){
                result.add(file.getName());
            }
        }
        return result;
    }
}
