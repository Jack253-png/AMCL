package com.mcreater.amcl.controls.items;

import com.jfoenix.controls.JFXComboBox;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ListItem<T> extends HBox {
    public Label name;
    public JFXComboBox<T> cont;
    public static BorderStroke borderStroke = new BorderStroke(null,null, Color.BLACK,null, null,null,BorderStrokeStyle.SOLID,null,null, BorderWidths.DEFAULT,new Insets(2));
    public ListItem (String name, double width){
        this.name = new Label(name);
        this.name.setFont(Fonts.t_f);
        cont = new JFXComboBox<>();
        cont.setBorder(new Border(borderStroke));
        HBox left = new HBox(this.name);
        left.setAlignment(Pos.CENTER_LEFT);
        FXUtils.ControlSize.setWidth(left, width / 2);
        HBox right = new HBox(cont);
        right.setAlignment(Pos.CENTER_RIGHT);
        FXUtils.ControlSize.setWidth(right, width / 2);
        this.getChildren().addAll(left, right);
    }
}
