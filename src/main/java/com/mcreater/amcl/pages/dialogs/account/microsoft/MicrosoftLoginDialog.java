package com.mcreater.amcl.pages.dialogs.account.microsoft;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.utils.JFXSmoothScroll;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static com.mcreater.amcl.Launcher.stage;

public class MicrosoftLoginDialog extends AbstractDialog {
    JFXButton cancel;
    JFXButton login;
    JFXSpinner bar;
    Label microsoftValidate;
    public void setCancelEvent(EventHandler<ActionEvent> event) {
        cancel.setOnAction(event);
    }
    public void setLoginEvent(EventHandler<ActionEvent> event) {
        login.setOnAction(event);
    }
    public MicrosoftLoginDialog(String title) {
        super(stage);
        JFXDialogLayout layout = new JFXDialogLayout();
        cancel = new JFXButton(Launcher.languageManager.get("ui.userselectpage.cancel"));
        cancel.setFont(Fonts.t_f);

        login = new JFXButton(Launcher.languageManager.get("ui.userselectpage.login.browser"));
        login.setFont(Fonts.t_f);
        login.setOnAction(event -> {
            login.setDisable(true);
            new Thread(() -> {
                try {
                    MicrosoftUser user = login();
                    System.out.println(user);
                } catch (Exception e) {
                    SimpleDialogCreater.exception(e);
                }
            }).start();
        });

        Label label = new Label(title);
        label.setFont(Fonts.s_f);

        microsoftValidate = new Label();
        microsoftValidate.setFont(Fonts.t_f);

        bar = new JFXSpinner();

        FXUtils.ControlSize.set(bar, 20, 20);
        ThemeManager.applyNode(bar);

        HBox pane = new HBox(bar);
        pane.setAlignment(Pos.TOP_LEFT);
        FXUtils.ControlSize.setWidth(pane, 200);

        ThemeManager.loadButtonAnimates(cancel, login, microsoftValidate, label, bar);
        layout.setBody(new VBox(microsoftValidate));
        layout.setActions(pane, cancel, login);
        layout.setHeading(label);
        setContent(layout);
    }
    public synchronized MicrosoftUser login() throws Exception {
        MSAuth.AUTH_INSTANCE.setUpdater((integer, s) -> {
            System.out.println(integer.doubleValue() / 100 + " " + s);
            JFXSmoothScroll.smoothScrollBarToValue(bar, integer.doubleValue() / 100);
        });
        return MSAuth.AUTH_INSTANCE.generateDeviceCode(s -> FXUtils.Platform.runLater(() -> microsoftValidate.setText(s)));
    }
}
