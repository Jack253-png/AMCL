package com.mcreater.amcl.api.auth;

public interface AbstractAuth<T> {
    T getUser(String... args);
}
