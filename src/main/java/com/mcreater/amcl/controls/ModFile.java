package com.mcreater.amcl.controls;

import com.jfoenix.controls.JFXCheckBox;
import com.mcreater.amcl.api.curseApi.modFile.CurseModFileModel;
import com.mcreater.amcl.pages.interfaces.Fonts;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Vector;

public class ModFile extends HBox {
    public JFXCheckBox checkBox;
    public CurseModFileModel model;
    public String version;
    public ModFile(CurseModFileModel model, String version){
        this.model = model;
        this.version = version;
        checkBox = new JFXCheckBox();
        if (model.releaseType == 1){
            checkBox.setCheckedColor(Color.LIGHTGREEN);
            checkBox.setUnCheckedColor(Color.LIGHTGREEN);
        }
        else if (model.releaseType == 2){
            checkBox.setCheckedColor(Color.LIGHTBLUE);
            checkBox.setUnCheckedColor(Color.LIGHTBLUE);
        }
        else{
            checkBox.setCheckedColor(Color.RED);
            checkBox.setUnCheckedColor(Color.RED);
        }
        checkBox.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        Label name = new Label(model.fileName);
        name.setFont(Fonts.s_f);
        Label loaders = new Label(getModLoaders(model.gameVersions, true).toString());
        loaders.setFont(Fonts.t_f);
        Label versions = new Label(getModLoaders(model.gameVersions, false).toString());
        versions.setFont(Fonts.t_f);
        VBox v = new VBox();
        v.getChildren().addAll(name, loaders, versions);
        this.getChildren().addAll(checkBox, v);
        this.setAlignment(Pos.TOP_LEFT);
    }
    public static Vector<String> getModLoaders(Vector<String> a, boolean b){
        Vector<String> loaders = new Vector<>();
        for (String s : a) {
            if (b) {
                if (!s.contains(".")) {
                    loaders.add(s);
                }
            }
            else{
                if (s.contains(".")) {
                    loaders.add(s);
                }
            }
        }
        return loaders;
    }
}
