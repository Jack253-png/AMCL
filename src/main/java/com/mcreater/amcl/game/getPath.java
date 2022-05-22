package com.mcreater.amcl.game;

import com.mcreater.amcl.util.LinkPath;

import java.util.List;
import java.util.Vector;

public class getPath {
    public static String get(String p){
        Vector<String> result = new Vector<>(List.of(p.split(":")));
        result.set(0,result.get(0).replace(".","\\"));

        return LinkPath.link(LinkPath.link(LinkPath.link(result.get(0),result.get(1)),result.get(2)),result.get(1)+"-"+result.get(2)+".jar");
    }
}
