package com.mcreater.amcl.api.modApi.modrinth;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mcreater.amcl.api.modApi.modrinth.mod.ModrinthModModel;
import com.mcreater.amcl.api.modApi.modrinth.modFile.ModrinthModFileDepencymModel;
import com.mcreater.amcl.api.modApi.modrinth.modFile.ModrinthModFileModel;
import com.mcreater.amcl.tasks.LambdaTask;
import com.mcreater.amcl.tasks.manager.TaskManager;
import com.mcreater.amcl.util.net.HttpClient;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class ModrinthAPI {
    private static final String host = "https://api.modrinth.com";

    private static String response(String url) throws Exception {
        return HttpClient.getInstance(host + url)
                .open()
                .timeout(10000)
                .header("Accept", "application/json")
                .header("User-Agent", "Abstract minecraft launcher")
                .readWithNoLog();
    }

    public static Vector<ModrinthModModel> search(String name, int pageSize) throws Exception {
        String url = String.format("/v2/search?query=%s&limit=%d&index=downloads&facets=", name, pageSize);
        String re = response(url + URLEncoder.encode("[[\"project_type:mod\"]]", "UTF-8"));

        Vector<ModrinthModModel> resu = new Vector<>();
        resu = GSON_PARSER.fromJson(JSONObject.parseObject(re).getJSONArray("hits").toString(), resu.getClass());

        for (int index = 0; index < resu.size(); index++) {
            resu.set(index, GSON_PARSER.fromJson(GSON_PARSER.toJson(resu.get(index)), ModrinthModModel.class));
        }

        return resu;
    }

    public static Map<String, Vector<ModrinthModFileModel>> getModFiles(ModrinthModModel model) throws Exception {
        String url = String.format("/v2/project/%s/version", model.slug);
        String re = response(url);

        Map<String, Vector<ModrinthModFileModel>> result = new HashMap<>();

        for (Object o : JSONArray.parseArray(re)) {
            ModrinthModFileModel model2 = GSON_PARSER.fromJson(o.toString(), ModrinthModFileModel.class);
            for (String ver : model2.game_versions) {
                if (result.get(ver) == null) result.put(ver, new Vector<>());
                if (result.get(ver) != null) result.get(ver).add(model2);
            }
        }
        return result;
    }

    public static ModrinthModModel getFromModId(String modid) throws Exception {
        String url = "/v2/project/" + modid;
        return GSON_PARSER.fromJson(response(url), ModrinthModModel.class);
    }

    public static Vector<ModrinthModModel> getModFileRequiredMods(ModrinthModFileModel model) throws Exception {
        Vector<ModrinthModModel> result = new Vector<>();
        TaskManager.setUpdater((integer, s) -> {
        });
        AtomicReference<Exception> exception = new AtomicReference<>();
        for (ModrinthModFileDepencymModel m : model.dependencies) {
            if (m.dependency_type.equals("required")) {
                TaskManager.addTasks(new LambdaTask(() -> {
                    try {
                        result.add(getFromModId(m.project_id));
                    } catch (Exception e) {
                        exception.set(e);
                    }
                }));
            }
        }
        try {
            TaskManager.execute("<mod relation>");
        } catch (Exception e) {
            exception.set(e);
        }
        if (exception.get() != null) throw exception.get();
        return result;
    }
}
