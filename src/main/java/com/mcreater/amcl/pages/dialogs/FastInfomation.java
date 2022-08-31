package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Modality;

import java.lang.reflect.Field;

public class FastInfomation {
    public static void create(String Title, String HeaderText, String ContentText){
        JFXAlert<String> alert = new JFXAlert<>(Launcher.stage);
        alert.setAnimation(JFXAlertAnimation.BOTTOM_ANIMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);

        JFXDialogLayout layout = new JFXDialogLayout();
        Label title = setFont(new Label(Title), Fonts.s_f);
        Label body = setFont(new Label(HeaderText + "\n" + ContentText), Fonts.t_f);
        layout.setHeading(title);
        layout.setBody(body);

        JFXButton addButton = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        addButton.setFont(Fonts.t_f);
        addButton.setDefaultButton(true);
        addButton.setOnAction(addEvent -> alert.close());
        layout.setActions(addButton);

        alert.setContent(layout);
        ThemeManager.loadButtonAnimates(title, body, addButton);
        alert.showAndWait();
    }
    private static Label setFont(Label l, Font font){
        l.setFont(font);
        return l;
    }
}
