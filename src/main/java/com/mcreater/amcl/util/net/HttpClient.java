package com.mcreater.amcl.util.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class HttpClient {
    public URL finalUrl;
    public HttpURLConnection conn;
    Logger logger = LogManager.getLogger(HttpClient.class);
    boolean ignoreHttpCode = false;

    public enum Method {
        GET,
        HEAD,
        POST,
        PUT,
        DELETE,
        CONNECT,
        OPTIONS,
        TRACE
    }

    private HttpClient(String u, Map<Object, Object> args) throws MalformedURLException {
        finalUrl = new URL(u + "?" + ofFormData1(args));
    }

    private HttpClient(String u) throws MalformedURLException {
        finalUrl = new URL(u);
    }

    public static String ofFormData1(Map<Object, Object> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            try {
                builder.append(URLEncoder.encode(entry.getKey().toString(), "UTF-8"));
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            } catch (Exception ignored) {

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

    public HttpClient open() throws IOException {
        conn = (HttpURLConnection) finalUrl.openConnection();
        return this;
    }

    public HttpClient open(Proxy proxy) throws IOException {
        conn = (HttpURLConnection) finalUrl.openConnection(proxy);
        return this;
    }

    public String read() throws Exception {
        return read(true, false);
    }

    public String readWithNoLog() throws Exception {
        return read(true, true);
    }

    public String read(boolean autoConnect, boolean NoLog) throws Exception {
        if (autoConnect) conn.connect();
        try {
            InputStream stream = conn.getInputStream();
            StringBuilder builder = new StringBuilder();
            if (stream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String temp;
                while ((temp = reader.readLine()) != null) {
                    builder.append(temp).append("\n");
                }
            }
            if (!NoLog) logger.info(String.format("fetched message : %s", builder));
            return builder.toString();
        } catch (Exception e) {
            InputStream stream = conn.getErrorStream();
            StringBuilder builder = new StringBuilder();
            if (stream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String temp;
                while ((temp = reader.readLine()) != null) {
                    builder.append(temp).append("\n");
                }
            }
            if (!ignoreHttpCode)
                throw new IOException(String.format("Server returned code %d with content %s", conn.getResponseCode(), builder));
            return builder.toString();
        }
    }

    public JSONObject readJSON() throws Exception {
        return readJSON(true);
    }

    public JSONObject readJSON(boolean autoConnect) throws Exception {
        return new JSONObject(read(autoConnect, false));
    }

    public HttpClient write(byte[] data) throws IOException {
        if (!conn.getDoOutput()) conn.setDoOutput(true);
        conn.connect();
        try (OutputStream os = conn.getOutputStream()) {
            os.write(data);
        }
        return this;
    }

    public HttpClient write(Map<Object, Object> data, Charset charset) throws IOException {
        return write(ofFormData1(data).getBytes(charset));
    }

    public HttpClient write(Map<Object, Object> data) throws IOException {
        return write(data, StandardCharsets.UTF_8);
    }

    public HttpClient writeJson(Map<Object, Object> data) throws IOException {
        if (!conn.getDoOutput()) conn.setDoOutput(true);
        conn.connect();
        BufferedWriter wrt2 = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        wrt2.write(GSON_PARSER.toJson(data));
        wrt2.flush();
        wrt2.close();
        return this;
    }

    public HttpClient ignoreHttpCode(boolean ignore) {
        this.ignoreHttpCode = ignore;
        return this;
    }

    public HttpClient header(String key, String value) {
        if (conn != null) {
            conn.setRequestProperty(key, value);
        }
        return this;
    }

    public HttpClient method(Method method) throws ProtocolException {
        if (conn != null) {
            conn.setRequestMethod(method.name());
        }
        return this;
    }

    public <T> T toJson(Class<T> clazz) throws Exception {
        return toJson(clazz, true);
    }

    public <T> T toJson(Class<T> clazz, boolean autoConnect) throws Exception {
        return GSON_PARSER.fromJson(read(autoConnect, true), clazz);
    }

    public HttpClient timeout(int time) {
        if (conn != null) {
            conn.setConnectTimeout(time);
        }
        return this;
    }
}
