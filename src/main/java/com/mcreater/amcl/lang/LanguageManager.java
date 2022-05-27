package com.mcreater.amcl.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class LanguageManager {
    String s;
    InputStream uri;
    Properties prp;
    public enum LanguageType {
        ENGLISH,
        CHINESE
    }
    public LanguageManager(LanguageType type){
        if (type == LanguageType.ENGLISH) {
            uri = this.getClass().getClassLoader().getResourceAsStream("assets/en_us.lang");
        }
        else if (type == LanguageType.CHINESE){
            uri = this.getClass().getClassLoader().getResourceAsStream("assets/zh_cn.lang");
        }
        try {
            prp = new Properties();
            prp.load(new InputStreamReader(uri, StandardCharsets.UTF_8));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public String get(String id){
        if (prp.get(id) != null) {
            return prp.get(id).toString();
        }
        else{
            return id;
        }
    }
}