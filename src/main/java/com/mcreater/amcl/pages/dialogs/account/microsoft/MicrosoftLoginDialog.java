package com.mcreater.amcl.pages.dialogs.account.microsoft;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.utils.JFXSmoothScroll;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.controls.JFXProgressBar;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import static com.mcreater.amcl.Launcher.stage;

public class MicrosoftLoginDialog extends AbstractDialog {
    JFXButton cancel;
    JFXButton login;
    JFXProgressBar bar;
    Label microsoftValidate;
    public void setCancelEvent(EventHandler<ActionEvent> event) {
        cancel.setOnAction(event);
    }
    public void setLoginEvent(EventHandler<ActionEvent> event) {
        login.setOnAction(event);
    }
    public MicrosoftLoginDialog() {
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

        microsoftValidate = new Label();
        microsoftValidate.setFont(Fonts.t_f);

        bar = new JFXProgressBar(0);
        bar.setId("game-memory-up");
        ThemeManager.applyNode(bar);

        ThemeManager.loadButtonAnimates(cancel, login, microsoftValidate, bar);
        layout.setBody(new VBox(bar, microsoftValidate));
        layout.setActions(cancel, login);
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
