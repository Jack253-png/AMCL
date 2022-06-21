package com.mcreater.amcl.download;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcreater.amcl.download.tasks.OptiFineInstallerDownloadTask;
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
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import com.google.gson.internal.LinkedTreeMap;

public class OptifineDownload {
    public static void download(boolean faster, String id, String minecraft_dir, String version_name, int chunkSize, String optifine_version) throws IOException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        String r = HttpConnectionUtil.doGet("https://optifine.cn/api");
        Gson g = new Gson();
        optifineAPIModel model = g.fromJson(r, optifineAPIModel.class);
        if (!model.versions.contains(id)){
            throw new IOException();
        }
        OriginalDownload.download(faster, id, minecraft_dir, version_name, chunkSize);
        JSONObject ob = new JSONObject(new Gson().fromJson(OriginalDownload.getVJ(), Map.class));
        String opti = null;
        for (optifineJarModel m : model.files){
            if (m.name.contains(id) && m.name.contains(optifine_version)) {
                opti = m.name;
            }
        }
        if (opti == null){
            throw new IOException();
        }
        new OptiFineInstallerDownloadTask(opti, "opti.jar").execute();
        ChangeDir.saveNowDir();
        URLClassLoader loader = new URLClassLoader(new URL[]{new File("opti.jar").toURL()});
        Class<?> installer = loader.loadClass("optifine.Installer");
        Object instance = installer.newInstance();
        Method getOptiFineVersion = installer.getDeclaredMethod("getOptiFineVersion");
        String ofVer = (String) getOptiFineVersion.invoke(instance);
        Class<?> utils = loader.loadClass("optifine.Utils");
        Method tokenize = utils.getDeclaredMethod("tokenize", String.class, String.class);
        String[] ofVers = (String[]) tokenize.invoke(utils, ofVer, "_");
        Method getOptiFineEdition = installer.getDeclaredMethod("getOptiFineEdition", String[].class);
        String ofEd = (String) getOptiFineEdition.invoke(instance, (Object) ofVers);
        Method installOptiFineLibrary = installer.getDeclaredMethod("installOptiFineLibrary", String.class, String.class, File.class, boolean.class);
        installOptiFineLibrary.setAccessible(true);
        installOptiFineLibrary.invoke(instance, id, ofEd, new File(LinkPath.link(minecraft_dir, "libraries")), false);
        try{
            Method installLaunchwrapperLibrary = installer.getDeclaredMethod("installLaunchwrapperLibrary", String.class, String.class, File.class);
            installLaunchwrapperLibrary.setAccessible(true);
            installLaunchwrapperLibrary.invoke(instance, id, ofEd, new File(LinkPath.link(minecraft_dir, "libraries")));
        }
        catch (Exception ignored){
            ignored.printStackTrace();
        }
        Method updateJson = installer.getDeclaredMethod("updateJson", File.class, String.class, File.class, String.class, String.class);
        updateJson.setAccessible(true);
        updateJson.invoke(instance, new File(LinkPath.link(minecraft_dir, "versions")), version_name, new File(LinkPath.link(minecraft_dir, "libraries")), id, ofEd);
        JSONObject f = new JSONObject(new Gson().fromJson(FileStringReader.read(String.format("%s\\versions\\%s\\%s.json", minecraft_dir, version_name, version_name)), Map.class));
        System.out.println(f.getJSONArray("libraries"));
        for (Object o : ob.getJSONArray("libraries")){
            f.getJSONArray("libraries").add(o);
        }
        f.put("assetIndex", ob.getJSONObject("assetIndex"));
        GsonBuilder gb = new GsonBuilder();
        gb.setPrettyPrinting();
        BufferedWriter writer = new BufferedWriter(new FileWriter(String.format("%s\\versions\\%s\\%s.json", minecraft_dir, version_name, version_name)));
        writer.write(gb.create().toJson(f));
        writer.close();
    }
}
