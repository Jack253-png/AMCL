package com.mcreater.amcl.model.mod;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.net.URLEncoder;
import java.util.Vector;

public class CommonModInfoModel {
    public String version = "";
    public String name = "";
    public String description = "";
    public Vector<String> authorList = new Vector<>();
    public String path;
    public Image icon = new WritableImage(1, 1);
    public String toMCModLink() throws Exception {
        return String.format("https://search.mcmod.cn/s?key=%s", name == null ? "" : URLEncoder.encode(name, "UTF-8"));
    }
    public String toModrinthLink() throws Exception {
        return String.format("https://modrinth.com/mods?q=%s", name == null ? "" : URLEncoder.encode(name, "UTF-8"));
    }
    public String toCurseforgeLink() throws Exception {
        return String.format("https://www.curseforge.com/minecraft/mc-mods/search?search=%s", name == null ? "" : URLEncoder.encode(name, "UTF-8"));
    }
}
