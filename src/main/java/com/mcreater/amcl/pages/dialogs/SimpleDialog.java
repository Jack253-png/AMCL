package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.StableMain;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;

public class SimpleDialog extends JFXAlert<String> {
    public static enum MessageType {
        OK("ui.dialogs.information.ok.name"),
        CONTINUE("ui.dialogs.information.continue.name"),
        QUIT("ui.dialogs.information.quit.name");
        public final String lang_key;
        MessageType(String lang_key){
            this.lang_key = lang_key;
        }
    }

    public SimpleDialog(String title, String message, MessageType type, EventHandler<ActionEvent> onAction){
        JFXButton quit = new JFXButton(StableMain.manager.get(type.lang_key));
        quit.setFont(Fonts.t_f);
        quit.setOnAction(onAction);

        Label mess = AbstractDialog.setFont(new Label(message), Fonts.t_f);

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setBody(mess);
        layout.setActions(quit);

        ThemeManager.loadButtonAnimates(quit, mess);

        setTitle(title);
        setContent(layout);
    }
}
