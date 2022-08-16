package com.mcreater.amcl.download;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.game.MavenPathConverter;
import com.mcreater.amcl.model.fabric.*;
import com.mcreater.amcl.tasks.LibDownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.tasks.taskmanager.TaskManager;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.StringUtils;
import static com.mcreater.amcl.util.FileUtils.*;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.net.HttpConnectionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

public class FabricDownload {
    static int chunkSize;
    static Vector<Task> tasks = new Vector<>();
    static Logger logger = LogManager.getLogger(FabricDownload.class);
    public static void download(boolean faster, String id, String minecraft_dir, String version_name, int chunkSize, String fabric_version, Runnable ru) throws IOException, InterruptedException {
        tasks.clear();
        FabricDownload.chunkSize = chunkSize;
        String fabricVersions = FasterUrls.fast("https://meta.fabricmc.net/v2/versions/loader", FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
        Gson g = new Gson();
        String raw = String.format("{\"versions\" : %s}", HttpConnectionUtil.doGet(fabricVersions));

        FabricListModel versions = g.fromJson(raw, FabricListModel.class);
        String ver = null;
        for (FabricLoaderVersionModel model : versions.versions) {
            if (Objects.equals(model.version, fabric_version)) {
                ver = model.version;
                break;
            }
        }
        if (ver == null) {
            throw new IOException();
        }
        OriginalDownload.download(faster, id, minecraft_dir, version_name, chunkSize);
        ru.run();
        String fab = FasterUrls.fast(String.format("https://meta.fabricmc.net/v2/versions/loader/%s/%s", id, ver), FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
        String r = HttpConnectionUtil.doGet(fab);
        try {
            FabricVersionModel model = g.fromJson(r, FabricVersionModel.class);
            String lib_base = LinkPath.link(minecraft_dir, "libraries");
            String versionJson = String.format("%s\\versions\\%s\\%s.json", minecraft_dir, version_name, version_name);
            JSONObject ao = new JSONObject(FileStringReader.read(versionJson));
            ao.put("mainClass", model.launcherMeta.mainClass.client);
            for (FabricLibModel lib : model.launcherMeta.libraries.common) {
                if (lib.name.contains("launchwrapper")) {
                    throw new IOException();
                }
                String url = FasterUrls.fast(lib.url + MavenPathConverter.get(lib.name).replace("\\", "/"), FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
                String path = LinkPath.link(lib_base, MavenPathConverter.get(lib.name));
                new File(StringUtils.GetFileBaseDir.get(path)).mkdirs();
                ao.getJSONArray("libraries").put(g.fromJson(g.toJson(lib), Map.class));
                tasks.add(new LibDownloadTask(url, path, chunkSize));
            }
            for (FabricLibModel lib : model.launcherMeta.libraries.client) {
                if (lib.name.contains("launchwrapper")) {
                    throw new IOException();
                }
                String url = FasterUrls.fast(lib.url + MavenPathConverter.get(lib.name).replace("\\", "/"), FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
                String path = LinkPath.link(lib_base, MavenPathConverter.get(lib.name));
                new File(StringUtils.GetFileBaseDir.get(path)).mkdirs();
                ao.getJSONArray("libraries").put(g.fromJson(g.toJson(lib), Map.class));
                tasks.add(new LibDownloadTask(url, path, chunkSize));
            }
            String url = FasterUrls.fast("https://maven.fabricmc.net/" + MavenPathConverter.get(model.intermediary.maven).replace("\\", "/"), FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
            String path = LinkPath.link(lib_base, MavenPathConverter.get(model.intermediary.maven));
            new File(StringUtils.GetFileBaseDir.get(path)).mkdirs();
            FabricLibModel model1 = new FabricLibModel();
            model1.name = model.intermediary.maven;
            model1.url = "https://maven.fabricmc.net/";
            ao.getJSONArray("libraries").put(g.fromJson(g.toJson(model1), Map.class));
            tasks.add(new LibDownloadTask(url, path, chunkSize));

            url = FasterUrls.fast("https://maven.fabricmc.net/" + MavenPathConverter.get(model.loader.maven).replace("\\", "/"), FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
            path = LinkPath.link(lib_base, MavenPathConverter.get(model.loader.maven));
            new File(StringUtils.GetFileBaseDir.get(path)).mkdirs();
            model1 = new FabricLibModel();
            model1.name = model.loader.maven;
            model1.url = "https://maven.fabricmc.net/";
            ao.getJSONArray("libraries").put(g.fromJson(g.toJson(model1), Map.class));
            tasks.add(new LibDownloadTask(url, path, chunkSize));
            TaskManager.addTasks(tasks);
            TaskManager.execute("<fabric>");
            BufferedWriter bw = new BufferedWriter(new FileWriter(versionJson));
            bw.write(ao.toString());
            bw.close();
        }
        catch (JsonSyntaxException e){
            OldFabricVersionModel model = g.fromJson(r, OldFabricVersionModel.class);
            String lib_base = LinkPath.link(minecraft_dir, "libraries");
            String versionJson = String.format("%s\\versions\\%s\\%s.json", minecraft_dir, version_name, version_name);
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
                String url = FasterUrls.fast(J8Utils.requireNonNullElse(s.url, "https://libraries.minecraft.net/") + MavenPathConverter.get(s.name).replace("\\", "/"), FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
                String path = LinkPath.link(lib_base, MavenPathConverter.get(s.name));
                new File(StringUtils.GetFileBaseDir.get(path)).mkdirs();
                ao.getJSONArray("libraries").put(g.fromJson(g.toJson(s), Map.class));
                tasks.add(new LibDownloadTask(url, path, chunkSize));
            }
            for (FabricLibModel s : model.launcherMeta.libraries.client){
                String url = FasterUrls.fast(J8Utils.requireNonNullElse(s.url, "https://libraries.minecraft.net/") + MavenPathConverter.get(s.name).replace("\\", "/"), FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
                String path = LinkPath.link(lib_base, MavenPathConverter.get(s.name));
                new File(StringUtils.GetFileBaseDir.get(path)).mkdirs();
                ao.getJSONArray("libraries").put(g.fromJson(g.toJson(s), Map.class));
                tasks.add(new LibDownloadTask(url, path, chunkSize));
            }
            String url = FasterUrls.fast("https://maven.fabricmc.net/" + MavenPathConverter.get(model.loader.maven).replace("\\", "/"), FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
            String path = LinkPath.link(lib_base, MavenPathConverter.get(model.loader.maven));
            FabricLibModel model1 = new FabricLibModel();
            model1.name = model.loader.maven;
            ao.getJSONArray("libraries").put(g.fromJson(g.toJson(model1), Map.class));
            tasks.add(new LibDownloadTask(url, path, chunkSize));

            String url1 = FasterUrls.fast("https://maven.fabricmc.net/" + MavenPathConverter.get(model.intermediary.maven).replace("\\", "/"), FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
            String path1 = LinkPath.link(lib_base, MavenPathConverter.get(model.intermediary.maven));
            FabricLibModel model2 = new FabricLibModel();
            model1.name = model.intermediary.maven;
            ao.getJSONArray("libraries").put(g.fromJson(g.toJson(model2), Map.class));
            tasks.add(new LibDownloadTask(url1, path1, chunkSize));
            TaskManager.addTasks(tasks);
            TaskManager.execute("<old fabric>");
            BufferedWriter w = new BufferedWriter(new FileWriter(versionJson));
            w.write(ao.toString());
            w.close();
        }
    }
}
