package com.mcreater.amcl.api.auth;

import com.mcreater.amcl.api.auth.users.OffLineUser;

public class OffLineAuth implements AbstractAuth<OffLineUser>{
    public OffLineUser getUser(String... args) {
        return new OffLineUser(args[0], args[1], false, args[2], args[3], args[4]);
    }
}
