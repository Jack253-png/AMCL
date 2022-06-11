package com.mcreater.amcl.pages.interfaces;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class SettingPage extends ScrollPane implements SettingsAnimationPage {
    public double width, height;
    public SettingPage(double width, double height, VBox content){
        this.setMinSize(width, height);
        this.setMaxSize(width, height);
        this.width = width;
        this.height = height;
//        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setStyle("-fx-background-color : rgba(255, 255, 255, 0.75)");
        this.getStylesheets().add("assets/a.css");
        this.setContent(content);
        this.getStylesheets().add("assets/b.css");
    }
    public void setTypeAll(boolean t) {
        for (Node n : this.getChildrenUnmodifiable()) {
            n.setDisable(t);
        }
    }
    public boolean CanMovePage(){
        return this.getOpacity() == 1;
    }
}
