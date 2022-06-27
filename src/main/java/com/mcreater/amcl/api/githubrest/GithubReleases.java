package com.mcreater.amcl.api.githubrest;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.mcreater.amcl.api.githubrest.models.ReleaseModel;
import com.mcreater.amcl.util.Vars;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        re += temp + "\n";
                    }
                }
            }
            Vector<ReleaseModel> models = new Vector<>();
            Gson g = new Gson();
            Vector<LinkedTreeMap<?, ?>> res = g.fromJson(re, Vector.class);
            for (Object o : res){
                ReleaseModel model = g.fromJson(g.toJson(o), ReleaseModel.class);
                models.add(model);
            }
            return models;
        }
        catch (IOException e){
            return new Vector<>();
        }
    }
    public static int getVersionsBehind(){
        Vector<ReleaseModel> result = GithubReleases.getReleases();
        int i = -1;
        for (int index = 0;index < result.size();index++){
            if (Objects.equals(result.get(index).tag_name, Vars.launcher_version)){
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
