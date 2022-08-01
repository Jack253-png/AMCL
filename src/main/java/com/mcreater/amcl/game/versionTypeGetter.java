package com.mcreater.amcl.game;

import com.google.gson.Gson;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.model.LibModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.util.fileUtils.FileStringReader;
import com.mcreater.amcl.util.fileUtils.LinkPath;

import java.util.*;

public class versionTypeGetter {
    public static boolean modded(String dir, String version){
        return get(dir, version).contains("fabric") || get(dir, version).contains("forge") || get(dir, version).contains("optifine");
    }
    public static String get(String dir, String version){
        VersionJsonModel v = getVersionModel(dir, version);
        Vector<String> forge = new Vector<>();
        forge.add("cpw.mods.modlauncher.Launcher");
        forge.add("cpw.mods.bootstraplauncher.BootstrapLauncher");

        if (v.mainClass.contains("net.fabricmc.loader") || getTweakClass(v).contains("net.fabricmc.loader.launch.FabricClientTweaker")){
            return "fabric";
        }
        if (getTweakClass(v).contains("net.minecraftforge.fml.common.launcher.FMLTweaker") || getTweakClass(v).contains("cpw.mods.fml.common.launcher.FMLTweaker")){
            return "forge";
        }
        if (forge.contains(v.mainClass) || getTweakClass(v).contains("net.minecraftforge.fml.common.launcher.FMLTweaker")){
            if (getTweakClass(v).contains("optifine.OptiFineForgeTweaker")){
                return "forge-optifine";
            }
            else{
                return "forge";
            }
        }
        if (getTweakClass(v).contains("com.mumfrey.liteloader.launch.LiteLoaderTweaker")){
            return "liteloader";
        }
        if (getTweakClass(v).contains("optifine.OptiFineTweaker")){
            return "optifine";
        }
        return "original";
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
        for (LibModel model : v.libraries){
            if (model.name.contains("net.fabricmc:fabric-loader:")){
                fabricVersion = String.format(Launcher.languageManager.get("ui.versioninfopage.hasfabric"), List.of(model.name.split(":")).get(2));
            }
        }
        return fabricVersion;
    }
    public static String getForgeVersion(String dir, String version){
        VersionJsonModel v = getVersionModel(dir, version);
        String forge = Launcher.languageManager.get("ui.versioninfopage.noForge");
        for (LibModel model : v.libraries){
            if (model.name.contains("net.minecraftforge:forge:") || model.name.contains("net.minecraftforge:fmlloader:")){
                String n = List.of(List.of(model.name.split(":")).get(2).split("-")).get(1);
                forge = String.format(Launcher.languageManager.get("ui.versioninfopage.hasforge"), n).replace(v.id+"-", "");
            }
        }
        return forge;
    }
    public static String getOptifineVersion(String dir, String version){
        VersionJsonModel v = getVersionModel(dir, version);
        String opti = Launcher.languageManager.get("ui.versioninfopage.noOptifine");
        for (LibModel model : v.libraries){
            if (model.name.contains("optifine:OptiFine:")) {
                opti = String.format(Launcher.languageManager.get("ui.versioninfopage.hasoptifine"), List.of(model.name.split(":")).get(2));
            }
        }
        return opti;
    }
    public static String getLiteLoaderVersion(String dir, String version){
        VersionJsonModel v = getVersionModel(dir, version);
        String lite = Launcher.languageManager.get("ui.versioninfopage.noLiteloader");
        for (LibModel model : v.libraries) {
            if (model.name.contains("com.mumfrey:liteloader:")) {
                lite = String.format(Launcher.languageManager.get("ui.versioninfopage.hasliteloader"), List.of(model.name.split(":")).get(2));
            }
        }
        return lite;
    }
    public static VersionJsonModel getVersionModel(String dir, String version){
        String version_json = LinkPath.link(LinkPath.link(LinkPath.link(dir, "versions"), version), version + ".json");
        String json_result = FileStringReader.read(version_json);
        Gson g = new Gson();
        return g.fromJson(json_result, VersionJsonModel.class);
    }
}
