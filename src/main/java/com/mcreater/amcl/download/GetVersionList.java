package com.mcreater.amcl.download;

import com.google.gson.Gson;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.curseApi.CurseAPI;
import com.mcreater.amcl.api.curseApi.modFile.CurseModFileModel;
import com.mcreater.amcl.download.model.NewForgeItemFileModel;
import com.mcreater.amcl.download.model.NewForgeItemModel;
import com.mcreater.amcl.download.model.OriginalVersionModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.model.fabric.FabricListModel;
import com.mcreater.amcl.model.fabric.FabricLoaderVersionModel;
import com.mcreater.amcl.model.optifine.OptifineAPIModel;
import com.mcreater.amcl.model.optifine.OptifineJarModel;
import com.mcreater.amcl.model.original.VersionsModel;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.net.HttpConnectionUtil;
import com.mcreater.amcl.util.xml.ForgeVersionXMLHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

public class GetVersionList {
    public static Vector<OriginalVersionModel> getOriginalList(FasterUrls.Servers server) throws Exception {
        String url = FasterUrls.getVersionJsonv2WithFaster(server);
        VersionsModel model = new Gson().fromJson(HttpConnectionUtil.doGet(url), VersionsModel.class);
        Vector<OriginalVersionModel> t = new Vector<>();
        model.versions.forEach(s -> t.add(new OriginalVersionModel(s.id, s.type, s.releaseTime, s.url)));
        t.sort((originalVersionModel, t1) -> {
            String a = originalVersionModel.time;
            String b = t1.time;
            a = J8Utils.createList(a.split("\\+")).get(0).replace("T", " ");
            b = J8Utils.createList(b.split("\\+")).get(1).replace("T", " ");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            Date d1, d2;
            try {
                d1 = dateFormat.parse(a);
                d2 = dateFormat.parse(b);
            } catch (ParseException e) {
                return 0;
            }
            if (d1.after(d2)){
                return 1;
            }
            else{
                return -1;
            }
        });
        return t;
    }

    public static Vector<NewForgeItemModel> getForgeInstallers(String version) throws Exception {
        String url = FasterUrls.fast("https://bmclapi2.bangbang93.com/forge/minecraft/" + version, FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
        String r = HttpConnectionUtil.doGet(url);
        Vector<NewForgeItemModel> result = new Vector<>();
        result = new Gson().fromJson(r, result.getClass());

        Vector<NewForgeItemModel> r2 = new Vector<>();

        for (Object o : result) {
            Gson gson = new Gson();
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
    public static boolean isMirror() {
        return FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer) != FasterUrls.Servers.MOJANG;
    }

    public static String getForgeInstallerDownloadURL(NewForgeItemModel model, String ori) {
        if (isMirror()) {
            return FasterUrls.fast("https://bmclapi2.bangbang93.com/forge/download/" + model.build, FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
        }
        else {
            return String.format("https://files.minecraftforge.net/maven/net/minecraftforge/forge/%s-%s/forge-%s-%s-installer.jar", ori, model.version, ori, model.version);
        }
    }


    public static Vector<String> getForgeVersionList(String version) throws Exception {
        String url = FasterUrls.fast("https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml", FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
        Map<String, Vector<String>> vectorMap = ForgeVersionXMLHandler.load(HttpConnectionUtil.doGet(url));
        vectorMap.remove("1.1");
        vectorMap.remove("1.2.3");
        vectorMap.remove("1.2.4");
        vectorMap.remove("1.2.5");
        vectorMap.remove("1.3.2");
        vectorMap.remove("1.4.0");
        vectorMap.remove("1.4.2");
        vectorMap.remove("1.4.3");
        vectorMap.remove("1.4.4");
        vectorMap.remove("1.4.5");
        vectorMap.remove("1.4.6");
        vectorMap.remove("1.4.7");
        vectorMap.remove("1.5");
        vectorMap.remove("1.5.1");
        vectorMap.remove("1.5.2");
        if (vectorMap.get(version) != null) {
            return vectorMap.get(version);
        }
        else{
            return new Vector<>();
        }
    }
    public static Vector<String> getFabricVersionList(String version) throws Exception {
        String fabricVersions = FasterUrls.fast("https://meta.fabricmc.net/v2/versions/game", FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
        String loaderVersions = FasterUrls.fast("https://meta.fabricmc.net/v2/versions/loader", FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
        Vector<Map<String, String>> s = new Vector<>();
        s = new Gson().fromJson(HttpConnectionUtil.doGet(fabricVersions), s.getClass());
        Vector<String> versions = new Vector<>();
        for (Map<String, String> m : s){
            versions.add(m.get("version"));
        }
        if (versions.contains(version)){
            Vector<String> result = new Vector<>();
            String raw = String.format("{\"versions\" : %s}", HttpConnectionUtil.doGet(loaderVersions));
            FabricListModel vs = new Gson().fromJson(raw, FabricListModel.class);
            for (FabricLoaderVersionModel model : vs.versions){
                result.add(model.version);
            }
            return result;
        }
        else{
            return new Vector<>();
        }
    }
    public static Vector<String> getQuiltVersionList(String version) throws Exception {
        String quiltVersions = FasterUrls.fast("https://meta.quiltmc.org/v3/versions/game", FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
        String loaderVersions = FasterUrls.fast("https://meta.quiltmc.org/v3/versions/loader", FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));

        Vector<Map<String, String>> s = new Vector<>();
        s = new Gson().fromJson(HttpConnectionUtil.doGet(quiltVersions), s.getClass());
        Vector<String> versions = new Vector<>();
        for (Map<String, String> m : s){
            versions.add(m.get("version"));
        }
        if (versions.contains(version)){
            Vector<String> result = new Vector<>();
            String raw = String.format("{\"versions\" : %s}", HttpConnectionUtil.doGet(loaderVersions));
            FabricListModel vs = new Gson().fromJson(raw, FabricListModel.class);
            for (FabricLoaderVersionModel model : vs.versions){
                result.add(model.version);
            }
            return result;
        }
        else{
            return new Vector<>();
        }
    }
    public static OptifineAPIModel getOptifineVersionRaw() throws Exception {
        String r = HttpConnectionUtil.doGet("https://optifine.cn/api");
        Gson g = new Gson();
        return g.fromJson(r, OptifineAPIModel.class);
    }
    public static Vector<OptifineJarModel> getOptifineVersionList(String version) throws Exception {
        String r = HttpConnectionUtil.doGet("https://optifine.cn/api");
        OptifineAPIModel model = new Gson().fromJson(r, OptifineAPIModel.class);
        if (model.versions.contains(version)){
            Vector<OptifineJarModel> jars = new Vector<>();
            model.files.forEach(optifineJarModel -> {
                if (!Objects.equals(optifineJarModel.version, "beta")) {
                    if (!optifineJarModel.name.contains("legacy")) {
                        if (Objects.equals(optifineJarModel.version, version)){
                            jars.add(optifineJarModel);
                        }
                    }
                }
            });
            for (OptifineJarModel jar : jars){
                jar.isPreview = jar.name.contains("preview");
                jar.name = jar.name.replace("OptiFine_"+jar.version+"_", "");
                jar.name = jar.name.replace("preview_", "");
                jar.name = jar.name.replace(".jar", "");
            }
            jars.sort((o1, o2) -> {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
                    Date date1 = sdf.parse(o1.time);
                    Date date2 = sdf.parse(o2.time);

                    if (date1.after(date2)) return 1;
                    if (date2.after(date1)) return -1;

                    return 0;
                }
                catch (Exception e){
                    return 0;
                }
            });
            Collections.reverse(jars);
            return jars;
        }
        else{
            return new Vector<>();
        }
    }
    public static Vector<CurseModFileModel> getFabricAPIVersionList(String version, FasterUrls.Servers server) throws Exception {
        return CurseAPI.getModFiles(CurseAPI.getFromModId(306612), getSnap(version, server));
    }
    public static String getSnapShotName(String raw_name, FasterUrls.Servers server) throws Exception {
        OriginalVersionModel n = null;
        for (OriginalVersionModel model : getOriginalList(server)){
            if (Objects.equals(raw_name, model.id)){
                n = model;
            }
        }
        if (n == null){
            return null;
        }
        else{
            if (Objects.equals(n.type, "snapshot")) {
                VersionJsonModel model = new Gson().fromJson(HttpConnectionUtil.doGet(n.url), VersionJsonModel.class);
                return model.assetIndex.get("id")+"-Snapshot";
            }
        }
        return raw_name;
    }
    public static String getSnap(String raw_name, FasterUrls.Servers server) throws Exception {
        OriginalVersionModel n = null;
        for (OriginalVersionModel model : getOriginalList(server)){
            if (Objects.equals(raw_name, model.id)){
                n = model;
            }
        }
        if (n == null){
            return null;
        }
        else{
            if (Objects.equals(n.type, "snapshot")) {
                VersionJsonModel model = new Gson().fromJson(HttpConnectionUtil.doGet(n.url), VersionJsonModel.class);
                return model.assetIndex.get("id");
            }
        }
        return raw_name;
    }
    public static Vector<CurseModFileModel> getOptiFabricVersionList(String version, FasterUrls.Servers server) throws Exception {
        return CurseAPI.getModFiles(CurseAPI.getFromModId(322385), getSnap(version, server));
    }

    public static Vector<CurseModFileModel> getQuiltAPIVersionList(String version, FasterUrls.Servers server) throws Exception {
        return CurseAPI.getModFiles(CurseAPI.getFromModId(634179), getSnap(version, server));
    }
}
