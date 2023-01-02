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
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.mcreater.amcl.Launcher.stage;

public class MicrosoftLoginDialog extends AbstractDialog {
    JFXButton cancel;
    JFXButton login;
    com.jfoenix.controls.JFXProgressBar bar;
    Label microsoftValidate;
    Label loginState;
    Thread loginThread;
    @NotNull Consumer<MicrosoftUser> processor = user -> {};
    public void setCancelEvent(EventHandler<ActionEvent> event) {
        cancel.setOnAction(event);
    }
    public void setLoginEvent(@NotNull Consumer<MicrosoftUser> processor) {
        this.processor = processor;
    }
    public MicrosoftLoginDialog(String title) {
        super(stage);
        setTitle(title);
        JFXDialogLayout layout = new JFXDialogLayout();
        cancel = new JFXButton(Launcher.languageManager.get("ui.userselectpage.cancel"));
        cancel.setFont(Fonts.t_f);

        login = new JFXButton(Launcher.languageManager.get("ui.userselectpage.login.browser"));
        login.setFont(Fonts.t_f);
        login.setOnAction(event -> {
            login.setDisable(true);
            loginThread = new Thread(() -> {
                try {
                    MicrosoftUser user = login();
                    processor.accept(user);
                } catch (Exception e) {
                    FXUtils.Platform.runLater(this::close);
                    SimpleDialogCreater.exception(e);
                }
                login.setDisable(false);
            });
            loginThread.start();
        });

        Label label = new Label(title);
        label.setFont(Fonts.s_f);

        microsoftValidate = new Label();
        microsoftValidate.setFont(Fonts.t_f);

        loginState = new Label();
        loginState.setFont(Fonts.t_f);

        bar = JFXProgressBar.createProgressBar();

        ThemeManager.loadNodeAnimations(cancel, login, microsoftValidate, label, bar, loginState);
        layout.setBody(new VBox(microsoftValidate, loginState));
        layout.setActions(bar, cancel, login);
        layout.setHeading(label);
        setContent(layout);
    }
    public void stopLogin() {
        if (loginThread != null) {
            loginThread.stop();
        }
    }
    public synchronized MicrosoftUser login() throws Exception {
        JFXSmoothScroll.smoothScrollBarToValue(bar, 0.05);
        MSAuth.AUTH_INSTANCE.setUpdater((integer, s) -> {
            FXUtils.Platform.runLater(() -> loginState.setText(s));
            JFXSmoothScroll.smoothScrollBarToValue(bar, integer.doubleValue() / 100);
        });
        return MSAuth.AUTH_INSTANCE.generateDeviceCode((code, url) -> FXUtils.Platform.runLater(() -> microsoftValidate.setText(Launcher.languageManager.get("ui.mslogin.token", code, url))));
    }
}
