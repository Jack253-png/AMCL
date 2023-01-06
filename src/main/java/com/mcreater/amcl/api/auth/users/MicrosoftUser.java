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
import java.util.Map;
import java.util.Vector;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class MicrosoftUser extends AbstractUser {
    public MicrosoftUser(String accessToken, String username, String uuid, String refreshToken) {
        super(accessToken, username, uuid, refreshToken);
    }
    public Vector<MSAuth.McProfileModel.McCapeModel> getCapes() throws Exception {
        Vector<MSAuth.McProfileModel.McCapeModel> capes = new Vector<>();
        HttpClient c = HttpClient.getInstance(MSAuth.MC_PROFILE_API_URL);
        c.openConnection();
        c.conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        JSONObject ob = c.readJSON();
        for (Object o : ob.getJSONArray("capes")){
            if (o instanceof JSONObject) {
                JSONObject t1 = (JSONObject) o;
                MSAuth.McProfileModel.McCapeModel model = new MSAuth.McProfileModel.McCapeModel();
                model.url = t1.getString("url");
                model.id = t1.getString("id");
                model.alias = t1.getString("alias");
                model.state = t1.getString("state").equals("ACTIVE");
                capes.add(model);
            }
        }
        return capes;
        // https://wiki.vg/Mojang_API
    }
    public Vector<MSAuth.McProfileModel.McSkinModel> getSkins() throws IOException {
        HttpClient c = HttpClient.getInstance(MSAuth.MC_PROFILE_API_URL);
        c.openConnection();
        c.conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        JSONObject obj = c.readJSON();
        Vector<MSAuth.McProfileModel.McSkinModel> result = new Vector<>();
        for (Object ob : obj.getJSONArray("skins")) {
            if (ob instanceof JSONObject) {
                JSONObject r = (JSONObject) ob;
                MSAuth.McProfileModel.McSkinModel model = new MSAuth.McProfileModel.McSkinModel();
                model.variant = r.getString("variant");
                model.id = r.getString("id");
                model.state = r.getString("state");
                model.url = r.getString("url");
                model.isSlim = r.getString("variant").equals("SLIM");
                result.add(model);
            }
        }
        return result;
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
        HttpPost httpPost = new HttpPost(MSAuth.MC_SKIN_MODIFY_URL);
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
        }
        else {
            throw new Exception();
        }
    }

    public void hideCape() throws Exception {
        HttpClient client = HttpClient.getInstance(MSAuth.MC_CAPE_MODIFY_URL);
        client.openConnection();
        client.conn.setRequestMethod("DELETE");
        client.conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        client.conn.connect();
        client.read();
    }
    public void showCape(MSAuth.McProfileModel.McCapeModel model) throws Exception {
        Map<Object, Object> data = J8Utils.createMap("capeId", model.id);
        HttpClient client = HttpClient.getInstance(MSAuth.MC_CAPE_MODIFY_URL);
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
                "client_id", MSAuth.CLIENT_ID,
                   "refresh_token", refreshToken,
                   "grant_type", "refresh_token"
        );

        HttpClient client = HttpClient.getInstance(MSAuth.TOKEN_URL);
        client.openConnection();
        client.conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        client.conn.setRequestMethod("POST");
        client.conn.setDoInput(true);
        client.conn.setDoOutput(true);
        client.write(data);

        JSONObject ob = client.readJSON();
        this.refreshToken = ob.getString("refresh_token");
        String at = ob.getString("access_token");
        MicrosoftUser newUser = MSAuth.AUTH_INSTANCE.getUserFromToken(new ImmutablePair<>("d=" + at, refreshToken));
        this.accessToken = newUser.accessToken;
    }

    public boolean validate() {
        try {
            MSAuth.AUTH_INSTANCE.checkMcStore(accessToken);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
