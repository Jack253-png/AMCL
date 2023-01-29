package com.mcreater.amcl.controls;

import com.jfoenix.controls.JFXSpinner;
import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.FileUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Objects;

public class LocalMod extends HBox {
    public Label name;
    public String path;
    public ImageView icon;
    public final CommonModInfoModel model;

    public LocalMod(CommonModInfoModel model) {
        this.model = model;
        this.path = model.path;
        name = new Label(Objects.equals(model.name, "") ? "null" : model.name);
        name.setFont(Fonts.s_f);
        icon = new ImageView();
        icon.setFitWidth(50);
        icon.setFitHeight(50);

        new Thread(() -> {
            if (model.icon != null) {
                try {
                    Image image = new Image(FileUtils.ZipUtil.readBinaryFileInZip(path, model.icon));
                    FXUtils.Platform.runLater(() -> icon.setImage(image));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        
        setAlignment(Pos.TOP_LEFT);
        setSpacing(15);
        this.getChildren().addAll(icon, name);
    }
}
