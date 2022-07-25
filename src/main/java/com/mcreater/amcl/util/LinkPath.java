package com.mcreater.amcl.util;

import java.io.File;

public class LinkPath {
    public static String link(String p1,String p2){
        return rep(new File(p1, p2).getPath());
    }
    public static String rep(String p){
        return p.replace("/",File.separator)
                .replace("\\", File.separator);
    }
}
