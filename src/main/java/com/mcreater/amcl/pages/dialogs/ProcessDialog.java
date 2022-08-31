package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.controls.JFXProgressBar;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Vector;

public class ProcessDialog extends AbstractDialog {
    public Vector<com.jfoenix.controls.JFXProgressBar> progresses;
    public Label l;
    public JFXDialogLayout layout;
    public ProcessDialog(int process_num, String title){
        super(Launcher.stage);
        layout = new JFXDialogLayout();
        progresses = new Vector<>();
        l = new Label();
        l.setFont(Fonts.t_f);
        VBox b = new VBox();
        b.setSpacing(10);
        for (int i = 0;i < process_num;i++){
            com.jfoenix.controls.JFXProgressBar bar = JFXProgressBar.createProgressBar(-1.0D);
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
}
