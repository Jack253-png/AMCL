package com.mcreater.amcl.pages.dialogs.account.microsoft;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.controls.items.RadioButtonGroupItem;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

import static com.mcreater.amcl.Launcher.stage;

public class MicrosoftModifyDialog extends AbstractDialog {
    JFXButton finish;
    JFXButton upload;
    RadioButtonGroupItem base_model;
    VBox content;
    AdvancedScrollPane pane;
    public void setFinish(EventHandler<ActionEvent> handler) {
        finish.setOnAction(handler);
    }
    public MicrosoftModifyDialog(String title, MicrosoftUser user) {
        super();
        JFXDialogLayout layout = new JFXDialogLayout();

        Label label = new Label(title);
        label.setFont(Fonts.s_f);

        finish = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        finish.setFont(Fonts.t_f);

        upload = new JFXButton(Launcher.languageManager.get("ui.userselectpage.msaccount.skin.upload"));
        upload.setFont(Fonts.t_f);
        upload.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    Launcher.languageManager.get("ui.userselectpage.custom.file.desc"),
                    "*.png"
            ));
            File f = chooser.showOpenDialog(stage);
            try {
                user.upload(base_model.cont.getSelectedItem() == 0 ? MicrosoftUser.SkinType.STEVE : MicrosoftUser.SkinType.ALEX, f);
            } catch (Exception e) {
                SimpleDialogCreater.exception(e);
            }
        });

        base_model = new RadioButtonGroupItem(Launcher.languageManager.get("ui.userselectpage.custom.model"), 400, Orientation.HORIZONTAL, "Steve", "Alex");

        content = new VBox(base_model, upload);
        content.setSpacing(10);

        pane = new AdvancedScrollPane(400, 300, content, false);
        pane.setId("opc");

        ThemeManager.loadNodeAnimations(label, finish);

        layout.setHeading(label);
        layout.setBody(pane);
        layout.setActions(finish);
        setContent(layout);
    }
}