package com.mcreater.amcl.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.Vector;

public class LanguageManager {
    Properties prp;
    public static Vector<String> vaild_languages = new Vector<>();
    public enum LanguageType {
        ENGLISH,
        CHINESE
    }
    public void initlaze(){
        vaild_languages.add("ENGLISH");
        vaild_languages.add("CHINESE");
    }
    public LanguageManager(){
        initlaze();
        setLanguage(null);
    }
    public LanguageManager(LanguageType type){
        initlaze();
        setLanguage(type);
    }
    public String get(String id){
        if (prp.get(id) != null) {
            return prp.get(id).toString();
        }
        else{
            return id;
        }
    }
    public void setLanguage(LanguageType type){
        prp = getPrp(getPath(type));
    }
    public InputStream getPath(LanguageType type){
        if (type == LanguageType.ENGLISH) {
            return this.getClass().getClassLoader().getResourceAsStream("assets/en_us.lang");
        }
        else if (type == LanguageType.CHINESE){
            return this.getClass().getClassLoader().getResourceAsStream("assets/zh_cn.lang");
        }
        else{
            return null;
        }
    }
    public Properties getPrp(InputStream i){
        if (i != null) {
            try {
                Properties p = new Properties();
                p.load(new InputStreamReader(i, StandardCharsets.UTF_8));
                return p;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        else{
            return null;
        }
    }
    public static LanguageType valueOf(String s){
        if (Objects.equals(s, "ENGLISH")){
            return LanguageType.ENGLISH;
        }
        else if (Objects.equals(s, "CHINESE")){
            return LanguageType.CHINESE;
        }
        else{
            return null;
        }
    }
}