package com.mcreater.amcl.pages.dialogs.commons;

import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSpinner;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class LoadingDialog extends AbstractDialog {
    public Label l;
    public JFXSpinner spinner;
    public LoadingDialog(String title){
        super(Launcher.stage);
        JFXDialogLayout layout = new JFXDialogLayout();
        l = new Label();
        l.setFont(Fonts.t_f);
        spinner = new JFXSpinner();
        VBox b = new VBox();
        b.setSpacing(10);
        b.setAlignment(Pos.CENTER);
        b.getChildren().add(spinner);
        Label title1 = setFont(new Label(title), Fonts.s_f);
        layout.setHeading(title1);
        layout.setBody(b);
        this.setContent(layout);
        ThemeManager.loadButtonAnimates(title1, spinner);
    }
}
