package com.mcreater.amcl.download;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcreater.amcl.api.reflect.ReflectHelper;
import com.mcreater.amcl.api.reflect.ReflectedJar;
import com.mcreater.amcl.tasks.OptiFineInstallerDownloadTask;
import com.mcreater.amcl.model.optifine.optifineAPIModel;
import com.mcreater.amcl.model.optifine.optifineJarModel;
import com.mcreater.amcl.util.ChangeDir;
import com.mcreater.amcl.util.FileStringReader;
import com.mcreater.amcl.util.LinkPath;
import com.mcreater.amcl.util.net.HttpConnectionUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class OptifineDownload {
    public static void download(boolean faster, String id, String minecraft_dir, String version_name, int chunkSize, String optifine_version) throws IOException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        String r = HttpConnectionUtil.doGet("https://optifine.cn/api");
        Gson g = new Gson();
        optifineAPIModel model = g.fromJson(r, optifineAPIModel.class);
        if (!model.versions.contains(id)){
            throw new IOException();
        }
        String opti = null;
        for (optifineJarModel m : model.files){
            if (m.name.contains(id.replace("beta ", "beta_")) && m.name.contains(optifine_version)) {
                opti = m.name;
                break;
            }
        }
        if (opti == null){
            throw new IOException();
        }
        if (opti.contains("legacy")){
            throw new IOException();
        }
        OriginalDownload.download(faster, id, minecraft_dir, version_name, chunkSize);
        JSONObject ob = new JSONObject(new Gson().fromJson(OriginalDownload.getVJ(), Map.class));
        new OptiFineInstallerDownloadTask(opti, "opti.jar").execute();
        ChangeDir.saveNowDir();
        ReflectedJar jar = ReflectHelper.getReflectedJar("opti.jar");
        int installer = jar.createNewInstance(jar.getJarClass("optifine.Installer"));

        String ofVer = (String) jar.invokeNoArgsMethod(installer, "getOptiFineVersion");
        String[] ofVers = (String[]) jar.invokeStaticMethod(jar.getJarClass("optifine.Utils"), "tokenize", new String[]{ofVer, "_"}, String.class, String.class);
        String ofEd = (String) jar.invokeMethod(installer, "getOptiFineEdition", new Object[]{ofVers}, String[].class);

        jar.invokeMethod(installer, "installOptiFineLibrary", new Object[]{version_name, ofEd, new File(LinkPath.link(minecraft_dir, "libraries")), false}, String.class, String.class, File.class, boolean.class);

        try{
            jar.invokeMethod(installer, "installLaunchwrapperLibrary", new Object[]{id, ofEd, new File(LinkPath.link(minecraft_dir, "libraries"))}, String.class, String.class, File.class);
        }
        catch (Exception ignored){}

        jar.invokeMethod(installer, "updateJson", new Object[]{new File(LinkPath.link(minecraft_dir, "versions")), version_name, new File(LinkPath.link(minecraft_dir, "libraries")), id, ofEd}, File.class, String.class, File.class, String.class, String.class);

        JSONObject f = new JSONObject(new Gson().fromJson(FileStringReader.read(String.format("%s\\versions\\%s\\%s.json", minecraft_dir, version_name, version_name)), Map.class));
        Vector<Map<String, String>> oflibs = new Vector<>();
        for (Object o : f.getJSONArray("libraries")){
            Map<String, String> s = (Map<String, String>) o;
            if (s.get("name").contains("optifine:OptiFine")){
                List<String> l = new ArrayList<>(List.of(s.get("name").split(":")));
                l.set(2, String.format("%s_%s", version_name, ofEd));
                s.put("name", String.join(":", l));
            }
            oflibs.add(s);
        }
        f.getJSONArray("libraries").clear();
        f.getJSONArray("libraries").addAll(oflibs);
        for (Object o : ob.getJSONArray("libraries")){
            f.getJSONArray("libraries").add(o);
        }
        f.put("assetIndex", ob.getJSONObject("assetIndex"));
        f.put("downloads", ob.getJSONObject("downloads"));
        if (ob.getJSONObject("arguments") != null){
            Vector<Object> finalArgs = new Vector<Object>(ob.getJSONObject("arguments").getJSONArray("game"));
            finalArgs.addAll(f.getJSONObject("arguments").getJSONArray("game"));
            JSONArray array = new JSONArray(finalArgs);
            f.getJSONObject("arguments").put("game", array);
        }
        GsonBuilder gb = new GsonBuilder();
        gb.setPrettyPrinting();
        BufferedWriter writer = new BufferedWriter(new FileWriter(String.format("%s\\versions\\%s\\%s.json", minecraft_dir, version_name, version_name)));
        writer.write(gb.create().toJson(f));
        writer.close();
    }
}
