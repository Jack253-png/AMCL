package com.mcreater.amcl.nativeInterface;

import java.io.InputStream;
import java.net.URL;

public class ResourceGetter {
    public InputStream get(String s){
        return this.getClass().getClassLoader().getResourceAsStream(s);
    }
    public URL getUrl(String s){
        return this.getClass().getClassLoader().getResource(s);
    }
}
