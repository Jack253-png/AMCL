package com.mcreater.amcl.controls.items;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class BooleanListItem<T> extends HBox {
    public Label name;
    public JFXComboBox<T> cont;
    public JFXToggleButton button;
    public BooleanListItem (String name, double width){
        BorderStroke borderStroke = new BorderStroke(null,null, Color.BLACK,null, null,null, BorderStrokeStyle.SOLID,null,null, BorderWidths.DEFAULT,new Insets(2));
        this.name = new Label(name);
        this.name.setFont(Fonts.t_f);
        cont = new JFXComboBox<>();
        cont.setBorder(new Border(borderStroke));
        button = new JFXToggleButton();
        HBox left = new HBox(this.name);
        left.setAlignment(Pos.CENTER_LEFT);
        FXUtils.ControlSize.setWidth(left, width / 2);
        HBox right = new HBox(cont, button);
        right.setAlignment(Pos.CENTER);
        right.setSpacing(40);
        FXUtils.ControlSize.setWidth(right, width / 2);
        this.getChildren().addAll(left, right);
    }
}
