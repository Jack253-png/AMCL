package com.mcreater.amcl.pages.dialogs.mod;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.RemoteMod;
import com.mcreater.amcl.controls.SmoothableListView;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.scene.control.Label;

public class RequiredModDialog extends AbstractDialog {
    public SmoothableListView<RemoteMod> items;
    public RequiredModDialog(String title) {
        super();
        JFXDialogLayout layout = new JFXDialogLayout();

        Label head = setFont(new Label(title), Fonts.s_f);
        layout.setHeading(head);

        items = new SmoothableListView<>(400, 300);
        layout.setBody(items.page);

        JFXButton button = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        button.setFont(Fonts.t_f);
        button.setOnAction(event -> close());

        layout.setActions(button);
        setContent(layout);
        ThemeManager.loadNodeAnimations(head, button);
    }
}
