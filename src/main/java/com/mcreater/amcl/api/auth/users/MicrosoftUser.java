package com.mcreater.amcl.api.auth.users;

import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.net.HttpClient;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class MicrosoftUser extends AbstractUser {
    public MicrosoftUser(String accessToken, String username, String uuid, String refreshToken) {
        super(accessToken, username, uuid, refreshToken);
    }

    public List<MSAuth.McProfileModel.McCapeModel> getCapes() throws Exception {
        MSAuth.UserProfileModel ob = HttpClient.getInstance(MSAuth.MC_PROFILE_API_URL)
                .open()
                .header("Authorization", "Bearer " + accessToken)
                .toJson(MSAuth.UserProfileModel.class);
        return ob.capes
                .stream()
                .map(userContentModel -> {
                    MSAuth.McProfileModel.McCapeModel model = new MSAuth.McProfileModel.McCapeModel();
                    model.url = userContentModel.url;
                    model.id = userContentModel.id;
                    model.alias = userContentModel.alias;
                    model.state = userContentModel.state.equals("ACTIVE");
                    return model;
                })
                .collect(Collectors.toList());
        // https://wiki.vg/Mojang_API
    }

    public List<MSAuth.McProfileModel.McSkinModel> getSkins() throws Exception {
        MSAuth.UserProfileModel obj = HttpClient.getInstance(MSAuth.MC_PROFILE_API_URL)
                .open()
                .header("Authorization", "Bearer " + accessToken)
                .toJson(MSAuth.UserProfileModel.class);
        return obj.skins
                .stream()
                .map(userContentModel -> {
                    MSAuth.McProfileModel.McSkinModel model = new MSAuth.McProfileModel.McSkinModel();
                    model.variant = userContentModel.variant;
                    model.id = userContentModel.id;
                    model.state = userContentModel.state;
                    model.url = userContentModel.url;
                    model.isSlim = userContentModel.variant.equals("SLIM");
                    return model;
                })
                .collect(Collectors.toList());
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

    public static class NameChangeCheckModel {
        public String changedAt;
        public String createdAt;
        public boolean nameChangeAllowed;
    }

    public void upload(SkinType type, File path) throws Exception {
        HttpPost httpPost = new HttpPost(MSAuth.MC_SKIN_MODIFY_URL);
        httpPost.addHeader("Authorization", "Bearer " + accessToken);

        String respContent;

        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .addBinaryBody("file", path)
                .addTextBody("variant", type.type);

        HttpEntity multipart = builder.build();

        httpPost.setEntity(multipart);
        HttpResponse resp = HttpClients.createDefault().execute(httpPost);

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
        MSAuth.NameStateModel object = HttpClient.getInstance(String.format(MSAuth.MC_NAME_CHECK_URL, name))
                .open()
                .method(HttpClient.Method.GET)
                .header("Authorization", "Bearer " + accessToken)
                .toJson(MSAuth.NameStateModel.class);
        return object.status == null ? NameState.NOT_ALLOWED : NameState.valueOf(object.status);
    }

    public void changeName(String name) throws Exception {
        MSAuth.UserProfileModel model = HttpClient.getInstance(String.format(MSAuth.MC_NAME_CHANGE_URL, name))
                .open()
                .method(HttpClient.Method.PUT)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Length", String.valueOf(name.length()))
                .toJson(MSAuth.UserProfileModel.class);

        username = name;
    }

    public NameChangeCheckModel checkName() throws Exception {
        return HttpClient.getInstance(MSAuth.MC_NAME_CHANGE_CHECK_URL)
                .open()
                .header("Authorization", "Bearer " + accessToken)
                .toJson(NameChangeCheckModel.class);
    }

    public void hideCape() throws Exception {
        HttpClient.getInstance(MSAuth.MC_CAPE_MODIFY_URL)
                .open()
                .method(HttpClient.Method.DELETE)
                .header("Authorization", "Bearer " + accessToken)
                .read();
    }

    public void showCape(MSAuth.McProfileModel.McCapeModel model) throws Exception {
        MSAuth.UserProfileModel model2 = HttpClient.getInstance(MSAuth.MC_CAPE_MODIFY_URL)
                .open()
                .method(HttpClient.Method.PUT)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .writeJson(J8Utils.createMap("capeId", model.id))
                .toJson(MSAuth.UserProfileModel.class);
    }

    public void refresh() throws Exception {
        MSAuth.TokenResponse ob = HttpClient.getInstance(MSAuth.TOKEN_URL)
                .open()
                .method(HttpClient.Method.POST)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .write(J8Utils.createMap(
                        "client_id", MSAuth.CLIENT_ID,
                        "refresh_token", refreshToken,
                        "grant_type", "refresh_token"
                ))
                .toJson(MSAuth.TokenResponse.class);
        MicrosoftUser newUser = MSAuth.AUTH_INSTANCE.getUserFromToken(new ImmutablePair<>("d=" + ob.access_token, refreshToken));
        this.refreshToken = ob.refresh_token;
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
