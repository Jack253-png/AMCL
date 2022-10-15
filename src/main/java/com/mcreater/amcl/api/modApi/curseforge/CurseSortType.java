package com.mcreater.amcl.api.modApi.curseforge;

public class CurseSortType extends CurseAbstractType{
    public enum Types{
        ASCENDING,
        DESCENDING,
        DEFAULT
    }
    public static String get(Types type){
        switch (type){
            case ASCENDING:
                return "asc";
            case DESCENDING:
            case DEFAULT:
            default:
                return "desc";
        }
    }
}
