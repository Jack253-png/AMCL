package com.mcreater.amcl.controls.items;

import com.jfoenix.controls.JFXToggleButton;
import com.mcreater.amcl.controls.SmoothableComboBox;
import com.mcreater.amcl.pages.interfaces.Fonts;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class BooleanListItem<T extends Region> extends VBox {
    public Label name;
    public SmoothableComboBox<T> cont;
    public JFXToggleButton button;
    public BooleanListItem (String name, double width){
        this.name = new Label(name);
        this.name.setFont(Fonts.t_f);
        cont = new SmoothableComboBox<>(width, 200);
        cont.setStyle("-fx-background-color: transparent");
        cont.page.setStyle("-fx-background-color: transparent");
        button = new JFXToggleButton();
        this.getChildren().addAll(this.name, button, cont.pane);
    }
}
