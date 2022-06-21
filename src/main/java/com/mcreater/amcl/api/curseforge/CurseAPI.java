package com.mcreater.amcl.api.curseforge;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.mcreater.amcl.api.curseforge.mod.CurseModModel;
import com.mcreater.amcl.api.curseforge.modFile.CurseModFileModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class CurseAPI {
    private static final String host = "https://api.curseforge.com";
    private static final String apiKey = "$2a$10$o8pygPrhvKBHuuh5imL2W.LCNFhB15zBYAExXx/TqTx/Zp5px2lxu";
    private static StringBuilder response(String url) throws IOException {
        URL u = new URL(host+url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setRequestProperty("x-api-key", apiKey);
        conn.setRequestProperty("accept", "application/json");
        conn.setConnectTimeout(5000);
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
    public static Vector<CurseModModel> search(String name, CurseResourceType.Types res, CurseSortType.Types sort, int pageSize) throws IOException {
        String url = String.format("/v1/mods/search?gameId=432&searchFilter=%s&classId=%d&sortOrder=%s&pageSize=%d", name, CurseResourceType.get(res), CurseSortType.get(sort), pageSize);
        Map<? , ?> m = new LinkedTreeMap<>();
        Gson g = new Gson();
        m = g.fromJson(response(url).toString(), Map.class);
        Vector<CurseModModel> result = new Vector<>();
        ArrayList<?> a = (ArrayList<?>) m.get("data");
        for (Object o : a){
            CurseModModel model;
            model = g.fromJson(g.toJson(o), CurseModModel.class);
            result.add(model);
        }
        return result;
    }
    public static Vector<CurseModFileModel> getModFiles(CurseModModel mod) throws InterruptedException {
        Vector<String> versions_list = new Vector<>();
        for (Map<String, String> m : mod.latestFilesIndexes){
            if (!versions_list.contains(m.get("gameVersion"))){
                versions_list.add(m.get("gameVersion"));
            }
        }
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
                        CurseModFileModel model;
                        model = g.fromJson(g.toJson(o), CurseModFileModel.class);
                        files.add(model);
                    }
                }
                latch.countDown();
            }).start();
        }
        latch.await();
        return files;
    }
}
