package com.mcreater.amcl.pages.dialogs.mod;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.controls.LocalMod;
import com.mcreater.amcl.controls.items.radio.ListButtonItem;
import com.mcreater.amcl.lang.ModTransitions;
import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.model.mod.transitions.ModTransitionsModel;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.dialogs.commons.LoadingDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.os.SystemActions;
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
    private ModTransitionsModel model;
    private final Object lock = new Object();
    ListButtonItem<LinkLabel> links;

    public LocalModInfoDialog(LocalMod c) {
        LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.downloadaddonsselectpage.loading.title"));
        dialog.show();
        new Thread(() -> {
            synchronized (lock) {
                model = ModTransitions.MODS.translate(c.model.modid, c.model.name);
                FXUtils.Platform.runLater(dialog::close);
                FXUtils.Platform.runLater(this::loadLinks);
            }
        }).start();
        {
            JFXDialogLayout layout = new JFXDialogLayout();

            CommonModInfoModel model = c.model;

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

            links = new ListButtonItem<>(Launcher.languageManager.get("ui.versioninfopage.openlink"), 350);
            links.title.setOnAction(event -> {
                String s = links.cont.getSelectionModel().getSelectedItem().url;
                if (s != null) SystemActions.openBrowser(s);
            });

            HBox content = new HBox(icon, new VBox(desc, version, author, links));
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

    public void show() {
        synchronized (lock) {
            super.show();
        }
    }

    private void loadLinks() {
        if (this.model.mcmod != null) links.cont.getItems().add(new LinkLabel("mcmod", this.model.mcmod));
        if (this.model.mcbbs != null) links.cont.getItems().add(new LinkLabel("mcbbs", this.model.mcbbs));
        if (this.model.offical != null)
            links.cont.getItems().add(new LinkLabel(Launcher.languageManager.get("ui.versioninfopage.mod.offical"), this.model.offical));

        if (this.model.metadata.main != null) {
            if (this.model.metadata.main.curseforge != null)
                links.cont.getItems().add(new LinkLabel("Curseforge", this.model.metadata.main.curseforge));
            if (this.model.metadata.main.modrinth != null)
                links.cont.getItems().add(new LinkLabel("Modrinth", this.model.metadata.main.modrinth));
            if (this.model.metadata.main.github != null)
                links.cont.getItems().add(new LinkLabel("Github", this.model.metadata.main.github));
        }

        if (this.model.metadata.fabric != null) {
            if (this.model.metadata.fabric.curseforge != null)
                links.cont.getItems().add(new LinkLabel("Curseforge (Fabric)", this.model.metadata.fabric.curseforge));
            if (this.model.metadata.fabric.modrinth != null)
                links.cont.getItems().add(new LinkLabel("Modrinth (Fabric)", this.model.metadata.fabric.modrinth));
            if (this.model.metadata.fabric.github != null)
                links.cont.getItems().add(new LinkLabel("Github (Fabric)", this.model.metadata.fabric.github));
        }
    }

    public static class LinkLabel extends Label {
        public final String url;

        public LinkLabel(String name, String url) {
            super(name);
            this.url = url;
        }
    }
}
