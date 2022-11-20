package com.mcreater.amcl.pages.dialogs.account;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.items.RadioButtonGroupItem;
import com.mcreater.amcl.controls.items.StringButtonItem;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class OfflineUserCustomSkinDialog extends AbstractDialog {
    StringButtonItem skinItem;
    StringButtonItem capeItem;
    JFXButton cancel;
    JFXButton event;
    public final RadioButtonGroupItem group;
    private final SimpleStringProperty skinPath = new SimpleStringProperty();
    private final SimpleStringProperty capePath = new SimpleStringProperty();
    public String getSkinPath() {
        return skinPath.get();
    }
    public String getCapePath() {
        return capePath.get();
    }
    public void setSkinPath(String skin) {
        skinPath.set(skin);
    }
    public void setCapePath(String cape) {
        capePath.set(cape);
    }
    public int getSelectedModelType() {
        return group.cont.getSelectedItem();
    }
    public void setCancel(EventHandler<ActionEvent> handler) {
        cancel.setOnAction(handler);

    }
    public void setEvent(EventHandler<ActionEvent> handler) {
        event.setOnAction(handler);
    }
    public OfflineUserCustomSkinDialog(String title) {
        super(Launcher.stage);
        JFXDialogLayout layout = new JFXDialogLayout();

        skinItem = new StringButtonItem(Launcher.languageManager.get("ui.userselectpage.skin.select"), 300);
        capeItem = new StringButtonItem(Launcher.languageManager.get("ui.userselectpage.cape.select"), 300);

        skinItem.cont.setEditable(false);
        capeItem.cont.setEditable(false);

        skinPath.addListener((observable, oldValue, newValue) -> skinItem.cont.setText(newValue));
        capePath.addListener((observable, oldValue, newValue) -> capeItem.cont.setText(newValue));

        skinItem.title.setOnAction(event -> {
            try {
                File image = genChooser(ImageType.SKIN).showOpenDialog(Launcher.stage);
                BufferedImage image1 = ImageIO.read(image);
                if (image1 == null) throw new NullPointerException("Broken image");
                int pr = image1.getWidth() / image1.getHeight();
                if (pr == 2 || pr == 1) {
                    skinPath.set(image.getAbsolutePath());
                }
                else throw new IOException("Wrong image size.");
            }
            catch (Exception e) {
                SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.userselectpage.skin.select"));
            }
        });

        capeItem.title.setOnAction(event -> {
            try {
                File image = genChooser(ImageType.CAPE).showOpenDialog(Launcher.stage);
                if (ImageIO.read(image) == null) throw new NullPointerException("Broken image");
                capePath.set(image.getAbsolutePath());
            }
            catch (Exception e) {
                SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.userselectpage.cape.select"));
            }
        });

        group = new RadioButtonGroupItem("  " + Launcher.languageManager.get("ui.userselectpage.custom.model"), 300, Orientation.HORIZONTAL, "Steve", "Alex");

        VBox box = new VBox(skinItem, capeItem, group);
        box.setAlignment(Pos.TOP_LEFT);
        box.setSpacing(15);

        Label t = setFont(new Label(title), Fonts.s_f);

        event = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        cancel = new JFXButton(Launcher.languageManager.get("ui.userselectpage.cancel"));
        ThemeManager.loadButtonAnimates(t, event, cancel, skinItem, capeItem, group);
        layout.setActions(event, cancel);
        layout.setBody(box);
        layout.setHeading(t);
        setContent(layout);
    }
    private enum ImageType {
        SKIN,
        CAPE
    }
    private FileChooser genChooser(ImageType type) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(type == ImageType.SKIN ? Launcher.languageManager.get("ui.userselectpage.skin.select") : Launcher.languageManager.get("ui.userselectpage.cape.select"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(Launcher.languageManager.get("ui.userselectpage.custom.file.desc"), "*.png"));
        return chooser;
    }
}
