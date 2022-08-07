package com.mcreater.amcl.api.auth.users;

public abstract class AbstractUser {
    public final String accessToken;
    public final String username;
    public final String uuid;
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
}
