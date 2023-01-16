package com.mcreater.amcl.api.auth;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.concurrent.Sleeper;
import com.mcreater.amcl.util.net.HttpClient;
import com.mcreater.amcl.util.os.SystemActions;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

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

    public static final String MC_CAPE_MODIFY_URL = "https://api.minecraftservices.com/minecraft/profile/capes/active";
    public static final String MC_PROFILE_API_URL = "https://api.minecraftservices.com/minecraft/profile";
    public static final String MC_SKIN_MODIFY_URL = "https://api.minecraftservices.com/minecraft/profile/skins";
    public static final String MC_SKIN_RESET_URL = "https://api.minecraftservices.com/minecraft/profile/skins/active";

    public static final String MC_NAME_CHECK_URL = "https://api.minecraftservices.com/minecraft/profile/name/%s/available";
    public static final String MC_NAME_CHANGE_URL = "https://api.minecraftservices.com/minecraft/profile/name/%s";

    private static final String SCOPE = "XboxLive.signin offline_access";
    private static final String DEVICE_CODE_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode";
    public static final String TOKEN_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token";
    public static final String CLIENT_ID = "1a969022-f24f-4492-a91c-6f4a6fcb373c";
    public static class DeviceCodeModel {
        public String user_code;
        public String device_code;
        public String verification_uri;
        public int expires_in;
        public int interval;
    }
    public MicrosoftUser generateDeviceCode(BiConsumer<String, String> regIs) throws Exception {
        DeviceCodeModel model = HttpClient.getInstance(DEVICE_CODE_URL, J8Utils.createMap(
                "client_id", CLIENT_ID,
                "scope", SCOPE
        ))
                .openConnection()
                .toJson(DeviceCodeModel.class);

        regIs.accept(model.user_code, model.verification_uri);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = new StringSelection(model.user_code);
        clipboard.setContents(t, (clipboard1, transferable) -> {});
        SystemActions.openBrowser(model.verification_uri);

        long startTime = System.nanoTime();
        int interval = model.interval;

        while (true) {
            Sleeper.sleep(Math.max(interval, 1));

            // We stop waiting if user does not respond our authentication request in 15 minutes.
            long estimatedTime = System.nanoTime() - startTime;
            if (TimeUnit.SECONDS.convert(estimatedTime, TimeUnit.NANOSECONDS) >= Math.min(model.expires_in, 900)) {
                throw new IOException("timed out");
            }

            try {
                JSONObject object = HttpClient.getInstance(TOKEN_URL)
                        .openConnection()
                        .setRequestMethod(HttpClient.Method.POST)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .write(J8Utils.createMap(
                                "grant_type", "urn:ietf:params:oauth:grant-type:device_code",
                                "client_id", CLIENT_ID,
                                "code", model.device_code
                        ))
                        .readJSON();
                try {
                    return getUserFromToken(new ImmutablePair<>("d=" + object.getString("access_token"), object.getString("refresh_token")));
                }
                catch (Exception e) {
                    throw e;
                }
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public BiConsumer<Integer, String> updater = (value, mess) -> {};
    public void setUpdater(@NotNull BiConsumer<Integer, String> updater) {
        this.updater = updater;
    }

    private MSAuth() {}
    public ImmutablePair<String, String> acquireAccessToken(String authcode) {
        try {
            JSONObject ob = HttpClient.getInstance(AUTH_TOKEN_URL, J8Utils.createMap(
                            "client_id", "00000000402b5328",
                            "code", authcode,
                            "grant_type", "authorization_code",
                            "redirect_uri", "https://login.live.com/oauth20_desktop.srf",
                            "scope", "service::user.auth.xboxlive.com::MBI_SSL"
                    ))
                    .openConnection()
                    .timeout(500)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .readJSON();
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
            JSONObject ob = HttpClient.getInstance(XBL_AUTH_URL)
                    .openConnection()
                    .setRequestMethod(HttpClient.Method.POST)
                    .header("Content-Type","application/json")
                    .writeJson(J8Utils.createMap(
                            "Properties", J8Utils.createMap(
                                    "AuthMethod", "RPS",
                                    "SiteName", "user.auth.xboxlive.com",
                                    "RpsTicket", accessToken
                            ),
                            "RelyingParty", "http://auth.xboxlive.com",
                            "TokenType", "JWT"
                    ))
                    .readJSON();
            String token = ob.getString("Token");
            String uhs = "";
            for (Object o : ob.getJSONObject("DisplayClaims").getJSONArray("xui")){
                uhs = ((JSONObject) o).getString("uhs");
            }
            return new ImmutablePair<>(token, uhs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ImmutablePair<String, String> acquireXsts(String xblToken) {
        try {
            JSONObject ob = HttpClient.getInstance(XSTS_AUTH_URL)
                    .openConnection()
                    .setRequestMethod(HttpClient.Method.POST)
                    .header("Content-Type","application/json")
                    .header("Accept", "application/json")
                    .writeJson(J8Utils.createMap(
                            "Properties", J8Utils.createMap(
                                    "SandboxId", "RETAIL",
                                    "UserTokens", J8Utils.createList(xblToken)
                            ),
                            "RelyingParty", "rp://api.minecraftservices.com/",
                            "TokenType", "JWT"
                    ))
                    .readJSON();
            String uhs = "";
            for (Object o : ob.getJSONObject("DisplayClaims").getJSONArray("xui")){
                uhs = ((JSONObject) o).getString("uhs");
            }
            return new ImmutablePair<>(ob.getString("Token"), uhs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ImmutablePair<String, ImmutablePair<String, String>> acquireMinecraftToken(String xblUhs, String xblXsts, BiConsumer<Integer, String> updater) {
        try {
            JSONObject ob = HttpClient.getInstance(MC_LOGIN_URL)
                    .openConnection()
                    .setRequestMethod(HttpClient.Method.POST)
                    .header("Content-Type","application/json")
                    .header("Accept", "application/json")
                    .writeJson(J8Utils.createMap(
                            "identityToken", "XBL3.0 x=" + xblUhs + ";" + xblXsts
                    ))
                    .readJSON();
            String accessToken = ob.getString("access_token");
            McProfileModel contents = checkMcProfile(accessToken);
            updater.accept(85, Launcher.languageManager.get("ui.msauth._05_1"));
            if (contents.checkProfile() && checkMcStore(accessToken)){
                return new ImmutablePair<>(accessToken, new ImmutablePair<>(contents.name, contents.id));
            }
            else {
                throw new IOException("This user didn't had minecraft");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkMcStore(String mcAccessToken) {
        try {
            JSONObject ob = HttpClient.getInstance(MC_STORE_URL)
                    .openConnection()
                    .header("Authorization", String.format("Bearer %s", mcAccessToken))
                    .readJSON();
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public McProfileModel checkMcProfile(String mcAccessToken) {
        try {
            return HttpClient.getInstance(MC_PROFILE_URL)
                    .openConnection()
                    .header("Authorization", String.format("Bearer %s", mcAccessToken))
                    .toJson(McProfileModel.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public MicrosoftUser getUser(String... args) throws RuntimeException {
        updater.accept(20, Launcher.languageManager.get("ui.msauth._02"));
        ImmutablePair<String, String> token = acquireAccessToken(args[0]);
        return getUserFromToken(token);
    }
    public MicrosoftUser getUserFromToken(ImmutablePair<String, String> token) throws RuntimeException {
        updater.accept(40, Launcher.languageManager.get("ui.msauth._03"));
        ImmutablePair<String, String> xbl_token = acquireXBLToken(token.getKey());
        updater.accept(60, Launcher.languageManager.get("ui.msauth._04"));
        ImmutablePair<String, String> xsts = acquireXsts(xbl_token.getKey());
        updater.accept(80, Launcher.languageManager.get("ui.msauth._05"));
        ImmutablePair<String, ImmutablePair<String, String>> content = acquireMinecraftToken(xbl_token.getValue(), xsts.getKey(), updater);
        updater.accept(90, Launcher.languageManager.get("ui.msauth._06"));
        MicrosoftUser msu = new MicrosoftUser(content.getKey(), content.getValue().getKey(), content.getValue().getValue(), token.getValue());
        updater.accept(100, "");
        return msu;
    }
    public static class McProfileModel {
        public String id;
        public String name;
        public McSkinModel skin;
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
        }
        public static class McCapeModel {
            public String alias;
            public String id;
            public boolean state;
            public String url;
        }
    }
    public static McProfileModel getUserSkinFromName(String name) throws Exception {
        return getUserSkin(HttpClient.getInstance(String.format("https://api.mojang.com/users/profiles/minecraft/%s", name.toLowerCase()))
                .openConnection()
                .toJson(McProfileModel.class).id
        );
    }
    public static String getUserUUID(String name) throws Exception {
        return HttpClient.getInstance(String.format("https://api.mojang.com/users/profiles/minecraft/%s", name.toLowerCase()))
                .openConnection()
                .toJson(McProfileModel.class).id;
    }
    public static McProfileModel getUserSkin(String uuid) throws Exception {

        String value = null;
        JSONObject objj;
        try {
            objj = HttpClient.getInstance(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s", uuid))
                    .openConnection()
                    .readJSON();
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
        McProfileModel model = GSON_PARSER.fromJson(objj.toString(), McProfileModel.class);
        model.skin = model1;
        return model;
    }
}
