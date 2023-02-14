package com.mcreater.amcl.controls;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.lang.LanguageManager;
import com.mcreater.amcl.lang.ModTransitions;
import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.builders.ThreadBuilder;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class LocalMod extends HBox {
    public Label name;
    public Label translate;
    public String path;
    public ImageView icon;
    public final CommonModInfoModel model;

    public LocalMod(CommonModInfoModel model) {
        this.model = model;
        this.path = model.path;
        name = new AdvancedLabel(Objects.equals(model.name, "") ? "null" : model.name);
        name.setFont(Fonts.s_f);
        translate = new AdvancedLabel();
        icon = new ImageView();
        icon.setFitWidth(50);
        icon.setFitHeight(50);

        ThreadBuilder.createBuilder()
                .runTarget(() -> {
                    if (model.icon != null) {
                        try {
                            Image image = new Image(FileUtils.ZipUtil.readBinaryFileInZip(path, model.icon));
                            FXUtils.Platform.runLater(() -> icon.setImage(image));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .name("Icon process thread")
                .buildAndRun();
        refreshLanguage();

        setAlignment(Pos.TOP_LEFT);
        setSpacing(15);
        this.getChildren().addAll(icon, new VBox(name, translate));
    }

    public void refreshLanguage() {
        ThreadBuilder.createBuilder()
                .runTarget(() -> {
                    if (Launcher.languageManager.getLanguage() == LanguageManager.LanguageType.CHINESE) {
                        try {
                            String s = ModTransitions.MODS.translateString(model.modid, model.name);
                            FXUtils.Platform.runLater(() -> translate.setText(s));
                        } catch (Exception ignored) {

                        }
                    }
                })
                .name("Mod translation process thread")
                .buildAndRun();
    }
}
