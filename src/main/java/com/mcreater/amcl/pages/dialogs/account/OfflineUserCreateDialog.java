package com.mcreater.amcl.pages.dialogs.account;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.items.ListItem;
import com.mcreater.amcl.controls.items.StringItem;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class OfflineUserCreateDialog extends AbstractDialog {
    JFXButton create;
    JFXButton cancel;
    StringItem userName;
    ListItem<Label> userSkin;
    public void setCreate(EventHandler<ActionEvent> handler) {
        create.setOnAction(handler);
    }
    public void setCancel(EventHandler<ActionEvent> handler) {
        cancel.setOnAction(handler);
    }
    public OfflineUserCreateDialog(String title) {
        super(Launcher.stage);
        JFXDialogLayout layout = new JFXDialogLayout();

        userName = new StringItem(Launcher.languageManager.get("ui.userselectpage.nameItem"), 300);
        userSkin = new ListItem<>(Launcher.languageManager.get("ui.userselectpage.login.offlineskin"), 300);

        userSkin.cont.getItems().addAll(
                setFont(new Label("Steve"), Fonts.t_f),
                setFont(new Label("Alex"), Fonts.t_f),
                setFont(new Label(Launcher.languageManager.get("ui.userselectpage.skin.online")), Fonts.t_f),
                setFont(new Label(Launcher.languageManager.get("ui.userselectpage.skin.custom")), Fonts.t_f)
        );

        create = new JFXButton(Launcher.languageManager.get("ui.userselectpage.user.create"));
        cancel = new JFXButton(Launcher.languageManager.get("ui.userselectpage.cancel"));

        Label t = setFont(new Label(title), Fonts.s_f);

        VBox content = new VBox(userName, userSkin);
        content.setAlignment(Pos.TOP_LEFT);
        content.setSpacing(10);

        ThemeManager.applyNode(userSkin.cont);
        userSkin.cont.getSelectionModel().selectFirst();

        ThemeManager.loadButtonAnimates(cancel, create, t, userName, userSkin);
        layout.setHeading(t);
        layout.setActions(cancel, create);
        layout.setBody(content);
        setContent(layout);
    }
    public int getSelected() {
        return userSkin.cont.getSelectionModel().getSelectedIndex();
    }
    public String getInputedName() {
        return userName.cont.getText();
    }
}
