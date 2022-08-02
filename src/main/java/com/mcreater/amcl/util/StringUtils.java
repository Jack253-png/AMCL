package com.mcreater.amcl.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class StringUtils {
    public static class ForgeMapplings {
        public static String get(String raw){
            raw = raw.replace("[", "").replace("]", "");
            Vector<String> f = new Vector<>(List.of(raw.split(":")));
            String s1 = f.get(0);
            String s2 = f.get(1);
            String s3 = List.of(f.get(2).split("@")).get(0);
            String p = List.of(f.get(2).split("@")).get(1);
            return String.format("%s\\%s\\%s\\%s-%s.%s", s1.replace(".", "\\"), s2, s3, s2, s3, p);
        }
        public static String getLong(String raw){
            raw = raw.replace("[", "").replace("]", "");
            Vector<String> f = new Vector<>(List.of(raw.split(":")));
            String s1 = f.get(0).replace(".", "\\");
            String s2 = f.get(1);
            String s3 = f.get(2);
            String s4 = f.get(3);
            if (s4.contains("@")){s4 = s4.replace("@", ".");}
            else{s4 += ".jar";}
            return String.format("%s\\%s\\%s\\%s-%s-%s", s1, s2, s3, s2, s3, s4);
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
            String sr = s.replace("\\", "/");
            List<String> t = Arrays.asList(sr.split("/"));
            return sr.replace(t.get(t.size() - 1), "")
                    .replace("/", File.separator)
                    .replace("\\", File.separator);
        }
        public static String forgeGet(String s){
            List<String> st = List.of(s.split(":"));
            try {
                return String.format("%s\\%s\\%s\\%s-%s-%s.jar", st.get(0).replace(".", "\\"), st.get(1), st.get(2), st.get(1), st.get(2), st.get(3));
            }
            catch (Exception e){
                return String.format("%s\\%s\\%s\\%s-%s.jar", st.get(0).replace(".", "\\"), st.get(1), st.get(2), st.get(1), st.get(2));
            }
        }
    }
    public static class LinkCommands {
        public static String link(String... coms){
            StringBuilder res = new StringBuilder();
            for (String s : coms){
                res.append(s).append(" ");
            }
            return String.valueOf(res);
        }
    }
}
