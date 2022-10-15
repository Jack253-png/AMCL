package com.mcreater.amcl.api.modApi.curseforge.modFile;

import com.mcreater.amcl.api.modApi.common.AbstractModFileModel;

import java.util.Vector;

public class CurseModFileModel extends AbstractModFileModel {
    public long id;
    public String fileName;
    public String displayName;
    public int releaseType;
    public Vector<CurseModFileHashModel> hashes;
    public String fileDate;
    public long fileLength;
    public String downloadUrl;
    public Vector<String> gameVersions;
    public Vector<CurseModRequireModel> dependencies;
    public int modId;
}
