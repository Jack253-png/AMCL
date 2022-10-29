package com.mcreater.amcl.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcreater.amcl.api.auth.users.OffLineUser;
import com.mcreater.amcl.lang.LanguageManager;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.operatingSystem.LocateHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class ConfigWriter {
    public ConfigModel configModel;
    File file;
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson g;
    public String p;
    public ConfigWriter(File f) throws IOException {
        p = f.getPath();
        ConfigReader configReader = new ConfigReader(f);
        configModel = configReader.read();
        gsonBuilder.setPrettyPrinting();
        g = gsonBuilder.create();
        file = f;
    }
    public void write(){
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(g.toJson(configModel));
            fileWriter.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public void check_and_write(){
        if (configModel.selected_java == null){
            configModel.selected_java = new Vector<>();
        }
        if (configModel.selected_minecraft_dir == null){
            configModel.selected_minecraft_dir = new Vector<>();
        }
        if (configModel.selected_minecraft_dir_index == null){
            configModel.selected_minecraft_dir_index = "";
        }
        if (configModel.selected_java_index == null){
            configModel.selected_java_index = "";
        }
        if (configModel.selected_version_index == null){
            configModel.selected_version_index = "";
        }
        if (configModel.max_memory < 16 || configModel.max_memory > J8Utils.getMcMaxMemory()){
            configModel.max_memory = 1024;
        }
        if (configModel.downloadChunkSize < 512 || configModel.downloadChunkSize > 8192){
            configModel.downloadChunkSize = 2048;
        }
        if (!LanguageManager.vaild_languages.contains(configModel.language)){
            configModel.language = LocateHelper.get();
        }
        if (configModel.showingUpdateSpped < 500 || configModel.showingUpdateSpped > 1000){
            configModel.showingUpdateSpped = 500;
        }
        if (!FasterUrls.vaild_servers.contains(configModel.downloadServer)){
            configModel.downloadServer = "MCBBS";
        }
        Vector<String> dirs = new Vector<>(configModel.selected_minecraft_dir);
        configModel.selected_minecraft_dir.forEach(s -> {
            if (!new File(s).exists()){
                dirs.remove(s);
            }
        });
        configModel.selected_minecraft_dir = dirs;
        try {
            ConfigModel.UserType.valueOf(configModel.last_userType);
        }
        catch (Exception e){
            configModel.last_userType = "OFFLINE";
        }
        write();
    }
}
