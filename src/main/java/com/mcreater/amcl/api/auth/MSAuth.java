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

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class MSAuth implements AbstractAuth<MicrosoftUser> {
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
    public static final String MC_NAME_CHANGE_CHECK_URL = "https://api.minecraftservices.com/minecraft/profile/namechange";

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

    public static class TokenResponse extends ErrorResponse {
        public String token_type;
        public int expires_in;
        public int ext_expires_in;
        public String scope;
        public String access_token;
        public String refresh_token;
    }

    public static class ErrorResponse {
        public String error;
        public String error_description;
        public String correlation_id;
    }

    public static class UserProfileModel {
        public String id;
        public String name;
        public Vector<UserContentModel> skins;
        public Vector<UserContentModel> capes;
        public Map<Object, Object> profileActions;

        public static class UserContentModel {
            public String id;
            public String state;
            public String url;
            public String alias;
            public String variant;
        }
    }

    public static class DeviceCodeParsedModel {
        public String access_token;
        public String refresh_token;
        public int expires_in;
    }

    public static class XBLLoginModel {
        public String IssueInstant;
        public String NotAfter;
        public String Token;
        public DisplayClaimsModel DisplayClaims;

        public static class DisplayClaimsModel {
            public Vector<UserHashModel> xui;

            public static class UserHashModel {
                public String uhs;
            }
        }
    }

    public static class MinecraftLoginModel {
        public String username;
        public Vector<Object> rules;
        public Map<Object, Object> metadata;
        public String access_token;
        public int expires_in;
        public String token_type;
    }

    public static class MinecraftStoreModel {
        public Vector<ProductItemModel> items;

        public static class ProductItemModel {
            public String name;
            public String signature;
        }
    }

    public static class NameStateModel {
        public String status;
    }

    public MicrosoftUser generateDeviceCode(BiConsumer<String, String> regIs) throws Exception {
        DeviceCodeModel model = HttpClient.getInstance(DEVICE_CODE_URL, J8Utils.createMap(
                        "client_id", CLIENT_ID,
                        "scope", SCOPE
                ))
                .open()
                .toJson(DeviceCodeModel.class);

        regIs.accept(model.user_code, model.verification_uri);

        SystemActions.copyContent(model.user_code);
        SystemActions.openBrowser(model.verification_uri);

        long startTime = System.nanoTime();
        int interval = model.interval;

        while (true) {
            Sleeper.sleep(Math.max(interval, 1));

            long estimatedTime = System.nanoTime() - startTime;
            if (TimeUnit.SECONDS.convert(estimatedTime, TimeUnit.NANOSECONDS) >= Math.min(model.expires_in, 900)) {
                throw new IOException("timed out");
            }

            try {
                TokenResponse response = HttpClient.getInstance(TOKEN_URL)
                        .open()
                        .method(HttpClient.Method.POST)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .ignoreHttpCode(true)
                        .write(J8Utils.createMap(
                                "grant_type", "urn:ietf:params:oauth:grant-type:device_code",
                                "client_id", CLIENT_ID,
                                "code", model.device_code
                        ))
                        .toJson(TokenResponse.class);
                if ("authorization_pending".equals(response.error)) continue;
                else if ("expired_token".equals(response.error)) throw new IOException("Token expired");
                else if ("slow_down".equals(response.error)) {
                    interval += 5;
                    continue;
                }
                try {
                    return getUserFromToken(new ImmutablePair<>("d=" + response.access_token, response.refresh_token));
                } catch (Exception e) {
                    throw e;
                }
            } catch (Exception ignored) {

            }
        }
    }

    public BiConsumer<Integer, String> updater = (value, mess) -> {
    };

    public void setUpdater(@NotNull BiConsumer<Integer, String> updater) {
        this.updater = updater;
    }

    private MSAuth() {
    }

    public ImmutablePair<String, String> acquireAccessToken(String authcode) {
        try {
            DeviceCodeParsedModel ob = HttpClient.getInstance(AUTH_TOKEN_URL, J8Utils.createMap(
                            "client_id", "00000000402b5328",
                            "code", authcode,
                            "grant_type", "authorization_code",
                            "redirect_uri", "https://login.live.com/oauth20_desktop.srf",
                            "scope", "service::user.auth.xboxlive.com::MBI_SSL"
                    ))
                    .open()
                    .timeout(500)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .toJson(DeviceCodeParsedModel.class);
            return new ImmutablePair<>(ob.access_token, ob.refresh_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // first : Token
    // second : uhs
    public ImmutablePair<String, String> acquireXBLToken(String accessToken) {
        try {
            XBLLoginModel ob = HttpClient.getInstance(XBL_AUTH_URL)
                    .open()
                    .method(HttpClient.Method.POST)
                    .header("Content-Type", "application/json")
                    .writeJson(J8Utils.createMap(
                            "Properties", J8Utils.createMap(
                                    "AuthMethod", "RPS",
                                    "SiteName", "user.auth.xboxlive.com",
                                    "RpsTicket", accessToken
                            ),
                            "RelyingParty", "http://auth.xboxlive.com",
                            "TokenType", "JWT"
                    ))
                    .toJson(XBLLoginModel.class);

            if (ob.DisplayClaims.xui
                    .stream()
                    .map(userHashModel -> userHashModel.uhs)
                    .filter(Objects::nonNull)
                    .count() == 0) throw new IOException("User hash not found.");

            Optional<String> ush = ob.DisplayClaims.xui
                    .stream()
                    .map(userHashModel -> userHashModel.uhs)
                    .filter(Objects::nonNull)
                    .findFirst();

            if (ush.isPresent()) return new ImmutablePair<>(ob.Token, ush.get());
            else throw new IOException("User hash null");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ImmutablePair<String, String> acquireXsts(String xblToken) {
        try {
            XBLLoginModel ob = HttpClient.getInstance(XSTS_AUTH_URL)
                    .open()
                    .method(HttpClient.Method.POST)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .writeJson(J8Utils.createMap(
                            "Properties", J8Utils.createMap(
                                    "SandboxId", "RETAIL",
                                    "UserTokens", J8Utils.createList(xblToken)
                            ),
                            "RelyingParty", "rp://api.minecraftservices.com/",
                            "TokenType", "JWT"
                    ))
                    .toJson(XBLLoginModel.class);

            if (ob.DisplayClaims.xui
                    .stream()
                    .map(userHashModel -> userHashModel.uhs)
                    .filter(Objects::nonNull)
                    .count() == 0) throw new IOException("User hash not found.");

            Optional<String> ush = ob.DisplayClaims.xui
                    .stream()
                    .map(userHashModel -> userHashModel.uhs)
                    .filter(Objects::nonNull)
                    .findFirst();

            if (ush.isPresent()) return new ImmutablePair<>(ob.Token, ush.get());
            else throw new IOException("User hash null");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ImmutablePair<String, ImmutablePair<String, String>> acquireMinecraftToken(String xblUhs, String xblXsts, BiConsumer<Integer, String> updater) {
        try {
            MinecraftLoginModel ob = HttpClient.getInstance(MC_LOGIN_URL)
                    .open()
                    .method(HttpClient.Method.POST)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .writeJson(J8Utils.createMap(
                            "identityToken", "XBL3.0 x=" + xblUhs + ";" + xblXsts
                    ))
                    .toJson(MinecraftLoginModel.class);

            String accessToken = ob.access_token;
            McProfileModel contents = checkMcProfile(accessToken);
            updater.accept(85, Launcher.languageManager.get("ui.msauth._05_1"));
            if (contents.checkProfile() && checkMcStore(accessToken)) {
                return new ImmutablePair<>(accessToken, new ImmutablePair<>(contents.name, contents.id));
            } else {
                throw new IOException("This user didn't had minecraft");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkMcStore(String mcAccessToken) {
        try {
            MinecraftStoreModel ob = HttpClient.getInstance(MC_STORE_URL)
                    .open()
                    .header("Authorization", String.format("Bearer %s", mcAccessToken))
                    .toJson(MinecraftStoreModel.class);

            return ob.items.stream()
                    .map(productItemModel -> productItemModel.name)
                    .filter(s -> "game_minecraft".equals(s) || "product_minecraft".equals(s))
                    .count() >= 2;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public McProfileModel checkMcProfile(String mcAccessToken) {
        try {
            return HttpClient.getInstance(MC_PROFILE_URL)
                    .open()
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

        public boolean checkProfile() {
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
                .open()
                .toJson(McProfileModel.class).id
        );
    }

    public static String getUserUUID(String name) throws Exception {
        return HttpClient.getInstance(String.format("https://api.mojang.com/users/profiles/minecraft/%s", name.toLowerCase()))
                .open()
                .toJson(McProfileModel.class).id;
    }

    public static McProfileModel getUserSkin(String uuid) throws Exception {

        String value = null;
        JSONObject objj;
        try {
            objj = HttpClient.getInstance(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s", uuid))
                    .open()
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
        } catch (Exception ignored) {
            model1.url = "https://";
        }
        try {
            model1.cape = obj.getJSONObject("textures").getJSONObject("CAPE").getString("url");
        } catch (Exception ignored) {
            model1.cape = "https://";
        }
        try {
            obj.getJSONObject("textures").getJSONObject("SKIN").getJSONObject("metadata").getString("model");
            model1.isSlim = true;
        } catch (Exception ignored) {
            model1.isSlim = false;
        }
        McProfileModel model = GSON_PARSER.fromJson(objj.toString(), McProfileModel.class);
        model.skin = model1;
        return model;
    }
}
