package com.mcreater.amcl.model.forge;

import com.mcreater.amcl.model.LibModel;

import java.util.Map;
import java.util.Vector;

public class ForgeInjectModel {
    public Map<String, Map<String, String>> data;
    public Vector<ForgeProcessorModel> processors;
    public Vector<LibModel> libraries;
}
