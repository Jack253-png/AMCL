package com.mcreater.amcl.pages.dialogs.account.microsoft;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.controls.CapeSelectionLabel;
import com.mcreater.amcl.controls.items.ListItem;
import com.mcreater.amcl.controls.items.RadioButtonGroupItem;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.dialogs.commons.LoadingDialog;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Vector;

import static com.mcreater.amcl.Launcher.stage;

public class MicrosoftModifyDialog extends AbstractDialog {
    JFXButton finish;
    JFXButton upload;
    RadioButtonGroupItem base_model;
    VBox content;
    AdvancedScrollPane pane;
    final MicrosoftUser user;
    Vector<MSAuth.McProfileModel.McCapeModel> capes;
    ListItem<CapeSelectionLabel> capeSelect;
    public void setFinish(EventHandler<ActionEvent> handler) {
        finish.setOnAction(handler);
    }
    public MicrosoftModifyDialog(String title, MicrosoftUser user) {
        super();
        this.user = user;
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
            LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.skin.upload"));
            dialog.Create();
            new Thread(() -> {
                try {
                    user.upload(base_model.cont.getSelectedItem() == 0 ? MicrosoftUser.SkinType.STEVE : MicrosoftUser.SkinType.ALEX, f);
                } catch (Exception e) {
                    SimpleDialogCreater.exception(e);
                }
                FXUtils.Platform.runLater(dialog::close);
            }).start();
        });

        base_model = new RadioButtonGroupItem(Launcher.languageManager.get("ui.userselectpage.custom.model"), 400, Orientation.HORIZONTAL, "Steve", "Alex");

        capeSelect = new ListItem<>(Launcher.languageManager.get("ui.userselectpage.msaccount.cape.select"), 400 - 20);
        capeSelect.cont.setOnAction(event -> {
            CapeSelectionLabel label1 = capeSelect.cont.getSelectionModel().getSelectedItem();
            MSAuth.McProfileModel.McCapeModel model = label1 == null ? null : label1.getModel();

            LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.msaccount.cape.update"));
            dialog.show();

            new Thread(() -> {
                try {
                    if (model == null || model.id == null) {
                        user.hideCape();
                    }
                    else {
                        user.showCape(model);
                    }
                    FXUtils.Platform.runLater(dialog::close);
                }
                catch (Exception e) {
                    FXUtils.Platform.runLater(dialog::close);
                    SimpleDialogCreater.exception(e);
                }
            }).start();
        });

        content = new VBox(base_model, upload, capeSelect);
        content.setSpacing(10);

        pane = new AdvancedScrollPane(400, 300, content, false);
        pane.setId("opc");

        ThemeManager.loadNodeAnimations(label, finish);

        layout.setHeading(label);
        layout.setBody(content);
        layout.setActions(finish);
        setContent(layout);
    }
    public void show() {
        LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.msaccount.cape.fetch"));
        dialog.Create();
        new Thread(() -> {
            try {
                capes = user.getCapes();
                FXUtils.Platform.runLater(() -> {
                    capeSelect.cont.getItems().clear();
                    MSAuth.McProfileModel.McCapeModel model = new MSAuth.McProfileModel.McCapeModel();
                    model.alias = Launcher.languageManager.get("ui.userselectpage.msaccount.cape.disable");
                    capeSelect.cont.getItems().add(setFont(new CapeSelectionLabel(model), Fonts.t_f, CapeSelectionLabel.class));

                    capes.forEach(model2 -> capeSelect.cont.getItems().add(setFont(new CapeSelectionLabel(model2), Fonts.t_f, CapeSelectionLabel.class)));
                    boolean hasCape = false;
                    for (CapeSelectionLabel label : capeSelect.cont.getItems()) {
                        if (label.getModel().state) {
                            hasCape = true;
                            capeSelect.cont.getSelectionModel().select(label);
                            break;
                        }
                    }
                    if (!hasCape) capeSelect.cont.getSelectionModel().selectFirst();
                });
                FXUtils.Platform.runLater(() -> {
                    dialog.close();
                    super.show();
                });
            } catch (Exception e) {
                FXUtils.Platform.runLater(dialog::close);
                SimpleDialogCreater.exception(e);
            }
        }).start();
    }
}