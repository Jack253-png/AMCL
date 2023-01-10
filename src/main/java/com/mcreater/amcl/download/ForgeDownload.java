package com.mcreater.amcl.download;

import com.google.gson.internal.LinkedTreeMap;
import com.mcreater.amcl.download.model.NewForgeItemModel;
import com.mcreater.amcl.game.MavenPathConverter;
import com.mcreater.amcl.model.LibModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.model.forge.ForgeInjectModel;
import com.mcreater.amcl.model.forge.ForgeProcessorModel;
import com.mcreater.amcl.model.forge.ForgeVersionModel;
import com.mcreater.amcl.model.forge.OldForgeLibModel;
import com.mcreater.amcl.model.forge.OldForgeVersionModel;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.tasks.ForgeExtractTask;
import com.mcreater.amcl.tasks.ForgeInstallerDownloadTask;
import com.mcreater.amcl.tasks.ForgePatchTask;
import com.mcreater.amcl.tasks.LibDownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.tasks.manager.TaskManager;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.StringUtils;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.net.GetFileExists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import static com.mcreater.amcl.util.FileUtils.OperateUtil.createDirectory;
import static com.mcreater.amcl.util.FileUtils.OperateUtil.createDirectoryDirect;
import static com.mcreater.amcl.util.FileUtils.OperateUtil.deleteDirectory;
import static com.mcreater.amcl.util.FileUtils.OperateUtil.deleteFile;
import static com.mcreater.amcl.util.FileUtils.PathUtil.buildPath;
import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;
import static com.mcreater.amcl.util.StringUtils.ForgeMapplings.checkIsForgePath;
import static com.mcreater.amcl.util.StringUtils.ForgeMapplings.checkIsMapKey;
import static com.mcreater.amcl.util.StringUtils.ForgeMapplings.get;
import static com.mcreater.amcl.util.StringUtils.ForgeMapplings.getLong;

public class ForgeDownload {
    static int chunkSize;
    static Vector<Task> tasks = new Vector<>();
    static Logger logger = LogManager.getLogger(ForgeDownload.class);
    static String versiondir;
    static String u = "";

    public static void download(String id, String minecraft_dir, String version_name, int chunkSize, NewForgeItemModel forge_version, Runnable r3, Runnable r, Runnable r2, FasterUrls.Servers server) throws Exception {
        if (FileUtils.getJavaExecutable() == null) throw new IOException("Java executable not found.");
        tasks.clear();
        ForgeDownload.chunkSize = chunkSize;
        String temp_path = "forgeTemp";
        OriginalDownload.download(id, minecraft_dir, version_name, chunkSize, server);
        r.run();
        String versionPath = FileUtils.LinkPath.link(temp_path, "version.json");
        versiondir = String.format(buildPath("%s", "versions", "%s", "%s.json"), minecraft_dir, version_name, version_name);
        VersionJsonModel model225 = GSON_PARSER.fromJson(FileUtils.FileStringReader.read(versiondir), VersionJsonModel.class);
        try {
            u = FasterUrls.fast(model225.downloads.get("client_mappings").url, server);
        }
        catch (NullPointerException ignored){}
        String name = "installer.jar";
        String installer_path = FileUtils.LinkPath.link(temp_path, name);
        String installer_url = GetVersionList.getForgeInstallerDownloadURL(forge_version, id, server);

        logger.info(String.format("finded forge installer url : %s", installer_url));
        deleteDirectory(new File(temp_path), temp_path);
        createDirectoryDirect(temp_path);
        if (!GetFileExists.get(installer_url)){
            throw new IOException("this version of forge cannot be automated");
        }
        r3.run();

        new ForgeInstallerDownloadTask(installer_url, installer_path, chunkSize).execute();
        if (new File(temp_path).exists()) {
            FileUtils.ZipUtil.unzipAll(installer_path, temp_path);
        }
        else{
            throw new IOException();
        }
        String lib_base = FileUtils.LinkPath.link(minecraft_dir, "libraries");

        if (new File(versionPath).exists()){
            String t = FileUtils.FileStringReader.read(versionPath);
            ForgeVersionModel model = GSON_PARSER.fromJson(t, ForgeVersionModel.class);
            JSONObject ao = new JSONObject(FileUtils.FileStringReader.read(String.format(buildPath("%s", "versions", "%s", "%s.json"), minecraft_dir, version_name, version_name)));
            ao = ao.put("mainClass", model.mainClass);
            for (LibModel m : model.libraries) {
                ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(m, LibModel.class), Map.class));
                if (Objects.equals(m.downloads.artifact.get("url"), "")){
                    String extract = StringUtils.GetFileBaseDir.get(FileUtils.LinkPath.link(lib_base, m.downloads.artifact.get("path")));
                    int returnCode = new ForgeExtractTask(extract, installer_path, new String[]{"--extract", extract}).execute();
                    if (returnCode != 0){
                        throw new IOException("Install Failed");
                    }
                }
                else {
                    createDirectory(FileUtils.LinkPath.link(lib_base, m.downloads.artifact.get("path")));
                    tasks.add(new LibDownloadTask(FasterUrls.fast(m.downloads.artifact.get("url"), server), FileUtils.LinkPath.link(lib_base, m.downloads.artifact.get("path")), chunkSize).setHash(m.downloads.artifact.get("sha1")));
                }
            }
            if (model.minecraftArguments != null){
                ao.put("minecraftArguments", model.minecraftArguments);
            }
            else {
                for (Object o : model.arguments.game){
                    ao.getJSONObject("arguments").getJSONArray("game").put(o.toString());
                }
                if (model.arguments.jvm != null) {
                    for (Object o1 : model.arguments.jvm) {
                        ao.getJSONObject("arguments").getJSONArray("jvm").put(o1.toString());
                    }
                }
            }
            BufferedWriter w = new BufferedWriter(new FileWriter(String.format(buildPath("%s", "versions", "%s", "%s.json"), minecraft_dir, version_name, version_name)));
            w.write(ao.toString());
            w.close();

            ForgeInjectModel model1 = GSON_PARSER.fromJson(FileUtils.FileStringReader.read(FileUtils.LinkPath.link(temp_path, "install_profile.json")), ForgeInjectModel.class);
            for (LibModel m1 : model1.libraries){
                createDirectory(FileUtils.LinkPath.link(lib_base, m1.downloads.artifact.get("path")));
                if (!Objects.equals(m1.downloads.artifact.get("url"), "")) {
                    tasks.add(new LibDownloadTask(FasterUrls.fast(m1.downloads.artifact.get("url"), server), FileUtils.LinkPath.link(lib_base, m1.downloads.artifact.get("path")), chunkSize).setHash(m1.downloads.artifact.get("sha1")));
                }
            }
            TaskManager.addTasks(tasks);
            TaskManager.execute("<forge>");
            Map<String, String> mapplings = new LinkedTreeMap<>();
            if (model1.data != null){
                for (String key : model1.data.keySet()){
                    if (checkIsForgePath(model1.data.get(key).get("client"))){
                        try {
                            mapplings.put(String.format("{%s}", key), FileUtils.LinkPath.link(lib_base, getLong(model1.data.get(key).get("client"))));
                        }
                        catch (Exception ignored){}
                    }
                }
            }
            mapplings.put("{SIDE}", "client");
            mapplings.put("{MINECRAFT_JAR}", String.format(buildPath("%s", "versions", "%s", "%s.jar"), minecraft_dir, version_name, version_name));
            mapplings.put("{BINPATCH}", buildPath("forgeTemp", "data", "client.lzma"));
            r2.run();
            Vector<Task> tasks2 = new Vector<>();
            for (ForgeProcessorModel model2 : model1.processors){
                if (model2.sides == null || model2.sides.contains("client")) {
                    Vector<String> args = new Vector<>();
                    for (String a : model2.args) {
                        if (checkIsForgePath(a)){
                            args.add(FileUtils.LinkPath.link(lib_base, get(a)));
                        }
                        else if (checkIsMapKey(a)){
                            args.add(mapplings.get(a));
                        }
                        else{
                            args.add(a);
                        }
                    }
                    tasks2.add(new ForgePatchTask(lib_base, model2.jar, model2.classpath, args.toArray(new String[0]), server));
                }
            }
            TaskManager.addTasks(tasks2);
            TaskManager.executeForge("<forge build>");
        }
        else {
            String rr = FileUtils.LinkPath.link(temp_path, "install_profile.json");
            String rw = FileUtils.FileStringReader.read(rr);
            OldForgeVersionModel model = GSON_PARSER.fromJson(rw, OldForgeVersionModel.class);
            JSONObject ao = new JSONObject(FileUtils.FileStringReader.read(String.format(buildPath("%s", "versions", "%s", "%s.json"), minecraft_dir, version_name, version_name)));
            ao = ao.put("mainClass", model.versionInfo.mainClass);
            ao = ao.put("minecraftArguments", model.versionInfo.minecraftArguments);

            for (OldForgeLibModel model1 : model.versionInfo.libraries){
                String p = StringUtils.GetFileBaseDir.get(FileUtils.LinkPath.link(lib_base, MavenPathConverter.get(model1.name)));
                createDirectory(p);
                if (model1.clientreq == null && model1.serverreq == null){
                    if (model1.name.contains("net.minecraftforge:minecraftforge") || model1.name.contains("net.minecraftforge:forge")) {
                        FileUtils.ChangeDir.saveNowDir();
                        String i = FileUtils.LinkPath.link(FileUtils.ChangeDir.dirs, installer_path);
                        FileUtils.ChangeDir.changeTo(p);
                        if (new ForgeExtractTask(p, i, new String[]{"--extract"}).execute() != 0) {
                            throw new IOException("Forge Extract Failed");
                        }
                        FileUtils.ChangeDir.changeToDefault();
                        ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(model1), Map.class));
                        for (File f : new File(p).listFiles()) {
                            if (f.isFile()) {
                                if (f.getPath().endsWith(".jar")) {
                                    String excepted = FileUtils.LinkPath.link(lib_base, MavenPathConverter.get(model1.name))
                                            .replace(".jar", "");
                                    String orig = f.getPath()
                                            .replace(".jar", "");

                                    if (orig.contains(excepted)) {
                                        try {
                                            f.renameTo(new File(FileUtils.LinkPath.link(lib_base, MavenPathConverter.get(model1.name))));
                                        }
                                        catch (Exception ignored) {

                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        String path = FileUtils.LinkPath.link(lib_base, MavenPathConverter.get(model1.name));
                        String url;
                        url = "https://maven.minecraftforge.net/" + MavenPathConverter.get(model1.name).replace("\\", "/");
                        url = FasterUrls.fast(url, server);
                        if (!GetFileExists.get(url)){
                            url = "https://libraries.minecraft.net/" + MavenPathConverter.get(model1.name).replace("\\", "/");
                            url = FasterUrls.fast(url, server);
                        }
                        if (model1.name.contains("guava")){
                            Iterator<Object> t = ao.getJSONArray("libraries").iterator();
                            while (t.hasNext()){
                                JSONObject jo = (JSONObject) t.next();
                                if (jo.getString("name").contains("guava")){
                                    t.remove();
                                }
                            }
                        }
                        ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(model1), Map.class));
                        LibDownloadTask te = new LibDownloadTask(FasterUrls.fast(url, server), path, chunkSize);
                        if (model1.checksums != null){
                            te.setHash(model1.checksums.get(0));
                        }
                        tasks.add(te);
                    }
                }
                else{
                    String path = FileUtils.LinkPath.link(lib_base, MavenPathConverter.get(model1.name));
                    String url;
                    url = "https://maven.minecraftforge.net/" + MavenPathConverter.get(model1.name).replace("\\", "/");
                    url = FasterUrls.fast(url, server);
                    if (!GetFileExists.get(url)){
                        url = "https://libraries.minecraft.net/" + MavenPathConverter.get(model1.name).replace("\\", "/");
                        url = FasterUrls.fast(url, server);
                    }
                    url = FasterUrls.fast(url, server);
                    if (model1.name.contains("guava")){
                        Iterator<Object> t = ao.getJSONArray("libraries").iterator();
                        while (t.hasNext()){
                            JSONObject jo = (JSONObject) t.next();
                            if (jo.getString("name").contains("guava")){
                                t.remove();
                            }
                        }
                    }
                    ao.getJSONArray("libraries").put(GSON_PARSER.fromJson(GSON_PARSER.toJson(model1), Map.class));
                    LibDownloadTask te = new LibDownloadTask(FasterUrls.fast(url, server), path, chunkSize);
                    if (model1.checksums != null){
                        te.setHash(model1.checksums.get(0));
                    }
                    tasks.add(te);
                }
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(String.format(buildPath("%s", "versions", "%s", "%s.json"), minecraft_dir, version_name, version_name)));
            bufferedWriter.write(ao.toString());
            bufferedWriter.close();
            TaskManager.addTasks(tasks);
            TaskManager.execute("<old forge>");
        }
        FileUtils.del(temp_path);
        deleteFile(String.format("%s.log", name));
    }

    public static void download_mojmaps(String local, FasterUrls.Servers server) throws IOException {
        createDirectory(local);
        new DownloadTask(FasterUrls.fast(u, server), local, 1024).execute();
    }
}
