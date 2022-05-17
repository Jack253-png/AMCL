package com.mcreater.amcl.pages;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public interface AnimationPage {
    static FadeTransition in = new FadeTransition();
    default void set(Node n){
        in.setDuration(Duration.millis(500));
        in.setFromValue(0);
        in.setToValue(1);
        in.setNode(n);
    }
}
