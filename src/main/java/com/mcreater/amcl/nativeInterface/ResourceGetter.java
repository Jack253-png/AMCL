package com.mcreater.amcl.nativeInterface;

import java.io.InputStream;

public class ResourceGetter {
    public InputStream get(String s){
        return this.getClass().getClassLoader().getResourceAsStream(s);
    }
}
