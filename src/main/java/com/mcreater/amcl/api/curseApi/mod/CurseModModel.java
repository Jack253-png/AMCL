package com.mcreater.amcl.api.curseApi.mod;

import java.util.Map;
import java.util.Vector;

public class CurseModModel {
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
    public CurseLogoModel logo;

}
