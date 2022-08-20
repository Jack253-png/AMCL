package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Modality;

public class InputDialog extends JFXAlert<String> {
    EventHandler<ActionEvent> event = event -> {};
    JFXButton addButton;
    public JFXTextField f;
    public void setEvent(EventHandler<ActionEvent> event){
        this.event = event;
        addButton.setOnAction(this.event);
    }
    public InputDialog(String title){
        super(Launcher.stage);
        this.setAnimation(JFXAlertAnimation.BOTTOM_ANIMATION);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setOverlayClose(false);
        JFXDialogLayout layout = new JFXDialogLayout();

        f = new JFXTextField();
        f.setDisableAnimation(false);

        Label head = setFont(new Label(title), Fonts.s_f);
        ThemeManager.loadButtonAnimates(f, head);
        layout.setHeading(head);
        layout.setBody(f);
        addButton = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        addButton.setFont(Fonts.t_f);
        addButton.setDefaultButton(true);
        addButton.setOnAction(event);
        layout.setActions(addButton);
        this.setOnHidden(event -> {});
        this.setOnHiding(event -> {});
        this.setContent(layout);
    }
    public void Create(){
        Platform.runLater(this::show);
    }
    private static Label setFont(Label l, Font font){
        l.setFont(font);
        return l;
    }
}
