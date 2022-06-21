package com.mcreater.amcl.pages.interfaces;

import com.mcreater.amcl.Application;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

public interface AnimationPage {
    Timeline in = new Timeline();
    Timeline out = new Timeline();
    long delay = 500;
    default void set(){
        in.setCycleCount(1);
        in.getKeyFrames().clear();
        in.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(Application.stage.opacityProperty(), 1)));
        in.getKeyFrames().add(new KeyFrame(new Duration(delay), new KeyValue(Application.stage.opacityProperty(), 0)));

        out.setCycleCount(1);
        out.getKeyFrames().clear();
        out.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(Application.stage.opacityProperty(), 0)));
        out.getKeyFrames().add(new KeyFrame(new Duration(delay), new KeyValue(Application.stage.opacityProperty(), 1)));
    }
    default void setOut(){
        out.play();
    }
    default void setIn(){
        in.play();
    }
}
