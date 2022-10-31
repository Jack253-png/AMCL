package com.mcreater.amcl.model.mod;

import java.util.Vector;

public class OldForgeModInfoModel {
    public int modinfoversion;
    public Vector<ModListItemModel> modlist;

    public static class ModListItemModel {
        public String modid;
        public String name;
        public String version;
        public String description;
        public Vector<String> authorList;
        public String url;
        public String logoFile;
        public String credits;
        public Vector<String> requiredMods;
        public boolean useDependencyInformation;
    }

}
