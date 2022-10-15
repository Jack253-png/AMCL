package com.mcreater.amcl.api.modApi.curseforge.mod;

import com.mcreater.amcl.api.modApi.common.AbstractModModel;
import com.mcreater.amcl.api.modApi.curseforge.modFile.CurseModFileModel;

import java.util.Map;
import java.util.Vector;

public class CurseModModel extends AbstractModModel {
    public int id;
    public String name;
    public CurseModLinkModel links;
    public String summary;
    public long downloadCount;
    public Vector<CurseModCategorieModel> categories;
    public Vector<CurseModAuthorModel> authors;
    public Vector<CurseModScreenShotModel> screenshots;
    public String dateCreated;
    public String dateModified;
    public String dateReleased;
    public Vector<Map<String, String>> latestFilesIndexes;
    public Vector<CurseModFileModel> latestFiles;
    public CurseLogoModel logo;
    public String toString(){
        return name;
    }

}
