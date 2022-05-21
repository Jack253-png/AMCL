package com.mcreater.amcl;

import com.mcreater.amcl.util.net.HttpConnectionUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.util.HashMap;


public class UtilMain {
    public static void main(String[] args){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        System.out.println(sendPostWithJson("https://authserver.mojang.com/authenticate", """
                {
                   "agent": {
                       "name": "Minecraft",
                       "version": 1
                   },
                   "username": "000",
                   "password": "0001",
                   "clientToken": "000"
                }""", headers));
    }
    public static String sendPostWithJson(String url, String jsonStr, HashMap<String,String> headers) {
        // 返回的结果
        String jsonResult = "";
        try {
            HttpClient client = new HttpClient();
            // 连接超时
            client.getHttpConnectionManager().getParams().setConnectionTimeout(3*1000);
            // 读取数据超时
            client.getHttpConnectionManager().getParams().setSoTimeout(3*60*1000);
            client.getParams().setContentCharset("UTF-8");
            PostMethod postMethod = new PostMethod(url);

            postMethod.setRequestHeader("content-type", headers.get("content-type"));

            // 非空
            if (null != jsonStr && !"".equals(jsonStr)) {
                StringRequestEntity requestEntity = new StringRequestEntity(jsonStr, headers.get("content-type"), "UTF-8");
                postMethod.setRequestEntity(requestEntity);
            }
            int status = client.executeMethod(postMethod);
            if (status == HttpStatus.SC_OK) {
                jsonResult = postMethod.getResponseBodyAsString();
            } else {
                System.out.println(postMethod.getResponseBodyAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResult;
    }
}