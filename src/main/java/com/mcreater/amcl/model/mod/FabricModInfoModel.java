package com.mcreater.amcl.model.mod;

import java.util.Vector;

public class FabricModInfoModel extends AbstractFabricModInfoModel {
    public String id;
    public String version;
    public String name;
    public String description;
    public String icon;
    public Vector<Object> authors;
    public FabricModContactModel contact = new FabricModContactModel();
    public static class FabricModContactModel {
        public String homepage;
        public String sources;
        public String issues;
    }
}
