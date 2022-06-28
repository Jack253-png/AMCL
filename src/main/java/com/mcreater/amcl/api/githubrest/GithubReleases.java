package com.mcreater.amcl.api.githubrest;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.mcreater.amcl.api.githubrest.models.AssetsModel;
import com.mcreater.amcl.api.githubrest.models.ReleaseModel;
import com.mcreater.amcl.util.Vars;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.Vector;

public class GithubReleases {
    static {
        trustAllHosts();
    }
    private static String api_url = "https://api.github.com";
    public static Vector<ReleaseModel> getReleases(){
        try{
            String url = String.format("%s/repos/Jack253-png/AMCL/releases", api_url);
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.connect();
            String re = "";
            boolean t = conn.getResponseCode() == 200;
            if (t) {
                InputStream is = conn.getInputStream();
                if (null != is) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        re += temp + "\n";
                    }
                }
            }
            else{
                throw new IllegalStateException();
            }
            Gson g = new Gson();
            Vector<LinkedTreeMap<?, ?>> res = g.fromJson(re, Vector.class);
            Vector<ReleaseModel> releases = new Vector<>();
            for (Object o : res){
                releases.add(g.fromJson(g.toJson(o), ReleaseModel.class));
            }
            int h = getVersionsBehind(releases, Vars.launcher_version);
            for (ReleaseModel model : releases){
                int i = -1;
                for (int index = 0;index < releases.size();index++){
                    if (Objects.equals(releases.get(index).tag_name, model.tag_name)){
                        i = index;
                    }
                }
                model.outdated = !(i <= h - 1);
                model.iscurrent = i == h;
            }
            return releases;
        }
        catch (IOException e) {
            return new Vector<>();
        }
    }
    public static int getVersionsBehind(){
        return getVersionsBehind(GithubReleases.getReleases(), Vars.launcher_version);
    }
    public static int getVersionsBehind(Vector<ReleaseModel> result, String node_name){
        int i = -1;
        for (int index = 0;index < result.size();index++){
            if (Objects.equals(result.get(index).tag_name, node_name)){
                i = index;
            }
        }
        return i;
    }
    public static boolean outDated() {
        return (getVersionsBehind() != 0) && !isDevelop();
    }
    public static boolean isDevelop() {
        return getVersionsBehind() < 0;
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
