package com.mcreater.amcl.lang;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Vector;

public class LanguageManager extends AbstractLanguageManager {
    Properties prp;
    public static Vector<String> vaild_languages = new Vector<>();
    private Runnable listener = () -> {};
    public void setListener(@NotNull Runnable listener) {
        this.listener = listener;
    }
    public enum LanguageType {
        ENGLISH,
        CHINESE
    }
    public void initlaze(){
        vaild_languages.add("ENGLISH");
        vaild_languages.add("CHINESE");
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
    public String get(String id, Object... args) {
        return String.format(get(id), args);
    }
    public void setLanguage(LanguageType type){
        prp = getPrp(getPath(type));
        listener.run();
    }
    public InputStream getPath(LanguageType type){
        if (type == LanguageType.ENGLISH) {
            return this.getClass().getClassLoader().getResourceAsStream("assets/langs/en_us.lang");
        }
        else if (type == LanguageType.CHINESE){
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
}