package com.mcreater.amcl.util.operatingSystem;

public class LinkCommands {
    public static String link(String... coms){
        StringBuilder res = new StringBuilder();
        for (String s : coms){
            res.append(s).append(" ");
        }
        String result = String.valueOf(res);
        return result;
    }
}
