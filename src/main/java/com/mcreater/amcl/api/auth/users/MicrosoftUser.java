package com.mcreater.amcl.api.auth.users;

import java.util.Map;

public class MicrosoftUser extends AbstractUser{
    public MicrosoftUser(String accessToken, String username, String uuid) {
        super(accessToken, username, uuid);
    }
}
