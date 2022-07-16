package com.mcreater.amcl.pages.interfaces;

import com.mcreater.amcl.nativeInterface.ResourceGetter;
import javafx.scene.text.Font;

import java.io.*;

public class Fonts {
    public static Font b_f;
    public static Font s_f;
    public static Font t_f;
    public static void loadFont(){
        b_f = Font.loadFont(new ResourceGetter().get("assets/fonts/GNU Unifont.ttf"), 28);
        s_f = Font.loadFont(new ResourceGetter().get("assets/fonts/GNU Unifont.ttf"), 22);
        t_f = Font.loadFont(new ResourceGetter().get("assets/fonts/GNU Unifont.ttf"), 16);
    }
}
