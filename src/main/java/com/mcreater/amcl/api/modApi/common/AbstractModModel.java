package com.mcreater.amcl.api.modApi.common;

import com.mcreater.amcl.api.modApi.curseforge.mod.CurseModModel;
import com.mcreater.amcl.api.modApi.modrinth.mod.ModrinthModModel;

public abstract class AbstractModModel {
    public CurseModModel toCurseMod() {
        return (CurseModModel) this;
    }
    public ModrinthModModel toModrinthMod() {
        return (ModrinthModModel) this;
    }

    public boolean isCurseMod() {
        return this instanceof CurseModModel;
    }
}
