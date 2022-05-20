package com.mcreater.amcl.config;

import java.util.Vector;

public class ConfigModel {
    public Vector<String> selected_java;
    public Vector<String> selected_minecraft_dir;
    public String selected_minecraft_dir_index;
    public String selected_java_index;
    public String selected_version_index;
    public boolean use_classic_wallpaper;
    public boolean change_game_dir;

    public ConfigModel getI(){
        selected_java = new Vector<>();
        selected_minecraft_dir = new Vector<>();
        selected_minecraft_dir_index = "";
        selected_java_index = "";
        selected_version_index = "";
        use_classic_wallpaper = false;
        change_game_dir = false;
        return this;
    }
}
