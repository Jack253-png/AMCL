package com.mcreater.amcl.controls.items;

import com.jfoenix.controls.JFXTextField;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class StringItem extends HBox {
    public Label title;
    public JFXTextField cont;
    HBox left;
    HBox right;
    double width;
    public StringItem(String title, double width){
        this(title, width, false);
    }
    public StringItem(String title, double width, boolean isSearch){
        this.title = new Label(title);
        this.title.setFont(Fonts.t_f);
        cont = new JFXTextField();
        cont.setFont(Fonts.t_f);
        FXUtils.fixJFXTextField(cont);
        double wv = isSearch ? width / 5 * 4 : width / 2;
        FXUtils.ControlSize.setWidth(cont, wv);
        left = new HBox(this.title);
        left.setAlignment(Pos.CENTER_LEFT);
        FXUtils.ControlSize.setWidth(left, isSearch ? width / 5 : width / 2);
        right = new HBox(cont);
        right.setAlignment(Pos.CENTER_RIGHT);
        FXUtils.ControlSize.setWidth(right, wv);
        this.getChildren().addAll(left, right);
        this.width = width;
    }
}
