package com.mcreater.amcl.api.curseApi;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.mcreater.amcl.api.curseApi.mod.CurseModModel;
import com.mcreater.amcl.api.curseApi.modFile.CurseModFileModel;
import com.sun.javafx.logging.PlatformLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class CurseAPI {
    public static Vector<String> versions_list;
    private static final String host = "https://api.curseforge.com";
    private static final String apiKey = "$2a$10$o8pygPrhvKBHuuh5imL2W.LCNFhB15zBYAExXx/TqTx/Zp5px2lxu";
    private static StringBuilder response(String url) throws IOException {
        int tried = 0;
        while (true) {
            try{
                URL u = new URL(host + url);
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setRequestProperty("x-api-key", apiKey);
                conn.setRequestProperty("accept", "application/json");
                conn.setConnectTimeout(10000);
                boolean t = conn.getResponseCode() == 200;
                StringBuilder f = new StringBuilder();
                if (t) {
                    InputStream is = conn.getInputStream();
                    if (null != is) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String temp = null;
                        while (null != (temp = br.readLine())) {
                            f.append(temp);
                        }
                    }
                }
                return f;
            }
            catch (Exception e){
                tried += 1;
                e.printStackTrace();
                if (tried >= 4){
                    throw new IOException();
                }
            }
        }
    }
    public static Vector<CurseModModel> search(String name, CurseResourceType.Types res, CurseSortType.Types sort, int pageSize) throws IOException {
        String url = String.format("/v1/mods/search?gameId=432&searchFilter=%s&classId=%d&sortOrder=%s&pageSize=%d", name, CurseResourceType.get(res), CurseSortType.get(sort), pageSize);
        Gson g = new Gson();
        Map<? , ?> m = g.fromJson(response(url).toString(), Map.class);
        Vector<CurseModModel> result = new Vector<>();
        ArrayList<?> a = (ArrayList<?>) m.get("data");
        for (Object o : a){
            result.add(g.fromJson(g.toJson(o), CurseModModel.class));
        }
        return result;
    }
    public static void main(String[] args) throws IOException {
        System.out.println(getModFiles(search("Create", CurseResourceType.Types.MOD, CurseSortType.Types.DESCENDING, 20).get(0)));
    }
    public static CurseModModel getFromModId(int id) throws IOException {
        String url = String.format("/v1/mods/%d", id);
        Gson g = new Gson();
        String s = response(url).toString();
        Map<? , ?> m = g.fromJson(s, Map.class);
        Vector<CurseModModel> result = new Vector<>();
        LinkedTreeMap<?, ?> a = (LinkedTreeMap<?, ?>) m.get("data");
        return g.fromJson(g.toJson(a), CurseModModel.class);
    }
    public static Vector<CurseModFileModel> getModFiles(CurseModModel mod){
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
                String url = String.format("/v1/mods/%s/files?gameVersion=%s", mod.id, v);
                Map<?, ?> m;
                Gson g = new Gson();
                try {
                    m = g.fromJson(response(url).toString(), Map.class);
                } catch (IOException e) {
                    latch.countDown();
                    throw new RuntimeException(e);
                }
                if (m != null) {
                    ArrayList<?> a = (ArrayList<?>) m.get("data");
                    for (Object o : a) {
                        files.add(g.fromJson(g.toJson(o), CurseModFileModel.class));
                    }
                }
                latch.countDown();
            }).start();
        }
        try {latch.await();}
        catch (InterruptedException ignored){}
        return files;
    }
}
