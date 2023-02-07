package com.mcreater.amcl.controls;

import com.jfoenix.controls.JFXCheckBox;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.modApi.common.AbstractModFileModel;
import com.mcreater.amcl.pages.interfaces.Fonts;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Date;
import java.util.Vector;

import static com.mcreater.amcl.util.StringUtils.parseDate;

public class RemoteModFile extends HBox implements Comparable<RemoteModFile> {
    public JFXCheckBox checkBox;
    public AbstractModFileModel model;
    public String version;

    public RemoteModFile(AbstractModFileModel model, String version) {
        this.model = model;
        this.version = version;
        checkBox = new JFXCheckBox();
        if (model.isCurseFile()) {
            if (model.toCurseFile().releaseType == 1) {
                checkBox.setCheckedColor(Color.LIGHTGREEN);
                checkBox.setUnCheckedColor(Color.LIGHTGREEN);
            } else if (model.toCurseFile().releaseType == 2) {
                checkBox.setCheckedColor(Color.LIGHTBLUE);
                checkBox.setUnCheckedColor(Color.LIGHTBLUE);
            } else {
                checkBox.setCheckedColor(Color.RED);
                checkBox.setUnCheckedColor(Color.RED);
            }
        } else {
            if (model.toModrinthFile().version_type.equals("release")) {
                checkBox.setCheckedColor(Color.LIGHTGREEN);
                checkBox.setUnCheckedColor(Color.LIGHTGREEN);
            } else if (model.toModrinthFile().version_type.equals("beta")) {
                checkBox.setCheckedColor(Color.LIGHTBLUE);
                checkBox.setUnCheckedColor(Color.LIGHTBLUE);
            } else {
                checkBox.setCheckedColor(Color.RED);
                checkBox.setUnCheckedColor(Color.RED);
            }
        }

        checkBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        Label name = new Label();
        name.setText(model.isCurseFile() ? model.toCurseFile().displayName : model.toModrinthFile().name);
        name.setFont(Fonts.s_f);

        Label loaders = new Label(Launcher.languageManager.get("ui.mod.loader") + (model.isCurseFile() ? String.join(", ", getModLoaders(model.toCurseFile().gameVersions, true)) : String.join(", ", model.toModrinthFile().loaders)));
        loaders.setFont(Fonts.t_f);
        Label versions = new Label(Launcher.languageManager.get("ui.mod.version") + (model.isCurseFile() ? String.join(", ", getModLoaders(model.toCurseFile().gameVersions, false)) : String.join(", ", model.toModrinthFile().game_versions)));
        versions.setFont(Fonts.t_f);

        VBox v = new VBox();
        v.getChildren().addAll(name, loaders, versions);
        this.getChildren().addAll(checkBox, v);
        this.setAlignment(Pos.TOP_LEFT);
    }

    public static Vector<String> getModLoaders(Vector<String> a, boolean b) {
        Vector<String> loaders = new Vector<>();
        for (String s : a) {
            if (b) {
                if (!s.contains(".")) {
                    loaders.add(s);
                }
            } else {
                if (s.contains(".")) {
                    loaders.add(s);
                }
            }
        }
        return loaders;
    }

    public int compareTo(@NotNull RemoteModFile aLong) {
        Date time1, time2;
        try {
            if (model.isCurseFile()) {
                time1 = parseDate(this.model.toCurseFile().fileName);
                time2 = parseDate(aLong.model.toCurseFile().fileDate);
            } else {
                time1 = parseDate(this.model.toModrinthFile().date_published);
                time2 = parseDate(aLong.model.toModrinthFile().date_published);
            }

        } catch (ParseException e) {
            return 0;
        }
        if (time1.after(time2)) {
            return -1;
        } else {
            return 1;
        }
    }
}
