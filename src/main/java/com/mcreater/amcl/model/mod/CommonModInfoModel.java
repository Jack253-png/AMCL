package com.mcreater.amcl.model.mod;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Vector;

public class CommonModInfoModel {
    public String version = "";
    public String name = "";
    public String description = "";
    public Vector<String> authorList = new Vector<>();
    public String path;
    public Image icon = new WritableImage(1, 1);
}
