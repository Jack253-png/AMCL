package com.mcreater.amcl.model.forge;

import com.mcreater.amcl.model.JarModel;
import com.mcreater.amcl.model.LibModel;
import com.mcreater.amcl.model.NewArgumentsModel;

import java.util.Map;
import java.util.Vector;

public class ForgeVersionModel {
    public String minecraftArguments;
    public NewArgumentsModel arguments;
    public String mainClass;
    public Vector<LibModel> libraries;
}
