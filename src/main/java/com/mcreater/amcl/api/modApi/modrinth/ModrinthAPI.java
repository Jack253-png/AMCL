package com.mcreater.amcl.api.modApi.modrinth;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.mcreater.amcl.api.modApi.curseforge.CurseAPI;
import com.mcreater.amcl.api.modApi.modrinth.mod.ModrinthModModel;
import com.mcreater.amcl.api.modApi.modrinth.modFile.ModrinthModFileDepencymModel;
import com.mcreater.amcl.api.modApi.modrinth.modFile.ModrinthModFileModel;
import com.mcreater.amcl.tasks.LambdaTask;
import com.mcreater.amcl.tasks.taskmanager.TaskManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

public class ModrinthAPI {
    private static final Logger logger = LogManager.getLogger(CurseAPI.class);
    private static final String host = "https://api.modrinth.com";
    private static String response(String url) throws IOException {
        int tried = 0;
        while (true) {
            try {
                long startTime = System.currentTimeMillis();
                URL u = new URL(host + url);
                logger.info(String.format("requesting modrinthApi with url %s", u));
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("User-Agent", "abstract minecraft launcher");
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

    public static Vector<ModrinthModModel> search(String name, int pageSize) throws IOException {
        String url = String.format("/v2/search?query=%s&limit=%d&index=downloads&facets=", name, pageSize);
        String re = response(url + URLEncoder.encode("[[\"project_type:mod\"]]", "UTF-8"));
        Gson g = new Gson();

        Vector<ModrinthModModel> resu = new Vector<>();
        resu = g.fromJson(JSONObject.parseObject(re).getJSONArray("hits").toString(), resu.getClass());

        for (int index = 0; index < resu.size(); index++) {
            resu.set(index, g.fromJson(g.toJson(resu.get(index)), ModrinthModModel.class));
        }

        return resu;
    }
    public static Map<String, Vector<ModrinthModFileModel>> getModFiles(ModrinthModModel model) throws IOException {
        String url = String.format("/v2/project/%s/version", model.slug);
        String re = response(url);
        Gson g = new Gson();

        Map<String, Vector<ModrinthModFileModel>> result = new HashMap<>();

        for (Object o : JSONArray.parseArray(re)) {
            ModrinthModFileModel model2 = g.fromJson(o.toString(), ModrinthModFileModel.class);
            for (String ver : model2.game_versions) {
                if (result.get(ver) == null) result.put(ver, new Vector<>());
                if (result.get(ver) != null) result.get(ver).add(model2);
            }
        }
        return result;
    }

    public static ModrinthModModel getFromModId(String modid) throws IOException {
        String url = "/v2/project/" + modid;
        return new Gson().fromJson(response(url), ModrinthModModel.class);
    }

    public static Vector<ModrinthModModel> getModFileRequiredMods(ModrinthModFileModel model) throws Exception {
        Vector<ModrinthModModel> result = new Vector<>();
        TaskManager.setUpdater((integer, s) -> {});
        AtomicReference<Exception> exception = new AtomicReference<>();
        for (ModrinthModFileDepencymModel m : model.dependencies) {
            if (m.dependency_type.equals("required")) {
                TaskManager.addTasks(new LambdaTask(() -> {
                    try {
                        result.add(getFromModId(m.project_id));
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
}
