package com.mcreater.amcl.pages.dialogs.account.offline;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.users.OffLineUser;
import com.mcreater.amcl.controls.items.ListItem;
import com.mcreater.amcl.controls.items.StringItem;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

public class OfflineUserModifyDialog extends AbstractDialog {
    OffLineUser user;
    JFXButton okButton;
    JFXButton cancelButton;
    StringItem userName;
    ListItem<Label> userSkin;
    public void setAction(EventHandler<ActionEvent> handler) {
        okButton.setOnAction(handler);
    }
    public void setCancel(EventHandler<ActionEvent> handler) {
        cancelButton.setOnAction(handler);
    }
    public OfflineUserModifyDialog(@NotNull OffLineUser user, String title) {
        super(Launcher.stage);
        setTitle(title);
        this.user = user;

        JFXDialogLayout layout = new JFXDialogLayout();

        okButton = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.continue.name"));
        cancelButton = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.quit.name"));
        userName = new StringItem(Launcher.languageManager.get("ui.userselectpage.nameItem"), 300);
        userName.cont.setText(user.username);

        userSkin = new ListItem<>(Launcher.languageManager.get("ui.userselectpage.login.offlineskin"), 300);

        userSkin.cont.getItems().addAll(
                setFont(new Label("Steve"), Fonts.t_f),
                setFont(new Label("Alex"), Fonts.t_f),
                setFont(new Label(Launcher.languageManager.get("ui.userselectpage.skin.online")), Fonts.t_f),
                setFont(new Label(Launcher.languageManager.get("ui.userselectpage.skin.custom")), Fonts.t_f)
        );
        userSkin.cont.getSelectionModel().select(getUserSkinType());
        ThemeManager.applyNode(userSkin.cont);

        VBox container = new VBox(userName, userSkin);
        container.setSpacing(15);

        Label t = setFont(new Label(title), Fonts.s_f);

        layout.setBody(container);
        layout.setActions(cancelButton, okButton);
        layout.setHeading(t);
        ThemeManager.loadButtonAnimates(cancelButton, okButton, userName, userSkin, t);
        setContent(layout);
    }
    public String getInputedUserName() {
        return userName.cont.getText();
    }
    public int getSelection() {
        return userSkin.cont.getSelectionModel().getSelectedIndex();
    }
    private int getUserSkinType() {
        if (user.skinUseable() || user.capeUseable()) {
            return 3;
        }
        switch (user.uuid.toLowerCase()) {
            case "000000000000300a9d83f9ec9e7fae8e":
                return 0;
            case "000000000000300a9d83f9ec9e7fae8d":
                return 1;
            default:
                return 2;
        }
    }
}
