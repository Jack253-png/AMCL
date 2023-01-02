package com.mcreater.amcl.pages.dialogs.account.microsoft;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;

import static com.mcreater.amcl.Launcher.stage;

public class MicrosoftModifyDialog extends AbstractDialog {
    JFXButton finish;
    public void setFinish(EventHandler<ActionEvent> handler) {
        finish.setOnAction(handler);
    }
    public MicrosoftModifyDialog(String title) {
        super(stage);
        setTitle(title);
        JFXDialogLayout layout = new JFXDialogLayout();

        Label label = new Label(title);
        label.setFont(Fonts.s_f);

        finish = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        finish.setFont(Fonts.t_f);

        ThemeManager.loadNodeAnimations(label, finish);

        layout.setHeading(label);
        layout.setActions(finish);
        setContent(layout);
    }
}
