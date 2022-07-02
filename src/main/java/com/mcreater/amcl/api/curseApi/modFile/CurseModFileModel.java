package com.mcreater.amcl.api.curseApi.modFile;

import java.util.Vector;

public class CurseModFileModel {
    public long id;
    public String fileName;
    public int releaseType;
    public Vector<CurseModFileHashModel> hashes;
    public String fileDate;
    public long fileLength;
    public String downloadUrl;
    public Vector<String> gameVersions;
    public Vector<CurseModRequireModel> dependencies;
}
