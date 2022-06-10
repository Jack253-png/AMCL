package com.mcreater.amcl.download;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mcreater.amcl.download.tasks.AbstractTask;
import com.mcreater.amcl.download.tasks.LibDownloadTask;
import com.mcreater.amcl.game.getPath;
import com.mcreater.amcl.model.fabric.*;
import com.mcreater.amcl.util.FasterUrls;
import com.mcreater.amcl.util.FileStringReader;
import com.mcreater.amcl.util.GetPath;
import com.mcreater.amcl.util.LinkPath;
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
import java.util.concurrent.atomic.AtomicInteger;

public class FabricDownload {
    static int chunkSize;
    static Vector<AbstractTask> tasks = new Vector<>();
    static Logger logger = LogManager.getLogger(FabricDownload.class);
    public static void download(boolean faster, String id, String minecraft_dir, String version_name, int chunkSize, String fabric_version) throws IOException, InterruptedException {
        tasks.clear();
        FabricDownload.chunkSize = chunkSize;
        String fabricVersions = FasterUrls.fast("https://meta.fabricmc.net/v2/versions/loader", faster);
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
        String fab = FasterUrls.fast(String.format("https://meta.fabricmc.net/v2/versions/loader/%s/%s", id, ver), faster);
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
                String url = FasterUrls.fast(lib.url + getPath.get(lib.name).replace("\\", "/"), faster);
                String path = LinkPath.link(lib_base, getPath.get(lib.name));
                new File(GetPath.get(path)).mkdirs();
                ao.getJSONArray("libraries").put(g.fromJson(g.toJson(lib), Map.class));
                tasks.add(new LibDownloadTask(url, path, chunkSize));
            }
            for (FabricLibModel lib : model.launcherMeta.libraries.client) {
                if (lib.name.contains("launchwrapper")) {
                    throw new IOException();
                }
                String url = FasterUrls.fast(lib.url + getPath.get(lib.name).replace("\\", "/"), faster);
                String path = LinkPath.link(lib_base, getPath.get(lib.name));
                new File(GetPath.get(path)).mkdirs();
                ao.getJSONArray("libraries").put(g.fromJson(g.toJson(lib), Map.class));
                tasks.add(new LibDownloadTask(url, path, chunkSize));
            }
            String url = FasterUrls.fast("https://maven.fabricmc.net/" + getPath.get(model.intermediary.maven).replace("\\", "/"), faster);
            String path = LinkPath.link(lib_base, getPath.get(model.intermediary.maven));
            new File(GetPath.get(path)).mkdirs();
            FabricLibModel model1 = new FabricLibModel();
            model1.name = model.intermediary.maven;
            model1.url = "https://maven.fabricmc.net/";
            ao.getJSONArray("libraries").put(g.fromJson(g.toJson(model1), Map.class));
            tasks.add(new LibDownloadTask(url, path, chunkSize));

            url = FasterUrls.fast("https://maven.fabricmc.net/" + getPath.get(model.loader.maven).replace("\\", "/"), faster);
            path = LinkPath.link(lib_base, getPath.get(model.loader.maven));
            new File(GetPath.get(path)).mkdirs();
            model1 = new FabricLibModel();
            model1.name = model.loader.maven;
            model1.url = "https://maven.fabricmc.net/";
            ao.getJSONArray("libraries").put(g.fromJson(g.toJson(model1), Map.class));
            tasks.add(new LibDownloadTask(url, path, chunkSize));

            AtomicInteger d = new AtomicInteger();
            for (AbstractTask t : tasks) {
                new Thread(() -> {
                    while (true) {
                        try {
                            t.execute();
                            d.addAndGet(1);
                            break;
                        } catch (IOException ignored) {
                        }
                    }
                }).start();
            }
            do {
                logger.info(String.format("%s / %s", d.get(), tasks.size()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            } while (d.get() != tasks.size());
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
                String url = FasterUrls.fast(Objects.requireNonNullElse(s.url, "https://libraries.minecraft.net/") + getPath.get(s.name).replace("\\", "/"), faster);
                String path = LinkPath.link(lib_base, getPath.get(s.name));
                new File(GetPath.get(path)).mkdirs();
                ao.getJSONArray("libraries").put(g.fromJson(g.toJson(s), Map.class));
                tasks.add(new LibDownloadTask(url, path, chunkSize));
            }
            for (FabricLibModel s : model.launcherMeta.libraries.client){
                String url = FasterUrls.fast(Objects.requireNonNullElse(s.url, "https://libraries.minecraft.net/") + getPath.get(s.name).replace("\\", "/"), faster);
                String path = LinkPath.link(lib_base, getPath.get(s.name));
                new File(GetPath.get(path)).mkdirs();
                ao.getJSONArray("libraries").put(g.fromJson(g.toJson(s), Map.class));
                tasks.add(new LibDownloadTask(url, path, chunkSize));
            }
            String url = FasterUrls.fast("https://maven.fabricmc.net/" + getPath.get(model.loader.maven).replace("\\", "/"), faster);
            String path = LinkPath.link(lib_base, getPath.get(model.loader.maven));
            FabricLibModel model1 = new FabricLibModel();
            model1.name = model.loader.maven;
            ao.getJSONArray("libraries").put(g.fromJson(g.toJson(model1), Map.class));
            tasks.add(new LibDownloadTask(url, path, chunkSize));

            String url1 = FasterUrls.fast("https://maven.fabricmc.net/" + getPath.get(model.intermediary.maven).replace("\\", "/"), faster);
            String path1 = LinkPath.link(lib_base, getPath.get(model.intermediary.maven));
            FabricLibModel model2 = new FabricLibModel();
            model1.name = model.intermediary.maven;
            ao.getJSONArray("libraries").put(g.fromJson(g.toJson(model2), Map.class));
            tasks.add(new LibDownloadTask(url1, path1, chunkSize));

            AtomicInteger d = new AtomicInteger();
            for (AbstractTask t : tasks) {
                new Thread(() -> {
                    while (true){
                        try {
                            t.execute();
                            d.addAndGet(1);
                            break;
                        }
                        catch (Exception ignored){}
                    }
                }).start();
            }
            do {
                logger.info(String.format("%s / %s", d.get(), tasks.size()));
                Thread.sleep(1000);
            } while (d.get() != tasks.size());
            BufferedWriter w = new BufferedWriter(new FileWriter(versionJson));
            w.write(ao.toString());
            w.close();
        }
    }
}
