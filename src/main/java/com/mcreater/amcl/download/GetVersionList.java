package com.mcreater.amcl.download;

import com.google.gson.Gson;
import com.mcreater.amcl.api.modApi.curseforge.CurseAPI;
import com.mcreater.amcl.api.modApi.curseforge.modFile.CurseModFileModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.model.download.NewForgeItemFileModel;
import com.mcreater.amcl.model.download.NewForgeItemModel;
import com.mcreater.amcl.model.download.OriginalVersionModel;
import com.mcreater.amcl.model.fabric.FabricLoaderVersionModel;
import com.mcreater.amcl.model.optifine.OptifineAPIModel;
import com.mcreater.amcl.model.optifine.OptifineJarModel;
import com.mcreater.amcl.model.original.VersionsModel;
import com.mcreater.amcl.util.JsonUtils;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.net.HttpConnectionUtil;
import com.mcreater.amcl.util.parsers.ForgeVersionXMLHandler;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;
import static com.mcreater.amcl.util.StringUtils.parseDate2;
import static com.mcreater.amcl.util.StringUtils.parseDate3;
import static com.mcreater.amcl.util.net.FasterUrls.FABRIC_LOADER_INDEX;
import static com.mcreater.amcl.util.net.FasterUrls.FABRIC_VER_INDEX;
import static com.mcreater.amcl.util.net.FasterUrls.FORGE_INDEX;
import static com.mcreater.amcl.util.net.FasterUrls.FORGE_INSTALLER_FORMAT;
import static com.mcreater.amcl.util.net.FasterUrls.MIRROR_FORGE_CORE;
import static com.mcreater.amcl.util.net.FasterUrls.MIRROR_FORGE_INDEX;
import static com.mcreater.amcl.util.net.FasterUrls.QUILT_LOADER_INDEX;
import static com.mcreater.amcl.util.net.FasterUrls.QUILT_VER_INDEX;

public class GetVersionList {
    private static final Vector<String> ignoreList = new Vector<>(Arrays.asList(
            "1.1",
            "1.2.3",
            "1.2.4",
            "1.2.5",
            "1.3.2",
            "1.4.0",
            "1.4",
            "1.4.2",
            "1.4.3",
            "1.4.4",
            "1.4.5",
            "1.4.6",
            "1.4.7"
    ));

    public static Vector<OriginalVersionModel> getOriginalList(FasterUrls.Server server) throws Exception {
        String url = FasterUrls.getVersionJsonv2WithFaster(server);
        VersionsModel model = GSON_PARSER.fromJson(HttpConnectionUtil.doGet(url), VersionsModel.class);
        Vector<OriginalVersionModel> t = new Vector<>();
        model.versions.forEach(s -> t.add(new OriginalVersionModel(s.id, s.type, s.releaseTime, s.url)));
        t.sort((originalVersionModel, t1) -> {
            Date d1, d2;
            try {
                d1 = parseDate2(originalVersionModel.time);
                d2 = parseDate2(t1.time);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
            if (d1.after(d2)) {
                return -1;
            } else {
                return 1;
            }
        });
        return t;
    }

    public static Vector<NewForgeItemModel> getForgeInstallers(String version, FasterUrls.Server server) throws Exception {
        if (ignoreList.contains(version)) return new Vector<>();
        String url = FasterUrls.fast(MIRROR_FORGE_INDEX + version, server);
        String r = HttpConnectionUtil.doGet(url);
        Vector<NewForgeItemModel> result = new Vector<>();
        result = GSON_PARSER.fromJson(r, result.getClass());

        Vector<NewForgeItemModel> r2 = new Vector<>();

        for (Object o : result) {
            Gson gson = GSON_PARSER;
            r2.add(gson.fromJson(gson.toJson(o), NewForgeItemModel.class));
        }

        Vector<NewForgeItemModel> rm = new Vector<>();
        r2.forEach(newForgeItemModel -> {
            for (NewForgeItemFileModel m : newForgeItemModel.files) {
                if (m.format.equals("jar") && m.category.equals("installer")) {
                    return;
                }
            }
            rm.add(newForgeItemModel);
        });
        r2.removeAll(rm);
        r2.sort((o1, o2) -> {
            if (o1.build == o2.build) return 0;
            return o1.build > o2.build ? -1 : 1;
        });
        return r2;
    }

    public static boolean isMirror(FasterUrls.Server server) {
        return server != FasterUrls.Server.MOJANG;
    }

    public static String getForgeInstallerDownloadURL(NewForgeItemModel model, String ori, FasterUrls.Server server) {
        if (isMirror(server)) {
            return FasterUrls.fast(MIRROR_FORGE_CORE + model.build, server);
        } else {
            return String.format(FORGE_INSTALLER_FORMAT, ori, model.version, ori, model.version);
        }
    }


    public static Vector<String> getForgeVersionList(String version, FasterUrls.Server server) throws Exception {
        String url = FasterUrls.fast(FORGE_INDEX, server);
        Map<String, Vector<String>> vectorMap = ForgeVersionXMLHandler.load(HttpConnectionUtil.doGet(url));
        for (String ver : ignoreList) {
            vectorMap.remove(ver);
        }
        if (vectorMap.get(version) != null) {
            return vectorMap.get(version);
        } else {
            return new Vector<>();
        }
    }

    public static Vector<String> getFabricVersionList(String version, FasterUrls.Server server) throws Exception {
        String fabricVersions = FasterUrls.fast(FABRIC_VER_INDEX, server);
        String loaderVersions = FasterUrls.fast(FABRIC_LOADER_INDEX, server);
        Vector<String> versions = new Vector<>();
        GSON_PARSER.fromJson(HttpConnectionUtil.doGet(fabricVersions), Vector.class).forEach(o -> {
            if (o instanceof Map) versions.add(((Map<?, ? extends String>) o).get("version"));
            else versions.add(o.toString());
        });
        if (versions.contains(version)) {
            Vector<String> result = new Vector<>();
            for (FabricLoaderVersionModel model : JsonUtils.readArray(HttpConnectionUtil.doGet(loaderVersions), FabricLoaderVersionModel.class)) {
                result.add(model.version);
            }
            return result;
        } else {
            return new Vector<>();
        }
    }

    public static Vector<String> getQuiltVersionList(String version, FasterUrls.Server server) throws Exception {
        String quiltVersions = FasterUrls.fast(QUILT_VER_INDEX, server);
        String loaderVersions = FasterUrls.fast(QUILT_LOADER_INDEX, server);
        Vector<String> versions = new Vector<>();
        GSON_PARSER.fromJson(HttpConnectionUtil.doGet(quiltVersions), Vector.class).forEach(o -> {
            if (o instanceof Map) versions.add(((Map<?, ? extends String>) o).get("version"));
            else versions.add(o.toString());
        });
        if (versions.contains(version)) {
            Vector<String> result = new Vector<>();
            for (FabricLoaderVersionModel model : JsonUtils.readArray(HttpConnectionUtil.doGet(loaderVersions), FabricLoaderVersionModel.class)) {
                result.add(model.version);
            }
            return result;
        } else {
            return new Vector<>();
        }
    }

    public static OptifineAPIModel getOptifineVersionRaw() throws Exception {
        String r = HttpConnectionUtil.doGet("https://optifine.cn/api");
        Gson g = GSON_PARSER;
        return g.fromJson(r, OptifineAPIModel.class);
    }

    public static Vector<OptifineJarModel> getOptifineVersionList(String version) throws Exception {
        String r = HttpConnectionUtil.doGet("https://optifine.cn/api");
        OptifineAPIModel model = GSON_PARSER.fromJson(r, OptifineAPIModel.class);
        if (model.versions.contains(version)) {
            Vector<OptifineJarModel> jars = new Vector<>();
            model.files.forEach(optifineJarModel -> {
                if (!Objects.equals(optifineJarModel.version, "beta")) {
                    if (!optifineJarModel.name.contains("legacy")) {
                        if (Objects.equals(optifineJarModel.version, version)) {
                            jars.add(optifineJarModel);
                        }
                    }
                }
            });
            for (OptifineJarModel jar : jars) {
                jar.isPreview = jar.name.contains("preview");
                jar.name = jar.name.replace("OptiFine_" + jar.version + "_", "").replace("preview_", "").replace(".jar", "");
            }
            jars.sort((o1, o2) -> {
                try {
                    if (o1.isPreview && !o2.isPreview) return -1;
                    if (!o1.isPreview && o2.isPreview) return 1;
                    Date date1 = parseDate3(o1.time);
                    Date date2 = parseDate3(o2.time);

                    if (date1.after(date2)) return 1;
                    if (date2.after(date1)) return -1;

                    return 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            });
            Collections.reverse(jars);
            return jars;
        } else {
            return new Vector<>();
        }
    }

    public static Vector<CurseModFileModel> getFabricAPIVersionList(String version, FasterUrls.Server server) throws Exception {
        return CurseAPI.getModFiles(CurseAPI.getFromModId(306612), getSnap(version, server), server);
    }

    public static String getSnapShotName(String raw_name, FasterUrls.Server server) throws Exception {
        OriginalVersionModel n = null;
        for (OriginalVersionModel model : getOriginalList(server)) {
            if (Objects.equals(raw_name, model.id)) {
                n = model;
            }
        }
        if (n == null) {
            return null;
        } else {
            if (Objects.equals(n.type, "snapshot")) {
                VersionJsonModel model = GSON_PARSER.fromJson(HttpConnectionUtil.doGet(n.url), VersionJsonModel.class);
                return model.assetIndex.get("id") + "-Snapshot";
            }
        }
        return raw_name;
    }

    public static String getSnap(String raw_name, FasterUrls.Server server) throws Exception {
        OriginalVersionModel n = null;
        for (OriginalVersionModel model : getOriginalList(server)) {
            if (Objects.equals(raw_name, model.id)) {
                n = model;
            }
        }
        if (n == null) {
            return null;
        } else {
            if (Objects.equals(n.type, "snapshot")) {
                VersionJsonModel model = GSON_PARSER.fromJson(HttpConnectionUtil.doGet(n.url), VersionJsonModel.class);
                return model.assetIndex.get("id");
            }
        }
        return raw_name;
    }

    public static Vector<CurseModFileModel> getOptiFabricVersionList(String version, FasterUrls.Server server) throws Exception {
        return CurseAPI.getModFiles(CurseAPI.getFromModId(322385), getSnap(version, server), server);
    }

    public static Vector<CurseModFileModel> getQuiltAPIVersionList(String version, FasterUrls.Server server) throws Exception {
        return CurseAPI.getModFiles(CurseAPI.getFromModId(634179), getSnap(version, server), server);
    }
}
