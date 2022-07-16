package com.mcreater.amcl.model;

import java.util.Map;
import java.util.Vector;

public class VersionJsonModel {
    public String id;
    public Map<String , String> assetIndex;
    public String minecraftArguments;
    public NewArgumentsModel arguments;
    public String mainClass;
    public Vector<LibModel> libraries;
    public Map<String , String> javaVersion;
    public Map<String , JarModel> downloads;
    public String type;
    public String releaseTime;
}
