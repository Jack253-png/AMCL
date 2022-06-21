package com.mcreater.amcl.pages.interfaces;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class SettingPage extends ScrollPane implements SettingsAnimationPage {
    public double width, height;
    public VBox content;
    public SettingPage(double width, double height, VBox content){
        this.setMinSize(width, height);
        this.setMaxSize(width, height);
        this.width = width;
        this.height = height;
//        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setContent(content);
        this.content = content;
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
