package com.mcreater.amcl.download;

import com.google.gson.Gson;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.curseApi.CurseAPI;
import com.mcreater.amcl.api.curseApi.modFile.CurseModFileModel;
import com.mcreater.amcl.download.model.OriginalVersionModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.model.fabric.FabricListModel;
import com.mcreater.amcl.model.fabric.FabricLoaderVersionModel;
import com.mcreater.amcl.model.optifine.OptifineAPIModel;
import com.mcreater.amcl.model.optifine.OptifineJarModel;
import com.mcreater.amcl.model.original.VersionsModel;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.xml.ForgeVersionXMLHandler;
import com.mcreater.amcl.util.net.HttpConnectionUtil;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GetVersionList {
    public static Vector<OriginalVersionModel> getOriginalList() throws Exception {
        String url = FasterUrls.getVersionJsonv2WithFaster(FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
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
            jars.sort(new Comparator<OptifineJarModel>() {
                @Override
                public int compare(OptifineJarModel m, OptifineJarModel t) {
                    int main = getReturn(getMainVersion(m), getMainVersion(t));
                    if (main != 0){
                        return main;
                    }
                    else{
                        int major = getReturn(getMajorVersion(m), getMajorVersion(t));
                        if (major != 0){
                            return major;
                        }
                        else{
                            if (getPreVersion(m) == 0){
                                return -1;
                            }
                            else if (getPreVersion(t) == 0){
                                return 1;
                            }
                            else{
                                return getReturn(getPreVersion(m), getPreVersion(t));
                            }
                        }
                    }
                }
                public int getReturn(int i, int j){
                    return Integer.compare(j, i);
                }
                public int getPreVersion(OptifineJarModel model){
                    if (model.isPreview){
                        String s = J8Utils.createList(model.name.split("_")).get(3).replace("pre", "");
                        return Integer.parseInt(s);
                    }
                    else{
                        return 0;
                    }
                }
                public int getMajorVersion(OptifineJarModel model){
                    String s = J8Utils.createList(model.name.split("_")).get(2).substring(1, 2);
                    return Integer.parseInt(s);
                }
                public int getMainVersion(OptifineJarModel model){
                    String s = J8Utils.createList(model.name.split("_")).get(2).substring(0, 1);
                    switch (s){
                        case "A":
                            return 1;
                        case "B":
                            return 2;
                        case "C":
                            return 3;
                        case "D":
                            return 4;
                        case "E":
                            return 5;
                        case "F":
                            return 6;
                        case "G":
                            return 7;
                        case "H":
                            return 8;
                        case "I":
                            return 9;
                        case "J":
                            return 10;
                        case "K":
                            return 11;
                        case "L":
                            return 12;
                        case "M":
                            return 13;
                        case "N":
                            return 14;
                        case "O":
                            return 15;
                        case "P":
                            return 16;
                        case "Q":
                            return 17;
                        case "R":
                            return 18;
                        case "S":
                            return 19;
                        case "T":
                            return 20;
                        case "U":
                            return 21;
                        case "V":
                            return 22;
                        case "W":
                            return 23;
                        case "X":
                            return 24;
                        case "Y":
                            return 25;
                        case "Z":
                            return 26;
                        default:
                            return 27;
                    }
                }
            });
            return jars;
        }
        else{
            return new Vector<>();
        }
    }
    public static Vector<CurseModFileModel> getFabricAPIVersionList(String version) throws Exception {
        return CurseAPI.getModFiles(CurseAPI.getFromModId(306612), getSnap(version));
    }
    public static String getSnapShotName(String raw_name) throws Exception {
        OriginalVersionModel n = null;
        for (OriginalVersionModel model : getOriginalList()){
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
    public static String getSnap(String raw_name) throws Exception {
        OriginalVersionModel n = null;
        for (OriginalVersionModel model : getOriginalList()){
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
    public static Vector<CurseModFileModel> getOptiFabricVersionList(String version) throws Exception {
        return CurseAPI.getModFiles(CurseAPI.getFromModId(322385), getSnap(version));
    }
}
