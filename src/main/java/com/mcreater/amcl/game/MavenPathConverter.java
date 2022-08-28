package com.mcreater.amcl.game;

import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.J8Utils;

import java.io.File;
import java.util.Vector;

public class MavenPathConverter {
    public static String get(String p){
        Vector<String> result = new Vector<>(J8Utils.createList(p.split(":")));
        result.set(0,result.get(0).replace(".", File.separator));

        return LinkPath.link(LinkPath.link(LinkPath.link(result.get(0),result.get(1)),result.get(2)),result.get(1)+"-"+result.get(2)+".jar");
    }
}
