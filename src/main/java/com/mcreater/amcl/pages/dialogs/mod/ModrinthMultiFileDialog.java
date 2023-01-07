package com.mcreater.amcl.pages.dialogs.mod;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.items.radio.RadioButtonGroupV;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.scene.control.Label;

import java.util.Vector;

public class ModrinthMultiFileDialog extends AbstractDialog {
    RadioButtonGroupV group;
    boolean closed = false;
    public ModrinthMultiFileDialog(Vector<String> f, String title) {
        super();
        JFXDialogLayout layout = new JFXDialogLayout();

        JFXButton ok = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        ok.setFont(Fonts.t_f);
        ok.setOnAction(event -> {
            close();
            closed = true;
        });

        group = new RadioButtonGroupV(f.toArray(new String[0]));
        group.items.get(0).setSelected(true);
        layout.setBody(group);
        layout.setActions(ok);
        layout.setHeading(setFont(new Label(title), Fonts.s_f));
        setContent(layout);
        ThemeManager.loadNodeAnimations(layout);
    }

    public int getIndex() {
        return closed ? group.getSelectedItem() : -1;
    }
}
