package com.mcreater.amcl.download;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.GsonBuilder;
import com.mcreater.amcl.api.reflect.ReflectHelper;
import com.mcreater.amcl.api.reflect.ReflectedJar;
import com.mcreater.amcl.model.optifine.OptifineAPIModel;
import com.mcreater.amcl.model.optifine.OptifineJarModel;
import com.mcreater.amcl.tasks.OptiFineInstallerDownloadTask;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.net.FasterUrls;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import static com.mcreater.amcl.util.FileUtils.FileStringReader;
import static com.mcreater.amcl.util.FileUtils.LinkPath;
import static com.mcreater.amcl.util.FileUtils.OperateUtil.deleteFile;
import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class OptifineDownload {
    public static void download(String id, String minecraft_dir, String version_name, int chunkSize, String optifine_version, Runnable r1, Runnable r2, FasterUrls.Servers server) throws Exception {
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
        OriginalDownload.download(id, minecraft_dir, version_name, chunkSize, server);
        JSONObject ob = new JSONObject(GSON_PARSER.fromJson(OriginalDownload.getVJ(), Map.class));
        r1.run();

        new OptiFineInstallerDownloadTask(opti, "opti.jar").execute();
        FileUtils.ChangeDir.saveNowDir();
        ReflectedJar jar = ReflectHelper.getReflectedJar("opti.jar");
        int installer = jar.createNewInstance(jar.getJarClass("optifine.Installer"));
        r2.run();

        String ofEd;

        try {
            String ofVer = (String) jar.invokeNoArgsMethod(
                    installer,
                    "getOptiFineVersion");
            String[] ofVers = (String[]) jar.invokeStaticMethod(
                    jar.getJarClass("optifine.Utils"),
                    "tokenize",
                    new String[]{ofVer, "_"},
                    String.class, String.class);
            ofEd = (String) jar.invokeMethod(
                    installer,
                    "getOptiFineEdition",
                    new Object[]{ofVers},
                    String[].class);
        }
        catch (Exception e) {
            ofEd = optifine_version;
        }

        String fileSrc = new File("opti.jar").getAbsolutePath();
        String fileBase = FileUtils.LinkPath.link(minecraft_dir, String.format("versions/%s/%s.jar", version_name, version_name));
        String fileDest = FileUtils.LinkPath.link(minecraft_dir, String.format("libraries/optifine/OptiFine/%s_%s/OptiFine-%s_%s.jar", id, ofEd, id, ofEd));
        new File(fileDest).getParentFile().mkdirs();

        jar.invokeStaticMethod(
                jar.getJarClass("optifine.Patcher"),
                "process",
                new File[]{
                        new File(fileBase),
                        new File(fileSrc),
                        new File(fileDest)
                },
                File.class, File.class, File.class
        );

        // from 1.13
        // optifine launchwrapper
        try {
            jar.invokeMethod(
                    installer,
                    "installLaunchwrapperLibrary",
                    new Object[]{id, ofEd, new File(LinkPath.link(minecraft_dir, "libraries"))},
                    String.class, String.class, File.class);
        }
        catch (Exception ignored){}

        // extract optifine json
        jar.invokeMethod(
                installer,
                "updateJson",
                new Object[]{new File(LinkPath.link(minecraft_dir, "versions")), version_name, new File(LinkPath.link(minecraft_dir, "libraries")), id, ofEd},
                File.class, String.class, File.class, String.class, String.class);

        // merge json
        JSONObject f = new JSONObject(GSON_PARSER.fromJson(FileStringReader.read(String.format("%s/versions/%s/%s.json", minecraft_dir, version_name, version_name)), Map.class));
        Vector<Map<String, String>> oflibs = new Vector<>();
        for (Object o : f.getJSONArray("libraries")){
            oflibs.add((Map<String, String>) o);
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
        BufferedWriter writer = new BufferedWriter(new FileWriter(String.format("%s/versions/%s/%s.json", minecraft_dir, version_name, version_name)));
        writer.write(GSON_PARSER.toJson(f));
        writer.close();

        jar.close();
        deleteFile("opti.jar");
    }
}
