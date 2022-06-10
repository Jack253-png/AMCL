package com.mcreater.amcl.util;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class GetFileExists {
    public static boolean get(String s){
        try {

            URL httpurl = new URL(new URI(s).toASCIIString());

            HttpURLConnection u = (HttpURLConnection) httpurl.openConnection();
            return u.getResponseCode() <= 299;
        } catch (Exception e) {
            return false;
        }
    }
}
