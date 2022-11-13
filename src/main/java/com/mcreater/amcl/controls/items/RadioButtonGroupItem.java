package com.mcreater.amcl.controls.items;

import com.jfoenix.controls.JFXTextField;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class RadioButtonGroupItem extends HBox {
    public Label title;
    public AbstractRadioButtonGroup cont;
    HBox left;
    HBox right;
    double width;
    public RadioButtonGroupItem(String title, double width, Orientation orientation, String... s){
        this.title = new Label(title);
        this.title.setFont(Fonts.t_f);
        cont = orientation == Orientation.HORIZONTAL ? new RadioButtonGroupH(s) : new RadioButtonGroupV(s);
        double wv = width / 2;
        FXUtils.ControlSize.setWidth((Pane) cont, wv);
        left = new HBox(this.title);
        left.setAlignment(Pos.CENTER_LEFT);
        FXUtils.ControlSize.setWidth(left, width / 2);
        right = new HBox((Pane) cont);
        right.setAlignment(Pos.CENTER_RIGHT);
        FXUtils.ControlSize.setWidth(right, wv);
        this.getChildren().addAll(left, right);
        this.width = width;
    }
}