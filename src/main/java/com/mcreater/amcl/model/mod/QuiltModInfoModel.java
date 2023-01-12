package com.mcreater.amcl.model.mod;

import java.util.Map;

public class QuiltModInfoModel {
    public QuiltLoaderInfoModel quilt_loader = new QuiltLoaderInfoModel();
    public static class QuiltLoaderInfoModel {
        public String group;
        public String id;
        public String version;
        public QuiltMetadataModel metadata = new QuiltMetadataModel();
    }
    public static class QuiltMetadataModel {
        public String name;
        public String description;
        public Map<Object, Object> contributors;
        public Map<Object, Object> contact;
        public String license;
        public String icon;
    }
}
