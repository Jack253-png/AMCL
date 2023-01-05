package com.mcreater.amcl.pages.interfaces;

import com.mcreater.amcl.nativeInterface.ResourceGetter;
import javafx.scene.text.Font;

import java.io.*;
import java.lang.reflect.Field;

public class Fonts {
    public static Font b_f;
    public static Font s_f;
    public static Font t_f;
    public static Font ts_f;
    public static java.awt.Font awt_b_f;
    public static java.awt.Font awt_s_f;
    public static java.awt.Font awt_t_f;
    public static java.awt.Font awt_ts_f;
    public static final String path = "assets/fonts/GNU Unifont.ttf";
    public static void loadFont(){
        b_f = Font.loadFont(ResourceGetter.get(path), 28);
        s_f = Font.loadFont(ResourceGetter.get(path), 22);
        t_f = Font.loadFont(ResourceGetter.get(path), 16);
        ts_f = Font.loadFont(ResourceGetter.get(path), 12);
        loadSwingFont();
        patchJavaFXDefaultFont();
    }
    public static void loadSwingFont(){
        try {
            InputStream is = ResourceGetter.get(path);
            awt_b_f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is).deriveFont(28F);
            is.close();

            InputStream is2 = ResourceGetter.get(path);
            awt_s_f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is2).deriveFont(22F);
            is2.close();

            InputStream is3 = ResourceGetter.get(path);
            awt_t_f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is3).deriveFont(16F);
            is3.close();

            InputStream is4 = ResourceGetter.get(path);
            awt_ts_f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is4).deriveFont(12F);
            is4.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void patchJavaFXDefaultFont(){
        try {
            Field fi = Font.class.getDeclaredField("DEFAULT");
            fi.setAccessible(true);
            fi.set(null, Fonts.t_f);
        }
        catch (Exception ignored){
            ignored.printStackTrace();
        }
    }
}
