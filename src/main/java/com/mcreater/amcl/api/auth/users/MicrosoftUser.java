package com.mcreater.amcl.api.auth.users;

import com.google.gson.Gson;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.githubApi.GithubReleases;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.net.HttpClient;
import javafx.util.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MicrosoftUser extends AbstractUser {
    final Vector<MSAuth.McProfileModel.McSkinModel> skins;
    public Map<String, String> capes = new HashMap<>();
    public MicrosoftUser(String accessToken, String username, String uuid, Vector<MSAuth.McProfileModel.McSkinModel> skins, String refreshToken) {
        super(accessToken, username, uuid, refreshToken);
        this.skins = skins;
    }
    public String toString(){
        return super.toString() + "\nSkins : " + skins;
    }
    public Map<String, String> getCapes() throws Exception {
        String url = "https://api.minecraftservices.com/minecraft/profile";
        HttpClient c = HttpClient.getInstance(url);
        c.openConnection();
        c.conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        JSONObject ob = c.readJSON();
        for (Object o : ob.getJSONArray("capes")){
            JSONObject t1 = (JSONObject) o;
            capes.put(t1.getString("alias"), t1.getString("id"));
        }
        return capes;
        // https://wiki.vg/Mojang_API
    }
    public enum SkinType {
        STEVE("classic"),
        ALEX("slim");
        public final String type;
        SkinType(String typeString){
            type = typeString;
        }
    }
    public void upload(SkinType type, File path) throws Exception {
        String url = "https://api.minecraftservices.com/minecraft/profile/skins";
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Authorization", "Bearer " + accessToken);

        CloseableHttpClient client = HttpClients.createDefault();
        String respContent;

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", path);
        builder.addTextBody("variant", type.type);

        HttpEntity multipart = builder.build();

        HttpResponse resp;
        httpPost.setEntity(multipart);
        resp = client.execute(httpPost);

        if (resp.getStatusLine().getStatusCode() <= 399) {
            HttpEntity he = resp.getEntity();
            respContent = EntityUtils.toString(he, "UTF-8");
            System.out.println(respContent);
        }
        else {
            throw new Exception();
        }

    }

    public void hideCape() throws Exception {
        String url = "https://api.minecraftservices.com/minecraft/profile/capes/active";
        HttpClient client = HttpClient.getInstance(url);
        client.openConnection();
        client.conn.setRequestMethod("DELETE");
        client.conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        client.conn.connect();
        client.read();

    }
    public void showCape(String capeName) throws Exception {
        if (capes.get(capeName) == null) throw new IOException();
        Map<Object, Object> data = J8Utils.createMap("capeId", capes.get(capeName));
        String url = "https://api.minecraftservices.com/minecraft/profile/capes/active";
        HttpClient client = HttpClient.getInstance(url);
        client.openConnection();
        client.conn.setDoOutput(true);
        client.conn.setDoInput(true);
        client.conn.setRequestMethod("PUT");
        client.conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        client.conn.setRequestProperty("Content-Type", "application/json");
        client.conn.connect();
        BufferedWriter wrt2=new BufferedWriter(new OutputStreamWriter(client.conn.getOutputStream()));
        wrt2.write(new Gson().toJson(data));
        wrt2.flush();
        wrt2.close();
        client.read(false, false);
    }

    public void refresh() throws IOException, RuntimeException {
        Map<Object, Object> data = J8Utils.createMap(
                "client_id", "00000000402b5328",
                   "refresh_token", refreshToken,
                   "grant_type", "refresh_token",
                   "redirect_uri", "https://login.live.com/oauth20_desktop.srf",
                   "scope", "service::user.auth.xboxlive.com::MBI_SSL");
        HttpClient client = HttpClient.getInstance(MSAuth.authTokenUrl, data);
        client.openConnection();
        client.conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        JSONObject ob = client.readJSON();
        this.refreshToken = ob.getString("refresh_token");
        String at = ob.getString("access_token");
        MicrosoftUser newUser = new MSAuth().getUserFromToken(new Pair<>(at, refreshToken));
        this.accessToken = newUser.accessToken;
    }

    public boolean vaildate() {
        try {
            GithubReleases.trustAllHosts();
            new MSAuth().checkMcStore(accessToken);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
