package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Application;
import com.mcreater.amcl.pages.interfaces.Fonts;
import javafx.scene.control.Label;
import javafx.stage.Modality;

public class FastInfomation {
    public static void create(String Title, String HeaderText, String ContentText){
        JFXAlert<String> alert = new JFXAlert<>(Application.stage);
        alert.setAnimation(JFXAlertAnimation.BOTTOM_ANIMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(Title));
        layout.setBody(new Label(HeaderText + "\n" + ContentText));

        JFXButton addButton = new JFXButton(Application.languageManager.get("ui.dialogs.information.ok.name"));
        addButton.setFont(Fonts.t_f);
        addButton.setDefaultButton(true);
        addButton.setOnAction(addEvent -> {
            alert.close();
        });
        layout.setActions(addButton);
        alert.setContent(layout);
        alert.showAndWait();
    }
}
