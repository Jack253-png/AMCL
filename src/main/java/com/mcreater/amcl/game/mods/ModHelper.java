package com.mcreater.amcl.game.mods;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.mcreater.amcl.Application;
import com.mcreater.amcl.game.versionTypeGetter;
import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.model.mod.FabricModInfoModel;
import com.mcreater.amcl.model.mod.ForgeModInfoModel;
import com.mcreater.amcl.model.mod.SimpleModInfoModel;
import com.mcreater.amcl.util.FileStringReader;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.LinkPath;
import com.mcreater.amcl.util.ZipUtil;

import java.io.*;
import java.util.Arrays;
import java.util.Vector;

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
    public static CommonModInfoModel getModInfo(String path){
        FileUtils.del("modTemp");
        ZipUtil.unzipAll(path, "modTemp");
        String version = "";
        String name = "";
        String description = "";
        Vector<String> authorList = new Vector<>();
        if (new File("modTemp/mcmod.info").exists()){
            Gson g = new Gson();
            String j = FileStringReader.read("modTemp/mcmod.info");
            j = j.substring(1, j.length() - 2);
            ForgeModInfoModel model = g.fromJson(j, ForgeModInfoModel.class);

            version = model.version;
            name = model.name;
            description = model.description;
            authorList = model.authorList;
        }
        else if (new File("modTemp/pack.mcmeta").exists()){
            String j = FileStringReader.read("modTemp/pack.mcmeta");
            SimpleModInfoModel model = new Gson().fromJson(j, SimpleModInfoModel.class);
            description = model.pack.get("description");
        }
        if (new File("modTemp/fabric.mod.json").exists()){
            String j = FileStringReader.read("modTemp/fabric.mod.json");
            FabricModInfoModel model = new Gson().fromJson(j, FabricModInfoModel.class);
            version = model.version;
            name = model.name;
            description = model.description;
            authorList = model.authors;
        }
        CommonModInfoModel m1 = new CommonModInfoModel();
        m1.version = version;
        m1.name = name;
        m1.description = description;
        m1.authorList = authorList;
        return m1;
    }
}