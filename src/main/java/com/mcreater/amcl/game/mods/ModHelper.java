package com.mcreater.amcl.game.mods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.game.versionTypeGetter;
import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.model.mod.FabricModInfoModel;
import com.mcreater.amcl.model.mod.ForgeModInfoModel;
import com.mcreater.amcl.model.mod.SimpleModInfoModel;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.FileUtils.*;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class ModHelper {
    public static Vector<File> getMod(String dir, String version_name){
        Vector<File> result = new Vector<>();
        boolean changed = Launcher.configReader.configModel.change_game_dir;
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
    public static CommonModInfoModel getModInfo(String path) throws IOException {
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
            name = model.pack.get("description");
            description = model.pack.get("description");
        }
        if (new File("modTemp/fabric.mod.json").exists()) {
            String j = FileStringReader.read("modTemp/fabric.mod.json");
            FabricModInfoModel model;
            try {
                model = new Gson().fromJson(j, FabricModInfoModel.class);
            } catch (Exception e) {
                e.printStackTrace();
                CommonModInfoModel mode = new CommonModInfoModel();
                JSONObject ob = JSON.parseObject(j);
                mode.name = ob.getString("name");
                mode.description = ob.getString("description");
                mode.version = ob.getString("version");
                return mode;
            }
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
        m1.path = path;
        FileUtils.del("modTemp");
        return m1;
    }
}
