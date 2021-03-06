package com.mcreater.amcl.download;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.mcreater.amcl.game.getPath;
import com.mcreater.amcl.model.LibModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.model.forge.*;
import com.mcreater.amcl.tasks.taskmanager.TaskManager;
import com.mcreater.amcl.tasks.*;
import com.mcreater.amcl.util.*;
import com.mcreater.amcl.util.net.HttpConnectionUtil;
import com.mcreater.amcl.util.xml.ForgeVersionXMLHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static com.mcreater.amcl.util.ForgeMapplingsPathGetter.*;

public class ForgeDownload {
    static int chunkSize;
    static Vector<Task> tasks = new Vector<>();
    static Logger logger = LogManager.getLogger(ForgeDownload.class);
    static String versiondir;
    static String u;
    private static int getCont(String raw){
        int count = 0;
        int index = 0;
        while ((index = raw.indexOf("-", index)) != -1) {
            index = index + 1;
            count++;
        }
        return count;
    }
    public static void download(boolean faster, String id, String minecraft_dir, String version_name, int chunkSize, String forge_version) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        tasks.clear();
        ForgeDownload.chunkSize = chunkSize;
        Map<String, Vector<String>> vectorMap = ForgeVersionXMLHandler.load(HttpConnectionUtil.doGet(FasterUrls.fast("https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml", faster)));
        String c = null;
        if (vectorMap.get(id) != null){
            for (String version : vectorMap.get(id)){
                System.out.println(version);
                if (Objects.equals(version, forge_version)){
                    c = version;
                    break;
                }
            }
            if (c == null){
                throw new IOException();
            }
        }
        else{
            throw new IOException();
        }
        String temp_path = "forgeTemp";
        OriginalDownload.download(faster, id, minecraft_dir, version_name, chunkSize);
        String versionPath = LinkPath.link(temp_path, "version.json");
        versiondir = String.format("%s\\versions\\%s\\%s.json", minecraft_dir, version_name, version_name);
        VersionJsonModel model225 = new Gson().fromJson(FileStringReader.read(versiondir), VersionJsonModel.class);
        try {
            u = FasterUrls.fast(model225.downloads.get("client_mappings").url, faster);
        }
        catch (NullPointerException ignored){}
        String installer_path = LinkPath.link(temp_path, "installer.jar");
        String final_version = String.format("%s-%s", id, c);
        String installer_url = String.format("https://files.minecraftforge.net/maven/net/minecraftforge/forge/%s/forge-%s-installer.jar", final_version, final_version);
        installer_url = FasterUrls.fast(installer_url, faster);
        logger.info(String.format("finded forge installer url : %s", installer_url));
        deleteDirectory(new File(temp_path), temp_path);
//        int i = 0;
        new File(temp_path).mkdirs();
        if (!GetFileExists.get(installer_url)){
            throw new IOException("this version of forge cannot be automated");
        }
        new ForgeInstallerDownloadTask(installer_url, installer_path, chunkSize).execute();
//      int i = 0;
        if (new File(temp_path).exists()) {
            ZipUtil.unzipAll(installer_path, temp_path);
        }
        else{
            throw new IOException();
        }
        String lib_base = LinkPath.link(minecraft_dir, "libraries");
        if (new File(versionPath).exists()){
            String t = FileStringReader.read(versionPath);
            Gson g = new Gson();
            ForgeVersionModel model = g.fromJson(t, ForgeVersionModel.class);
            JSONObject ao = new JSONObject(FileStringReader.read(String.format("%s\\versions\\%s\\%s.json", minecraft_dir, version_name, version_name)));
            ao = ao.put("mainClass", model.mainClass);
            for (LibModel m : model.libraries) {
                ao.getJSONArray("libraries").put(g.fromJson(g.toJson(m, LibModel.class), Map.class));
                if (Objects.equals(m.downloads.artifact.get("url"), "")){
                    String extract = GetPath.get(LinkPath.link(lib_base, m.downloads.artifact.get("path")));
                    String com = String.format("\"%s\" -jar %s --extract %s",LinkPath.link(System.getProperty("java.home"), "bin\\java.exe").replace("\\", "/"), installer_path.replace("\\", "/"), extract);
                    int returnCode = new ForgeExtractTask(com, extract, installer_path.replace("\\", "/"), new String[]{"--extract", extract}).execute();
                    if (returnCode != 0){
                        throw new IOException("Install Failed");
                    }
                }
                else {
                    new File(LinkPath.link(lib_base, m.downloads.artifact.get("path").replace("/", "\\"))).mkdirs();
                    tasks.add(new LibDownloadTask(FasterUrls.fast(m.downloads.artifact.get("url"), faster), LinkPath.link(lib_base, m.downloads.artifact.get("path").replace("/", "\\")), chunkSize).setHash(m.downloads.artifact.get("sha1")));
                }
            }
            if (model.minecraftArguments != null){
                ao.put("minecraftArguments", model.minecraftArguments);
            }
            else{
                for (Object o : model.arguments.game){
                    ao.getJSONObject("arguments").getJSONArray("game").put(o.toString());
                }
                if (model.arguments.jvm != null) {
                    for (Object o1 : model.arguments.jvm) {
                        ao.getJSONObject("arguments").getJSONArray("jvm").put(o1.toString());
                    }
                }
            }
            BufferedWriter w = new BufferedWriter(new FileWriter(String.format("%s\\versions\\%s\\%s.json", minecraft_dir, version_name, version_name)));
            w.write(ao.toString());
            w.close();

            ForgeInjectModel model1 = g.fromJson(FileStringReader.read(LinkPath.link(temp_path, "install_profile.json")), ForgeInjectModel.class);
            for (LibModel m1 : model1.libraries){
                new File(GetPath.get(LinkPath.link(lib_base, m1.downloads.artifact.get("path").replace("/", "\\")))).mkdirs();
                tasks.add(new LibDownloadTask(FasterUrls.fast(m1.downloads.artifact.get("url"), faster), LinkPath.link(lib_base, m1.downloads.artifact.get("path").replace("/", "\\")), chunkSize).setHash(m1.downloads.artifact.get("sha1")));
            }
            TaskManager.addTasks(tasks);
            TaskManager.execute("<forge>");
            Map<String, String> mapplings = new LinkedTreeMap<>();
            if (model1.data != null){
                for (String key : model1.data.keySet()){
                    if (checkIsForgePath(model1.data.get(key).get("client"))){
                        try {
                            mapplings.put(String.format("{%s}", key), LinkPath.link(lib_base, getLong(model1.data.get(key).get("client"))).replace("\\", "/"));
                        }
                        catch (Exception ignored){}
                    }
                }
            }
            mapplings.put("{SIDE}", "client");
            mapplings.put("{MINECRAFT_JAR}", String.format("%s/versions/%s/%s.jar", minecraft_dir.replace("\\", "/"), version_name, version_name));
            mapplings.put("{BINPATCH}", "forgeTemp/data/client.lzma");
            for (ForgeProcessorModel model2 : model1.processors){
                if (model2.sides == null || model2.sides.contains("client")) {
                    StringBuilder argstr = new StringBuilder();
                    Vector<String> args = new Vector<>();
                    for (String a : model2.args) {
                        if (checkIsForgePath(a)){
                            argstr.append(" ").append(LinkPath.link(lib_base, get(a)).replace("\\", "/"));
                            args.add(LinkPath.link(lib_base, get(a)).replace("\\", "/"));
                        }
                        else if (checkIsMapKey(a)){
                            argstr.append(" ").append(mapplings.get(a));
                            args.add(mapplings.get(a));
                        }
                        else{
                            argstr.append(" ").append(a);
                            args.add(a);
                        }
                    }
                    new ForgePatchTask(lib_base, model2.jar, model2.classpath, argstr.toString(), args.toArray(new String[0])).execute();
                }
            }
        }
        else{
            String rr = LinkPath.link(temp_path, "install_profile.json");
            String rw = FileStringReader.read(rr);
            Gson g = new Gson();
            OldForgeVersionModel model = g.fromJson(rw, OldForgeVersionModel.class);
            JSONObject ao = new JSONObject(FileStringReader.read(String.format("%s\\versions\\%s\\%s.json", minecraft_dir, version_name, version_name)));
            ao = ao.put("mainClass", model.versionInfo.mainClass);
            ao = ao.put("minecraftArguments", model.versionInfo.minecraftArguments);
            Vector<Object> rem = new Vector<>();

            Gson g1 = new Gson();
            for (OldForgeLibModel model1 : model.versionInfo.libraries){
                String p = GetPath.get(LinkPath.link(lib_base, getPath.get(model1.name)));
                new File(p).mkdirs();
                if (model1.clientreq == null && model1.serverreq == null){
                    if (model1.name.contains("net.minecraftforge:minecraftforge")) {
                        ChangeDir.saveNowDir();
                        String i = LinkPath.link(ChangeDir.dirs, installer_path).replace("\\", "/");
                        ChangeDir.changeTo(p);
                        String com = String.format("\"%s\" -jar %s --extract", LinkPath.link(System.getProperty("java.home"), "bin\\java.exe").replace("\\", "/"), i);
                        System.out.println(new Gson().toJson(model1));
                        if (new ForgeExtractTask(com, p, i, new String[]{"--extract"}).execute() != 0) {
                            throw new IOException("Install Failed");
                        }
                        ChangeDir.changeToDefault();
                        ao.getJSONArray("libraries").put(g1.fromJson(g1.toJson(model1), Map.class));
                        for (File f : new File(p).listFiles()) {
                            if (f.isFile()) {
                                if (f.getPath().endsWith(".jar")) {
                                    if (f.getPath().replace("/", "\\").replace(".jar", "").contains(LinkPath.link(lib_base, getPath.get(model1.name)).replace(".jar", ""))) {
                                        f.renameTo(new File(LinkPath.link(lib_base, getPath.get(model1.name))));
                                    }
                                }
                            }
                        }
                    }
                    else{
                        String path = LinkPath.link(lib_base, getPath.get(model1.name));
                        String url;
                        url = "https://maven.minecraftforge.net/" + getPath.get(model1.name).replace("\\", "/");
                        url = FasterUrls.fast(url, faster);
                        if (!GetFileExists.get(url)){
                            url = "https://libraries.minecraft.net/" + getPath.get(model1.name).replace("\\", "/");
                            url = FasterUrls.fast(url, faster);
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
                        ao.getJSONArray("libraries").put(g1.fromJson(g1.toJson(model1), Map.class));
                        LibDownloadTask te = new LibDownloadTask(FasterUrls.fast(url, faster), path, chunkSize);
                        if (model1.checksums != null){
                            te.setHash(model1.checksums.get(0));
                        }
                        tasks.add(te);
                    }
                }
                else{
                    String path = LinkPath.link(lib_base, getPath.get(model1.name));
                    String url;
                    url = "https://maven.minecraftforge.net/" + getPath.get(model1.name).replace("\\", "/");
                    url = FasterUrls.fast(url, faster);
                    if (!GetFileExists.get(url)){
                        url = "https://libraries.minecraft.net/" + getPath.get(model1.name).replace("\\", "/");
                        url = FasterUrls.fast(url, faster);
                    }
                    url = FasterUrls.fast(url, faster);
                    if (model1.name.contains("guava")){
                        Iterator<Object> t = ao.getJSONArray("libraries").iterator();
                        while (t.hasNext()){
                            JSONObject jo = (JSONObject) t.next();
                            if (jo.getString("name").contains("guava")){
                                t.remove();
                            }
                        }
                    }
                    ao.getJSONArray("libraries").put(g1.fromJson(g1.toJson(model1), Map.class));
                    LibDownloadTask te = new LibDownloadTask(FasterUrls.fast(url, faster), path, chunkSize);
                    if (model1.checksums != null){
                        te.setHash(model1.checksums.get(0));
                    }
                    tasks.add(te);
                }
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(String.format("%s\\versions\\%s\\%s.json", minecraft_dir, version_name, version_name)));
            bufferedWriter.write(ao.toString());
            bufferedWriter.close();
            TaskManager.addTasks(tasks);
            TaskManager.execute("<old forge>");
        }
        FileUtils.del(temp_path);
    }
    public static boolean deleteDirectory(File f, String orgin){
        if (!f.exists()){
            return false;
        }
        if (f.isFile()){
            return f.delete();
        }
        else{
            for (File f1 : f.listFiles()){
                deleteDirectory(f1, orgin);
            }
        }
        if (!f.getPath().equals(orgin)) {
            return f.delete();
        }
        else{
            return true;
        }
    }
    public static void download_mojmaps(String local) throws IOException {
        logger.info(String.format("download mojmaps : %s", FasterUrls.fast(u, true)));
        new File(GetPath.get(local)).mkdirs();
        new DownloadTask(FasterUrls.fast(u, true), local, 1024).execute();
    }
}
