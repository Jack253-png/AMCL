package com.mcreater.amcl.api.auth.users;

import java.io.IOException;
import java.io.Serializable;

public abstract class AbstractUser implements Serializable {
    private static final long serialVersionUID = -187944731L;
    public static final int OFFLINE = 0;
    public static final int MICROSOFT = 1;
    public String accessToken;
    public String username;
    public String uuid;
    public String refreshToken;
    public boolean active = false;
    public int getUserType() {
        if (this instanceof MicrosoftUser) return MICROSOFT;
        return OFFLINE;
    }
    public OffLineUser toOfflineUser() {
        return (OffLineUser) this;
    }
    public MicrosoftUser toMicrosoftUser() {
        return (MicrosoftUser) this;
    }
    public AbstractUser(String accessToken, String username, String uuid, String refreshToken) {
        this.accessToken = accessToken;
        this.username = username;
        this.uuid = uuid;
        this.refreshToken = refreshToken;
    }
    public String toString() {
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
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public abstract void refresh() throws Exception;
    public abstract boolean validate();
}
