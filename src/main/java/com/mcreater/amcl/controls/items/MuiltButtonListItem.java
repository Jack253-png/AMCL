package com.mcreater.amcl.controls.items;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Vector;

public class MuiltButtonListItem<T> extends HBox{
    public Label name;
    public JFXComboBox<T> cont;
    public HBox group;
    public Vector<JFXButton> buttons;
    public MuiltButtonListItem(String name, double width) {
        BorderStroke borderStroke = new BorderStroke(null,null, Color.BLACK,null, null,null, BorderStrokeStyle.SOLID,null,null, BorderWidths.DEFAULT,new Insets(2));
        this.name = new Label(name);
        this.name.setFont(Fonts.t_f);
        cont = new JFXComboBox<>();
        cont.setBorder(new Border(borderStroke));
        FXUtils.ControlSize.setWidth(cont, width / 1.75);
        group = new HBox();
        buttons = new Vector<>();
        HBox left = new HBox(this.name);
        left.setAlignment(Pos.CENTER_LEFT);
        FXUtils.ControlSize.setWidth(left, width / 4);
        HBox right = new HBox(cont, group);
        right.setAlignment(Pos.CENTER);
        FXUtils.ControlSize.setWidth(right, width / 4 * 3);
        this.getChildren().addAll(left, right);
    }
    public void addButtons(JFXButton... buttons){
        this.buttons.addAll(List.of(buttons));
        group.getChildren().clear();
        group.getChildren().addAll(this.buttons);
    }
}
