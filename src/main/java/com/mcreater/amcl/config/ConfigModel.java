package com.mcreater.amcl.config;

import com.mcreater.amcl.api.auth.users.AbstractUser;
import com.mcreater.amcl.lang.LanguageManager;
import com.mcreater.amcl.util.net.FasterUrls;

import java.util.Vector;

public class ConfigModel {
    public Vector<String> selected_java = new Vector<>();
    public Vector<String> selected_minecraft_dir = new Vector<>();
    public String selected_minecraft_dir_index = "";
    public String selected_java_index = "";
    public String selected_version_index = "";
    public boolean change_game_dir = false;
    public int max_memory = 1024;
    public LanguageManager.LanguageType language = LanguageManager.LanguageType.ENGLISH;
    public int downloadChunkSize = 2048;
    public int showingUpdateSpped = 500;
    public FasterUrls.Servers downloadServer = FasterUrls.Servers.MCBBS;
    public Vector<AbstractUser> accounts = new Vector<>();
    public static ConfigModel getDefault(){
        return new ConfigModel();
    }
}
