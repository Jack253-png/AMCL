package com.mcreater.amcl.pages.dialogs.mod;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.items.RadioButtonGroup;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;

import java.util.Vector;

public class ModrinthMultiFileDialog extends AbstractDialog {
    RadioButtonGroup group;
    public ModrinthMultiFileDialog(Vector<String> f, String title) {
        super(Launcher.stage);
        JFXDialogLayout layout = new JFXDialogLayout();

        JFXButton ok = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        ok.setFont(Fonts.t_f);
        ok.setOnAction(event -> close());

        group = new RadioButtonGroup(f.toArray(new String[]{}));
        group.items.get(0).setSelected(true);
        layout.setBody(group);
        layout.setActions(ok);
        layout.setHeading(setFont(new Label(title), Fonts.s_f));
        setContent(layout);

        this.setOnHidden(event -> {});
        this.setOnHiding(event -> {});
        ThemeManager.loadButtonAnimates(layout);
    }

    public int getIndex() {
        showAndWait();
        return group.getSelectedItem();
    }
}
