package com.mcreater.amcl.api.modApi.common;

import com.mcreater.amcl.api.modApi.curseforge.modFile.CurseModFileModel;
import com.mcreater.amcl.api.modApi.modrinth.mod.ModrinthModModel;
import com.mcreater.amcl.api.modApi.modrinth.modFile.ModrinthModFileModel;

public abstract class AbstractModFileModel {
    public boolean isCurseFile() {
        return this instanceof CurseModFileModel;
    }
    public CurseModFileModel toCurseFile() {
        return (CurseModFileModel) this;
    }
    public ModrinthModFileModel toModrinthFile() {
        return (ModrinthModFileModel) this;
    }

}
