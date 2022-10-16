package com.mcreater.amcl.api.modApi.curseforge;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.mcreater.amcl.api.modApi.curseforge.mod.CurseModModel;
import com.mcreater.amcl.api.modApi.curseforge.modFile.CurseModFileModel;
import com.mcreater.amcl.api.modApi.curseforge.modFile.CurseModRequireModel;
import com.mcreater.amcl.controls.ModFile;
import com.mcreater.amcl.controls.ServerMod;
import com.mcreater.amcl.download.GetVersionList;
import com.mcreater.amcl.tasks.LambdaTask;
import com.mcreater.amcl.tasks.taskmanager.TaskManager;
import com.mcreater.amcl.util.net.FasterUrls;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public final class CurseAPI {
    private static boolean errored = false;
    private static final String host = "https://api.curseforge.com";
    private static final String apiKey = "$2a$10$o8pygPrhvKBHuuh5imL2W.LCNFhB15zBYAExXx/TqTx/Zp5px2lxu";
    private static final Logger logger = LogManager.getLogger(CurseAPI.class);
    static {
        logger.info("loading curseforge apiKey");
        logger.info(String.format("load success,ApiKey = %s", apiKey));
    }
    private static String response(String url) throws IOException {
        int tried = 0;
        while (true) {
            try {
                long startTime = System.currentTimeMillis();
                URL u = new URL(host + url);
                logger.info(String.format("requesting curseApi with url %s", u));
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setRequestProperty("x-api-key", apiKey);
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(10000);
                int respc = conn.getResponseCode();
                StringBuilder f = new StringBuilder();
                long endTime = System.currentTimeMillis();
                logger.info(String.format("Url %s returned code %d in %d ms", u, respc, endTime - startTime));
                if (respc <= 399) {
                    InputStream is = conn.getInputStream();
                    if (null != is) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String temp;
                        while (null != (temp = br.readLine())) {
                            f.append(temp);
                        }
                    }
                    else{
                        throw new SocketTimeoutException();
                    }
                    is.close();
                    return f.toString();
                }
                else{
                    throw new SocketTimeoutException();
                }
            }
            catch (SocketTimeoutException e){
                tried += 1;
                logger.error(String.format("failed to request target, tried num %d", tried), e);
                if (tried >= 4){
                    logger.error("tried num out!");
                    throw new IOException();
                }
            }
        }
    }
    public static Vector<CurseModModel> search(String name, CurseResourceType.Types res, CurseSortType.Types sort, int pageSize) throws IOException {
        String url = String.format("/v1/mods/search?gameId=432&searchFilter=%s&classId=%d&sortOrder=%s&pageSize=%d", name, CurseResourceType.get(res), CurseSortType.get(sort), pageSize);
        Gson g = new Gson();
        Map<? , ?> m = g.fromJson(response(url), Map.class);
        Vector<CurseModModel> result = new Vector<>();
        ArrayList<?> a = (ArrayList<?>) m.get("data");
        for (Object o : a){
            result.add(g.fromJson(g.toJson(o), CurseModModel.class));
        }
        return result;
    }

    public static Vector<CurseModModel> getModFileRequiredMods(CurseModFileModel model) throws Exception {
        Vector<CurseModModel> result = new Vector<>();
        TaskManager.setUpdater((integer, s) -> {});
        AtomicReference<Exception> exception = new AtomicReference<>();
        for (CurseModRequireModel m : model.dependencies) {
            if (m.relationType == 3.0) {
                TaskManager.addTasks(new LambdaTask(() -> {
                    try {
                        result.add(getFromModId(m.modId));
                    } catch (IOException e) {
                        exception.set(e);
                    }
                }));
            }
        }
        try {
            TaskManager.execute("<mod relation>");
        }
        catch (Exception e){
            exception.set(e);
        }
        if (exception.get() != null) throw exception.get();
        return result;
    }

    public static CurseModModel getFromModId(int id) throws IOException {
        String url = String.format("/v1/mods/%d", id);
        Gson g = new Gson();
        String s = response(url);
        Map<? , ?> m = g.fromJson(s, Map.class);
        LinkedTreeMap<?, ?> a = (LinkedTreeMap<?, ?>) m.get("data");
        return g.fromJson(g.toJson(a), CurseModModel.class);
    }
    public static Vector<CurseModFileModel> getModFiles(CurseModModel mod, String version, FasterUrls.Servers server){
        Vector<CurseModFileModel> files = new Vector<>();
        boolean cd = false;
        for (Map<String, String> m : mod.latestFilesIndexes){
            if (Objects.equals(m.get("gameVersion"), version)){
                cd = true;
                break;
            }
        }
        if (cd){
            String url = String.format("/v1/mods/%s/files?gameVersion=%s", mod.id, version);
            Map<?, ?> m;
            Gson g = new Gson();
            try {
                m = g.fromJson(response(url), Map.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (m != null) {
                ArrayList<?> a = (ArrayList<?>) m.get("data");
                for (Object o : a) {
                    CurseModFileModel model = g.fromJson(g.toJson(o), CurseModFileModel.class);
                    files.add(model);
                }
            }
            Vector<CurseModFileModel> forRemoval = new Vector<>();
            files.forEach(e -> {
                try {
                    if (!e.gameVersions.contains(GetVersionList.getSnapShotName(version, server))) {
                        forRemoval.add(e);
                    }
                }
                catch (Exception ignored){

                }
            });
            files.removeAll(forRemoval);
            return files;
        }
        else {
            return new Vector<>();
        }
    }
    public static Map<String, Vector<CurseModFileModel>> getModFiles(CurseModModel mod) throws IOException{
        Vector<String> versions_list = new Vector<>();
        for (Map<String, String> m : mod.latestFilesIndexes){
            if (!versions_list.contains(m.get("gameVersion"))){
                versions_list.add(m.get("gameVersion"));
            }
        }
        CountDownLatch latch = new CountDownLatch(versions_list.size());
        Vector<CurseModFileModel> re = new Vector<>();

        for (String v : versions_list) {
            new Thread(() -> {
                Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionsCaughter());
                String url = String.format("/v1/mods/%s/files?gameVersion=%s", mod.id, v);
                Map<?, ?> m;
                Gson g = new Gson();
                try {
                    m = g.fromJson(response(url), Map.class);
                } catch (IOException e) {
                    latch.countDown();
                    throw new RuntimeException(e);
                }
                if (m != null) {
                    ArrayList<?> a = (ArrayList<?>) m.get("data");
                    for (Object o : a) {
                        CurseModFileModel model = g.fromJson(g.toJson(o), CurseModFileModel.class);
                        if (model.downloadUrl == null){
                            model.downloadUrl = String.format("https://edge.forgecdn.net/files/%d/%d/%s", model.id / 1000, model.id % 1000, model.fileName);
                        }
                        re.add(model);
                    }
                }
                latch.countDown();
            }).start();
        }
        try {latch.await();}
        catch (InterruptedException ignored){}
        if (errored){
            errored = false;
            throw new IOException();
        }
        Map<String, Vector<CurseModFileModel>> result = new HashMap<>();
        for (CurseModFileModel model : re) {
            for (String ver : ModFile.getModLoaders(model.gameVersions, false)) {
                if (result.get(ver) == null) result.put(ver, new Vector<>());
                if (result.get(ver) != null) result.get(ver).add(model);
            }
        }
        return result;
    }
    public static class UncaughtExceptionsCaughter implements Thread.UncaughtExceptionHandler {
        public void uncaughtException(Thread thread, Throwable throwable) {
            logger.info(String.format("%s throws an exception %s", thread, throwable));
            CurseAPI.errored = true;
            throwable.printStackTrace();
        }
    }
}
