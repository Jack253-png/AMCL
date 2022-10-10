package com.mcreater.amcl.game.mods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.game.VersionTypeGetter;
import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.model.mod.FabricModInfoModel;
import com.mcreater.amcl.model.mod.ForgeModInfoModel;
import com.mcreater.amcl.model.mod.SimpleModInfoModel;
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
        if (d.exists()) {
            for (File f : d.listFiles()) {
                if (f.isFile()) {
                    if (f.getPath().endsWith(".jar") || f.getPath().endsWith(".litemod")) {
                        result.add(f);
                    }
                }
            }
        }
        return result;
    }
    public static boolean isModded(String dir, String version_name){
        return VersionTypeGetter.modded(dir, version_name);
    }
    public static CommonModInfoModel getModInfo(String path) throws IOException {
        String version = "";
        String name = "";
        String description = "";
        Vector<String> authorList = new Vector<>();

        String mcmodinfoFile = ZipUtil.readTextFileInZip(path, "mcmod.info");
        String packMcMetaFile = ZipUtil.readTextFileInZip(path, "pack.mcmeta");
        String fabricModJsonFile = ZipUtil.readTextFileInZip(path, "fabric.mod.json");

        if (mcmodinfoFile != null){
            Gson g = new Gson();
            String j = mcmodinfoFile;
            j = j.substring(1, j.length() - 2);
            ForgeModInfoModel model = g.fromJson(j, ForgeModInfoModel.class);

            version = model.version;
            name = model.name;
            description = model.description;
            authorList = model.authorList;
        }
        else if (packMcMetaFile != null){
            SimpleModInfoModel model = new Gson().fromJson(packMcMetaFile, SimpleModInfoModel.class);
            name = model.pack.get("description");
            description = model.pack.get("description");
        }
        if (fabricModJsonFile != null) {
            FabricModInfoModel model;
            try {
                model = new Gson().fromJson(fabricModJsonFile, FabricModInfoModel.class);
            } catch (Exception e) {
                e.printStackTrace();
                CommonModInfoModel mode = new CommonModInfoModel();
                JSONObject ob = JSON.parseObject(fabricModJsonFile);
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
        m1.authorList = authorList.size() == 0 ? null : authorList;
        m1.path = path;
        return m1;
    }
}
