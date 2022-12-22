package com.mcreater.amcl.util.parsers;

public class HttpTitleHandler {
    public static String load(String html) {
        int start = html.indexOf("<title>");
        int end = html.indexOf("</title>");
        if (start > 0 && end > 0){
            return " - " + html.substring(start + 7, end);
        }
        else {
            return "";
        }
    }
}
