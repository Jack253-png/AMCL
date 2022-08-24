package com.mcreater.amcl.controls.items;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.skins.JFXComboBoxListViewSkin;
import com.jfoenix.utils.JFXSmoothScroll;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ListItem<T> extends HBox {
    public Label name;
    public JFXComboBox<T> cont;
    public ListItem (String name, double width){
        this.name = new Label(name);
        this.name.setFont(Fonts.t_f);
        cont = new JFXComboBox<>();

        HBox left = new HBox(this.name);
        left.setAlignment(Pos.CENTER_LEFT);
        FXUtils.ControlSize.setWidth(left, width / 2);
        HBox right = new HBox(cont);
        right.setAlignment(Pos.CENTER_RIGHT);
        FXUtils.ControlSize.setWidth(right, width / 2);
        this.getChildren().addAll(left, right);
    }
}
