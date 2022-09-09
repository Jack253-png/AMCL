package com.mcreater.amcl.pages.dialogs.commons;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.stage.Modality;

public class InputDialog extends AbstractDialog {
    EventHandler<ActionEvent> event = event -> {};
    EventHandler<ActionEvent> cancel = event -> {};
    JFXButton addButton;
    JFXButton cancelButton;
    public JFXTextField f;
    public void setEvent(EventHandler<ActionEvent> event){
        this.event = event;
        addButton.setOnAction(this.event);
    }
    public void setCancel(EventHandler<ActionEvent> event){
        this.cancel = event;
        cancelButton.setOnAction(this.cancel);
    }
    public InputDialog(String title){
        super(Launcher.stage);
        JFXDialogLayout layout = new JFXDialogLayout();

        f = new JFXTextField();
        FXUtils.fixJFXTextField(f);

        Label head = setFont(new Label(title), Fonts.s_f);
        ThemeManager.loadButtonAnimates(f, head);
        layout.setHeading(head);
        layout.setBody(f);
        addButton = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        addButton.setFont(Fonts.t_f);
        addButton.setDefaultButton(true);
        addButton.setOnAction(event);
        cancelButton = new JFXButton(Launcher.languageManager.get("ui.userselectpage.cancel"));
        cancelButton.setFont(Fonts.t_f);
        cancelButton.setDefaultButton(true);
        cancelButton.setOnAction(cancel);
        layout.setActions(cancelButton, addButton);
        ThemeManager.loadButtonAnimates(addButton, cancelButton);
        this.setOnHidden(event -> {});
        this.setOnHiding(event -> {});
        this.setContent(layout);
    }
}
