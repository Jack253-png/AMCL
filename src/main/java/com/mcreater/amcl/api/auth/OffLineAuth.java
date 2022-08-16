package com.mcreater.amcl.api.auth;

import com.mcreater.amcl.api.auth.users.OffLineUser;

public class OffLineAuth implements AbstractAuth<OffLineUser>{
    public OffLineUser getUser(String... args) {
        return new OffLineUser(args[0], args[1]);
    }
}
