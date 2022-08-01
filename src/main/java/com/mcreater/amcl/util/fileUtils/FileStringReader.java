package com.mcreater.amcl.util.fileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileStringReader {
    public static String read(String p){
        File file = new File(p);
        BufferedReader reader = null;
        StringBuilder r = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                r.append(tempString).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Null to read file");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return r.toString();
    }
}
