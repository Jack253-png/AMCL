package com.mcreater.amcl.api.auth.users;

import com.google.gson.Gson;
import com.mcreater.amcl.Launcher;
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

    public static void main(String[] args) throws IOException {
        MicrosoftUser user  = new MicrosoftUser("eyJhbGciOiJIUzI1NiJ9.eyJ4dWlkIjoiMjUzNTQxNTIxNTg1NDAxMiIsImFnZyI6IkFkdWx0Iiwic3ViIjoiYmVjOGYyN2ItYjBhNi00ZTIxLWJiZTItYjU3OTRkZjAyMWYxIiwibmJmIjoxNjYxNDMxOTA2LCJhdXRoIjoiWEJPWCIsInJvbGVzIjpbXSwiaXNzIjoiYXV0aGVudGljYXRpb24iLCJleHAiOjE2NjE1MTgzMDYsImlhdCI6MTY2MTQzMTkwNiwicGxhdGZvcm0iOiJVTktOT1dOIiwieXVpZCI6ImY4NGExOWE5NmM5MDk0YjNkZmNiNGZjZWRiNzgyYzVhIn0.mPPcdTjkBzdtiGX9PYYg1wtMFcaxZKxpMJENJKKePxI", "Starcloudsea", "95883f77eef84bc6b7274f9c754a5a2c", new Vector<>(), "M.R3_BAY.-CdpGwdndRxAhP7fa7Lw9ArDlsQM1FHwWzEcRMvcqXCxaHGVGJ7D9PgcIAlaYVf31zFqYEaf9rTcwPjvHHEseVq!DoeUoJYtiN7aZMuQuxGFyHY4JYSEcJKRN4JT3zOFprqFmdqdJSYzREYwql0pdv6sCFD9UsyGJzOic6tYpNI8zAz85dHucFVkfaJBJZN6h88JCkpT7JFKQxYPGRsYwZHmo8oVjf0kPjPgjo6j28iZHD3MvACo8RBj3mBRyc7jiDd20YzsMtdMp9O3dodH90mU$");
        System.out.println(user.vaildate());
    }
    public MicrosoftUser(String accessToken, String username, String uuid, Vector<MSAuth.McProfileModel.McSkinModel> skins, String refreshToken) {
        super(accessToken, username, uuid, refreshToken);
        this.skins = skins;
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

    public boolean vaildate() {
        try {
            new MSAuth().checkMcStore(accessToken);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
