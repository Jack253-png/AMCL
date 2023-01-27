package com.mcreater.amcl.util.net;

public class HttpConnectionUtil {
    public static String doGet(String httpUrl) throws Exception {
        try {
            return HttpClient.getInstance(httpUrl)
                    .open()
                    .method(HttpClient.Method.GET)
                    .timeout(15000)
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.81 Mobile Safari/537.36 Edg/104.0.1293.54")
                    .header("content-type", "application/json")
                    .readWithNoLog();
        } catch (Exception e) {
            e.printStackTrace();
            return HttpClient.getInstance(FasterUrls.ReturnToOriginServer(httpUrl))
                    .open()
                    .method(HttpClient.Method.GET)
                    .timeout(15000)
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.81 Mobile Safari/537.36 Edg/104.0.1293.54")
                    .header("content-type", "application/json")
                    .readWithNoLog();
        }
    }
}
