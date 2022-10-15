package com.mcreater.amcl.controls;

import com.jfoenix.controls.JFXCheckBox;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.curseApi.modFile.CurseModFileModel;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
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

import static com.mcreater.amcl.pages.ModDownloadPage.getTimeTick;

public class ModFile extends HBox implements Comparable<ModFile>{
    public JFXCheckBox checkBox;
    public CurseModFileModel model;
    public String version;
    public ModFile(CurseModFileModel model, String version){
        this.model = model;
        this.version = version;
        checkBox = new JFXCheckBox();
        if (model.releaseType == 1){
            checkBox.setCheckedColor(Color.LIGHTGREEN);
            checkBox.setUnCheckedColor(Color.LIGHTGREEN);
        }
        else if (model.releaseType == 2){
            checkBox.setCheckedColor(Color.LIGHTBLUE);
            checkBox.setUnCheckedColor(Color.LIGHTBLUE);
        }
        else{
            checkBox.setCheckedColor(Color.RED);
            checkBox.setUnCheckedColor(Color.RED);
        }

        checkBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        Label name = new Label(model.fileName);
        name.setFont(Fonts.s_f);

        Label loaders = new Label(Launcher.languageManager.get("ui.mod.loader") + String.join(", ", getModLoaders(model.gameVersions, true)));
        loaders.setFont(Fonts.t_f);
        Label versions = new Label(Launcher.languageManager.get("ui.mod.version") + String.join(", ", getModLoaders(model.gameVersions, false)));
        versions.setFont(Fonts.t_f);

        VBox v = new VBox();
        v.getChildren().addAll(name, loaders, versions);
        this.getChildren().addAll(checkBox, v);
        this.setAlignment(Pos.TOP_LEFT);
    }
    public static Vector<String> getModLoaders(Vector<String> a, boolean b){
        Vector<String> loaders = new Vector<>();
        for (String s : a) {
            if (b) {
                if (!s.contains(".")) {
                    loaders.add(s);
                }
            }
            else{
                if (s.contains(".")) {
                    loaders.add(s);
                }
            }
        }
        return loaders;
    }
    public int compareTo(@NotNull ModFile aLong) {
        Date time1, time2;
        try {
            time1 = getTimeTick(this.model.fileDate);
            time2 = getTimeTick(aLong.model.fileDate);
        } catch (ParseException e) {
            return 0;
        }
        if (time1.after(time2)){
            return -1;
        }
        else{
            return 1;
        }
    }
}
