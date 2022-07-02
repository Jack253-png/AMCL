package com.mcreater.amcl.api.githubApi.models;

import java.util.Vector;

public class ReleaseModel {
    public String tag_name;
    public String body;
    public Vector<AssetsModel> assets;
    public boolean outdated;
    public boolean prerelease;
    public boolean iscurrent;
}
