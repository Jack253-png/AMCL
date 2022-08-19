package com.mcreater.amcl.api.auth.users;

import com.google.gson.Gson;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.net.HttpClient;
import javafx.util.Pair;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.Map;
import java.util.Vector;

public class MicrosoftUser extends AbstractUser {
    final Vector<MSAuth.McProfileModel.McSkinModel> skins;
    public String refreshToken;
    public MicrosoftUser(String accessToken, String username, String uuid, Vector<MSAuth.McProfileModel.McSkinModel> skins, String refreshToken) {
        super(accessToken, username, uuid);
        this.skins = skins;
        this.refreshToken = refreshToken;
    }
    public String toString(){
        return super.toString() + "\nSkins : " + skins;
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
}
