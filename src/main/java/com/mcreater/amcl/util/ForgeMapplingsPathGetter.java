package com.mcreater.amcl.util;

import java.util.List;
import java.util.Vector;

public class ForgeMapplingsPathGetter {
    public static String get(String raw){
        raw = raw.replace("[", "").replace("]", "");
        Vector<String> f = new Vector<>(List.of(raw.split(":")));
        String s1 = f.get(0);
        String s2 = f.get(1);
        String s3 = List.of(f.get(2).split("@")).get(0);
        String p = List.of(f.get(2).split("@")).get(1);
        return String.format("%s\\%s\\%s\\%s-%s.%s", s1.replace(".", "\\"), s2, s3, s2, s3, p);
    }
    public static String getLong(String raw){
        raw = raw.replace("[", "").replace("]", "");
        Vector<String> f = new Vector<>(List.of(raw.split(":")));
        String s1 = f.get(0).replace(".", "\\");
        String s2 = f.get(1);
        String s3 = f.get(2);
        String s4 = f.get(3);
        if (s4.contains("@")){s4 = s4.replace("@", ".");}
        else{s4 += ".jar";}
        return String.format("%s\\%s\\%s\\%s-%s-%s", s1, s2, s3, s2, s3, s4);
    }
    public static boolean checkIsForgePath(String s){
        return s.contains("[") && s.contains("]");
    }
    public static boolean checkIsMapKey(String s){
        return s.contains("{") && s.contains("}");
    }
}
