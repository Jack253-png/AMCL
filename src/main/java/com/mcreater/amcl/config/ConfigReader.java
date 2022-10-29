package com.mcreater.amcl.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

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
            fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(new ConfigModel().toDefault(), ConfigModel.class));
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
            String tempString = null;
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
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            Gson g = gsonBuilder.create();
            try {
                return g.fromJson(result.toString(), ConfigModel.class);
            }
            catch (Exception e){
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
