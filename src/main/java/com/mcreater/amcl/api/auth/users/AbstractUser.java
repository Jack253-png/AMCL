package com.mcreater.amcl.api.auth.users;

import java.io.IOException;
import java.net.MalformedURLException;

public abstract class AbstractUser {
    public String accessToken;
    public String username;
    public String uuid;
    public AbstractUser(String accessToken, String username, String uuid){
        this.accessToken = accessToken;
        this.username = username;
        this.uuid = uuid;
    }
    public String toString(){
        return String.format("accessToken : %s\nuserName : %s\nUUID : %s", accessToken, username, uuid);
    }
    public String getAccessToken() {
        return accessToken;
    }
    public String getUsername() {
        return username;
    }
    public String getUuid() {
        return uuid;
    }
    public void setAccessToken(String accessToken) {this.accessToken = accessToken;}
    public void setUsername(String username) {this.username = username;}
    public void setUuid(String uuid) {this.uuid = uuid;}

    public abstract void refresh() throws IOException;
}
