package com.mcreater.amcl.download;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.GsonBuilder;
import com.mcreater.amcl.api.reflect.ReflectHelper;
import com.mcreater.amcl.api.reflect.ReflectedJar;
import com.mcreater.amcl.download.model.NewForgeItemModel;
import com.mcreater.amcl.model.optifine.OptifineAPIModel;
import com.mcreater.amcl.model.optifine.OptifineJarModel;
import com.mcreater.amcl.tasks.OptiFineInstallerDownloadTask;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.net.FasterUrls;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ForgeOptifineDownload {
    public static void download(String id, String minecraft_dir, String version_name, int chunkSize, NewForgeItemModel forge_version, Runnable r3, Runnable r, Runnable r2, String optifine_version, Runnable r4, Runnable r5, FasterUrls.Servers server) throws Exception {
        OptifineAPIModel model = GetVersionList.getOptifineVersionRaw();
        if (!model.versions.contains(id)){
            throw new IOException();
        }
        String opti = null;
        for (OptifineJarModel m : model.files){
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
        ForgeDownload.download(id, minecraft_dir, version_name, chunkSize, forge_version, r3, r, r2, server);
        String version_dir = FileUtils.LinkPath.link(minecraft_dir, "versions/" + version_name);

        String vj = FileUtils.FileStringReader.read(FileUtils.LinkPath.link(version_dir, version_name + ".json"));
        r4.run();

        new OptiFineInstallerDownloadTask(opti, "opti.jar").execute();
        FileUtils.ChangeDir.saveNowDir();
        ReflectedJar jar = ReflectHelper.getReflectedJar("opti.jar");
        int installer = jar.createNewInstance(jar.getJarClass("optifine.Installer"));
        r5.run();

        String ofVer = (String) jar.invokeNoArgsMethod(
                installer,
                "getOptiFineVersion");
        String[] ofVers = (String[]) jar.invokeStaticMethod(
                jar.getJarClass("optifine.Utils"),
                "tokenize",
                new String[]{ofVer, "_"},
                String.class, String.class);

        String ofEd = (String) jar.invokeMethod(
                installer,
                "getOptiFineEdition",
                new Object[]{ofVers},
                String[].class);

        // optifine main jar
        jar.invokeMethod(
                installer,
                "installOptiFineLibrary",
                new Object[]{version_name, ofEd, new File(FileUtils.LinkPath.link(minecraft_dir, "libraries")), false},
                String.class, String.class, File.class, boolean.class);

        String libPath = String.format("optifine:OptiFine:%s_%s", version_name, ofEd);
        JSONObject ob = JSON.parseObject(vj);

        JSONObject obj2 = new JSONObject();
        obj2.put("name", libPath);
        ob.getJSONArray("libraries").add(obj2);

        GsonBuilder gb = new GsonBuilder();
        gb.setPrettyPrinting();
        BufferedWriter writer = new BufferedWriter(new FileWriter(String.format("%s/versions/%s/%s.json", minecraft_dir, version_name, version_name)));
        writer.write(gb.create().toJson(ob));
        writer.close();
    }
}
