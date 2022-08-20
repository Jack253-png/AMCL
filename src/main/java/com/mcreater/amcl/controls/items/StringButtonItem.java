package com.mcreater.amcl.controls.items;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class StringButtonItem extends HBox {
    public JFXButton title;
    public JFXTextField cont;
    public StringButtonItem(String title, double width){
        this.title = new JFXButton(title);
        this.title.setFont(Fonts.t_f);
        cont = new JFXTextField();
        cont.setFont(Fonts.t_f);
        HBox left = new HBox(this.title);
        left.setAlignment(Pos.CENTER_LEFT);
        FXUtils.ControlSize.setWidth(left, width / 2);
        HBox right = new HBox(cont);
        right.setAlignment(Pos.CENTER_RIGHT);
        FXUtils.ControlSize.setWidth(right, width / 2);
        this.getChildren().addAll(left, right);
    }
}
