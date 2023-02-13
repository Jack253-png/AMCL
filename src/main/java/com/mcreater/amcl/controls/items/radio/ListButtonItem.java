package com.mcreater.amcl.controls.items.radio;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class ListButtonItem<T> extends HBox {
    public JFXButton title;
    public JFXComboBox<T> cont;

    public ListButtonItem(String title, double width) {
        this.title = new JFXButton(title);
        this.title.setFont(Fonts.t_f);
        cont = new JFXComboBox<>();

        HBox left = new HBox(this.title);
        left.setAlignment(Pos.CENTER_LEFT);
        FXUtils.ControlSize.setWidth(left, width / 2);
        HBox right = new HBox(cont);
        right.setAlignment(Pos.CENTER_RIGHT);
        FXUtils.ControlSize.setWidth(right, width / 2);
        this.getChildren().addAll(left, right);
    }
}
