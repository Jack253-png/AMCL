package com.mcreater.amcl.util.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpClient {
    public URL finalUrl;
    public HttpURLConnection conn;
    Logger logger = LogManager.getLogger(HttpClient.class);
    private HttpClient(String u, Map<Object, Object> args) throws MalformedURLException {
        finalUrl = new URL(u + "?" + this.ofFormData1(args));
    }
    private HttpClient(String u) throws MalformedURLException {
        finalUrl = new URL(u);
    }
    public String ofFormData1(Map<Object, Object> data){
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            try {
                builder.append(URLEncoder.encode(entry.getKey().toString(), "UTF-8"));
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            }
            catch (Exception ignored){

            }
        }
        return builder.toString();
    }
    public static HttpClient getInstance(String u, Map<Object, Object> args) throws MalformedURLException {
        return new HttpClient(u, args);
    }
    public static HttpClient getInstance(String u) throws MalformedURLException {
        return new HttpClient(u);
    }
    public void openConnection() throws IOException {
        conn = (HttpURLConnection) finalUrl.openConnection();
    }
    public void openConnection(Proxy proxy) throws IOException {
        conn = (HttpURLConnection) finalUrl.openConnection(proxy);
    }
    public String read() throws IOException {
        return read(true, false);
    }
    public String readWithNoLog() throws IOException {
        return read(true, true);
    }
    public String read(boolean autoConnect, boolean NoLog) throws IOException {
        if (autoConnect) conn.connect();
        if (conn.getResponseCode() > 399) throw new IOException(String.format("Server returned code %d", conn.getResponseCode()));
        InputStream stream = conn.getInputStream();
        StringBuilder builder = new StringBuilder();
        if (stream != null){
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String temp;
            while ((temp = reader.readLine()) != null){
                builder.append(temp).append("\n");
            }
        }
        if (!NoLog) logger.info(String.format("fetched message : %s", builder));
        return builder.toString();
    }
    public JSONObject readJSON() throws IOException {
        return readJSON(true);
    }
    public JSONObject readJSON(boolean autoConnect) throws IOException {
        return new JSONObject(read(autoConnect, false));
    }
}
