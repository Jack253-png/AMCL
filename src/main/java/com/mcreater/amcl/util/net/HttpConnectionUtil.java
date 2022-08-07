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
                connection.connect();
                if (connection.getResponseCode() == 200) {
                    is = connection.getInputStream();
                    if (null != is) {
                        br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                        String temp = null;
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
