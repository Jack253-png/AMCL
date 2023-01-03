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
        return get(dir, version) == VersionType.FABRIC || get(dir, version) == VersionType.FORGE || get(dir, version) == VersionType.OPTIFINE || get(dir, version) == VersionType.QUILT;
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
            if (getTweakClass(v).get(0).contains("net.minecraftforge.legacy.") && getTweakClass(v).get(0).contains(".LibraryFixerTweaker")) {
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
    public static String getFabricVersion(String dir, String version){
        VersionJsonModel v = getVersionModel(dir, version);
        String fabricVersion = Launcher.languageManager.get("ui.versioninfopage.noFabric");
        for (LibModel model : v.libraries) {
            if (model.name.contains("net.fabricmc:fabric-loader:")){
                fabricVersion = Launcher.languageManager.get("ui.versioninfopage.hasfabric", J8Utils.createList(model.name.split(":")).get(2));
            }
        }
        return fabricVersion;
    }
    public static String getForgeVersion(String dir, String version){
        VersionJsonModel v = getVersionModel(dir, version);
        String forge = Launcher.languageManager.get("ui.versioninfopage.noForge");
        for (LibModel model : v.libraries){
            if (model.name.contains("net.minecraftforge:forge:") || model.name.contains("net.minecraftforge:fmlloader:")){
                String n = J8Utils.createList(J8Utils.createList(model.name.split(":")).get(2).split("-")).get(1);
                forge = Launcher.languageManager.get("ui.versioninfopage.hasforge", n).replace(v.id+"-", "");
            }
            else if (model.name.contains("net.minecraftforge:minecraftforge:")){
                String n = J8Utils.createList(J8Utils.createList(model.name.split(":")).get(2).split("-")).get(0);
                forge = Launcher.languageManager.get("ui.versioninfopage.hasforge", n);
            }
        }
        return forge;
    }
    public static String getOptifineVersion(String dir, String version){
        VersionJsonModel v = getVersionModel(dir, version);
        String opti = Launcher.languageManager.get("ui.versioninfopage.noOptifine");
        for (LibModel model : v.libraries){
            if (model.name.contains("optifine:OptiFine:")) {
                String f = J8Utils.createList(model.name.split(":")).get(2);
                Vector<String> f2 = new Vector<>(J8Utils.createList(f.split("_")));
                int idHD = f2.indexOf("HD");
                if (idHD >= 0) {
                    if (idHD > 0) {
                        f2.subList(0, idHD).clear();
                        opti = Launcher.languageManager.get("ui.versioninfopage.hasoptifine", String.join("_", f2));
                    }
                    else {
                        opti = Launcher.languageManager.get("ui.versioninfopage.hasoptifine", f);
                    }
                }
            }
        }
        return opti;
    }
    public static String getLiteLoaderVersion(String dir, String version){
        VersionJsonModel v = getVersionModel(dir, version);
        String lite = Launcher.languageManager.get("ui.versioninfopage.noLiteloader");
        for (LibModel model : v.libraries) {
            if (model.name.contains("com.mumfrey:liteloader:")) {
                lite = Launcher.languageManager.get("ui.versioninfopage.hasliteloader", J8Utils.createList(model.name.split(":")).get(2));
            }
        }
        return lite;
    }
    public static VersionJsonModel getVersionModel(String dir, String version){
        String version_json = LinkPath.link(LinkPath.link(LinkPath.link(dir, "versions"), version), version + ".json");
        String json_result = FileStringReader.read(version_json);
        return GSON_PARSER.fromJson(json_result, VersionJsonModel.class);
    }
}
