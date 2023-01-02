package com.mcreater.amcl.pages.dialogs.commons;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class SimpleDialogCreater {
    public static void create(String Title, String HeaderText, String ContentText) {
        FXUtils.Platform.runLater(() -> createImpl(Title, HeaderText, ContentText));
    }
    public static void createWithNoSync(String Title, String HeaderText, String ContentText) {
        createImpl(Title, HeaderText, ContentText);
    }
    private static void createImpl(String titleString, String HeaderText, String ContentText){
        AbstractDialog alert = new AbstractDialog(Launcher.stage) {};
        alert.setTitle(titleString);

        JFXDialogLayout layout = new JFXDialogLayout();
        Label title = setFont(new Label(titleString), Fonts.s_f);
        Label body = setFont(new Label(HeaderText + "\n" + ContentText), Fonts.t_f);
        layout.setHeading(title);
        layout.setBody(body);

        JFXButton addButton = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        addButton.setFont(Fonts.t_f);
        addButton.setDefaultButton(true);
        addButton.setOnAction(addEvent -> alert.close());
        layout.setActions(addButton);

        alert.setContent(layout);
        ThemeManager.loadNodeAnimations(title, body, addButton);
        alert.showAndWait();
    }
    public static void exception(Throwable cause, String subtitle) {
        FXUtils.Platform.runLater(() -> exceptionImpl(cause, subtitle));
    }
    public static void exception(Throwable cause) {
        exception(cause, Launcher.languageManager.get("ui.dialogs.exception.sub"));
    }
    private static void exceptionImpl(Throwable cause, String subtitle) {
        cause.printStackTrace();
        AbstractDialog alert = new AbstractDialog(Launcher.stage) {};

        StringWriter writer = new StringWriter();
        PrintWriter printer = new PrintWriter(writer);
        cause.printStackTrace(printer);

        VBox box = new VBox();
        box.setSpacing(2);
        int width = 180;
        for (String j : writer.getBuffer().toString().split("\n")) {
            try {
                Object o = Class.forName("sun.font.FontDesignMetrics").getDeclaredMethod("getMetrics", java.awt.Font.class).invoke(null, Fonts.awt_t_f);
                int t = ((FontMetrics) o).stringWidth(j);
                if (t > width) width = t;
            }
            catch (Exception ignored){}
        }
        width += 20;
        for (String i : writer.getBuffer().toString().split("\n")) {
            Label stack = new Label(i);
            stack.setFont(Fonts.t_f);
            box.getChildren().add(stack);
        }

        FXUtils.ControlSize.setWidth(box, width);

        AdvancedScrollPane page = new AdvancedScrollPane(400, 300, box, false);
        page.setId("opc");
        page.lThread.stop();

        ThemeManager.applyNode(page);

        JFXDialogLayout layout = new JFXDialogLayout();
        Label title = setFont(new Label(Launcher.languageManager.get("ui.common.exception.title") + " - " + subtitle), Fonts.s_f);
        layout.setHeading(title);

        JFXButton addButton = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        addButton.setFont(Fonts.t_f);
        addButton.setDefaultButton(true);
        addButton.setOnAction(addEvent -> alert.close());
        layout.setActions(addButton);
        layout.setBody(page);

        alert.setContent(layout);
        ThemeManager.loadNodeAnimations(title, addButton);
        alert.showAndWait();
    }
    private static Label setFont(Label l, Font font){
        l.setFont(font);
        return l;
    }
}
