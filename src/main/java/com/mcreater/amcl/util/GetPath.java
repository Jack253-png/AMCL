package com.mcreater.amcl.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class GetPath {
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
