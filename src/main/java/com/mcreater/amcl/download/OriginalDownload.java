package com.mcreater.amcl.download;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcreater.amcl.download.tasks.*;
import com.mcreater.amcl.model.LibModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.model.original.AssetsModel;
import com.mcreater.amcl.model.original.VersionModel;
import com.mcreater.amcl.model.original.VersionsModel;
import com.mcreater.amcl.util.FasterUrls;
import com.mcreater.amcl.util.LinkPath;
import com.mcreater.amcl.util.net.HttpConnectionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class OriginalDownload {
    static GsonBuilder gb;
    static Vector<AbstractTask> tasks = new Vector<>();
    static int chunkSize;
    static Logger logger = LogManager.getLogger(OriginalDownload.class);
    public static void download(boolean faster, String id, String minecraft_dir, String version_name, int chunkSize) throws IOException {
        tasks.clear();
        OriginalDownload.chunkSize = chunkSize;
        String url = "http://launchermeta.mojang.com/mc/game/version_manifest.json";
        gb = new GsonBuilder();
        gb.setPrettyPrinting();
        Gson g = gb.create();
        url = FasterUrls.fast(url, faster);
        String result = HttpConnectionUtil.doGet(url);

        VersionsModel model = g.fromJson(result, VersionsModel.class);
        String version_url = null;
        for (VersionModel m : model.versions){
            if (Objects.equals(id, m.id)){
                version_url = m.url;
            }
        }
        if (version_url == null){
            throw new IOException();
        }
        String version_dir = LinkPath.link(minecraft_dir, "versions\\" + version_name);
        new File(version_dir).mkdirs();

        String version_json = HttpConnectionUtil.doGet(version_url);
        BufferedWriter bw = new BufferedWriter(new FileWriter(LinkPath.link(version_dir, version_name + ".json")));
        bw.write(version_json);
        bw.close();

        VersionJsonModel ver_j = g.fromJson(version_json, VersionJsonModel.class);
        if (!Objects.equals(id, ver_j.id)){
            throw new IOException();
        }
        downloadCoreJar(ver_j, faster, minecraft_dir, version_dir, version_name);
        downloadLibs(ver_j, faster, minecraft_dir, version_dir, version_name);
        downloadAssets(ver_j, faster, minecraft_dir);
        runTasks();
    }
    private static void downloadCoreJar(VersionJsonModel model, boolean faster, String minecraft_dir, String version_dir, String version_name){
        String url = model.downloads.get("client").url;
        String path = LinkPath.link(version_dir, version_name + ".jar");
        String hash = model.downloads.get("client").sha1;
        tasks.add(new LibDownloadTask(url, path, chunkSize).setHash(hash));
    }
    public static void runTasks(){
        AtomicInteger i = new AtomicInteger(0);
        Vector<AbstractTask> temp = new Vector<>();
        new Thread(() -> {
            do {
                logger.info(String.format("%d / %d\n", i.get(), tasks.size()));
//                if (tasks.size() - i.get() <= 5){
//                    for (AbstractTask t : tasks){
//                        if (!temp.contains(t)){
//                            logger.info(t.server);
//                        }
//                    }
//                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } while (i.get() != tasks.size());
        }).start();
        for (AbstractTask task : tasks){
            new Thread(() -> {
                while (true) {
                    try {
                        task.execute();
                        i.addAndGet(1);
                        temp.add(task);
                        break;
                    } catch (Exception eignored) {
                        eignored.printStackTrace();
                    }
                }
            }).start();
        }
        do{}while (i.get() != tasks.size());
    }
    public static void createNewDir(String path){
        Vector<String> paths = new Vector<>(List.of(path.split("\\\\")));
        new File(path.replace(paths.get(paths.size() - 1), "")).mkdirs();
    }
    private static void downloadAssets(VersionJsonModel model, boolean faster, String minecraft_dir) throws IOException {
        String assets_root = LinkPath.link(minecraft_dir, "assets");
        String assets_indexes = LinkPath.link(assets_root, "indexes");
        String assets_objects = LinkPath.link(assets_root, "objects");
        new File(assets_root).mkdirs();
        new File(assets_indexes).mkdirs();
        new File(assets_objects).mkdirs();
        String result = HttpConnectionUtil.doGet(model.assetIndex.get("url"));
        String assets_index_path = LinkPath.link(assets_indexes, model.assetIndex.get("id") + ".json");
        BufferedWriter bw = new BufferedWriter(new FileWriter(assets_index_path));
        bw.write(result);
        bw.close();
        Gson g = gb.create();
        AssetsModel model1 = g.fromJson(result, AssetsModel.class);
        for (Map<String, String> h : model1.objects.values()){
            String hh = h.get("hash");
            new File(String.format("%s\\%s", assets_objects, hh.substring(0, 2))).mkdirs();
            tasks.add(new AssetsDownloadTask(hh, assets_objects, faster, chunkSize).setHash(hh));
        }
    }

    private static void downloadLibs(VersionJsonModel model, boolean faster, String minecraft_dir, String version_dir, String version_name){
        String lib_base_path = LinkPath.link(minecraft_dir, "libraries");
        String native_base_path = LinkPath.link(version_dir, version_name + "-natives");
        new File(lib_base_path).mkdirs();
        boolean has_321 = false;
        boolean has_322 = false;
        for (LibModel model1 : model.libraries) {
            if (model1.downloads.artifact != null) {
                String u = model1.downloads.artifact.get("url");
                if (!has_321 && u.contains("3.2.1")) {
                    has_321 = true;
                }
                if (!has_322 && u.contains("3.2.2")) {
                    has_322 = true;
                }
            }
        }
        for (LibModel model1 : model.libraries){
            boolean b0 = !(has_322 && model1.name.contains("3.2.1"));
            if (model1.downloads.classifiers != null) {
                if (model1.downloads.classifiers.get("natives-windows") != null) {
                    if (b0) {
                        String npath = LinkPath.link(lib_base_path, model1.downloads.classifiers.get("natives-windows").path);
                        String nurl = model1.downloads.classifiers.get("natives-windows").url;
                        String nhash = model1.downloads.classifiers.get("natives-windows").sha1;
                        createNewDir(npath);
                        tasks.add(new NativeDownloadTask(FasterUrls.fast(nurl, faster), npath, native_base_path, chunkSize).setHash(nhash));
                    }
                }
            }
            if (model1.downloads.artifact != null) {
                String path = LinkPath.link(lib_base_path, model1.downloads.artifact.get("path").replace("/", "\\"));
                String url = model1.downloads.artifact.get("url");
                String hash = model1.downloads.artifact.get("sha1");
                createNewDir(path);

                if (b0) {
                    if (model1.name.contains("natives-windows")) {
                        // path, url;
                        tasks.add(new NativeDownloadTask(FasterUrls.fast(url, faster), path, native_base_path, chunkSize).setHash(hash));
                    } else {
                        tasks.add(new LibDownloadTask(FasterUrls.fast(url, faster), path, chunkSize).setHash(hash));
                    }
                }
            }
        }
    }
}
