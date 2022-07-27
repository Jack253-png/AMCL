package com.mcreater.amcl.config;

import com.mcreater.amcl.lang.LanguageManager;

import java.util.Vector;

public class ConfigModel {
    public Vector<String> selected_java;
    public Vector<String> selected_minecraft_dir;
    public String selected_minecraft_dir_index;
    public String selected_java_index;
    public String selected_version_index;
    public boolean change_game_dir;
    public int max_memory;
    public String language;
    public boolean fastDownload;
    public int downloadChunkSize;
    public int swipeSpeed;
    public int showingUpdateSpped;
    public ConfigModel getI(){
        selected_java = new Vector<>();
        selected_minecraft_dir = new Vector<>();
        selected_minecraft_dir_index = "";
        selected_java_index = "";
        selected_version_index = "";
        change_game_dir = false;
        max_memory = 1024;
        language = "CHINESE";
        fastDownload = true;
        downloadChunkSize = 2048;
        showingUpdateSpped = 500;
        swipeSpeed = 8;
        return this;
    }
}
