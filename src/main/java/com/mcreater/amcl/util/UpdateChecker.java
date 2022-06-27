package com.mcreater.amcl.util;

import cc.carm.lib.githubreleases4j.GithubAsset;
import cc.carm.lib.githubreleases4j.GithubRelease;
import cc.carm.lib.githubreleases4j.GithubReleases4J;
import com.google.gson.internal.LinkedTreeMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UpdateChecker {
    static {
        trustAllHosts();
    }
    public static Map<String, Map<String, String>> getVersions(){
        List<GithubRelease> releases = GithubReleases4J.listReleases("Jack253-png", "AMCL");
        Map<String, Map<String, String>> s = new LinkedTreeMap<>();
        for (GithubRelease r : releases) {
            Map<String, String> temp = new LinkedTreeMap<>();
            temp.put("exe", null);
            temp.put("jar", null);
            List<GithubAsset> assets = r.getAssets();
            for (GithubAsset asset : assets) {
                temp.put(get(asset.getBrowserDownloadURL()), asset.getBrowserDownloadURL());
            }
            s.put(r.getName(), temp);
        }
        return s;
    }
    public static List<GithubRelease> getVersionsList(){
        return GithubReleases4J.listReleases("Jack253-png", "AMCL");
    }
    public static boolean outDated(){
        return !Objects.equals(GithubReleases4J.getVersionBehind("Jack253-png", "AMCL", Vars.launcher_version), 0) && !isDevelop();
    }
    public static boolean isDevelop(){
        return Objects.requireNonNull(GithubReleases4J.getVersionBehind("Jack253-png", "AMCL", Vars.launcher_version)) < 0;
    }
    private static Integer getVersionBehind(){
        return GithubReleases4J.getVersionBehind("Jack253-png", "AMCL", Vars.launcher_version);
    }
    private static String get(String url){
        List<String> s = List.of(url.split("\\."));
        return s.get(s.size() - 1);
    }
    private static void trustAllHosts() {
        final String TAG = "trustAllHosts";
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[] {};
                }
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }
            }
        };
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
