package com.mcreater.amcl.api.auth.users;

import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.githubApi.GithubReleases;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.net.HttpClient;
import org.apache.commons.lang3.tuple.ImmutablePair;
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

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class MicrosoftUser extends AbstractUser {
    public final MSAuth.McProfileModel.McSkinModel skin;
    public MicrosoftUser(String accessToken, String username, String uuid, MSAuth.McProfileModel.McSkinModel skin, String refreshToken) {
        super(accessToken, username, uuid, refreshToken);
        this.skin = skin;
    }
    public String toString(){
        return super.toString() + "\nSkins : " + skin;
    }
    public Map<String, ImmutablePair<String, Boolean>> getCapes() throws Exception {
        Map<String, ImmutablePair<String, Boolean>> capes = new HashMap<>();

        String url = "https://api.minecraftservices.com/minecraft/profile";
        HttpClient c = HttpClient.getInstance(url);
        c.openConnection();
        c.conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        JSONObject ob = c.readJSON();
        for (Object o : ob.getJSONArray("capes")){
            JSONObject t1 = (JSONObject) o;
            capes.put(t1.getString("alias"), new ImmutablePair<>(t1.getString("id"), t1.getString("state").equals("ACTIVE")));
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
    public void showCape(String capeID) throws Exception {
        Map<Object, Object> data = J8Utils.createMap("capeId", capeID);
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
        wrt2.write(GSON_PARSER.toJson(data));
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
        HttpClient client = HttpClient.getInstance(MSAuth.AUTH_TOKEN_URL, data);
        client.openConnection();
        client.conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        JSONObject ob = client.readJSON();
        this.refreshToken = ob.getString("refresh_token");
        String at = ob.getString("access_token");
        MicrosoftUser newUser = MSAuth.AUTH_INSTANCE.getUserFromToken(new ImmutablePair<>(at, refreshToken));
        this.accessToken = newUser.accessToken;
    }

    public boolean vaildate() {
        try {
            GithubReleases.trustAllHosts();
            MSAuth.AUTH_INSTANCE.checkMcStore(accessToken);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
