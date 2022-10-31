package com.mcreater.amcl.download;

import com.google.gson.Gson;
import com.mcreater.amcl.game.MavenPathConverter;
import com.mcreater.amcl.model.fabric.FabricLibModel;
import com.mcreater.amcl.model.fabric.FabricVersionModel;
import com.mcreater.amcl.tasks.LibDownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.tasks.taskmanager.TaskManager;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.StringUtils;
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
import java.util.Vector;

public class QuiltDownload {
    static int chunkSize;
    static Vector<Task> tasks = new Vector<>();
    static Logger logger = LogManager.getLogger(FabricDownload.class);
    public static void download(String id, String minecraft_dir, String version_name, int chunkSize, String quilt_version, Runnable ru, FasterUrls.Servers server) throws Exception {
        tasks.clear();
        FabricDownload.chunkSize = chunkSize;
        Gson g = new Gson();
        Vector<String> vers = GetVersionList.getQuiltVersionList(id, server);

        if (vers.size() == 0 || !vers.contains(quilt_version)) {
            throw new IOException();
        }
        OriginalDownload.download(id, minecraft_dir, version_name, chunkSize, server);
        ru.run();
        String fab = FasterUrls.fast(String.format("https://meta.quiltmc.org/v3/versions/loader/%s/%s", id, quilt_version), server);
        String r = HttpConnectionUtil.doGet(fab);
        FabricVersionModel model = g.fromJson(r, FabricVersionModel.class);
        String lib_base = FileUtils.LinkPath.link(minecraft_dir, "libraries");
        String versionJson = String.format("%s/versions/%s/%s.json", minecraft_dir, version_name, version_name);
        JSONObject ao = new JSONObject(FileUtils.FileStringReader.read(versionJson));
        ao.put("mainClass", model.launcherMeta.mainClass.client);
        for (FabricLibModel lib : model.launcherMeta.libraries.common) {
            if (lib.name.contains("launchwrapper")) {
                throw new IOException();
            }
            String url = FasterUrls.fast(lib.url + MavenPathConverter.get(lib.name).replace("\\", "/"), server);
            String path = FileUtils.LinkPath.link(lib_base, MavenPathConverter.get(lib.name));
            new File(StringUtils.GetFileBaseDir.get(path)).mkdirs();
            ao.getJSONArray("libraries").put(g.fromJson(g.toJson(lib), Map.class));
            tasks.add(new LibDownloadTask(url, path, chunkSize));
        }
        for (FabricLibModel lib : model.launcherMeta.libraries.client) {
            if (lib.name.contains("launchwrapper")) {
                throw new IOException();
            }
            String url = FasterUrls.fast(lib.url + MavenPathConverter.get(lib.name).replace("\\", "/"), server);
            String path = FileUtils.LinkPath.link(lib_base, MavenPathConverter.get(lib.name));
            new File(StringUtils.GetFileBaseDir.get(path)).mkdirs();
            ao.getJSONArray("libraries").put(g.fromJson(g.toJson(lib), Map.class));
            tasks.add(new LibDownloadTask(url, path, chunkSize));
        }

        String url = FasterUrls.fast("https://maven.fabricmc.net/" + MavenPathConverter.get(model.intermediary.maven).replace("\\", "/"), server);
        String path = FileUtils.LinkPath.link(lib_base, MavenPathConverter.get(model.intermediary.maven));
        new File(StringUtils.GetFileBaseDir.get(path)).mkdirs();
        FabricLibModel model1 = new FabricLibModel();
        model1.name = model.intermediary.maven;
        model1.url = "https://maven.fabricmc.net/";
        ao.getJSONArray("libraries").put(g.fromJson(g.toJson(model1), Map.class));
        tasks.add(new LibDownloadTask(url, path, chunkSize));

        url = FasterUrls.fast("https://maven.quiltmc.org/repository/release/" + MavenPathConverter.get(model.loader.maven).replace("\\", "/"), server);
        path = FileUtils.LinkPath.link(lib_base, MavenPathConverter.get(model.loader.maven));
        new File(StringUtils.GetFileBaseDir.get(path)).mkdirs();
        model1 = new FabricLibModel();
        model1.name = model.loader.maven;
        model1.url = "https://maven.quiltmc.org/repository/release/";
        tasks.add(new LibDownloadTask(url, path, chunkSize));
        ao.getJSONArray("libraries").put(g.fromJson(g.toJson(model1), Map.class));

        url = FasterUrls.fast("https://maven.quiltmc.org/repository/release/" + MavenPathConverter.get(model.hashed.maven).replace("\\", "/"), server);
        path = FileUtils.LinkPath.link(lib_base, MavenPathConverter.get(model.hashed.maven));
        new File(StringUtils.GetFileBaseDir.get(path)).mkdirs();
        model1 = new FabricLibModel();
        model1.name = model.hashed.maven;
        model1.url = "https://maven.quiltmc.org/repository/release/";
        ao.getJSONArray("libraries").put(g.fromJson(g.toJson(model1), Map.class));
        tasks.add(new LibDownloadTask(url, path, chunkSize));

        TaskManager.addTasks(tasks);
        TaskManager.execute("<quilt>");
        BufferedWriter bw = new BufferedWriter(new FileWriter(versionJson));
        bw.write(ao.toString());
        bw.close();
    }
}
