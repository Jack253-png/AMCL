package com.mcreater.amcl.nativeInterface;

import java.io.InputStream;
import java.net.URL;

public class ResourceGetter {
    public static InputStream get(String s){
        return ResourceGetter.class.getClassLoader().getResourceAsStream(s);
    }
    public static URL getUrl(String s){
        return ResourceGetter.class.getClassLoader().getResource(s);
    }
}
