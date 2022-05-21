package com.mcreater.amcl.pages;

import com.mcreater.amcl.HelloApplication;
import com.mcreater.amcl.config.ConfigReader;
import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Duration;
import static com.mcreater.amcl.pages.MainPage.configWriter;

import java.io.File;
import java.io.IOException;
import java.sql.Time;

public interface AnimationPage {
    FadeTransition in_old = new FadeTransition();
    Timeline in = new Timeline();
    Timeline out = new Timeline();
    boolean played = false;
    long delay = 500;
    default void set(){

        in.setCycleCount(1);
        in.getKeyFrames().clear();
        in.getKeyFrames().add(new KeyFrame(Duration.millis(1), new KeyValue(HelloApplication.stage.opacityProperty(), 1)));
        in.getKeyFrames().add(new KeyFrame(new Duration(delay), new KeyValue(HelloApplication.stage.opacityProperty(), 0)));

        out.setCycleCount(1);
        out.getKeyFrames().clear();
        out.getKeyFrames().add(new KeyFrame(Duration.millis(1), new KeyValue(HelloApplication.stage.opacityProperty(), 0)));
        out.getKeyFrames().add(new KeyFrame(new Duration(delay), new KeyValue(HelloApplication.stage.opacityProperty(), 1)));
    }
    default void setOut(){
        out.play();
    }
    default void setIn(){
        in.play();
    }
    default void setPlayed(){
        boolean played = true;
    }
}
