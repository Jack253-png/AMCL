package com.mcreater.amcl.api.auth.users;

import com.mcreater.amcl.api.auth.MSAuth;
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

import java.io.File;
import java.util.Vector;

public class MicrosoftUser extends AbstractUser {
    public MicrosoftUser(String accessToken, String username, String uuid, String refreshToken) {
        super(accessToken, username, uuid, refreshToken);
    }

    public Vector<MSAuth.McProfileModel.McCapeModel> getCapes() throws Exception {
        Vector<MSAuth.McProfileModel.McCapeModel> capes = new Vector<>();
        JSONObject ob = HttpClient.getInstance(MSAuth.MC_PROFILE_API_URL)
                .open()
                .header("Authorization", "Bearer " + accessToken)
                .readJSON();
        for (Object o : ob.getJSONArray("capes")) {
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

    public Vector<MSAuth.McProfileModel.McSkinModel> getSkins() throws Exception {
        JSONObject obj = HttpClient.getInstance(MSAuth.MC_PROFILE_API_URL)
                .open()
                .header("Authorization", "Bearer " + accessToken)
                .readJSON();
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

        SkinType(String typeString) {
            type = typeString;
        }
    }

    public enum NameState {
        DUPLICATE,
        AVAILABLE,
        NOT_ALLOWED
    }

    public void upload(SkinType type, File path) throws Exception {
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
        } else {
            throw new Exception();
        }
    }

    public void resetSkin() throws Exception {
        HttpClient.getInstance(MSAuth.MC_SKIN_RESET_URL)
                .open()
                .method(HttpClient.Method.DELETE)
                .header("Authorization", "Bearer " + accessToken)
                .read();
    }

    public NameState nameAvailable(String name) throws Exception {
        JSONObject object = HttpClient.getInstance(String.format(MSAuth.MC_NAME_CHECK_URL, name))
                .open()
                .method(HttpClient.Method.GET)
                .header("Authorization", "Bearer " + accessToken)
                .readJSON();
        return object.getString("status") == null ? NameState.NOT_ALLOWED : NameState.valueOf(object.getString("status"));
    }

    public void changeName(String name) throws Exception {
        HttpClient.getInstance(String.format(MSAuth.MC_NAME_CHANGE_URL, name))
                .open()
                .method(HttpClient.Method.PUT)
                .header("Authorization", "Bearer " + accessToken)
                .read();
        username = name;
    }

    public void hideCape() throws Exception {
        HttpClient.getInstance(MSAuth.MC_CAPE_MODIFY_URL)
                .open()
                .method(HttpClient.Method.DELETE)
                .header("Authorization", "Bearer " + accessToken)
                .read();
    }

    public void showCape(MSAuth.McProfileModel.McCapeModel model) throws Exception {
        HttpClient.getInstance(MSAuth.MC_CAPE_MODIFY_URL)
                .open()
                .method(HttpClient.Method.PUT)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .writeJson(J8Utils.createMap("capeId", model.id))
                .read();
    }

    public void refresh() throws Exception {
        JSONObject ob = HttpClient.getInstance(MSAuth.TOKEN_URL)
                .open()
                .method(HttpClient.Method.POST)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .write(J8Utils.createMap(
                        "client_id", MSAuth.CLIENT_ID,
                        "refresh_token", refreshToken,
                        "grant_type", "refresh_token"
                ))
                .readJSON();
        this.refreshToken = ob.getString("refresh_token");
        String at = ob.getString("access_token");
        MicrosoftUser newUser = MSAuth.AUTH_INSTANCE.getUserFromToken(new ImmutablePair<>("d=" + at, refreshToken));
        this.accessToken = newUser.accessToken;
    }

    public boolean validate() {
        try {
            MSAuth.AUTH_INSTANCE.checkMcStore(accessToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
