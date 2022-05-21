package com.mcreater.amcl.util;

import com.mcreater.amcl.game.launch.Launch;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class JavaInfoGetter {
    public static Vector<String> get(File f){
        try {
            String p = f.getPath();
            Process proc = Runtime.getRuntime().exec(LinkCommands.link(p, "-version"));
            String resu;
            resu = Launch.ret(proc.getErrorStream());
            Vector<String> compled = fromArrayToVector(resu.split("\n"));
            Vector<String> version_info = fromArrayToVector(compled.get(0).split(" "));
            String version = "";
            for (String s : version_info){
                if (s.contains(".")){
                    version = s;
                    break;
                }
            }
            String bits = "32";
            String type = "JRE";
            String company = "Oracle";
            version = version.replace("\"", "");
            version = version.replace("_", " update ");
            if (compled.get(2).contains("64")){
                bits = "64";
            }
            if (fromArrayToVector(version.split(" ")).get(0).replace("(TM)", "").equals("OpenJDK")){
                company = "Eclipse";
            }
            if (new File(change_filename(f.getPath(), "javac.exe")).exists() && new File(change_filename(f.getPath(), "jar.exe")).exists()){
                type = "JDK";
            }
            Vector<String> r = new Vector<>();
            r.add(version);
            r.add(bits);
            r.add(company);
            r.add(type);
            return r;
        }
        catch (IOException ignored){
        }
        Vector<String> r = new Vector<>();
        for (int i = 0;i < 4;i++) {
            r.add("null");
        }
        return r;
    }
    public static Vector<String> fromArrayToVector(String[] strings){
        return new Vector<>(List.of(strings));
    }
    public static String change_filename(String java, String filename){
        return java.replace("java.exe", filename);
    }
}
