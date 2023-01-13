package com.mcreater.amcl.model.mod;

import java.util.Vector;

public class ForgeTomlModel {
    public String license;
    public Vector<ForgeTomlModModel> mods;

    public static class ForgeTomlModModel {
        public String displayURL;
        public String logoFile;
        public String displayName;
        public String description;
        public String version;
        public String modId;
        public String authors;
    }
}
