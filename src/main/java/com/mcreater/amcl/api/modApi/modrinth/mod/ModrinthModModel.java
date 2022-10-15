package com.mcreater.amcl.api.modApi.modrinth.mod;

import com.mcreater.amcl.api.modApi.common.AbstractModModel;

import java.util.Vector;

public class ModrinthModModel extends AbstractModModel {
    public String project_id;
    public String slug;
    public String author;
    public String title;
    public String description;
    public Vector<String> versions;
    public String icon_url;
    public int downloads;
    public String date_created;
    public String date_modified;
    public String toString() {
        return title;
    }
}
