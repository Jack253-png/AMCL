package com.mcreater.amcl.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import static com.mcreater.amcl.util.FileUtils.PathUtil.buildPath;
import static com.mcreater.amcl.util.FileUtils.PathUtil.toPlatformPath;

public class StringUtils {
    public static class ForgeMapplings {
        public static String get(String raw){
            raw = raw.replace("[", "").replace("]", "");
            Vector<String> f = new Vector<>(J8Utils.createList(raw.split(":")));
            String s1 = f.get(0);
            String s2 = f.get(1);
            String s3 = J8Utils.createList(f.get(2).split("@")).get(0);
            String p = J8Utils.createList(f.get(2).split("@")).get(1);
            return String.format(buildPath("%s", "%s", "%s", "%s-%s.%s"), s1.replace(".", File.separator), s2, s3, s2, s3, p);
        }
        public static String getLong(String raw){
            raw = raw.replace("[", "").replace("]", "");
            Vector<String> f = new Vector<>(J8Utils.createList(raw.split(":")));
            String s1 = f.get(0).replace(".", File.separator);
            String s2 = f.get(1);
            String s3 = f.get(2);
            String s4 = f.get(3);
            if (s4.contains("@")){s4 = s4.replace("@", ".");}
            else{s4 += ".jar";}
            return String.format(buildPath("%s", "%s", "%s", "%s-%s-%s"), s1, s2, s3, s2, s3, s4);
        }
        public static boolean checkIsForgePath(String s){
            return s.contains("[") && s.contains("]");
        }
        public static boolean checkIsMapKey(String s){
            return s.contains("{") && s.contains("}");
        }
    }
    public static class GetFileBaseDir {
        public static String get(String s){
            return new File(s).getParent();
        }
        public static String forgeGet(String s){
            List<String> st = J8Utils.createList(s.split(":"));
            try {
                return String.format(buildPath("%s", "%s", "%s", "%s-%s-%s.jar"), st.get(0).replace(".", File.separator), st.get(1), st.get(2), st.get(1), st.get(2), st.get(3));
            }
            catch (Exception e){
                return String.format(buildPath("%s", "%s", "%s", "%s-%s.jar"), st.get(0).replace(".", File.separator), st.get(1), st.get(2), st.get(1), st.get(2));
            }
        }
    }
    public static class ArgReplace {
        public static String replace(String arg, Map<String, String> content){
            for (Map.Entry<String, String> entry : content.entrySet()) {
                if (arg.contains(entry.getKey())) return arg.replace(entry.getKey(), entry.getValue());
            }
            return arg;
        }
    }
}
