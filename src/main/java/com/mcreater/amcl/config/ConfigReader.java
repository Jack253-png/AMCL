package com.mcreater.amcl.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class ConfigReader {
    boolean first_config;
    File file;
    public ConfigReader(File f) throws IOException {
        first_config = false;
        if (!f.getPath().endsWith(".json")){
            throw new IllegalStateException("Unsupported file endswith");
        }
        if (!f.exists()){
            boolean e = f.createNewFile();
            if (!e){
                throw new IllegalStateException("Null to create config file");
            }
            first_config = true;
        }
        file = f;
        if (first_config){
            writeDefault();
        }
    }
    public void writeDefault(){
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(GSON_PARSER.toJson(ConfigModel.getDefault(), ConfigModel.class));
            fileWriter.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public ConfigModel read() throws IOException {
        BufferedReader reader = null;
        StringBuilder result = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                result.append(tempString).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Read Config File Failed");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        if (!result.toString().equals("")) {
            try {
                return GSON_PARSER.fromJson(result.toString(), ConfigModel.class);
            }
            catch (Exception e){
                e.printStackTrace();
                writeDefault();
                return new ConfigReader(file).read();
            }
        }
        else{
            writeDefault();
            return new ConfigReader(file).read();
        }
    }
}
