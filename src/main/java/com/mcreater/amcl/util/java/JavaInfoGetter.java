package com.mcreater.amcl.util.java;

import com.mcreater.amcl.nativeInterface.OSInfo;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.concurrent.Sleeper;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import static com.mcreater.amcl.util.ConsoleOutputHelper.readStreamContent;

public class JavaInfoGetter {
    public static Vector<String> getCore(File f) {
        try {
            // "-XshowSettings:properties"
            String p = f.getPath();
            Process proc = new ProcessBuilder(p, "-version").start();
            String resu = readStreamContent(proc.getErrorStream(), OSInfo.NATIVE_CHARSET);
            Vector<String> compled = fromArrayToVector(resu.split("\n"));
            Vector<String> version_info = fromArrayToVector(compled.get(0).split(" "));
            String version = "1.0.0";
            for (String s : version_info) {
                if (s.contains(".")) {
                    version = s;
                    break;
                }
            }
            String bits = "32";
            version = version.replace("\"", "");
            version = version.replace("_", " update ");
            if (compled.get(2).contains("64")) {
                bits = "64";
            }
            Vector<String> r = new Vector<>();
            r.add(version);
            r.add(bits);
            return r;
        } catch (IOException ignored) {
        }
        Vector<String> r = new Vector<>();
        for (int i = 0; i < 4; i++) {
            r.add("null");
        }
        return r;
    }

    public static Vector<String> fromArrayToVector(String[] strings) {
        return new Vector<>(J8Utils.createList(strings));
    }
}
