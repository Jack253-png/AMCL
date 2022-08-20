package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.items.ListItem;
import com.mcreater.amcl.controls.items.StringButtonItem;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.J8Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CustomSkinDialog extends JFXAlert<String> {
    EventHandler<ActionEvent> event = event -> {};
    JFXButton addButton;
    public String skin;
    public String cape;
    public ListItem<Label> changeModelSelect;
    public StringButtonItem skin_ui;
    public StringButtonItem cape_ui;
    public void setEvent(EventHandler<ActionEvent> event){
        this.event = event;
        addButton.setOnAction(this.event);
    }
    private static Label setFont(Label l, Font font){
        l.setFont(font);
        return l;
    }

    public CustomSkinDialog(String title){
        super(Launcher.stage);
        this.setAnimation(JFXAlertAnimation.BOTTOM_ANIMATION);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setOverlayClose(false);
        JFXDialogLayout layout = new JFXDialogLayout();

        changeModelSelect = new ListItem<>(Launcher.languageManager.get("ui.userselectpage.custom.model"), 400);
        changeModelSelect.cont.setBorder(FXUtils.generateBorder(Color.TRANSPARENT, BorderStrokeStyle.SOLID, false, false, false ,false, 1));
        skin_ui = new StringButtonItem(Launcher.languageManager.get("ui.userselectpage.skin.select"), 400);
        cape_ui = new StringButtonItem(Launcher.languageManager.get("ui.userselectpage.cape.select"), 400);

        for (String s : J8Utils.createList("Steve", "Alex")){
            Label l = new Label(s);
            l.setFont(Fonts.t_f);
            changeModelSelect.cont.getItems().add(l);
        }
        changeModelSelect.cont.getSelectionModel().select(0);

        skin_ui.cont.setEditable(false);
        cape_ui.cont.setEditable(false);

        Runnable s = () -> FastInfomation.create(Launcher.languageManager.get("ui.userselectpage.custom.skinwrong.title"), Launcher.languageManager.get("ui.userselectpage.custom.skinwrong.content"), "");

        skin_ui.title.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Minecraft 皮肤文件", "*.png"));
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
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Minecraft 皮肤文件", "*.png"));
            File f = chooser.showOpenDialog(Launcher.stage);
            if (f != null){
                try {
                    if (ImageIO.read(f) != null) {
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
                this.skin = null;
                skin_ui.cont.setText("");
            }
        });

        Label head = setFont(new Label(title), Fonts.s_f);
        ThemeManager.loadButtonAnimates(head, changeModelSelect, skin_ui, cape_ui);
        layout.setHeading(head);
        layout.setBody(new VBox(changeModelSelect, skin_ui, cape_ui));
        addButton = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        addButton.setFont(Fonts.t_f);
        addButton.setDefaultButton(true);
        addButton.setOnAction(event);
        layout.setActions(addButton);
        this.setOnHidden(event -> {});
        this.setOnHiding(event -> {});
        this.setContent(layout);
    }
    public void Create(){
        Platform.runLater(this::show);
    }
}
