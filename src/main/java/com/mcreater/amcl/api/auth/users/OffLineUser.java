package com.mcreater.amcl.api.auth.users;

import com.mcreater.amcl.controls.skin.SkinView;

import java.util.Random;

public class OffLineUser extends AbstractUser{
    public static final String STEVE = "000000000000300a9d83f9ec9e7fae8e";
    public static final String ALEX = "000000000000300a9d83f9ec9e7fae8d";

    public OffLineUser(String username, String uuid) {
        super(String.valueOf(new Random().nextInt(Integer.MAX_VALUE)), username, uuid);
    }

    public void refresh() {
        // Do Nothing
    }
}
