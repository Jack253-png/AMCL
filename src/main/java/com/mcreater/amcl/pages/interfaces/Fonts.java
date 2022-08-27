package com.mcreater.amcl.pages.interfaces;

import com.mcreater.amcl.nativeInterface.ResourceGetter;
import javafx.scene.text.Font;

import java.awt.*;
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
    public static void loadFont(){
        b_f = Font.loadFont(new ResourceGetter().get("assets/fonts/GNU Unifont.ttf"), 28);
        s_f = Font.loadFont(new ResourceGetter().get("assets/fonts/GNU Unifont.ttf"), 22);
        t_f = Font.loadFont(new ResourceGetter().get("assets/fonts/GNU Unifont.ttf"), 16);
        ts_f = Font.loadFont(new ResourceGetter().get("assets/fonts/GNU Unifont.ttf"), 12);
        try {
            InputStream is = new ResourceGetter().get("assets/fonts/GNU Unifont.ttf");
            awt_b_f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is);
            awt_b_f = awt_b_f.deriveFont(28F);
            is.close();
            InputStream is2 = new ResourceGetter().get("assets/fonts/GNU Unifont.ttf");
            awt_s_f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is2);
            awt_s_f = awt_s_f.deriveFont(22F);
            is2.close();
            InputStream is3 = new ResourceGetter().get("assets/fonts/GNU Unifont.ttf");
            awt_t_f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is3);
            awt_t_f = awt_t_f.deriveFont(16F);
            is3.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try {
            Field fi = Font.class.getDeclaredField("DEFAULT");
            fi.setAccessible(true);
            fi.set(null, Fonts.t_f);
        }
        catch (Exception ignored){}
    }
}
