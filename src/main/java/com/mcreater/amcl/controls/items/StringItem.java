package com.mcreater.amcl.controls.items;

import com.jfoenix.controls.JFXTextField;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.SetSize;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class StringItem extends HBox {
    public Label title;
    public JFXTextField cont;
    public StringItem(String title, double width){
        BorderStroke borderStroke = new BorderStroke(null,null, Color.BLACK,null, null,null, BorderStrokeStyle.SOLID,null,null, BorderWidths.DEFAULT,new Insets(2));
        this.title = new Label(title);
        this.title.setFont(Fonts.t_f);
        cont = new JFXTextField();
        cont.setBorder(new Border(borderStroke));
        HBox left = new HBox(this.title);
        left.setAlignment(Pos.CENTER_LEFT);
        SetSize.setWidth(left, width / 2);
        HBox right = new HBox(cont);
        right.setAlignment(Pos.CENTER_RIGHT);
        SetSize.setWidth(right, width / 2);
        this.getChildren().addAll(left, right);
    }
}
