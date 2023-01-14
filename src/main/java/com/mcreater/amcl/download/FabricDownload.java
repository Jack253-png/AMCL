package com.mcreater.amcl.download;

import com.google.gson.JsonSyntaxException;
import com.mcreater.amcl.game.MavenPathConverter;
import com.mcreater.amcl.model.fabric.FabricLibModel;
import com.mcreater.amcl.model.fabric.FabricVersionModel;
import com.mcreater.amcl.model.fabric.OldFabricVersionModel;
import com.mcreater.amcl.tasks.AbstractDownloadTask;
import com.mcreater.amcl.tasks.LibDownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.tasks.manager.TaskManager;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.net.HttpConnectionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import static com.mcreater.amcl.util.FileUtils.FileStringReader;
import static com.mcreater.amcl.util.FileUtils.LinkPath;
import static com.mcreater.amcl.util.FileUtils.OperateUtil.createDirectory;
import static com.mcreater.amcl.util.FileUtils.PathUtil.buildPath;
import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class FabricDownload {
    static int chunkSize;
    static Vector<Task> tasks = new Vector<>();
    static Logger logger = LogManager.getLogger(FabricDownload.class);
    public static void download(String id, String minecraft_dir, String version_name, int chunkSize, String fabric_version, Runnable ru, FasterUrls.Servers server) throws Exception {
        tasks.clear();
        FabricDownload.chunkSize = chunkSize;
        Vector<String> vers = GetVersionList.getFabricVersionList(id, server);

        if (vers.size() == 0 || !vers.contains(fabric_version)) {
            throw new IOException();
        }
        OriginalDownload.download(id, minecraft_dir, version_name, chunkSize, server);
        ru.run();
        String fab = FasterUrls.fast(String.format("https://meta.fabricmc.net/v2/versions/loader/%s/%s", id, fabric_version), server);
        String r = HttpConnectionUtil.doGet(fab);
        try {
            FabricVersionModel model = GSON_PARSER.fromJson(r, FabricVersionModel.class);
            String lib_base = LinkPath.link(minecraft_dir, "libraries");
            String versionJson = String.format(buildPath("%s", "versions", "%s", "%s.json"), minecraft_dir, version_name, version_name);
            JSONObject ao = new JSONObject(FileStringReader.read(versionJson));
            ao.put("mainClass", model.launcherMeta.mainClass.client);
            for (FabricLibModel lib : model.launcherMeta.libraries.common) {
                if (lib.name.contains("launchwrapper")) {
                    throw new IOException();
                }
                String url = FasterUrls.fast(lib.url + MavenPathConverter.get(lib.name).replace("\\", "/"), server);
                String path = LinkPath.link(lib_base, MavenPathConverter.get(lib.name));
                createDirectory(path);
                ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(lib), Map.class));
                tasks.add(new LibDownloadTask(url, path, chunkSize));
            }
            for (FabricLibModel lib : model.launcherMeta.libraries.client) {
                if (lib.name.contains("launchwrapper")) {
                    throw new IOException();
                }
                String url = FasterUrls.fast(lib.url + MavenPathConverter.get(lib.name).replace("\\", "/"), server);
                String path = LinkPath.link(lib_base, MavenPathConverter.get(lib.name));
                createDirectory(path);
                ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(lib), Map.class));
                tasks.add(new LibDownloadTask(url, path, chunkSize));
            }
            String url = FasterUrls.fast("https://maven.fabricmc.net/" + MavenPathConverter.get(model.intermediary.maven).replace("\\", "/"), server);
            String path = LinkPath.link(lib_base, MavenPathConverter.get(model.intermediary.maven));
            createDirectory(path);
            FabricLibModel model1 = new FabricLibModel();
            model1.name = model.intermediary.maven;
            model1.url = "https://maven.fabricmc.net/";
            ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(model1), Map.class));
            tasks.add(new LibDownloadTask(url, path, chunkSize));

            url = FasterUrls.fast("https://maven.fabricmc.net/" + MavenPathConverter.get(model.loader.maven).replace("\\", "/"), server);
            path = LinkPath.link(lib_base, MavenPathConverter.get(model.loader.maven));
            createDirectory(path);
            model1 = new FabricLibModel();
            model1.name = model.loader.maven;
            model1.url = "https://maven.fabricmc.net/";
            ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(model1), Map.class));
            tasks.add(new LibDownloadTask(url, path, chunkSize));

            tasks.forEach(task -> {
                if (task instanceof AbstractDownloadTask) ((AbstractDownloadTask) task).setType(AbstractDownloadTask.TaskType.FABRIC);
            });

            TaskManager.addTasks(tasks);
            TaskManager.execute("<fabric>");
            BufferedWriter bw = new BufferedWriter(new FileWriter(versionJson));
            bw.write(ao.toString());
            bw.close();
        }
        catch (JsonSyntaxException e){
            OldFabricVersionModel model = GSON_PARSER.fromJson(r, OldFabricVersionModel.class);
            String lib_base = LinkPath.link(minecraft_dir, "libraries");
            String versionJson = String.format(buildPath("%s", "versions", "%s", "%s.json"), minecraft_dir, version_name, version_name);
            JSONObject ao = new JSONObject(FileStringReader.read(versionJson));
            ao.put("mainClass", model.launcherMeta.mainClass);
            for (String a : model.launcherMeta.arguments.common){
                ao.getJSONObject("arguments").getJSONArray("game").put(a);
            }
            for (String a : model.launcherMeta.arguments.client){
                ao.getJSONObject("arguments").getJSONArray("game").put(a);
            }

            ao.getJSONObject("arguments").getJSONArray("game").put("--tweakClass");
            ao.getJSONObject("arguments").getJSONArray("game").put(model.launcherMeta.launchwrapper.get("tweakers").client.get(0));

            for (FabricLibModel s : model.launcherMeta.libraries.common){
                String url = FasterUrls.fast(J8Utils.requireNonNullElse(s.url, "https://libraries.minecraft.net/") + MavenPathConverter.get(s.name).replace("\\", "/"), server);
                String path = LinkPath.link(lib_base, MavenPathConverter.get(s.name));
                createDirectory(path);
                ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(s), Map.class));
                tasks.add(new LibDownloadTask(url, path, chunkSize));
            }
            for (FabricLibModel s : model.launcherMeta.libraries.client){
                String url = FasterUrls.fast(J8Utils.requireNonNullElse(s.url, "https://libraries.minecraft.net/") + MavenPathConverter.get(s.name).replace("\\", "/"), server);
                String path = LinkPath.link(lib_base, MavenPathConverter.get(s.name));
                createDirectory(path);
                ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(s), Map.class));
                tasks.add(new LibDownloadTask(url, path, chunkSize));
            }
            String url = FasterUrls.fast("https://maven.fabricmc.net/" + MavenPathConverter.get(model.loader.maven).replace("\\", "/"), server);
            String path = LinkPath.link(lib_base, MavenPathConverter.get(model.loader.maven));
            FabricLibModel model1 = new FabricLibModel();
            model1.name = model.loader.maven;
            ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(model1), Map.class));
            tasks.add(new LibDownloadTask(url, path, chunkSize));

            String url1 = FasterUrls.fast("https://maven.fabricmc.net/" + MavenPathConverter.get(model.intermediary.maven).replace("\\", "/"), server);
            String path1 = LinkPath.link(lib_base, MavenPathConverter.get(model.intermediary.maven));
            FabricLibModel model2 = new FabricLibModel();
            model1.name = model.intermediary.maven;
            ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(model2), Map.class));
            tasks.add(new LibDownloadTask(url1, path1, chunkSize));

            tasks.forEach(task -> {
                if (task instanceof AbstractDownloadTask) ((AbstractDownloadTask) task).setType(AbstractDownloadTask.TaskType.FABRIC);
            });

            TaskManager.addTasks(tasks);
            TaskManager.execute("<old fabric>");
            BufferedWriter w = new BufferedWriter(new FileWriter(versionJson));
            w.write(ao.toString());
            w.close();
        }
    }
}
