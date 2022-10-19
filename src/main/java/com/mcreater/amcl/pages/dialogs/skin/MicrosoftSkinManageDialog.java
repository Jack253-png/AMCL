package com.mcreater.amcl.pages.dialogs.skin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.controls.items.RadioButtonGroup;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.dialogs.commons.LoadingDialog;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.util.Map;

public class MicrosoftSkinManageDialog extends AbstractDialog {
    public JFXButton addButton;
    JFXButton uploadSkin;
    MicrosoftUser user;
    JFXComboBox<CapeLabel> capes;
    RadioButtonGroup group = new RadioButtonGroup("Steve", "Alex");
    public MicrosoftSkinManageDialog(String title, MicrosoftUser user) {
        super(Launcher.stage);
        this.user = user;
        JFXDialogLayout layout = new JFXDialogLayout();
        addButton = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        addButton.setFont(Fonts.t_f);
        addButton.setDefaultButton(true);
        addButton.setOnAction(event -> close());

        group.items.get(0).setSelected(true);

        uploadSkin = new JFXButton(Launcher.languageManager.get("ui.userselectpage.skin.select"));
        uploadSkin.setFont(Fonts.t_f);
        uploadSkin.setDefaultButton(true);
        uploadSkin.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            Launcher.languageManager.get("ui.userselectpage.custom.file.desc"), "*.png"
                    )
            );
            chooser.setTitle(Launcher.languageManager.get("ui.userselectpage.custom.title"));
            File skin = chooser.showOpenDialog(Launcher.stage);
            LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.skin.upload"));
            dialog.show();
            new Thread(() -> {
                try {
                    MicrosoftUser.SkinType type;
                    if (group.getSelectedItem() == 0) {
                        type = MicrosoftUser.SkinType.STEVE;
                    }
                    else {
                        type = MicrosoftUser.SkinType.ALEX;
                    }
                    user.upload(type, skin);
                    Platform.runLater(dialog::close);
                }
                catch (Exception e){
                    Platform.runLater(dialog::close);
                    Platform.runLater(() -> SimpleDialogCreater.exception(e));
                }
            }).start();
        });

        Label cape = new Label(Launcher.languageManager.get("ui.userselectpage.cape.select"));
        cape.setFont(Fonts.t_f);

        capes = new JFXComboBox<>();
        capes.setOnAction(event -> {
            CapeLabel l = capes.getSelectionModel().getSelectedItem();
            LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.capes.change"));
            dialog.show();
            new Thread(() -> {
                try {
                    if (l.raw.getValue() == null) {
                        user.hideCape();
                    } else {
                        user.showCape(l.raw.getValue().getKey());
                    }
                    Platform.runLater(dialog::close);
                }
                catch (Exception e){
                    Platform.runLater(() -> {
                        dialog.close();
                        SimpleDialogCreater.exception(e);
                    });
                }
            }).start();
        });

        VBox v = new VBox(group, uploadSkin);
        v.setSpacing(20);
        HBox b = new HBox(cape, capes);
        b.setSpacing(35);
        FXUtils.ControlSize.setWidth(b, 300);

        VBox fin = new VBox(v, b);
        fin.setSpacing(20);
        layout.setBody(fin);

        Label head = setFont(new Label(title), Fonts.s_f);

        ThemeManager.loadButtonAnimates(addButton, head, group, uploadSkin, cape, capes);
        layout.setActions(addButton);
        layout.setHeading(head);
        this.setOnHidden(event -> {});
        this.setOnHiding(event -> {});
        setContent(layout);
    }
    public void loadCapes() throws Exception {
        Platform.runLater(() -> {});
        CapeLabel l = new CapeLabel(new ImmutablePair<>(Launcher.languageManager.get("ui.capes.noCape"), null));
        l.setFont(Fonts.t_f);
        capes.getItems().add(l);
        capes.getSelectionModel().selectFirst();
        for (Map.Entry<String, ImmutablePair<String, Boolean>> entry : user.getCapes().entrySet()) {
            CapeLabel label = new CapeLabel(new ImmutablePair<>(entry.getKey(), entry.getValue()));
            label.setFont(Fonts.t_f);
            capes.getItems().add(label);
            if (label.raw.getValue().getValue()) capes.getSelectionModel().select(label);
        }
    }

    public static class CapeLabel extends Label {
        public final ImmutablePair<String, ImmutablePair<String, Boolean>> raw;
        public CapeLabel(ImmutablePair<String, ImmutablePair<String, Boolean>> raw) {
            super(raw.getKey());
            this.raw = raw;
        }
    }
}
