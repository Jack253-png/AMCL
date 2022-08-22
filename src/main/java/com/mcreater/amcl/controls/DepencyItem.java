package com.mcreater.amcl.controls;

import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class DepencyItem extends VBox {
    public Label name;
    public Label copyright;
    public Label lencinses;
    public DepencyItem(String name, String copyright, String lencinses){
        this.name = new Label(name);
        this.name.setFont(Fonts.s_f);
        this.copyright = new Label(copyright);
        this.copyright.setFont(Fonts.t_f);
        this.copyright.setStyle("-fx-text-fill: rgba(0, 0, 0, 0.5)");
        this.lencinses = new Label(lencinses);
        this.lencinses.setFont(Fonts.t_f);
        this.lencinses.setStyle("-fx-text-fill: rgba(0, 0, 0, 0.5)");
        this.setSpacing(8);
        this.getChildren().addAll(this.name, this.copyright, this.lencinses);
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.03)");
        ThemeManager.loadButtonAnimates(this);
    }
}
