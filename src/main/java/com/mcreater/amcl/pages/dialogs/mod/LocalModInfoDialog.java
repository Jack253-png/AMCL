package com.mcreater.amcl.pages.dialogs.mod;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.FileUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LocalModInfoDialog extends AbstractDialog {
    Label title;
    Label desc;
    Label version;
    Label author;
    JFXButton close;
    public ImageView icon;


    public LocalModInfoDialog(CommonModInfoModel model) {
        JFXDialogLayout layout = new JFXDialogLayout();

        title = new Label(model.name);
        title.setFont(Fonts.s_f);

        desc = new Label(model.description);
        desc.setFont(Fonts.t_f);

        version = new Label(Launcher.languageManager.get("ui.versioninfopage.info.version", model.version));
        version.setFont(Fonts.t_f);

        author = new Label(model.authorList != null ? Launcher.languageManager.get("ui.versioninfopage.info.author", String.join(", ", model.authorList)) : Launcher.languageManager.get("ui.versioninfopage.info.author.null"));
        author.setFont(Fonts.t_f);

        close = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        close.setOnAction(event -> close());

        icon = new ImageView();
        icon.setFitWidth(50);
        icon.setFitHeight(50);

        new Thread(() -> {
            if (model.icon != null) {
                try {
                    Image image = new Image(FileUtils.ZipUtil.readBinaryFileInZip(model.path, model.icon));
                    FXUtils.Platform.runLater(() -> icon.setImage(image));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        HBox content = new HBox(icon, new VBox(desc, version, author));
        content.setSpacing(15);

        AdvancedScrollPane pane = new AdvancedScrollPane(400, 300, content, false);
        pane.setId("opc");

        layout.setHeading(title);
        layout.setBody(pane);
        layout.setActions(close);
        setContent(layout);
        ThemeManager.loadNodeAnimations(layout);
    }
}
