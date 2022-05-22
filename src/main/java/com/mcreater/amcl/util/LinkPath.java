package com.mcreater.amcl.util;

public class LinkPath {
    public static String link(String p1,String p2){
        if (!p1.endsWith("\\")){
            return rep(p1) + "\\" + rep(p2);
        }
        else{
            return rep(p1) + rep(p2);
        }
    }
    private static String rep(String p){
        return p.replace("/","\\");
    }
}
