package com.mcreater.amcl.download;

import com.google.gson.Gson;
import com.mcreater.amcl.api.curseApi.CurseAPI;
import com.mcreater.amcl.api.curseApi.mod.CurseModModel;
import com.mcreater.amcl.api.curseApi.modFile.CurseModFileModel;
import com.mcreater.amcl.controls.ModFile;
import com.mcreater.amcl.download.model.OriginalVersionModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.model.fabric.FabricListModel;
import com.mcreater.amcl.model.fabric.FabricLoaderVersionModel;
import com.mcreater.amcl.model.optifine.optifineAPIModel;
import com.mcreater.amcl.model.optifine.optifineJarModel;
import com.mcreater.amcl.model.original.VersionsModel;
import com.mcreater.amcl.util.FasterUrls;
import com.mcreater.amcl.util.ForgeVersionXMLHandler;
import com.mcreater.amcl.util.net.HttpConnectionUtil;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GetVersionList {
    public static Vector<OriginalVersionModel> getOriginalList(boolean faster) {
        String url = FasterUrls.getVersionJsonv2WithFaster(faster);
        VersionsModel model = new Gson().fromJson(HttpConnectionUtil.doGet(url), VersionsModel.class);
        Vector<OriginalVersionModel> t = new Vector<>();
        model.versions.forEach(s -> t.add(new OriginalVersionModel(s.id, s.type, s.releaseTime, s.url)));
        t.sort((originalVersionModel, t1) -> {
            String a = originalVersionModel.time;
            String b = t1.time;
            a = List.of(a.split("\\+")).get(0).replace("T", " ");
            b = List.of(b.split("\\+")).get(1).replace("T", " ");
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
    public static Vector<String> getForgeVersionList(boolean faster, String version) throws ParserConfigurationException, IOException, SAXException {
        Map<String, Vector<String>> vectorMap = ForgeVersionXMLHandler.load(HttpConnectionUtil.doGet(FasterUrls.fast("https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml", faster)));
        if (vectorMap.get(version) != null) {
            return vectorMap.get(version);
        }
        else{
            return new Vector<>();
        }
    }
    public static Vector<String> getFabricVersionList(boolean faster, String version) {
        String fabricVersions = FasterUrls.fast("https://meta.fabricmc.net/v2/versions/game", faster);
        String loaderVersions = FasterUrls.fast("https://meta.fabricmc.net/v2/versions/loader", faster);
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
    public static Vector<optifineJarModel> getOptifineVersionList(boolean faster, String version) {
        String r = HttpConnectionUtil.doGet("https://optifine.cn/api");
        optifineAPIModel model = new Gson().fromJson(r, optifineAPIModel.class);
        if (model.versions.contains(version)){
            Vector<optifineJarModel> jars = new Vector<>();
            model.files.forEach(optifineJarModel -> {
                if (!Objects.equals(optifineJarModel.version, "beta")) {
                    if (!optifineJarModel.name.contains("legacy")) {
                        if (Objects.equals(optifineJarModel.version, version)){
                            jars.add(optifineJarModel);
                        }
                    }
                }
            });
            for (optifineJarModel jar : jars){
                jar.isPreview = jar.name.contains("preview");
                jar.name = jar.name.replace("OptiFine_"+jar.version+"_", "");
                jar.name = jar.name.replace("preview_", "");
                jar.name = jar.name.replace(".jar", "");
            }
            jars.sort(new Comparator<optifineJarModel>() {
                @Override
                public int compare(optifineJarModel m, optifineJarModel t) {
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
                public int getPreVersion(optifineJarModel model){
                    if (model.isPreview){
                        String s = List.of(model.name.split("_")).get(3).replace("pre", "");
                        return Integer.parseInt(s);
                    }
                    else{
                        return 0;
                    }
                }
                public int getMajorVersion(optifineJarModel model){
                    String s = List.of(model.name.split("_")).get(2).substring(1, 2);
                    return Integer.parseInt(s);
                }
                public int getMainVersion(optifineJarModel model){
                    String s = List.of(model.name.split("_")).get(2).substring(0, 1);
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
    public static Vector<CurseModFileModel> getFabricAPIVersionList(boolean faster, String version) throws IOException {
        CurseModModel model = CurseAPI.getFromModId(306612);
        Vector<CurseModFileModel> files = CurseAPI.getModFiles(model);
        Vector<CurseModFileModel> result = new Vector<>();
        String s = getSnapShotName(version, faster);
        files.forEach(m -> {
            if (ModFile.getModLoaders(m.gameVersions, false).contains(s)){
                result.add(m);
            }
        });
        return result;
    }
    private static String getSnapShotName(String raw_name, boolean faster) {
        OriginalVersionModel n = null;
        for (OriginalVersionModel model : getOriginalList(faster)){
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
    public static Vector<CurseModFileModel> getOptiFabricVersionList(boolean faster, String version) throws IOException {
        CurseModModel mod = CurseAPI.getFromModId(322385);
        Vector<CurseModFileModel> files = CurseAPI.getModFiles(mod);
        Vector<CurseModFileModel> result = new Vector<>();
        String s = getSnapShotName(version, faster);
        files.forEach(m -> {
            if (ModFile.getModLoaders(m.gameVersions, false).contains(s)){
                result.add(m);
                System.out.println(m.fileName);
            }
        });
        return result;
    }
}
