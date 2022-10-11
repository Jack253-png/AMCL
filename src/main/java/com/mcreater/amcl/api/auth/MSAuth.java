package com.mcreater.amcl.api.auth;

import com.google.gson.Gson;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.api.githubApi.GithubReleases;
import com.mcreater.amcl.pages.dialogs.commons.ProcessDialog;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.concurrent.ValueSet3;
import com.mcreater.amcl.util.net.HttpClient;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

public class MSAuth implements AbstractAuth<MicrosoftUser>{
    public static final MSAuth AUTH_INSTANCE = new MSAuth();
    public static final String LOGIN_URL = "https://login.live.com/oauth20_authorize.srf?client_id=00000000402b5328&response_type=code&scope=service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL&redirect_uri=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf";
    public static final String REDIRECT_URL_SUFFIX = "https://login.live.com/oauth20_desktop.srf?code=";
    public static final String AUTH_TOKEN_URL = "https://login.live.com/oauth20_token.srf";
    private static final String XBL_AUTH_URL = "https://user.auth.xboxlive.com/user/authenticate";
    private static final String XSTS_AUTH_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";
    private static final String MC_LOGIN_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private static final String MC_STORE_URL = "https://api.minecraftservices.com/entitlements/mcstore";
    private static final String MC_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";
    ProcessDialog dialog;
    public void bindDialog(ProcessDialog dialog){
        this.dialog = dialog;
    }
    private MSAuth() {}
    public ImmutablePair<String, String> acquireAccessToken(String authcode) {
        GithubReleases.trustAllHosts();
        Map<Object, Object> data = new HashMap<>();
        data.put("client_id", "00000000402b5328");
        data.put("code", authcode);
        data.put("grant_type", "authorization_code");
        data.put("redirect_uri", "https://login.live.com/oauth20_desktop.srf");
        data.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");
        try {
            HttpClient client = HttpClient.getInstance(AUTH_TOKEN_URL, data);
            client.openConnection();
            client.conn.setConnectTimeout(5000);
            client.conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            JSONObject ob = client.readJSON();
            return new ImmutablePair<>(ob.getString("access_token"), ob.getString("refresh_token"));
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // first : Token
    // second : uhs
    public ImmutablePair<String, String> acquireXBLToken(String accessToken) {
        try {
            Map<Object, Object> data = J8Utils.createMap(
                    "Properties", J8Utils.createMap(
                            "AuthMethod", "RPS",
                            "SiteName", "user.auth.xboxlive.com",
                            "RpsTicket", accessToken
                    ),
                    "RelyingParty", "http://auth.xboxlive.com",
                    "TokenType", "JWT"
            );
            HttpClient client = HttpClient.getInstance(XBL_AUTH_URL);
            client.openConnection();
            client.conn.setDoInput(true);
            client.conn.setDoOutput(true);
            client.conn.setRequestMethod("POST");
            client.conn.setRequestProperty("Content-Type","application/json");
            client.conn.connect();
            BufferedWriter wrt2=new BufferedWriter(new OutputStreamWriter(client.conn.getOutputStream()));
            wrt2.write(new Gson().toJson(data));
            wrt2.flush();
            wrt2.close();
            JSONObject ob = client.readJSON(false);
            String token = ob.getString("Token");
            String uhs = "";
            for (Object o : ob.getJSONObject("DisplayClaims").getJSONArray("xui")){
                uhs = ((JSONObject) o).getString("uhs");
            }
            return new ImmutablePair<>(token, uhs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ImmutablePair<String, String> acquireXsts(String xblToken) {
        try {

            Map<Object, Object> data = J8Utils.createMap(
                    "Properties", J8Utils.createMap(
                            "SandboxId", "RETAIL",
                            "UserTokens", J8Utils.createList(xblToken)
                    ),
                    "RelyingParty", "rp://api.minecraftservices.com/",
                    "TokenType", "JWT"
            );

            HttpClient client = HttpClient.getInstance(XSTS_AUTH_URL);
            client.openConnection();
            client.conn.setDoInput(true);
            client.conn.setDoOutput(true);
            client.conn.setRequestMethod("POST");
            client.conn.setRequestProperty("Content-Type","application/json");
            client.conn.setRequestProperty("Accept", "application/json");
            client.conn.connect();
            BufferedWriter wrt2=new BufferedWriter(new OutputStreamWriter(client.conn.getOutputStream()));
            wrt2.write(new Gson().toJson(data));
            wrt2.flush();
            wrt2.close();
            JSONObject ob = client.readJSON(false);
            String uhs = "";
            for (Object o : ob.getJSONObject("DisplayClaims").getJSONArray("xui")){
                uhs = ((JSONObject) o).getString("uhs");
            }
            return new ImmutablePair<>(ob.getString("Token"), uhs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ValueSet3<String, ImmutablePair<String, String>, Vector<McProfileModel.McSkinModel>> acquireMinecraftToken(String xblUhs, String xblXsts) {
        try {
            Map<Object, Object> data = J8Utils.createMap(
                    "identityToken", "XBL3.0 x=" + xblUhs + ";" + xblXsts
            );
            HttpClient client = HttpClient.getInstance(MC_LOGIN_URL);
            client.openConnection();
            client.conn.setDoInput(true);
            client.conn.setDoOutput(true);
            client.conn.setRequestMethod("POST");
            client.conn.setRequestProperty("Content-Type","application/json");
            client.conn.setRequestProperty("Accept", "application/json");
            client.conn.connect();
            BufferedWriter wrt2=new BufferedWriter(new OutputStreamWriter(client.conn.getOutputStream()));
            wrt2.write(new Gson().toJson(data));
            wrt2.flush();
            wrt2.close();
            JSONObject ob = client.readJSON(false);
            String accessToken = ob.getString("access_token");
            McProfileModel contents = checkMcProfile(accessToken);
            if (contents.checkProfile() && checkMcStore(accessToken)){
                return new ValueSet3<>(accessToken, new ImmutablePair<>(contents.name, contents.id), contents.skins);
            }
            else {
                throw new IOException("This user didn't had minecraft");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkMcStore(String mcAccessToken) {
        try {
            HttpClient client = HttpClient.getInstance(MC_STORE_URL);
            client.openConnection();
            client.conn.setRequestProperty("Authorization", String.format("Bearer %s", mcAccessToken));
            JSONObject ob = client.readJSON();
            boolean has_product = false;
            boolean has_game = false;
            for (Object o : ob.getJSONArray("items")){
                if (Objects.equals(((JSONObject) o).getString("name"), "game_minecraft")){
                    has_game = true;
                }
                else if (Objects.equals(((JSONObject) o).getString("name"), "product_minecraft")){
                    has_product = true;
                }
            }
            return has_game && has_product;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public McProfileModel checkMcProfile(String mcAccessToken) {
        try {
            HttpClient client = HttpClient.getInstance(MC_PROFILE_URL);
            client.openConnection();
            client.conn.setRequestProperty("Authorization", String.format("Bearer %s", mcAccessToken));
            McProfileModel model = new Gson().fromJson(client.read(), McProfileModel.class);
            return model;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void setV(int index, int value, String message){
        if (dialog != null) dialog.setV(index, value, message);
    }


    public MicrosoftUser getUser(String... args) throws RuntimeException {
        ImmutablePair<String, String> token = acquireAccessToken(args[0]);
        setV(0, 20, Launcher.languageManager.get("ui.msauth._02"));
        return getUserFromToken(token);
    }
    public MicrosoftUser getUserFromToken(ImmutablePair<String, String> token) throws RuntimeException {

        ImmutablePair<String, String> xbl_token = acquireXBLToken(token.getKey());
        setV(0, 40, Launcher.languageManager.get("ui.msauth._03"));
        ImmutablePair<String, String> xsts = acquireXsts(xbl_token.getKey());
        setV(0, 60, Launcher.languageManager.get("ui.msauth._04"));
        ValueSet3<String, ImmutablePair<String, String>, Vector<McProfileModel.McSkinModel>> content = acquireMinecraftToken(xbl_token.getValue(), xsts.getKey());
        setV(0, 80, Launcher.languageManager.get("ui.msauth._05"));
        MicrosoftUser msu = new MicrosoftUser(content.getValue1(), content.getValue2().getKey(), content.getValue2().getValue(), content.getValue3(), token.getValue());
        setV(0, 80, Launcher.languageManager.get("ui.msauth._06"));
        return msu;
    }
    public static class McProfileModel {
        public String id;
        public String name;
        public Vector<McSkinModel> skins;
        public boolean checkProfile(){
            return id != null && name != null;
        }
        public static class McSkinModel {
            public String id;
            public String state;
            public String url;
            public String variant;
            public String cape;
            public boolean isSlim;
            public String toString(){
                return url;
            }
        }
    }
    public static McProfileModel getUserSkinFromName(String name) throws Exception {
        String url = String.format("https://api.mojang.com/users/profiles/minecraft/%s", name.toLowerCase());
        HttpClient client = HttpClient.getInstance(url);
        client.openConnection();
        McProfileModel model = new Gson().fromJson(client.read(), McProfileModel.class);
        return getUserSkin(model.id);
    }
    public static String getUserUUID(String name) throws Exception {
        String url = String.format("https://api.mojang.com/users/profiles/minecraft/%s", name.toLowerCase());
        HttpClient client = HttpClient.getInstance(url);
        client.openConnection();
        McProfileModel model = new Gson().fromJson(client.read(), McProfileModel.class);
        return model.id;
    }
    public static McProfileModel getUserSkin(String uuid) throws Exception {
        String skinUrl = String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s", uuid);
        HttpClient skin = HttpClient.getInstance(skinUrl);
        skin.openConnection();
        String value = null;
        JSONObject objj;
        try {
            objj = skin.readJSON();
        } catch (JSONException e) {
            throw new IOException(e);
        }
        for (Object o : objj.getJSONArray("properties")) {
            JSONObject ob = (JSONObject) o;
            if (Objects.equals(ob.getString("name"), "textures")) {
                value = ob.getString("value");
                break;
            }
        }
        JSONObject obj = new JSONObject(new String(Base64.getDecoder().decode(value)));
        McProfileModel.McSkinModel model1 = new McProfileModel.McSkinModel();
        try {
            model1.url = obj.getJSONObject("textures").getJSONObject("SKIN").getString("url");
        }
        catch (Exception ignored){
            model1.url = "https://";
        }
        try {
            model1.cape = obj.getJSONObject("textures").getJSONObject("CAPE").getString("url");
        }
        catch (Exception ignored){
            model1.cape = "https://";
        }
        try {
            obj.getJSONObject("textures").getJSONObject("SKIN").getJSONObject("metadata").getString("model");
            model1.isSlim = true;
        }
        catch (Exception ignored){
            model1.isSlim = false;
        }
        McProfileModel model = new Gson().fromJson(objj.toString(), McProfileModel.class);
        model.skins = new Vector<>();
        model.skins.add(model1);
        return model;
    }
}
