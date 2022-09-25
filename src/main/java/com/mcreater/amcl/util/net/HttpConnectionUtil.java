package com.mcreater.amcl.util.net;

public class HttpConnectionUtil {
    public static String doGet(String httpUrl) throws Exception {
        try {
            HttpClient client = HttpClient.getInstance(httpUrl);
            client.openConnection();
            client.conn.setRequestMethod("GET");
            client.conn.setReadTimeout(15000);
            client.conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.81 Mobile Safari/537.36 Edg/104.0.1293.54");
            client.conn.setRequestProperty("content-type", "application/json");
            return client.readWithNoLog();
        }
        catch (Exception e){
            HttpClient client = HttpClient.getInstance(FasterUrls.ReturnToOriginServer(httpUrl));
            client.openConnection();
            client.conn.setRequestMethod("GET");
            client.conn.setReadTimeout(15000);
            client.conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.81 Mobile Safari/537.36 Edg/104.0.1293.54");
            client.conn.setRequestProperty("content-type", "application/json");
            return client.readWithNoLog();
        }
    }
}
