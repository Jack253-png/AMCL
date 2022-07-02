package com.mcreater.amcl.api.curseApi;

public class CurseResourceType extends CurseAbstractType {
    public enum Types {
        BUKKIT_PLUGIN,
        MOD,
        RESOURCE_PACK,
        WORLD,
        MODPACK,
        CUSTOMIZATION,
        ADDONS,
        UNKNOW1,
        UNKNOW2,
        UNKNOW3,
        DEFAULT
    }

    private static final int bukkit_plugin = 5;
    private static final int mod = 6;
    private static final int resource_pack = 12;
    private static final int world = 17;
    private static final int modpack = 4471;
    private static final int customization = 4546;
    private static final int addons = 4559;
    private static final int unknow1 = 4944;
    private static final int unknow2 = 4979;
    private static final int unknow3 = 4984;
    public static int get(Types type) {
        switch (type) {
            case BUKKIT_PLUGIN:
                return bukkit_plugin;
            case RESOURCE_PACK:
                return resource_pack;
            case WORLD:
                return world;
            case MODPACK:
                return modpack;
            case CUSTOMIZATION:
                return customization;
            case ADDONS:
                return addons;
            case UNKNOW1:
                return unknow1;
            case UNKNOW2:
                return unknow2;
            case UNKNOW3:
                return unknow3;
            case MOD:
            case DEFAULT:
            default:
                return mod;
        }
    }
}
