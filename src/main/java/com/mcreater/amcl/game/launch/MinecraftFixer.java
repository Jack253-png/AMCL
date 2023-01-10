package com.mcreater.amcl.game.launch;

import com.mcreater.amcl.StableMain;
import com.mcreater.amcl.game.MavenPathConverter;
import com.mcreater.amcl.model.JarModel;
import com.mcreater.amcl.model.LibModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.model.original.AssetsModel;
import com.mcreater.amcl.tasks.AbstractDownloadTask;
import com.mcreater.amcl.tasks.AssetsDownloadTask;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.tasks.LibDownloadTask;
import com.mcreater.amcl.tasks.NativeDownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.tasks.manager.TaskManager;
import com.mcreater.amcl.util.FileUtils.FileStringReader;
import com.mcreater.amcl.util.FileUtils.HashHelper;
import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.net.FasterUrls;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import static com.mcreater.amcl.download.OriginalDownload.checkAllowState;
import static com.mcreater.amcl.util.FileUtils.OperateUtil.createDirectory;
import static com.mcreater.amcl.util.FileUtils.OperateUtil.createDirectoryDirect;
import static com.mcreater.amcl.util.FileUtils.PathUtil.buildPath;
import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class MinecraftFixer {
    static Vector<Task> tasks = new Vector<>();
    public static void fix(int chunkSize, String dir, String versionName, FasterUrls.Servers server) throws IOException, InterruptedException {
        tasks.clear();
        String versionDir = LinkPath.link(dir, String.format(buildPath("versions", "%s"), versionName));
        String assetsDir = String.format(buildPath("%s", "assets", "indexes"), dir);
        if (!new File(versionDir).exists()){
            throw new IOException("version dir does not exists");
        }
        String versionJson = String.format(buildPath("%s", "%s.json"), versionDir, versionName);
        if (!new File(versionJson).exists()){
            throw new IOException("version json does not exists");
        }
        VersionJsonModel model = GSON_PARSER.fromJson(FileStringReader.read(versionJson), VersionJsonModel.class);
        if (model == null){
            throw new IOException("failed to read version json");
        }
        checkLibs(chunkSize, dir, model.libraries, versionDir, versionName, server);
        checkCoreJar(chunkSize, versionDir, versionName, model.downloads.get("client"), server);
        checkAssets(chunkSize, assetsDir, model, dir, server);
        runTasks();
    }
    public static void runTasks() throws InterruptedException {
        TaskManager.addTasks(tasks);
        TaskManager.execute("<full files>");
    }
    public static void checkAssets(int chunk, String assets, VersionJsonModel model, String minecraft_dir, FasterUrls.Servers server) throws IOException {
        String index = assets + model.assetIndex.get("id") + ".json";
        String assets_root = LinkPath.link(minecraft_dir, "assets");
        String assets_objects = LinkPath.link(assets_root, "objects");
        createDirectoryDirect(assets_objects);
        if (!HashHelper.validateSHA1(new File(index), model.assetIndex.get("sha1"))) {
            new DownloadTask(FasterUrls.fast(model.assetIndex.get("url"), server), index, chunk).execute();
        }
        AssetsModel m = GSON_PARSER.fromJson(FileStringReader.read(index), AssetsModel.class);
        for (Map.Entry<String, Map<String, String>> entry : m.objects.entrySet()) {
            String hash = entry.getValue().get("hash");
            String s = String.format(buildPath("%s", "%s", "%s"), assets_objects, hash.substring(0, 2), hash);
            if (!HashHelper.validateSHA1(new File(s), hash)) {
                boolean contained = false;
                for (Task task : tasks) {
                    if (task instanceof AbstractDownloadTask) {
                        if (Objects.equals(((AbstractDownloadTask) task).local, s)) {
                            contained = true;
                            break;
                        }
                    }
                }
                if (!contained) {
                    tasks.add(new AssetsDownloadTask(hash, assets_objects, chunk, server));
                }
            }
        }
    }
    public static void checkCoreJar(int chunk, String versionDir, String versionName, JarModel model, FasterUrls.Servers server) throws FileNotFoundException {
        String path = LinkPath.link(versionDir, String.format("%s.jar", versionName));
        String url = model.url;
        String hash = model.sha1;
        if (!HashHelper.validateSHA1(new File(path), hash)){
            tasks.add(new LibDownloadTask(FasterUrls.fast(url, server), path, chunk).setHash(hash));
        }
    }
    public static void checkLibs(int chunk, String dir, Vector<LibModel> libs, String version_dir, String version_name, FasterUrls.Servers server) throws FileNotFoundException {
        String lib_base_path = LinkPath.link(dir, "libraries");
        String native_base_path = LinkPath.link(version_dir, version_name + "-natives");
        createDirectoryDirect(lib_base_path);
        for (LibModel model1 : libs) {
            if (checkAllowState(model1)) {
                String nativeName = StableMain.getSystem2.run();
                if (model1.downloads != null) {
                    if (model1.downloads.classifiers != null) {
                        if (model1.downloads.classifiers.containsKey("natives-osx"))
                            nativeName = nativeName.replace("natives-macos", "natives-osx").replace("-arm64", "");

                        if (model1.downloads.classifiers.get(nativeName) != null) {
                            String npath = LinkPath.link(lib_base_path, model1.downloads.classifiers.get(nativeName).path);
                            String nurl = model1.downloads.classifiers.get(nativeName).url;
                            String nhash = model1.downloads.classifiers.get(nativeName).sha1;
                            createDirectory(npath);
                            if (!HashHelper.validateSHA1(new File(npath), nhash)) {
                                if (nhash == null && new File(npath).exists()) {
                                    continue;
                                }
                                tasks.add(new NativeDownloadTask(FasterUrls.fast(nurl, server), npath, native_base_path, chunk).setHash(nhash));
                            }
                        }
                    }
                    if (model1.downloads.artifact != null) {
                        String path = model1.downloads.artifact.get("path") != null ? LinkPath.link(lib_base_path, model1.downloads.artifact.get("path").replace("\\", File.separator)) : LinkPath.link(lib_base_path, MavenPathConverter.get(model1.name));
                        String url = model1.downloads.artifact.get("url");
                        String hash = model1.downloads.artifact.get("sha1");
                        createDirectory(path);

                        if (!HashHelper.validateSHA1(new File(path), hash)) {
                            if (hash == null && new File(path).exists()) {
                                continue;
                            }
                            if (model1.name.contains(nativeName)) {
                                tasks.add(new NativeDownloadTask(FasterUrls.fast(url, server), path, native_base_path, chunk).setHash(hash));
                            } else {
                                tasks.add(new LibDownloadTask(FasterUrls.fast(url, server), path, chunk).setHash(hash));
                            }
                        }
                    }
                }
            }
        }
    }
}
