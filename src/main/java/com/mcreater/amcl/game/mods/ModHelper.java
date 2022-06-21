package com.mcreater.amcl.game.mods;

import com.mcreater.amcl.Application;
import com.mcreater.amcl.game.versionTypeGetter;
import com.mcreater.amcl.util.FileStringReader;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.LinkPath;
import com.mcreater.amcl.util.ZipUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ModHelper {
    public static Vector<File> getMod(String dir, String version_name){
        Vector<File> result = new Vector<>();
        boolean changed = Application.configReader.configModel.change_game_dir;
        String modDir;
        if (changed){
            modDir = LinkPath.link(LinkPath.link(LinkPath.link(dir, "versions"), version_name), "mods");
        }
        else{
            modDir = LinkPath.link(dir, "mods");
        }
        File d = new File(modDir);
        d.mkdirs();
        for (File f : d.listFiles()){
            if (f.isFile()) {
                if (f.getPath().endsWith(".jar") || f.getPath().endsWith(".litemod")) {
                    result.add(f);
                }
            }
        }
        return result;
    }
    public static boolean isModded(String dir, String version_name){
        String v = versionTypeGetter.get(dir, version_name);
        return v.contains("forge") || v.contains("fabric") || v.contains("liteloader");
    }
    public static void getModInfo(String path){
        ZipUtil.unzipAll(path, "modTemp");
        if (new File("modTemp/mcmod.info").exists()){
            System.out.println(FileStringReader.read("modTemp/mcmod.info"));
        }

//        FileUtils.del("modTemp");
    }
}
