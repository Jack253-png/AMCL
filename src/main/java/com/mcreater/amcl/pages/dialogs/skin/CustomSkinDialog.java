package com.mcreater.amcl.pages.dialogs.skin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.items.ListItem;
import com.mcreater.amcl.controls.items.StringButtonItem;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
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
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CustomSkinDialog extends AbstractDialog {
    EventHandler<ActionEvent> event = event -> {};
    EventHandler<ActionEvent> cancel = event -> {};
    JFXButton addButton;
    JFXButton cancelButton;
    public String skin;
    public String cape;
    public String elytra;
    public ListItem<Label> changeModelSelect;
    public StringButtonItem skin_ui;
    public StringButtonItem cape_ui;
    public StringButtonItem elytra_ui;
    public void setEvent(EventHandler<ActionEvent> event){
        this.event = event;
        addButton.setOnAction(this.event);
    }

    public void setCancel(EventHandler<ActionEvent> cancel) {
        this.cancel = cancel;
        cancelButton.setOnAction(this.cancel);
    }

    public CustomSkinDialog(String title){
        super(Launcher.stage);
        JFXDialogLayout layout = new JFXDialogLayout();

        changeModelSelect = new ListItem<>(Launcher.languageManager.get("ui.userselectpage.custom.model"), 400);
        changeModelSelect.cont.setBorder(FXUtils.generateBorder(Color.TRANSPARENT, BorderStrokeStyle.SOLID, false, false, false ,false, 1));
        skin_ui = new StringButtonItem(Launcher.languageManager.get("ui.userselectpage.skin.select"), 400);
        cape_ui = new StringButtonItem(Launcher.languageManager.get("ui.userselectpage.cape.select"), 400);
        elytra_ui = new StringButtonItem(Launcher.languageManager.get(Launcher.languageManager.get("ui.userselectpage.elytra.select")), 400);

        for (String s : J8Utils.createList("Steve", "Alex")){
            Label l = new Label(s);
            l.setFont(Fonts.t_f);
            changeModelSelect.cont.getItems().add(l);
        }
        changeModelSelect.cont.getSelectionModel().select(0);

        skin_ui.cont.setEditable(false);
        cape_ui.cont.setEditable(false);

        Runnable s = () -> SimpleDialogCreater.create(Launcher.languageManager.get("ui.userselectpage.custom.skinwrong.title"), Launcher.languageManager.get("ui.userselectpage.custom.skinwrong.content"), "");

        skin_ui.title.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(Launcher.languageManager.get("ui.userselectpage.custom.file.desc"), "*.png"));
            File f = chooser.showOpenDialog(Launcher.stage);
            if (f != null){
                try {
                    BufferedImage image = ImageIO.read(f);
                    if (image != null) {
                        if (image.getWidth() % 32 == 0 && image.getWidth() / 32 > 0 &&
                                (image.getHeight() == image.getWidth() / 2 || image.getHeight() == image.getWidth())) {
                            this.skin = f.getAbsolutePath();
                            skin_ui.cont.setText(this.skin);
                        } else {
                            s.run();
                        }
                    }
                    else {
                        s.run();
                    }
                } catch (IOException ignored) {
                    s.run();
                }
            }
            else {
                this.skin = null;
                skin_ui.cont.setText("");
            }
        });
        cape_ui.title.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(Launcher.languageManager.get("ui.userselectpage.custom.file.desc"), "*.png"));
            File f = chooser.showOpenDialog(Launcher.stage);
            if (f != null){
                try {
                    if (ImageIO.read(f) != null) {
                        this.cape = f.getAbsolutePath();
                        cape_ui.cont.setText(this.cape);
                        this.elytra = f.getAbsolutePath();
                        elytra_ui.cont.setText(this.elytra);
                    }
                    else {
                        s.run();
                    }
                } catch (IOException ignored) {
                    s.run();
                }
            }
            else {
                this.cape = null;
                cape_ui.cont.setText("");
                this.elytra = null;
                elytra_ui.cont.setText("");
            }
        });
        elytra_ui.title.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(Launcher.languageManager.get("ui.userselectpage.custom.file.desc"), "*.png"));
            File f = chooser.showOpenDialog(Launcher.stage);
            if (f != null){
                try {
                    if (ImageIO.read(f) != null) {
                        this.elytra = f.getAbsolutePath();
                        elytra_ui.cont.setText(this.elytra);
                        this.cape = f.getAbsolutePath();
                        cape_ui.cont.setText(this.cape);
                    }
                    else {
                        s.run();
                    }
                } catch (IOException ignored) {
                    s.run();
                }
            }
            else {
                this.elytra = null;
                elytra_ui.cont.setText("");
                this.cape = null;
                cape_ui.cont.setText("");
            }
        });

        Label head = setFont(new Label(title), Fonts.s_f);
        ThemeManager.loadButtonAnimates(head, changeModelSelect, skin_ui, cape_ui, elytra_ui);
        layout.setHeading(head);
        layout.setBody(new VBox(changeModelSelect, skin_ui, cape_ui, elytra_ui));
        addButton = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        addButton.setFont(Fonts.t_f);
        addButton.setDefaultButton(true);
        addButton.setOnAction(event);
        cancelButton = new JFXButton(Launcher.languageManager.get("ui.userselectpage.cancel"));
        cancelButton.setFont(Fonts.t_f);
        cancelButton.setDefaultButton(true);
        cancelButton.setOnAction(cancel);
        layout.setActions(cancelButton, addButton);

        ThemeManager.loadButtonAnimates(addButton, cancelButton);
        this.setOnHidden(event -> {});
        this.setOnHiding(event -> {});
        this.setContent(layout);
    }
}
