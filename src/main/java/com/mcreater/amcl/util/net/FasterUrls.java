package com.mcreater.amcl.util.net;

import com.google.gson.internal.LinkedTreeMap;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.BiConsumer;

public class FasterUrls {
    public static Map<String, String> s = new LinkedTreeMap<>();
    public static Map<String, String> rs = new LinkedTreeMap<>();
    public static Vector<String> vaild_servers = new Vector<>(List.of("MOJANG", "BMCLAPI", "MCBBS"));
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
    public static String ReturnToOriginServer(String raw){
        for (String sr : rs.keySet()){
            if (raw.replace("https://download.mcbbs.net/", "https://bmclapi2.bangbang93.com/").contains(sr)){
                return raw.replace("https://download.mcbbs.net/", "https://bmclapi2.bangbang93.com/").replace(sr, rs.get(sr));
            }
        }
        return raw;
    }
    public enum Servers {
        BMCLAPI,
        MCBBS,
        MOJANG
    }
}
