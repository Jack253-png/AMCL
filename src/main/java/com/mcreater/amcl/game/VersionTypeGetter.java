package com.mcreater.amcl.game;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.model.LibModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.util.FileUtils.FileStringReader;
import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.J8Utils;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;
import static com.mcreater.amcl.util.svg.Images.fabric;
import static com.mcreater.amcl.util.svg.Images.forge;
import static com.mcreater.amcl.util.svg.Images.liteloader;
import static com.mcreater.amcl.util.svg.Images.optifine;
import static com.mcreater.amcl.util.svg.Images.original;
import static com.mcreater.amcl.util.svg.Images.quilt;

public class VersionTypeGetter {
    public enum VersionType {
        ORIGINAL,
        FORGE,
        FABRIC,
        OPTIFINE,
        LITELOADER,

        QUILT;
        public static Image getImage(VersionType type) {
            switch (type) {
                default:
                case ORIGINAL:
                    return original;
                case FORGE:
                    return forge;
                case FABRIC:
                    return fabric;
                case LITELOADER:
                    return liteloader;
                case OPTIFINE:
                    return optifine;
                case QUILT:
                    return quilt;
            }
        }
    }

    public static boolean modded(String dir, String version){
        VersionType type = get(dir, version);
        return type == VersionType.FABRIC ||
                type == VersionType.FORGE ||
                type == VersionType.QUILT;
    }
    public static VersionType get(String dir, String version){
        VersionJsonModel v = getVersionModel(dir, version);
        Vector<String> forge = new Vector<>();
        forge.add("cpw.mods.modlauncher.Launcher");
        forge.add("cpw.mods.bootstraplauncher.BootstrapLauncher");
        if (v.mainClass.contains("org.quiltmc.loader.impl.launch.knot.KnotClient")) {
            return VersionType.QUILT;
        }
        for (LibModel model : v.libraries) {
            if (model != null) {
                if (model.name.startsWith("org.quiltmc:")) {
                    return VersionType.QUILT;
                }
            }
        }
        if (v.mainClass.contains("net.fabricmc.loader") || getTweakClass(v).contains("net.fabricmc.loader.launch.FabricClientTweaker")){
            return VersionType.FABRIC;
        }
        if (getTweakClass(v).contains("net.minecraftforge.fml.common.launcher.FMLTweaker") || getTweakClass(v).contains("cpw.mods.fml.common.launcher.FMLTweaker")){
            return VersionType.FORGE;
        }
        if (getTweakClass(v).size() >= 1) {
            if (getTweakClass(v).get(0).matches("net\\.minecraftforge\\.legacy\\..*\\.LibraryFixerTweaker")) {
                return VersionType.FORGE;
            }
        }
        if (forge.contains(v.mainClass) || getTweakClass(v).contains("net.minecraftforge.fml.common.launcher.FMLTweaker")){
            return VersionType.FORGE;
        }
        if (getTweakClass(v).contains("com.mumfrey.liteloader.launch.LiteLoaderTweaker")){
            return VersionType.LITELOADER;
        }
        if (getTweakClass(v).contains("optifine.OptiFineTweaker")){
            return VersionType.OPTIFINE;
        }
        return VersionType.ORIGINAL;
    }
    public static Vector<String> getTweakClass(VersionJsonModel v){
        Vector<String> tweakClasses = new Vector<>();
        if (v.minecraftArguments != null){
            String[] splited = v.minecraftArguments.split(" ");
            int locate_index = -1;
            Vector<String> temp = new Vector<>(new ArrayList<>(Arrays.asList(splited)));
            for (String s : temp) {
                if (s.toLowerCase().contains("tweakclass")){
                    locate_index = temp.indexOf(s);
                }
            }

            if (!Objects.equals(locate_index, -1)){
                tweakClasses.add(temp.get(locate_index + 1));
            }
        }
        int l = -1;
        if (v.arguments != null){
            if (v.arguments.game != null){
                for (Object s : v.arguments.game) {
                    try {
                        if (((String) s).toLowerCase().contains("tweakclass")) {
                            l = v.arguments.game.indexOf(s);
                        }
                    }
                    catch (ClassCastException ignored){
                    }
                }
            }
            if (!Objects.equals(l, -1)){
                try {
                    tweakClasses.add((String) (v.arguments.game.get(l + 1)));
                }
                catch (ClassCastException ignored){
                }
            }
        }
        return tweakClasses;
    }
    public static String getFabricVersionSrc(String dir, String version){
        try {
            VersionJsonModel v = getVersionModel(dir, version);
            String fabricVersion = null;
            for (LibModel model : v.libraries) {
                if (model.name.contains("net.fabricmc:fabric-loader:")){
                    fabricVersion = model.name.split(":")[2];
                }
            }
            return fabricVersion;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getForgeVersionSrc(String dir, String version) {
        try {
            VersionJsonModel v = getVersionModel(dir, version);
            String forge = null;
            for (LibModel model : v.libraries) {
                if (model.name.contains("net.minecraftforge:forge:") || model.name.contains("net.minecraftforge:fmlloader:")){
                    forge = model.name.split(":")[2].split("-")[1].replace(v.id + "-", "").replace("-" + v.id, "");
                }
                else if (model.name.contains("net.minecraftforge:minecraftforge:")){
                    forge = model.name.split(":")[2].split("-")[0];
                }
            }
            return forge;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getOptifineVersionSrc(String dir, String version) {
        try {
            VersionJsonModel v = getVersionModel(dir, version);
            String opti = null;
            for (LibModel model : v.libraries){
                if (model.name.contains("optifine:OptiFine:")) {
                    String f = model.name.split(":")[2];
                    Vector<String> f2 = new Vector<>(J8Utils.createList(f.split("_")));
                    int idHD = f2.indexOf("HD");
                    if (idHD >= 0) {
                        if (idHD > 0) f2.subList(0, idHD).clear();
                        opti = idHD > 0 ? String.join("_", f2) : f;
                    }
                }
            }
            return opti;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getLiteLoaderVersionSrc(String dir, String version) {
        try {
            VersionJsonModel v = getVersionModel(dir, version);
            String lite = null;
            for (LibModel model : v.libraries) {
                if (model.name.contains("com.mumfrey:liteloader:")) {
                    lite = model.name.split(":")[2];
                }
            }
            return lite;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getQuiltVersionSrc(String dir, String version) {
        try {
            VersionJsonModel v = getVersionModel(dir, version);
            String quiltVersion = null;
            for (LibModel model : v.libraries) {
                if (model.name.contains("org.quiltmc:quilt-loader:")) {
                    quiltVersion = model.name.split(":")[2];
                }
            }
            return quiltVersion;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static VersionJsonModel getVersionModel(String dir, String version){
        String version_json = LinkPath.link(LinkPath.link(LinkPath.link(dir, "versions"), version), version + ".json");
        String json_result = FileStringReader.read(version_json);
        return GSON_PARSER.fromJson(json_result, VersionJsonModel.class);
    }
}
