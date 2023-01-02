package com.mcreater.amcl.config;

import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.J8Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class ConfigWriter {
    public ConfigModel configModel;
    File file;
    public String p;
    public ConfigWriter(File f) throws IOException {
        p = f.getPath();
        configModel = new ConfigReader(f).read();
        file = f;
    }
    public void write(){
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(GSON_PARSER.toJson(configModel));
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
        Vector<String> dirs = new Vector<>();
        configModel.selected_minecraft_dir.forEach(s -> {
            if (!new File(s).exists()){
                dirs.add(s);
            }
        });
        configModel.selected_minecraft_dir.removeAll(dirs);

        Vector<String> java = new Vector<>();
        configModel.selected_java.forEach(s -> {
            if (!new File(s).exists()) {
                java.add(s);
            }
        });
        configModel.selected_java.removeAll(java);

        if (configModel.selected_minecraft_dir.size() == 0) {
            File dir = new File(new File(System.getProperty("java.class.path")).getParent(), ".minecraft");
            dir.mkdirs();
            configModel.selected_minecraft_dir.add(dir.getAbsolutePath());
        }

        if (configModel.selected_java.size() == 0) {
            Vector<File> files = FileUtils.getJavaTotal();
            Vector<String> stringPaths = new Vector<>();
            files.forEach(file -> stringPaths.add(file.getAbsolutePath()));
            System.out.println(stringPaths);
            configModel.selected_java.addAll(stringPaths);
        }

        for (String s : configModel.selected_java) {
            if (s.equals(configModel.selected_java_index)) {
                configModel.selected_java_index = "";
                break;
            }
        }

        write();
    }
}
