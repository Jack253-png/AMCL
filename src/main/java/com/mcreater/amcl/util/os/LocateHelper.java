package com.mcreater.amcl.util.os;

import java.util.Locale;

public class LocateHelper {
    public static String get(){
        Locale defaultLocale = Locale.getDefault();
        if (Locale.CHINA.equals(defaultLocale)) {
            return "CHINESE";
        }
        else {
            return "ENGLISH";
        }
    }
}
