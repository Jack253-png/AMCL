package com.mcreater.amcl.pages.dialogs.commons;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ContinueDialog extends AbstractDialog {
    EventHandler<ActionEvent> event = event -> {};
    EventHandler<ActionEvent> cancel = event -> {};
    JFXButton addButton;
    JFXButton cancelButton;
    public void setEvent(EventHandler<ActionEvent> event){
        this.event = event;
        addButton.setOnAction(this.event);
    }

    public void setCancel(EventHandler<ActionEvent> cancel) {
        this.cancel = cancel;
        cancelButton.setOnAction(this.cancel);
    }
    public ContinueDialog(String title, String content) {
        super(Launcher.stage);
        JFXDialogLayout layout = new JFXDialogLayout();

        addButton = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        addButton.setFont(Fonts.t_f);
        addButton.setDefaultButton(true);
        addButton.setOnAction(event);
        cancelButton = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.continue.name"));
        cancelButton.setFont(Fonts.t_f);
        cancelButton.setDefaultButton(true);
        cancelButton.setOnAction(cancel);

        Label title2 = setFont(new Label(title), Fonts.s_f);
        Label content2 = setFont(new Label(content), Fonts.t_f);

        ThemeManager.loadButtonAnimates(cancelButton, addButton, title2, content2);
        layout.setActions(cancelButton, addButton);
        layout.setHeading(title2);
        layout.setBody(content2);
        setContent(layout);
    }
}
