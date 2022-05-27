package com.mcreater.amcl.pages.interfaces;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.HelloApplication;
import com.mcreater.amcl.util.SVG;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public abstract class AbstractAnimationPage extends GridPane implements AnimationPage {
    public String name;
    GridPane gpTitle;
    public AbstractAnimationPage l;
    public Color returnBlack() {
        return Color.BLACK;
    }

    public void setTypeAll(boolean t) {
        ArrayList<Node> Descendents = new ArrayList<>();
        for (Node n : this.getChildrenUnmodifiable()) {
            n.setDisable(t);
        }
    }

    public boolean getCanMovePage() {
        return HelloApplication.stage.opacityProperty().get() == 1;
    }
    public abstract void refresh();
    public abstract void refreshLanguage();
}
