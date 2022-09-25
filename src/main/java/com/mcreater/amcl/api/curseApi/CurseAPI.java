package com.mcreater.amcl.api.curseApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.mcreater.amcl.api.curseApi.mod.CurseModModel;
import com.mcreater.amcl.api.curseApi.modFile.CurseModFileModel;
import com.mcreater.amcl.api.curseApi.modFile.CurseModRequireModel;
import com.mcreater.amcl.controls.ModFile;
import com.mcreater.amcl.download.GetVersionList;
import com.mcreater.amcl.util.J8Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class CurseAPI {
    public static Vector<String> versions_list;
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
            try{
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
                long startTime = System.currentTimeMillis();
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
    public static CurseModFileModel getFromFileId(int modId, int fileId) throws IOException {
        String url = String.format("/v1/mods/%s/files/%s", modId, fileId);
        String s = response(url);
        Gson g = new Gson();
        Map<?, ?> m = g.fromJson(s, Map.class);
        return g.fromJson(g.toJson(m.get("data")), CurseModFileModel.class);
    }
    public static long getTimeFromString(String time) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.parse(J8Utils.createList(time.split("\\.")).get(0).replace("T", " ")).getTime();
        }
        catch (ParseException e){
            return -1;
        }
    }
    public static Vector<CurseModFileModel> getModFileRequiredMods(CurseModFileModel model, String gameVersion, String firstname) throws IOException{
        logger.info(String.format("Finding %s require mods", model.fileName));
        Vector<CurseModFileModel> requires = new Vector<>();
        Vector<Thread> requestingThreads = new Vector<>();
        AtomicInteger finished = new AtomicInteger();
        for (CurseModRequireModel m : model.dependencies){
            if (m.relationType == 3.0){
                for (Map<String, String> map : getFromModId(m.modId).latestFilesIndexes){
                    if (Objects.equals(map.get("gameVersion"), gameVersion)) {
                        requestingThreads.add(new Thread(() -> {
                            Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionsCaughter());
                            try {
                                CurseModFileModel model1 = getFromFileId(m.modId, (int) Double.parseDouble(map.get("fileId")));
                                if (modLoaderCanRunTogether(model1, model)) {
                                    Vector<CurseModFileModel> removeList = new Vector<>();
                                    for (CurseModFileModel v : requires){
                                        if (v.modId == model1.modId){
                                            if (getTimeFromString(model1.fileDate) >= getTimeFromString(v.fileDate)){
                                                removeList.add(v);
                                            }
                                            else {
                                                return;
                                            }
                                        }
                                    }
                                    requires.removeAll(removeList);
                                    requires.add(model1);
                                    if (model1.dependencies.size() >= 1){
                                        logger.info(String.format("mod %s has more requires, loading...", model1.fileName));
                                        Vector<CurseModFileModel> reqsreq = getModFileRequiredMods(model1, gameVersion, firstname);
                                        requires.addAll(reqsreq);
                                    }
                                }
                            } catch (IOException e) {
                                errored = true;
                            } finally{
                                finished.addAndGet(1);
                            }
                        }));
                    }
                }
            }
        }
        for (Thread t : requestingThreads){
            t.run();
        }
        while (true){
            if (finished.get() == requestingThreads.size()){
                break;
            }
        }
        logger.info(String.format("Mod %s loaded success, find %d required mods", model.fileName, requires.size()));
        if (Objects.equals(model.fileName, firstname)){
            logger.info("require mod loaded success");
        }
        Vector<CurseModFileModel> fabList = new Vector<>();
        requires.forEach(e -> {
            if (e.modId == 306612 || e.modId == 322385){
                fabList.add(e);
            }
        });
        requires.removeAll(fabList);
        for (CurseModFileModel file : requires){
            System.out.println(file.fileName);
        }
        return requires;
    }
    private static boolean modLoaderCanRunTogether(CurseModFileModel m1, CurseModFileModel m2){
        Vector<String> temp1 = ModFile.getModLoaders(m1.gameVersions, true);
        Vector<String> temp2 = ModFile.getModLoaders(m2.gameVersions, true);
        if (temp1.size() == 0 || temp2.size() == 0){
            return true;
        }
        else{
            return ListContainsAnother(temp1, temp2);
        }
    }
    private static boolean ListContainsAnother(Collection<String> c1, Collection<String> c2){
        int size = c1.size();
        for (String s : c2){
            if (c1.contains(s)){
                size -= 1;
            }
        }
        return size != c1.size();
    }
    public static CurseModModel getFromModId(int id) throws IOException {
        String url = String.format("/v1/mods/%d", id);
        Gson g = new Gson();
        String s = response(url);
        Map<? , ?> m = g.fromJson(s, Map.class);
        LinkedTreeMap<?, ?> a = (LinkedTreeMap<?, ?>) m.get("data");
        return g.fromJson(g.toJson(a), CurseModModel.class);
    }
    public static Vector<CurseModFileModel> getModFiles(CurseModModel mod, String version){
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
                    if (!e.gameVersions.contains(GetVersionList.getSnapShotName(version))) {
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
    public static Vector<CurseModFileModel> getModFiles(CurseModModel mod) throws IOException{
        Vector<String> versions_list = new Vector<>();
        for (Map<String, String> m : mod.latestFilesIndexes){
            if (!versions_list.contains(m.get("gameVersion"))){
                versions_list.add(m.get("gameVersion"));
            }
        }
        CurseAPI.versions_list = versions_list;
        CountDownLatch latch = new CountDownLatch(versions_list.size());
        Vector<CurseModFileModel> files = new Vector<>();
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
                        boolean contained = false;
                        while (true) {
                            try {
                                for (CurseModFileModel m1 : files) {
                                    if (m1.id == model.id) {
                                        contained = true;
                                        break;
                                    }
                                }
                                break;
                            }
                            catch (ConcurrentModificationException ignored){}
                        }
                        if (!contained) files.add(model);
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
        return files;
    }
    public static class UncaughtExceptionsCaughter implements Thread.UncaughtExceptionHandler {
        public void uncaughtException(Thread thread, Throwable throwable) {
            logger.info(String.format("%s throws an exception %s", thread, throwable));
            CurseAPI.errored = true;
            throwable.printStackTrace();
        }
    }
}
