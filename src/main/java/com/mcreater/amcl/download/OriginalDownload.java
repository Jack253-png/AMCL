package com.mcreater.amcl.download;

import com.mcreater.amcl.StableMain;
import com.mcreater.amcl.game.MavenPathConverter;
import com.mcreater.amcl.model.LibModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.model.original.AssetsModel;
import com.mcreater.amcl.model.original.VersionModel;
import com.mcreater.amcl.model.original.VersionsModel;
import com.mcreater.amcl.nativeInterface.OSInfo;
import com.mcreater.amcl.tasks.AssetsDownloadTask;
import com.mcreater.amcl.tasks.LibDownloadTask;
import com.mcreater.amcl.tasks.NativeDownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.tasks.manager.TaskManager;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.net.HttpConnectionUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mcreater.amcl.util.FileUtils.OperateUtil.createDirectory;
import static com.mcreater.amcl.util.FileUtils.OperateUtil.createDirectoryDirect;
import static com.mcreater.amcl.util.FileUtils.PathUtil.buildPath;
import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class OriginalDownload {
    static Vector<Task> tasks = new Vector<>();
    static int chunkSize;
    static String vj;

    public static String getVJ() {
        return vj;
    }

    public static void download(String id, String minecraft_dir, String version_name, int chunkSize, FasterUrls.Server server) throws Exception {
        tasks.clear();
        OriginalDownload.chunkSize = chunkSize;
        String url = FasterUrls.getVersionJsonv2WithFaster(server);
        url = FasterUrls.fast(url, server);
        String result = HttpConnectionUtil.doGet(url);

        VersionsModel model = GSON_PARSER.fromJson(result, VersionsModel.class);
        String version_url = null;
        for (VersionModel m : model.versions) {
            if (Objects.equals(id, m.id)) {
                version_url = m.url;
            }
        }
        if (version_url == null) {
            throw new IOException();
        }

        String version_dir = LinkPath.link(minecraft_dir, String.format(buildPath("versions", "%s"), version_name));
        createDirectoryDirect(version_dir);

        String version_json = HttpConnectionUtil.doGet(FasterUrls.fast(version_url, server));
        vj = version_json;

        BufferedWriter bw = new BufferedWriter(new FileWriter(LinkPath.link(version_dir, version_name + ".json")));
        bw.write(version_json);
        bw.close();

        VersionJsonModel ver_j = GSON_PARSER.fromJson(version_json, VersionJsonModel.class);
        if (!Objects.equals(id, ver_j.id)) {
            throw new IOException();
        }

        downloadCoreJar(ver_j, minecraft_dir, version_dir, version_name, server);
        downloadLibs(ver_j, minecraft_dir, version_dir, version_name, server, chunkSize);
        downloadAssets(ver_j, minecraft_dir, server, chunkSize);
        runTasks();
    }

    private static void downloadCoreJar(VersionJsonModel model, String minecraft_dir, String version_dir, String version_name, FasterUrls.Server server) throws FileNotFoundException {
        String url = FasterUrls.fast(model.downloads.get("client").url, server);
        String path = LinkPath.link(version_dir, version_name + ".jar");
        String hash = model.downloads.get("client").sha1;
        tasks.add(new LibDownloadTask(url, path, chunkSize).setHash(hash));
    }

    public static void runTasks() throws InterruptedException {
        TaskManager.addTasks(tasks);
        TaskManager.execute("<vanilla>");
    }

    private static void downloadAssets(VersionJsonModel model, String minecraft_dir, FasterUrls.Server server, int chunk) throws Exception {
        String assets_root = LinkPath.link(minecraft_dir, "assets");
        String assets_indexes = LinkPath.link(assets_root, "indexes");
        String assets_objects = LinkPath.link(assets_root, "objects");
        createDirectoryDirect(assets_root);
        createDirectoryDirect(assets_indexes);
        createDirectoryDirect(assets_objects);
        String result = HttpConnectionUtil.doGet(FasterUrls.fast(model.assetIndex.get("url"), server));
        String assets_index_path = LinkPath.link(assets_indexes, model.assetIndex.get("id") + ".json");
        if (!FileUtils.HashHelper.validateSHA1(new File(assets_index_path), model.assetIndex.get("sha1"))) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(assets_index_path));
            bw.write(result);
            bw.close();
        }

        AssetsModel m = GSON_PARSER.fromJson(result, AssetsModel.class);
        for (Map.Entry<String, Map<String, String>> entry : m.objects.entrySet()) {
            String hash = entry.getValue().get("hash");
            tasks.add(new AssetsDownloadTask(hash, assets_objects, chunk, server));
        }
    }

    private static void downloadLibs(VersionJsonModel model, String minecraft_dir, String version_dir, String version_name, FasterUrls.Server server, int chunk) throws FileNotFoundException {
        String lib_base_path = LinkPath.link(minecraft_dir, "libraries");
        String native_base_path = LinkPath.link(version_dir, version_name + "-natives");
        createDirectoryDirect(lib_base_path);

        String nativeName = StableMain.getSystem2.run();
        for (LibModel model1 : model.libraries) {
            if (checkAllowState(model1)) {
                if (model1.downloads != null) {
                    if (model1.downloads.classifiers != null) {
                        if (model1.downloads.classifiers.containsKey("natives-osx"))
                            nativeName = nativeName.replace("natives-macos", "natives-osx").replace("-arm64", "");

                        if (model1.downloads.classifiers.get(nativeName) != null) {
                            String npath = LinkPath.link(lib_base_path, model1.downloads.classifiers.get(nativeName).path);
                            String nurl = model1.downloads.classifiers.get(nativeName).url;
                            String nhash = model1.downloads.classifiers.get(nativeName).sha1;
                            createDirectory(npath);

                            if (!FileUtils.HashHelper.validateSHA1(new File(npath), nhash)) {
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

                        if (!FileUtils.HashHelper.validateSHA1(new File(path), hash)) {
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

    public static boolean checkAllowState(LibModel model) {
        if (model.rules == null) return true;
        AtomicBoolean allowed = new AtomicBoolean(false);
        AtomicBoolean usaled = new AtomicBoolean(false);
        model.rules.forEach(rulesModel -> {
            if (rulesModel.os == null) {
                if (!usaled.get()) allowed.set(toAllowState(rulesModel.action));
            } else {
                if (specialEqual(rulesModel.os.name, OSInfo.getOSNameCore()) && specialEqual(rulesModel.os.arch, OSInfo.getOSArchCore())) {
                    allowed.set(toAllowState(rulesModel.action));
                    usaled.set(true);
                }
            }
        });
        return allowed.get();
    }

    private static boolean toAllowState(String s) {
        return specialEqual(s, "allow");
    }

    private static boolean specialEqual(String a, String b) {
        if (a == null || b == null) return true;
        else return Objects.equals(a, b);
    }
}
