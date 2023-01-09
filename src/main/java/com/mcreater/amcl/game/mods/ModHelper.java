package com.mcreater.amcl.game.mods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.internal.LinkedTreeMap;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.model.mod.FabricModInfoModel;
import com.mcreater.amcl.model.mod.ForgeModInfoModel;
import com.mcreater.amcl.model.mod.LiteLoaderModInfoModel;
import com.mcreater.amcl.model.mod.OldForgeModInfoModel;
import com.mcreater.amcl.model.mod.SimpleModInfoModel;
import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.FileUtils.ZipUtil;
import com.mcreater.amcl.util.J8Utils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Vector;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

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
                    if (f.getPath().endsWith(".jar") || f.getPath().endsWith(".litemod") || f.getPath().endsWith(".zip")) {
                        result.add(f);
                    }
                }
            }
        }
        return result;
    }
    public static Vector<CommonModInfoModel> getModInfo(String path) throws Exception {
        String version = "";
        String name = "";
        String description = "";
        Vector<String> authorList = new Vector<>();
        Image icon = new WritableImage(1, 1);

        String mcmodinfoFile = ZipUtil.readTextFileInZip(path, "mcmod.info");
        String packMcMetaFile = ZipUtil.readTextFileInZip(path, "pack.mcmeta");
        String fabricModJsonFile = ZipUtil.readTextFileInZip(path, "fabric.mod.json");
        String quiltModJsonFile = ZipUtil.readTextFileInZip(path, "quilt.mod.json");

        String liteloaderInfoFile = ZipUtil.readTextFileInZip(path, "litemod.json");

        if (mcmodinfoFile != null){
            if (mcmodinfoFile.startsWith("[")) {
                Vector<LinkedTreeMap<?, ?>> commonInfo = new Vector<>();
                commonInfo = GSON_PARSER.fromJson(mcmodinfoFile, commonInfo.getClass());

                Vector<ForgeModInfoModel> cm = new Vector<>();
                commonInfo.forEach(linkedTreeMap -> cm.add(GSON_PARSER.fromJson(GSON_PARSER.toJson(linkedTreeMap), ForgeModInfoModel.class)));

                Vector<CommonModInfoModel> vec = new Vector<>();

                cm.forEach(mi -> {
                    CommonModInfoModel m2 = new CommonModInfoModel();
                    m2.path = path;
                    m2.name = mi.name;
                    m2.version = mi.version;
                    m2.authorList = mi.authorList;
                    m2.description = mi.description;
                    Image iconTemp = new WritableImage(1, 1);
                    try {
                        iconTemp = new Image(ZipUtil.readBinaryFileInZip(path, (mi.modid == null ? "" : mi.modid) + "-logo.png"));
                    }
                    catch (IOException ignored) {

                    }
                    m2.icon = iconTemp;
                    vec.add(m2);
                });

                return vec;
            }
            else {
                OldForgeModInfoModel model = GSON_PARSER.fromJson(mcmodinfoFile, OldForgeModInfoModel.class);
                Vector<CommonModInfoModel> vec = new Vector<>();
                model.modlist.forEach(mu -> {
                    CommonModInfoModel m2 = new CommonModInfoModel();
                    m2.path = path;
                    m2.name = mu.name;
                    m2.version = mu.version;
                    m2.authorList = mu.authorList;
                    m2.description = mu.description;
                    Image iconTemp = new WritableImage(1, 1);
                    try {
                        iconTemp = new Image(ZipUtil.readBinaryFileInZip(path, (mu.modid == null ? "" : mu.modid) + "-logo.png"));
                    }
                    catch (IOException ignored) {

                    }
                    m2.icon = iconTemp;
                    vec.add(m2);
                });
                return vec;
            }
        }
        else if (liteloaderInfoFile != null) {
            LiteLoaderModInfoModel model = GSON_PARSER.fromJson(liteloaderInfoFile, LiteLoaderModInfoModel.class);

            version = model.revision;
            name = model.name;
            description = model.description;
            authorList = new Vector<>(Collections.singletonList(model.author));
        }
        else if (packMcMetaFile != null){
            SimpleModInfoModel model = GSON_PARSER.fromJson(packMcMetaFile, SimpleModInfoModel.class);
            name = model.pack.get("description");
            description = model.pack.get("description");
            icon = new Image(ZipUtil.readBinaryFileInZip(path, "logo.png"));
        }
        if (fabricModJsonFile != null) {
            FabricModInfoModel model;
            try {
                model = GSON_PARSER.fromJson(fabricModJsonFile, FabricModInfoModel.class);
            } catch (Exception e) {
                e.printStackTrace();
                CommonModInfoModel mode = new CommonModInfoModel();
                JSONObject ob = JSON.parseObject(fabricModJsonFile);
                mode.name = ob.getString("name");
                mode.description = ob.getString("description");
                mode.version = ob.getString("version");
                return new Vector<>(J8Utils.createList(mode));
            }
            version = model.version;
            name = model.name;
            description = model.description;
            Vector<String> ve = new Vector<>();
            if (model.authors != null) {
                for (Object o : model.authors) {
                    if (o instanceof String) ve.add((String) o);
                    else if (o instanceof Map) {
                        Map map = (Map) o;
                        Object name2 = map.get("name");
                        if (name2 != null) ve.add((String) name2);
                    }
                }
            }
            authorList = ve;
            icon = new Image(ZipUtil.readBinaryFileInZip(path, model.icon));
        }
        else if (quiltModJsonFile != null) {
            JSONObject ob = JSON.parseObject(quiltModJsonFile);
            version = ob.getJSONObject("quilt_loader").getString("version");
            name = ob.getJSONObject("quilt_loader").getJSONObject("metadata").getString("name");
            description = ob.getJSONObject("quilt_loader").getJSONObject("metadata").getString("description");
            Vector<String> temp = new Vector<>();
            ob.getJSONObject("quilt_loader").getJSONObject("metadata").getJSONObject("contributors").keySet().forEach(o -> {
                try {
                    temp.add(o);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            });
            authorList = temp;
            icon = new Image(ZipUtil.readBinaryFileInZip(path, name = ob.getJSONObject("quilt_loader").getJSONObject("metadata").getString("icon")));
        }

        CommonModInfoModel m1 = new CommonModInfoModel();
        m1.version = version;
        m1.name = name;
        m1.description = description;
        m1.authorList = authorList != null && !authorList.isEmpty() ? authorList : null;
        m1.path = path;
        m1.icon = icon;
        return new Vector<>(J8Utils.createList(m1));
    }
}
