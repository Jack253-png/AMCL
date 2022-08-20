package com.mcreater.amcl.config;

import com.mcreater.amcl.api.auth.MSAuth;
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
    public int showingUpdateSpped;
    public String downloadServer;
    public String last_uuid;
    public String last_name;
    public String last_accessToken;
    public String last_refreshToken;
    public String last_userType;

    public String last_skin_path;
    public String last_cape_path;
    public boolean last_is_slim;
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
        downloadServer = "MCBBS";
        return this;
    }
    public enum UserType {
        OFFLINE,
        MICROSOFT
    }
}
