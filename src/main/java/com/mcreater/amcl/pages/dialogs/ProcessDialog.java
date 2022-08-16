package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXProgressBar;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;

import java.util.Vector;

public class ProcessDialog extends JFXAlert<String> {
    public Vector<JFXProgressBar> progresses;
    public Label l;
    public ProcessDialog(int process_num, String title){
        super(Launcher.stage);
        this.setAnimation(JFXAlertAnimation.BOTTOM_ANIMATION);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setOverlayClose(false);
        JFXDialogLayout layout = new JFXDialogLayout();
        progresses = new Vector<>();
        l = new Label();
        l.setFont(Fonts.t_f);
        VBox b = new VBox();
        b.setSpacing(10);
        for (int i = 0;i < process_num;i++){
            JFXProgressBar bar = new JFXProgressBar(-1.0D);
            progresses.add(bar);
            b.getChildren().add(bar);
            ThemeManager.loadButtonAnimates(bar);
        }
        Label head = setFont(new Label(title), Fonts.s_f);
        ThemeManager.loadButtonAnimates(l, head);
        b.getChildren().add(l);
        layout.setHeading(head);
        layout.setBody(b);
        this.setOnHidden(event -> {});
        this.setOnHiding(event -> {});
        this.setContent(layout);
    }
    public void setAll(int progress){
        Platform.runLater(() -> progresses.forEach(bar -> bar.setProgress((double) progress / 100)));
    }
    public void setV(int index, int progress){
        Platform.runLater(() -> this.progresses.get(index).setProgress((double) progress / 100));
    }
    public void setV(int index, int progress, String s){
        Platform.runLater(() -> this.progresses.get(index).setProgress((double) progress / 100));
        Platform.runLater(() -> l.setText(s));
    }
    public void Create(){
        Platform.runLater(this::show);
    }
    private static Label setFont(Label l, Font font){
        l.setFont(font);
        return l;
    }
}
