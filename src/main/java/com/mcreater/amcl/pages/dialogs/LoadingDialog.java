package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;

import java.util.Vector;

public class LoadingDialog extends JFXAlert<String> {
    public Vector<JFXProgressBar> progresses;
    public Label l;
    public JFXSpinner spinner;
    public LoadingDialog(String title){
        super(Launcher.stage);
        this.setAnimation(JFXAlertAnimation.BOTTOM_ANIMATION);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setOverlayClose(false);
        JFXDialogLayout layout = new JFXDialogLayout();
        progresses = new Vector<>();
        l = new Label();
        l.setFont(Fonts.t_f);
        spinner = new JFXSpinner();
        VBox b = new VBox();
        b.setSpacing(10);
        b.setAlignment(Pos.CENTER);
        b.getChildren().add(spinner);
        Label title1 = setFont(new Label(title), Fonts.s_f);
        layout.setHeading(title1);
        layout.setBody(b);
        this.setOnHidden(event -> {});
        this.setOnHiding(event -> {});
        this.setContent(layout);
        ThemeManager.loadButtonAnimates(title1, spinner);
    }
    public void Create(){
        Platform.runLater(this::show);
    }
    private static Label setFont(Label l, Font font){
        l.setFont(font);
        return l;
    }
}
