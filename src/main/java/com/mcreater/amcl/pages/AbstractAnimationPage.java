package com.mcreater.amcl.pages;

import com.mcreater.amcl.HelloApplication;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public abstract class AbstractAnimationPage extends GridPane implements AnimationPage {
    public String name;
    public void setTypeAll(boolean t){
        ArrayList<Node> Descendents = new ArrayList<>();
        for (Node n : this.getChildrenUnmodifiable()){
            n.setDisable(t);
        }
    }
    public boolean getCanMovePage(){
        return HelloApplication.stage.opacityProperty().get() == 1;
    }
}
