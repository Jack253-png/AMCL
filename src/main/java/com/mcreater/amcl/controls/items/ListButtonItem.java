package com.mcreater.amcl.controls.items;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class ListButtonItem<T> extends HBox {
    public JFXButton title;
    public JFXComboBox<T> cont;

    public ListButtonItem(String titleString, double width) {
        this.title = new JFXButton(titleString);
        this.title.setFont(Fonts.t_f);
        cont = new JFXComboBox<>();
        cont.getItems().addListener((ListChangeListener<T>) c -> title.setDisable(cont.getItems().size() == 0));

        HBox left = new HBox(this.title);
        left.setAlignment(Pos.CENTER_LEFT);
        FXUtils.ControlSize.setWidth(left, width / 2);
        HBox right = new HBox(cont);
        right.setAlignment(Pos.CENTER_RIGHT);
        FXUtils.ControlSize.setWidth(right, width / 2);
        this.getChildren().addAll(left, right);
    }
}
