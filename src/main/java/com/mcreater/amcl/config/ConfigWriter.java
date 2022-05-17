package com.mcreater.amcl.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigWriter{
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
    public void write() throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(g.toJson(configModel));
        fileWriter.close();
    }
}
