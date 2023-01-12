package com.mcreater.amcl.model.mod;

import java.util.Vector;

public class CommonModInfoModel {
    public String version = "";
    public String name = "";
    public String description = "";
    public Vector<String> authorList;
    public String path;
    public String icon;
    public String url;
    public CommonModInfoModel(String version, String name, String description, Vector<String> authorList, String path, String icon, String url) {
        this.version = version;
        this.name = name;
        this.description = description;
        this.authorList = authorList;
        this.path = path;
        this.icon = icon;
        this.url = url;
    }
}
