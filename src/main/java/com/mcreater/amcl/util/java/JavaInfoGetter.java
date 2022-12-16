package com.mcreater.amcl.util.java;

import com.mcreater.amcl.game.launch.Launch;
import com.mcreater.amcl.util.J8Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class JavaInfoGetter implements Callable<Vector<String>> {
    static File p;
    public static Vector<String> get(File f) throws ExecutionException, InterruptedException {
        p = f;
        FutureTask<Vector<String>> futureTask = new FutureTask<>(new JavaInfoGetter());
        Thread thread = new Thread(futureTask);
        thread.start();
        return futureTask.get();
    }
    public static Vector<String> getCore(File f){
        try {
            String p = f.getPath();
            Process proc = new ProcessBuilder(p, "-version").start();
            String resu;
            resu = Launch.ret(proc.getErrorStream());
            Vector<String> compled = fromArrayToVector(resu.split("\n"));
            Vector<String> version_info = fromArrayToVector(compled.get(0).split(" "));
            String version = "1.0.0";
            for (String s : version_info){
                if (s.contains(".")){
                    version = s;
                    break;
                }
            }
            String bits = "32";
            version = version.replace("\"", "");
            version = version.replace("_", " update ");
            if (compled.get(2).contains("64")){
                bits = "64";
            }
            Vector<String> r = new Vector<>();
            r.add(version);
            r.add(bits);
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
        return new Vector<>(J8Utils.createList(strings));
    }
    public static String change_filename(String java, String filename){
        return java.replace("java.exe", filename);
    }

    @Override
    public Vector<String> call() throws Exception {
        return getCore(p);
    }
}
