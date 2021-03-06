package com.mcreater.amcl.game.launch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcreater.amcl.model.JarModel;
import com.mcreater.amcl.model.LibModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.model.original.AssetsModel;
import com.mcreater.amcl.tasks.taskmanager.TaskManager;
import com.mcreater.amcl.tasks.*;
import com.mcreater.amcl.util.FasterUrls;
import com.mcreater.amcl.util.FileStringReader;
import com.mcreater.amcl.util.HashHelper;
import com.mcreater.amcl.util.LinkPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import static com.mcreater.amcl.download.OriginalDownload.createNewDir;

public class MinecraftFixer {
    static Vector<Task> tasks = new Vector<>();
    public static void fix(boolean faster, int chunkSize, String dir, String versionName) throws IOException, InterruptedException {
        String versionDir = LinkPath.link(dir, String.format("versions\\%s", versionName));
        String libDir = String.format("%s\\libraries", dir);
        String assetsDir = String.format("%s\\assets\\indexes\\", dir);
        if (!new File(versionDir).exists()){
            throw new IOException("version dir does not exists");
        }
        String versionJson = String.format("%s\\%s.json", versionDir, versionName);
        if (!new File(versionJson).exists()){
            throw new IOException("version json does not exists");
        }
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        VersionJsonModel model = gson.fromJson(FileStringReader.read(versionJson), VersionJsonModel.class);
        if (model == null){
            throw new IOException("failed to read version json");
        }
        checkLibs(faster, chunkSize, dir, model.libraries, versionDir, versionName);
        checkCoreJar(faster, chunkSize, versionDir, versionName, model.downloads.get("client"));
        checkAssets(faster, chunkSize, assetsDir, model, dir);
        runTasks();
    }
    public static void runTasks() throws InterruptedException {
        TaskManager.addTasks(tasks);
        TaskManager.execute("<full files>");
    }
    public static void checkAssets(boolean faster, int chunk, String assets, VersionJsonModel model, String minecraft_dir) throws IOException {
        String index = assets + model.assetIndex.get("id") + ".json";
        String assets_root = LinkPath.link(minecraft_dir, "assets");
        String assets_objects = LinkPath.link(assets_root, "objects");
        new File(assets_objects).mkdirs();
        if (!HashHelper.getFileSHA1(new File(index)).equals(model.assetIndex.get("sha1"))){
            new DownloadTask(FasterUrls.fast(model.assetIndex.get("url"), faster), index, chunk).setHash(model.assetIndex.get("sha1")).execute();
        }
        AssetsModel m = new Gson().fromJson(FileStringReader.read(index), AssetsModel.class);
        for (Map.Entry<String, Map<String, String>> entry : m.objects.entrySet()){
            String hash = entry.getValue().get("hash");
            String s = String.format("%s\\%s\\%s", assets_objects, hash.substring(0, 2), hash);
            if (!HashHelper.getFileSHA1(new File(s)).equals(hash)) {
                boolean contained = false;
                for (Task task : tasks){
                    if (Objects.equals(((AbstractTask) task).local, s)) {
                        contained = true;
                        break;
                    }
                }
                if (!contained) {
                    tasks.add(new AssetsDownloadTask(hash, assets_objects, faster, chunk));
                }
            }
        }
    }
    public static void checkCoreJar(boolean faster, int chunk, String versionDir, String versionName, JarModel model) throws FileNotFoundException {
        String path = LinkPath.link(versionDir, String.format("%s.jar", versionName));
        String url = model.url;
        String hash = model.sha1;
        if (!HashHelper.getFileSHA1(new File(path)).equals(hash)){
            tasks.add(new LibDownloadTask(FasterUrls.fast(url, faster), path, chunk).setHash(hash));
        }
    }
    public static void checkLibs(boolean faster, int chunk, String dir, Vector<LibModel> libs, String version_dir, String version_name) throws FileNotFoundException {
        String lib_base_path = LinkPath.link(dir, "libraries");
        String native_base_path = LinkPath.link(version_dir, version_name + "-natives");
        new File(lib_base_path).mkdirs();
        boolean has_321 = false;
        boolean has_322 = false;
        for (LibModel model1 : libs) {
            if (model1.downloads != null) {
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
        }
        for (LibModel model1 : libs){
            boolean b0 = !(has_322 && model1.name.contains("3.2.1"));
            if (model1.downloads != null){
                if (model1.downloads.classifiers != null) {
                    if (model1.downloads.classifiers.get("natives-windows") != null) {
                        if (b0) {
                            String npath = LinkPath.link(lib_base_path, model1.downloads.classifiers.get("natives-windows").path);
                            String nurl = model1.downloads.classifiers.get("natives-windows").url;
                            String nhash = model1.downloads.classifiers.get("natives-windows").sha1;
                            createNewDir(npath);
                            if (!HashHelper.getFileSHA1(new File(npath)).equals(nhash)) {
                                tasks.add(new NativeDownloadTask(FasterUrls.fast(nurl, faster), npath, native_base_path, chunk).setHash(nhash));
                            }
                        }
                    }
                }
                if (model1.downloads.artifact != null) {
                    String path = LinkPath.link(lib_base_path, model1.downloads.artifact.get("path").replace("/", "\\"));
                    String url = model1.downloads.artifact.get("url");
                    String hash = model1.downloads.artifact.get("sha1");
                    createNewDir(path);

                    if (b0) {
                        if (!HashHelper.getFileSHA1(new File(path)).equals(hash)) {
                            if (model1.name.contains("natives-windows")) {
                                tasks.add(new NativeDownloadTask(FasterUrls.fast(url, faster), path, native_base_path, chunk).setHash(hash));
                            } else {
                                tasks.add(new LibDownloadTask(FasterUrls.fast(url, faster), path, chunk).setHash(hash));
                            }
                        }
                    }
                }
            }
        }
    }
}
