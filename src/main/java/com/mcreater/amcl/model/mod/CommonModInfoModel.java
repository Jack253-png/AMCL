package com.mcreater.amcl.model.mod;

import java.util.List;

public class CommonModInfoModel {
    public String version = "";
    public String name = "";
    public String description = "";
    public List<String> authorList;
    public String path;
    public String icon;
    public String url;
    public String modid;
    public CommonModInfoModel(String version, String name, String description, List<String> authorList, String path, String icon, String url, String modid) {
        this.version = version;
        this.name = name;
        this.description = description;
        this.authorList = authorList;
        this.path = path;
        this.icon = icon;
        this.url = url;
        this.modid = modid;
    }
}
