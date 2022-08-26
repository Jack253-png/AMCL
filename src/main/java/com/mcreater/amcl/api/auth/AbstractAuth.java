package com.mcreater.amcl.api.auth;

import com.mcreater.amcl.api.auth.users.AbstractUser;

public interface AbstractAuth<T extends AbstractUser> {
    T getUser(String... args);
}
