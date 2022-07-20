package com.mcreater.amcl.controls;

import com.mcreater.amcl.download.model.OriginalVersionModel;
import com.mcreater.amcl.pages.interfaces.Fonts;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class VanilaVersionContent extends VBox {
    public OriginalVersionModel model;
    public Label id;
    public Label releaseTime;
    public VanilaVersionContent(OriginalVersionModel model){
        this.model = model;
        id = new Label(model.id);
        id.setFont(Fonts.t_f);
        releaseTime = new Label(model.time.split("\\+")[0].replace("T", " "));
        releaseTime.setFont(Fonts.t_f);
        this.getChildren().addAll(id, releaseTime);
    }
}
