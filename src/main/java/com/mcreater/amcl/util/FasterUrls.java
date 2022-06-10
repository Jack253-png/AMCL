package com.mcreater.amcl.util;

import com.google.gson.internal.LinkedTreeMap;

import java.util.Map;

public class FasterUrls {
    public static Map<String, String> s = new LinkedTreeMap<>();
    public static Map<String, String> rs = new LinkedTreeMap<>();
    static {
        s.put("http://launchermeta.mojang.com/mc/game/version_manifest.json", "https://bmclapi2.bangbang93.com/mc/game/version_manifest.json");
        s.put("http://launchermeta.mojang.com/mc/game/version_manifest_v2.json", "https://bmclapi2.bangbang93.com/mc/game/version_manifest_v2.json");
        s.put("https://launchermeta.mojang.com", "https://bmclapi2.bangbang93.com");
        s.put("https://launcher.mojang.com", "https://bmclapi2.bangbang93.com");
        s.put("http://resources.download.minecraft.net", "https://bmclapi2.bangbang93.com/assets");
        s.put("https://libraries.minecraft.net", "https://bmclapi2.bangbang93.com/maven");
        s.put("https://launchermeta.mojang.com/v1/products/java-runtime/2ec0cc96c44e5a76b9c8b7c39df7210883d12871/all.json", "https://bmclapi2.bangbang93.com/v1/products/java-runtime/2ec0cc96c44e5a76b9c8b7c39df7210883d12871/all.json");
        s.put("https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml", "https://bmclapi2.bangbang93.com/maven/net/minecraftforge/forge/maven-metadata.xml");
        s.put("https://files.minecraftforge.net/maven", "https://bmclapi2.bangbang93.com/maven");
//        s.put("https://maven.minecraftforge.net/", "https://bmclapi2.bangbang93.com/maven");
        s.put("http://dl.liteloader.com/versions/versions.json", "https://bmclapi.bangbang93.com/maven/com/mumfrey/liteloader/versions.json");
        s.put("https://authlib-injector.yushi.moe", "https://bmclapi2.bangbang93.com/mirrors/authlib-injector");
        s.put("https://meta.fabricmc.net", "https://bmclapi2.bangbang93.com/fabric-meta");
        s.put("https://maven.fabricmc.net", "https://bmclapi2.bangbang93.com/maven");
//        s.replaceAll((k, v) -> s.get(k).replace("https://bmclapi2.bangbang93.com", "https://download.mcbbs.net"));
        for (String s1 : s.keySet()){
            rs.put(s.get(s1), s1);
        }
    }
    public static String getUrl(String url, boolean t){
        if (t) {
            if (s.get(url) == null) {
                return url;
            } else {
                return s.get(url);
            }
        }
        else{
            return url;
        }
    }
    public static String getVersionJsonWithFaster(boolean t){
        return getUrl("http://launchermeta.mojang.com/mc/game/version_manifest.json", t);
    }
    public static String getLibsWithFaster(boolean t){
        return getUrl("https://libraries.minecraft.net", t);
    }
    public static String fast(String raw, boolean t){
        if (!t){
            return raw;
        }
        else{
            for (String sr : s.keySet()){
                if (raw.contains(sr)){
                    return raw.replace(sr, s.get(sr));
                }
            }
            return raw;
        }
    }
    public static String rev(String raw){
        for (String sr : rs.keySet()){
            if (raw.contains(sr)){
                return raw.replace(sr, rs.get(sr));
            }
        }
        return raw;
    }
}
