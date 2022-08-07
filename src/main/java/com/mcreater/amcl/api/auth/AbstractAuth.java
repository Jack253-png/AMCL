package com.mcreater.amcl.api.auth;

public abstract class AbstractAuth<T> {
    public abstract T getUser(String... args);
}
