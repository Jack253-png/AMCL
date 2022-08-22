package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class AbstractDialog extends JFXAlert<String> {
    public AbstractDialog(Stage stage) {
        super(stage);
        this.setAnimation(JFXAlertAnimation.BOTTOM_ANIMATION);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setOverlayClose(false);
    }

    public void Create(){
        Platform.runLater(this::show);
    }
    static Label setFont(Label l, Font font){
        l.setFont(font);
        return l;
    }
}
