package com.mcreater.amcl.util.net;

import com.google.gson.internal.LinkedTreeMap;
import com.mcreater.amcl.tasks.AbstractDownloadTask;
import com.mcreater.amcl.util.J8Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.BiConsumer;

public class FasterUrls {
    public static Map<String, String> s = new LinkedTreeMap<>();
    public static Map<String, String> rs = new LinkedTreeMap<>();
    public static Map<String, AbstractDownloadTask.TaskType> urlTypes = new LinkedTreeMap<>();

    static {
        s.put("http://launchermeta.mojang.com/mc/game/version_manifest.json", "https://bmclapi2.bangbang93.com/mc/game/version_manifest.json");
        s.put("http://launchermeta.mojang.com/mc/game/version_manifest_v2.json", "https://bmclapi2.bangbang93.com/mc/game/version_manifest_v2.json");
        s.put("https://launchermeta.mojang.com", "https://bmclapi2.bangbang93.com");
        s.put("https://launcher.mojang.com", "https://bmclapi2.bangbang93.com");
        s.put("https://piston-meta.mojang.com", "https://bmclapi2.bangbang93.com");
        s.put("https://piston-data.mojang.com", "https://bmclapi2.bangbang93.com");
        s.put("http://resources.download.minecraft.net", "https://bmclapi2.bangbang93.com/assets");
        s.put("https://libraries.minecraft.net", "https://bmclapi2.bangbang93.com/maven");
        s.put("https://launchermeta.mojang.com/v1/products/java-runtime/2ec0cc96c44e5a76b9c8b7c39df7210883d12871/all.json", "https://bmclapi2.bangbang93.com/v1/products/java-runtime/2ec0cc96c44e5a76b9c8b7c39df7210883d12871/all.json");
        s.put("https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml", "https://bmclapi2.bangbang93.com/maven/net/minecraftforge/forge/maven-metadata.xml");
        s.put("https://files.minecraftforge.net/maven", "https://bmclapi2.bangbang93.com/maven");
        s.put("https://maven.minecraftforge.net", "https://bmclapi2.bangbang93.com/maven");
        s.put("http://dl.liteloader.com/versions/versions.json", "https://bmclapi.bangbang93.com/maven/com/mumfrey/liteloader/versions.json");
        s.put("https://authlib-injector.yushi.moe", "https://bmclapi2.bangbang93.com/mirrors/authlib-injector");
        s.put("https://meta.fabricmc.net", "https://bmclapi2.bangbang93.com/fabric-meta");
        s.put("https://maven.fabricmc.net", "https://bmclapi2.bangbang93.com/maven");
        s.forEach((k, v) -> rs.put(v, k));

        urlTypes.put("http://launchermeta.mojang.com/mc/game/version_manifest.json", AbstractDownloadTask.TaskType.ORIGINAL);
        urlTypes.put("http://launchermeta.mojang.com/mc/game/version_manifest_v2.json", AbstractDownloadTask.TaskType.ORIGINAL);
        urlTypes.put("https://launchermeta.mojang.com", AbstractDownloadTask.TaskType.ORIGINAL);
        urlTypes.put("https://launcher.mojang.com", AbstractDownloadTask.TaskType.ORIGINAL);
        urlTypes.put("https://piston-meta.mojang.com", AbstractDownloadTask.TaskType.ORIGINAL);
        urlTypes.put("https://piston-data.mojang.com", AbstractDownloadTask.TaskType.ORIGINAL);
        urlTypes.put("http://resources.download.minecraft.net", AbstractDownloadTask.TaskType.ORIGINAL);
        urlTypes.put("https://libraries.minecraft.net", AbstractDownloadTask.TaskType.ORIGINAL);
        urlTypes.put("https://launchermeta.mojang.com/v1/products/java-runtime/2ec0cc96c44e5a76b9c8b7c39df7210883d12871/all.json", AbstractDownloadTask.TaskType.ORIGINAL);
        urlTypes.put("https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml", AbstractDownloadTask.TaskType.FORGE);
        urlTypes.put("https://files.minecraftforge.net/maven", AbstractDownloadTask.TaskType.FORGE);
        urlTypes.put("https://maven.minecraftforge.net", AbstractDownloadTask.TaskType.FORGE);
        urlTypes.put("http://dl.liteloader.com/versions/versions.json", AbstractDownloadTask.TaskType.LITELOADER);
        urlTypes.put("https://authlib-injector.yushi.moe", AbstractDownloadTask.TaskType.LITELOADER);
        urlTypes.put("https://meta.fabricmc.net", AbstractDownloadTask.TaskType.FABRIC);
        urlTypes.put("https://maven.fabricmc.net", AbstractDownloadTask.TaskType.FABRIC);
    }

    public static String getVersionJsonv2WithFaster(Servers t){
        return fast("http://launchermeta.mojang.com/mc/game/version_manifest_v2.json", t);
    }
    public static String fast(String raw, Servers server){
        switch (server) {
            default:
            case MOJANG:
                return raw;
            case BMCLAPI:
                return rawFast(raw);
            case MCBBS:
                return rawFast(raw).replace("https://bmclapi2.bangbang93.com/", "https://download.mcbbs.net/");
        }
    }
    private static String rawFast(String raw){
        for (String sr : s.keySet()) {
            if (raw.contains(sr)) {
                return raw.replace(sr, s.get(sr));
            }
        }
        return raw;
    }
    public static String ReturnToOriginServer(String raw) {
        return ReturnToOriginServer(raw, AbstractDownloadTask.TaskType.ORIGINAL);
    }
    public static String ReturnToOriginServer(String raw, AbstractDownloadTask.TaskType type) {
        if (raw.contains("https://download.mcbbs.net/")) {
            raw = raw.replace("https://download.mcbbs.net/", "https://bmclapi2.bangbang93.com/");
        }
        for (Map.Entry<String, String> entry : rs.entrySet()) {
            if (raw.startsWith(entry.getKey()) && urlTypes.get(entry.getValue()) == type)
                return raw.replace(entry.getKey(), entry.getValue());
        }
        return raw;
    }
    public enum Servers {
        BMCLAPI,
        MCBBS,
        MOJANG
    }
}
