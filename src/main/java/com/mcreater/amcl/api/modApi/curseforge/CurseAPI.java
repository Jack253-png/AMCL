package com.mcreater.amcl.api.modApi.curseforge;

import com.google.gson.internal.LinkedTreeMap;
import com.mcreater.amcl.api.modApi.curseforge.mod.CurseModModel;
import com.mcreater.amcl.api.modApi.curseforge.modFile.CurseModFileModel;
import com.mcreater.amcl.api.modApi.curseforge.modFile.CurseModRequireModel;
import com.mcreater.amcl.controls.RemoteModFile;
import com.mcreater.amcl.download.GetVersionList;
import com.mcreater.amcl.tasks.LambdaTask;
import com.mcreater.amcl.tasks.manager.TaskManager;
import com.mcreater.amcl.util.builders.ThreadBuilder;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.net.HttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public final class CurseAPI {
    private static boolean errored = false;
    private static final String host = "https://api.curseforge.com";
    private static final String apiKey = "$2a$10$o8pygPrhvKBHuuh5imL2W.LCNFhB15zBYAExXx/TqTx/Zp5px2lxu";
    private static final Logger logger = LogManager.getLogger(CurseAPI.class);

    static {
        logger.info("loading curseforge apiKey");
        logger.info(String.format("load success,ApiKey = %s", apiKey));
    }

    private static String response(String url) throws Exception {
        return HttpClient.getInstance(host + url)
                .open()
                .timeout(10000)
                .header("x-api-key", apiKey)
                .header("Accept", "application/json")
                .readWithNoLog();
    }

    public static Vector<CurseModModel> search(String name, CurseResourceType.Types res, CurseSortType.Types sort, int pageSize) throws Exception {
        String url = String.format("/v1/mods/search?gameId=432&searchFilter=%s&classId=%d&sortOrder=%s&pageSize=%d", URLEncoder.encode(name, "UTF-8"), CurseResourceType.get(res), CurseSortType.get(sort), pageSize);
        Map<?, ?> m = GSON_PARSER.fromJson(response(url), Map.class);
        Vector<CurseModModel> result = new Vector<>();
        ArrayList<?> a = (ArrayList<?>) m.get("data");
        for (Object o : a) {
            result.add(GSON_PARSER.fromJson(GSON_PARSER.toJson(o), CurseModModel.class));
        }
        return result;
    }

    public static Vector<CurseModModel> getModFileRequiredMods(CurseModFileModel model) throws Exception {
        Vector<CurseModModel> result = new Vector<>();
        TaskManager.setUpdater((integer, s) -> {
        });
        AtomicReference<Exception> exception = new AtomicReference<>();
        for (CurseModRequireModel m : model.dependencies) {
            if (m.relationType == 3.0) {
                TaskManager.addTasks(new LambdaTask(() -> {
                    try {
                        result.add(getFromModId(m.modId));
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

    public static CurseModModel getFromModId(int id) throws Exception {
        String url = String.format("/v1/mods/%d", id);
        String s = response(url);
        Map<?, ?> m = GSON_PARSER.fromJson(s, Map.class);
        LinkedTreeMap<?, ?> a = (LinkedTreeMap<?, ?>) m.get("data");
        return GSON_PARSER.fromJson(GSON_PARSER.toJson(a), CurseModModel.class);
    }

    public static Vector<CurseModFileModel> getModFiles(CurseModModel mod, String version, FasterUrls.Server server) throws IOException {
        Vector<CurseModFileModel> files = new Vector<>();
        boolean cd = false;
        for (Map<String, String> m : mod.latestFilesIndexes) {
            if (Objects.equals(m.get("gameVersion"), version)) {
                cd = true;
                break;
            }
        }
        if (cd) {
            String url = String.format("/v1/mods/%s/files?gameVersion=%s", mod.id, version);
            Map<?, ?> m;
            try {
                m = GSON_PARSER.fromJson(response(url), Map.class);
            } catch (Exception e) {
                throw new IOException(e);
            }
            if (m != null) {
                ArrayList<?> a = (ArrayList<?>) m.get("data");
                for (Object o : a) {
                    CurseModFileModel model = GSON_PARSER.fromJson(GSON_PARSER.toJson(o), CurseModFileModel.class);
                    files.add(model);
                }
            }
            Vector<CurseModFileModel> forRemoval = new Vector<>();
            files.forEach(e -> {
                try {
                    if (!e.gameVersions.contains(GetVersionList.getSnapShotName(version, server))) forRemoval.add(e);
                } catch (Exception ignored) {

                }
            });
            files.removeAll(forRemoval);
            return files;
        } else {
            return new Vector<>();
        }
    }

    public static Map<String, Vector<CurseModFileModel>> getModFiles(CurseModModel mod) throws IOException {
        Vector<String> versions_list = new Vector<>();
        for (Map<String, String> m : mod.latestFilesIndexes) {
            if (!versions_list.contains(m.get("gameVersion"))) {
                versions_list.add(m.get("gameVersion"));
            }
        }
        CountDownLatch latch = new CountDownLatch(versions_list.size());
        Vector<CurseModFileModel> re = new Vector<>();

        versions_list.forEach(v -> ThreadBuilder.createBuilder()
                .runTarget(() -> {
                    String url = String.format("/v1/mods/%s/files?gameVersion=%s", mod.id, v);
                    Map<?, ?> m;
                    try {
                        m = GSON_PARSER.fromJson(response(url), Map.class);
                    } catch (Exception e) {
                        latch.countDown();
                        throw new RuntimeException(e);
                    }
                    if (m != null) {
                        ArrayList<?> a = (ArrayList<?>) m.get("data");
                        for (Object o : a) {
                            CurseModFileModel model = GSON_PARSER.fromJson(GSON_PARSER.toJson(o), CurseModFileModel.class);
                            if (model.downloadUrl == null) {
                                model.downloadUrl = String.format("https://edge.forgecdn.net/files/%d/%d/%s", model.id / 1000, model.id % 1000, model.fileName);
                            }
                            re.add(model);
                        }
                    }
                    latch.countDown();
                })
                .handler(new UncaughtExceptionsHandler())
                .name("Curseforge mod file process thread")
                .buildAndRun());

        try {
            latch.await();
        } catch (InterruptedException ignored) {
        }
        if (errored) {
            errored = false;
            throw new IOException();
        }
        Map<String, Vector<CurseModFileModel>> result = new HashMap<>();
        re.forEach(model -> RemoteModFile.getModLoaders(model.gameVersions, false).forEach(ver -> {
            if (result.get(ver) == null) result.put(ver, new Vector<>());
            else result.get(ver).add(model);
        }));
        return result;
    }

    public static class UncaughtExceptionsHandler implements Thread.UncaughtExceptionHandler {
        public void uncaughtException(Thread thread, Throwable throwable) {
            logger.info(String.format("%s throws an exception %s", thread, throwable));
            CurseAPI.errored = true;
            throwable.printStackTrace();
        }
    }
}
