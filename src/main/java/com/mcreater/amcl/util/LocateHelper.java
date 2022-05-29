package com.mcreater.amcl.util;

import java.util.Locale;

public class LocateHelper {
    public static String get(){
        Locale defaultLocale = Locale.getDefault();
        String l = defaultLocale.getLanguage();
        switch (l){
            case "zh":
                return "CHINESE";
            default:
                return "ENGLISH";
        }
    }
}
