package com.mcreater.amcl.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpConnectionUtil {
    public static String doGet(String httpUrl){
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder result = new StringBuilder();
        while (true){
            try {
                URL url = new URL(httpUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(15000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.81 Mobile Safari/537.36 Edg/104.0.1293.54");
                connection.setRequestProperty("content-type", "application/json");
                connection.connect();
                if (connection.getResponseCode() == 200) {
                    is = connection.getInputStream();
                    if (null != is) {
                        br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                        String temp;
                        while (null != (temp = br.readLine())) {
                            result.append(temp);
                        }
                    }
                }
                break;
            } catch (IOException e) {
                e.printStackTrace();
                connection.disconnect();
                result = new StringBuilder();
            }
        }
        if (null != br) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != is) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        connection.disconnect();
        return result.toString();
    }
}
