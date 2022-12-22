package com.mcreater.amcl.download;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

import static com.mcreater.amcl.util.FileUtils.OperateUtil.deleteFile;
import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class ForgeOptifineDownload {
    public static void download(String id, String minecraft_dir, String version_name, int chunkSize, NewForgeItemModel forge_version, Runnable r3, Runnable r, Runnable r2, String optifine_version, Runnable r4, Runnable r5, FasterUrls.Servers server) throws Exception {
        if (FileUtils.getJavaExecutable() == null) throw new IOException("Java executable not found.");
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
        String fileDest = FileUtils.LinkPath.link(minecraft_dir, String.format("libraries/optifine/Optifine/%s_%s/OptiFine-%s_%s.jar", id, ofEd, id, ofEd));
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

        String libPath = String.format("optifine:OptiFine:%s_%s", id, ofEd);
        JSONObject ob = JSON.parseObject(vj);

        JSONObject obj2 = new JSONObject();
        obj2.put("name", libPath);
        ob.getJSONArray("libraries").add(obj2);

        BufferedWriter writer = new BufferedWriter(new FileWriter(String.format("%s/versions/%s/%s.json", minecraft_dir, version_name, version_name)));
        writer.write(GSON_PARSER.toJson(ob));
        writer.close();
        jar.close();
        deleteFile("opti.jar");
    }
}
