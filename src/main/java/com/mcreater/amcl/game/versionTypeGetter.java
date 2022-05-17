package com.mcreater.amcl.game;

import com.google.gson.Gson;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.util.FileStringReader;
import com.mcreater.amcl.util.LinkPath;

public class versionTypeGetter {
    public static void get(String dir, String version){
        String version_json = LinkPath.link(LinkPath.link(LinkPath.link(dir, "versions"), version), version + ".json");
        String json_result = FileStringReader.read(version_json);
        Gson g = new Gson();
        VersionJsonModel v = g.fromJson(json_result, VersionJsonModel.class);

    }
}
