package com.mcreater.amcl.pages.interfaces;

import com.mcreater.amcl.Application;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public abstract class AbstractAnimationPage extends GridPane implements AnimationPage {
    public String name;
    GridPane gpTitle;
    public AbstractAnimationPage l;
    public double width, height;
    public Color returnBlack() {
        return Color.BLACK;
    }
    public AbstractAnimationPage(double width, double height){
        this.setMinSize(width, height);
        this.setMaxSize(width, height);
        this.width = width;
        this.height = height;
    }

    public void setTypeAll(boolean t) {
        for (Node n : this.getChildrenUnmodifiable()) {
            n.setDisable(t);
        }
    }
    public boolean getCanMovePage() {
        return Application.stage.opacityProperty().get() == 1;
    }
    public abstract void refresh();
    public abstract void refreshLanguage();
    public abstract void refreshType();
    public abstract void onExitPage();
}
