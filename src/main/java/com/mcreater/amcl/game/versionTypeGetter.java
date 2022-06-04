package com.mcreater.amcl.game;

import com.google.gson.Gson;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.util.FileStringReader;
import com.mcreater.amcl.util.LinkPath;

import java.io.File;
import java.util.*;

public class versionTypeGetter {
    public static String get(String dir, String version){
        String version_json = LinkPath.link(LinkPath.link(LinkPath.link(dir, "versions"), version), version + ".json");
        String json_result = FileStringReader.read(version_json);
        Gson g = new Gson();
        VersionJsonModel v = g.fromJson(json_result, VersionJsonModel.class);
        Vector<String> forge = new Vector<>();
        forge.add("cpw.mods.modlauncher.Launcher");
        forge.add("cpw.mods.bootstraplauncher.BootstrapLauncher");

        if (Objects.equals(v.mainClass, "net.fabricmc.loader.impl.launch.knot.KnotClient")){
            return "fabric";
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
            Vector<String> temp = new Vector<>(new ArrayList<String>(Arrays.asList(splited)));
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
}
