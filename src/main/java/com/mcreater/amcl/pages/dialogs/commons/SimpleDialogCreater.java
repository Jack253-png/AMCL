package com.mcreater.amcl.pages.dialogs.commons;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.SettingPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Modality;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SimpleDialogCreater {
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
    public static void exception(Throwable cause) {
        cause.printStackTrace();
        JFXAlert<String> alert = new JFXAlert<>(Launcher.stage);
        alert.setAnimation(JFXAlertAnimation.BOTTOM_ANIMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);

        StringWriter writer = new StringWriter();
        PrintWriter printer = new PrintWriter(writer);
        cause.printStackTrace(printer);
        Label stack = new Label(writer.getBuffer().toString());
        stack.setFont(Fonts.ts_f);

        SettingPage page = new SettingPage(400, 300, new Pane(stack), false);
        page.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        page.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        page.lThread.stop();
        page.getStylesheets().add(String.format(ThemeManager.getPath(), "SettingPage"));

        JFXDialogLayout layout = new JFXDialogLayout();
        Label title = setFont(new Label(Launcher.languageManager.get("ui.common.exception.title")), Fonts.s_f);
        layout.setHeading(title);

        JFXButton addButton = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        addButton.setFont(Fonts.t_f);
        addButton.setDefaultButton(true);
        addButton.setOnAction(addEvent -> alert.close());
        layout.setActions(addButton);
        layout.setBody(page);

        alert.setContent(layout);
        ThemeManager.loadButtonAnimates(title, addButton);
        alert.showAndWait();
    }
    private static Label setFont(Label l, Font font){
        l.setFont(font);
        return l;
    }
}
