package com.mcreater.amcl.download;

import com.mcreater.amcl.game.MavenPathConverter;
import com.mcreater.amcl.model.fabric.FabricLibModel;
import com.mcreater.amcl.model.fabric.FabricVersionModel;
import com.mcreater.amcl.tasks.AbstractDownloadTask;
import com.mcreater.amcl.tasks.LibDownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.tasks.manager.TaskManager;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.net.HttpConnectionUtil;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import static com.mcreater.amcl.util.FileUtils.OperateUtil.createDirectory;
import static com.mcreater.amcl.util.FileUtils.PathUtil.buildPath;
import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;
import static com.mcreater.amcl.util.net.FasterUrls.FABRIC_MAVEN_ROOT;
import static com.mcreater.amcl.util.net.FasterUrls.QUILT_LOADER_FORMAT;
import static com.mcreater.amcl.util.net.FasterUrls.QUILT_MAVEN_ROOT;

public class QuiltDownload {
    static Vector<Task> tasks = new Vector<>();

    public static void download(String id, String minecraft_dir, String version_name, int chunkSize, String quilt_version, Runnable ru, FasterUrls.Server server) throws Exception {
        tasks.clear();
        FabricDownload.chunkSize = chunkSize;
        Vector<String> vers = GetVersionList.getQuiltVersionList(id, server);

        if (vers.size() == 0 || !vers.contains(quilt_version)) {
            throw new IOException();
        }
        OriginalDownload.download(id, minecraft_dir, version_name, chunkSize, server);
        ru.run();
        String fab = FasterUrls.fast(String.format(QUILT_LOADER_FORMAT, id, quilt_version), server);
        String r = HttpConnectionUtil.doGet(fab);
        FabricVersionModel model = GSON_PARSER.fromJson(r, FabricVersionModel.class);
        String lib_base = FileUtils.LinkPath.link(minecraft_dir, "libraries");
        String versionJson = String.format(buildPath("%s", "versions", "%s", "%s.json"), minecraft_dir, version_name, version_name);
        JSONObject ao = new JSONObject(FileUtils.FileStringReader.read(versionJson));
        ao.put("mainClass", model.launcherMeta.mainClass.client);
        for (FabricLibModel lib : model.launcherMeta.libraries.common) {
            if (lib.name.contains("launchwrapper")) {
                throw new IOException();
            }
            String url = FasterUrls.fast(lib.url + MavenPathConverter.get(lib.name).replace("\\", "/"), server);
            String path = FileUtils.LinkPath.link(lib_base, MavenPathConverter.get(lib.name));
            createDirectory(path);
            ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(lib), Map.class));
            tasks.add(new LibDownloadTask(url, path, chunkSize));
        }
        for (FabricLibModel lib : model.launcherMeta.libraries.client) {
            if (lib.name.contains("launchwrapper")) {
                throw new IOException();
            }
            String url = FasterUrls.fast(lib.url + MavenPathConverter.get(lib.name).replace("\\", "/"), server);
            String path = FileUtils.LinkPath.link(lib_base, MavenPathConverter.get(lib.name));
            createDirectory(path);
            ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(lib), Map.class));
            tasks.add(new LibDownloadTask(url, path, chunkSize));
        }

        String url = FasterUrls.fast(FABRIC_MAVEN_ROOT + MavenPathConverter.get(model.intermediary.maven).replace("\\", "/"), server);
        String path = FileUtils.LinkPath.link(lib_base, MavenPathConverter.get(model.intermediary.maven));
        createDirectory(path);
        FabricLibModel model1 = new FabricLibModel();
        model1.name = model.intermediary.maven;
        model1.url = FABRIC_MAVEN_ROOT;
        ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(model1), Map.class));
        tasks.add(new LibDownloadTask(url, path, chunkSize));

        url = FasterUrls.fast(QUILT_MAVEN_ROOT + MavenPathConverter.get(model.loader.maven).replace("\\", "/"), server);
        path = FileUtils.LinkPath.link(lib_base, MavenPathConverter.get(model.loader.maven));
        createDirectory(path);
        model1 = new FabricLibModel();
        model1.name = model.loader.maven;
        model1.url = QUILT_MAVEN_ROOT;
        tasks.add(new LibDownloadTask(url, path, chunkSize));
        ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(model1), Map.class));

        url = FasterUrls.fast(QUILT_MAVEN_ROOT + MavenPathConverter.get(model.hashed.maven).replace("\\", "/"), server);
        path = FileUtils.LinkPath.link(lib_base, MavenPathConverter.get(model.hashed.maven));
        createDirectory(path);
        model1 = new FabricLibModel();
        model1.name = model.hashed.maven;
        model1.url = QUILT_MAVEN_ROOT;
        ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(model1), Map.class));
        tasks.add(new LibDownloadTask(url, path, chunkSize));

        tasks.forEach(task -> {
            if (task instanceof AbstractDownloadTask)
                ((AbstractDownloadTask) task).setType(AbstractDownloadTask.TaskType.FABRIC);
        });

        TaskManager.addTasks(tasks);
        TaskManager.execute("<quilt>");
        BufferedWriter bw = new BufferedWriter(new FileWriter(versionJson));
        bw.write(ao.toString());
        bw.close();
    }
}
