package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.items.ListItem;
import com.mcreater.amcl.controls.items.StringItem;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.J8Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class EditAccountContentDialog extends AbstractDialog {
    JFXButton addButton;
    EventHandler<ActionEvent> event = event -> {};
    EventHandler<ActionEvent> cancel = event -> {};
    Label onlineUser;
    Label custom;
    public StringItem item;
    public ListItem<Label> item2;
    JFXButton cancelButton;

    public void setEvent(EventHandler<ActionEvent> event){
        this.event = event;
        addButton.setOnAction(this.event);
    }

    public void setCancel(EventHandler<ActionEvent> cancel) {
        this.cancel = cancel;
        cancelButton.setOnAction(this.cancel);
    }

    public EditAccountContentDialog(String title){
        super(Launcher.stage);

        item = new StringItem(Launcher.languageManager.get("ui.userselectpage.account.edit.username.name"), 400);
        item2 = new ListItem<>(Launcher.languageManager.get("ui.userselectpage.login.offlineskin"), 400);

        onlineUser = new Label(Launcher.languageManager.get("ui.userselectpage.skin.online"));
        onlineUser.setFont(Fonts.t_f);
        custom = new Label(Launcher.languageManager.get("ui.userselectpage.skin.custom"));
        custom.setFont(Fonts.t_f);
        for (String s : J8Utils.createList("Steve", "Alex")){
            Label l = new Label(s);
            l.setFont(Fonts.t_f);
            item2.cont.getItems().add(l);
        }
        item2.cont.getItems().addAll(onlineUser, custom);

        item.cont.setBorder(FXUtils.generateBorder(Color.TRANSPARENT, BorderStrokeStyle.SOLID, false, false, false, false, 1));
        item2.cont.setBorder(FXUtils.generateBorder(Color.TRANSPARENT, BorderStrokeStyle.SOLID, false, false, false, false, 1));

        JFXDialogLayout layout = new JFXDialogLayout();
        Label head = setFont(new Label(title), Fonts.s_f);
        ThemeManager.loadButtonAnimates(head, item, item2);
        layout.setHeading(head);
        layout.setBody(new VBox(item, item2));
        addButton = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        addButton.setFont(Fonts.t_f);
        addButton.setDefaultButton(true);
        addButton.setOnAction(event);
        cancelButton = new JFXButton(Launcher.languageManager.get("ui.userselectpage.cancel"));
        cancelButton.setFont(Fonts.t_f);
        cancelButton.setDefaultButton(true);
        cancelButton.setOnAction(cancel);
        layout.setActions(cancelButton, addButton);
        ThemeManager.loadButtonAnimates(cancelButton, addButton);
        this.setOnHidden(event -> {});
        this.setOnHiding(event -> {});
        this.setContent(layout);
    }
}
