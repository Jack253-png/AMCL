package com.mcreater.amcl.api.auth.users;

import java.util.Random;

public class OffLineUser extends AbstractUser{
    public OffLineUser(String username, String uuid) {
        super(String.valueOf(new Random().nextInt(Integer.MAX_VALUE)), username, uuid);
    }
}
