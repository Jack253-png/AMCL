package com.mcreater.amcl.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.Vector;

public class PreLanguageManager {
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

    public PreLanguageManager(LanguageManager.LanguageType type){
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
    public void setLanguage(LanguageManager.LanguageType type){
        prp = getPrp(getPath(type));
    }
    public InputStream getPath(LanguageManager.LanguageType type){
        if (type == LanguageManager.LanguageType.ENGLISH) {
            return this.getClass().getClassLoader().getResourceAsStream("assets/langs/en_us.lang");
        }
        else if (type == LanguageManager.LanguageType.CHINESE){
            return this.getClass().getClassLoader().getResourceAsStream("assets/langs/zh_cn.lang");
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
    public static LanguageManager.LanguageType valueOf(String s){
        if (Objects.equals(s, "ENGLISH")){
            return LanguageManager.LanguageType.ENGLISH;
        }
        else if (Objects.equals(s, "CHINESE")){
            return LanguageManager.LanguageType.CHINESE;
        }
        else{
            return null;
        }
    }
}
