package com.mcreater.amcl.api.auth;

import com.google.gson.Gson;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.util.net.HttpClient;
import javafx.util.Pair;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MSAuth extends AbstractAuth<MicrosoftUser>{
    public static final String loginUrl = "https://login.live.com/oauth20_authorize.srf?client_id=00000000402b5328&response_type=code&scope=service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL&redirect_uri=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf";
    public static final String redirectUrlSuffix = "https://login.live.com/oauth20_desktop.srf?code=";
    private static final String authTokenUrl = "https://login.live.com/oauth20_token.srf";
    private static final String xblAuthUrl = "https://user.auth.xboxlive.com/user/authenticate";
    private static final String xstsAuthUrl = "https://xsts.auth.xboxlive.com/xsts/authorize";
    private static final String mcLoginUrl = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private static final String mcStoreUrl = "https://api.minecraftservices.com/entitlements/mcstore";
    private static final String mcProfileUrl = "https://api.minecraftservices.com/minecraft/profile";
    public String acquireAccessToken(String authcode) {
        Map<Object, Object> data = new HashMap<>();
        data.put("client_id", "00000000402b5328");
        data.put("code", authcode);
        data.put("grant_type", "authorization_code");
        data.put("redirect_uri", "https://login.live.com/oauth20_desktop.srf");
        data.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");
        try {
            HttpClient client = HttpClient.getInstance(authTokenUrl, data);
            client.openConnection();
            client.conn.setConnectTimeout(5000);
            client.conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            JSONObject ob = client.readJSON();
            return ob.getString("access_token");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return acquireAccessToken(authcode);
    }

    // first : Token
    // second : uhs
    public Pair<String, String> acquireXBLToken(String accessToken) {
        try {
            Map<Object, Object> data = Map.of(
                    "Properties", Map.of(
                            "AuthMethod", "RPS",
                            "SiteName", "user.auth.xboxlive.com",
                            "RpsTicket", accessToken
                    ),
                    "RelyingParty", "http://auth.xboxlive.com",
                    "TokenType", "JWT"
            );
            HttpClient client = HttpClient.getInstance(xblAuthUrl);
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
            return new Pair<>(token, uhs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return acquireXBLToken(accessToken);
    }

    public Pair<String, String> acquireXsts(String xblToken) {
        try {

            Map<Object, Object> data = Map.of(
                    "Properties", Map.of(
                            "SandboxId", "RETAIL",
                            "UserTokens", List.of(xblToken)
                    ),
                    "RelyingParty", "rp://api.minecraftservices.com/",
                    "TokenType", "JWT"
            );

            HttpClient client = HttpClient.getInstance(xstsAuthUrl);
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
            return new Pair<>(ob.getString("Token"), uhs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return acquireXsts(xblToken);
    }

    public Pair<String, Pair<String, String>> acquireMinecraftToken(String xblUhs, String xblXsts) {
        try {
            Map<Object, Object> data = Map.of(
                    "identityToken", "XBL3.0 x=" + xblUhs + ";" + xblXsts
            );
            HttpClient client = HttpClient.getInstance(mcLoginUrl);
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
            String[] contents = checkMcProfile(accessToken);
            if (contents.length == 2 && checkMcStore(accessToken)){
                return new Pair<>(accessToken, new Pair<>(contents[0], contents[1]));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return acquireMinecraftToken(xblUhs, xblXsts);
    }

    private boolean checkMcStore(String mcAccessToken) {
        try {
            HttpClient client = HttpClient.getInstance(mcStoreUrl);
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
            e.printStackTrace();
        }
        return checkMcStore(mcAccessToken);
    }

    private String[] checkMcProfile(String mcAccessToken) {
        String[] result = new String[2];
        try {
            HttpClient client = HttpClient.getInstance(mcProfileUrl);
            client.openConnection();
            client.conn.setRequestProperty("Authorization", String.format("Bearer %s", mcAccessToken));
            JSONObject ob = client.readJSON();
            result[0] = ob.getString("name");
            result[1] = ob.getString("id");
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checkMcProfile(mcAccessToken);
    }
    public MicrosoftUser getUser(String... args){
        String token = acquireAccessToken(args[0]);
        Pair<String, String> xbl_token = acquireXBLToken(token);
        Pair<String, String> xsts = acquireXsts(xbl_token.getKey());
        Pair<String, Pair<String, String>> content = acquireMinecraftToken(xbl_token.getValue(), xsts.getKey());
        return new MicrosoftUser(content.getKey(), content.getValue().getKey(), content.getValue().getValue());
    }
    public static void main(String[] args){
        MSAuth auth = new MSAuth();
        String token = auth.acquireAccessToken("M.R3_BAY.af972917-d323-2a96-72d8-b9b1b363c197");
        Pair<String, String> xbl_token = auth.acquireXBLToken(token);
        Pair<String, String> xsts = auth.acquireXsts(xbl_token.getKey());
        Pair<String, Pair<String, String>> content = auth.acquireMinecraftToken(xbl_token.getValue(), xsts.getKey());
        System.out.println(content);
    }
}
