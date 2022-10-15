package com.mcreater.amcl.api.modApi.modrinth.modFile;

import com.mcreater.amcl.api.modApi.common.AbstractModFileModel;

import java.util.Vector;

public class ModrinthModFileModel extends AbstractModFileModel {
    public String id;
    public String project_id;
    public String name;
    public String version_number;
    public String date_published;
    public int downloads;
    public String version_type;
    public Vector<ModrinthModFileItemModel> files;
    public Vector<ModrinthModFileDepencymModel> dependencies;
    public Vector<String> game_versions;
    public Vector<String> loaders;

}
