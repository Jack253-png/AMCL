package com.mcreater.amcl.pages.dialogs.commons;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.controls.DepencyItem;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.pages.stages.UpgradePage;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.VersionChecker;
import com.mcreater.amcl.util.VersionInfo;
import com.mcreater.amcl.util.os.SystemActions;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import static com.mcreater.amcl.theme.ThemeManager.loadSingleNodeAnimate;

public class AboutDialog extends AbstractDialog {
    JFXButton ok;
    JFXButton checkUpdate;
    public AboutDialog() {
        super();
        JFXDialogLayout layout = new JFXDialogLayout();
        Label title = new Label(Launcher.languageManager.get("ui.about.title"));
        title.setFont(Fonts.s_f);
        ok = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
        ok.setFont(Fonts.t_f);
        checkUpdate = new JFXButton(Launcher.languageManager.get("ui.about.checkUpdate"));
        checkUpdate.setFont(Fonts.t_f);

        ok.setOnAction(event -> this.close());
        checkUpdate.setOnAction(event -> {
            checkUpdate.setDisable(true);
            new Thread("Version update checker") {
                public void run() {
                    VersionChecker.check((s, aBoolean) -> FXUtils.Platform.runLater(() -> PopupMessage.createMessage(s, aBoolean ? PopupMessage.MessageTypes.HYPERLINK : PopupMessage.MessageTypes.LABEL, aBoolean ? event2 -> new UpgradePage().open() : null)));
                    close();
                    checkUpdate.setDisable(false);
                }
            }.start();
        });

        VBox v = new VBox();
        AdvancedScrollPane page = new AdvancedScrollPane(400, 300, v, false);
        page.setId("opc");
        ThemeManager.applyNode(page);

        Hyperlink hyperlink = new Hyperlink(Launcher.languageManager.get("ui.about.opensource.name"));
        hyperlink.setOnAction(event -> SystemActions.openBrowser("https://github.com/Jack253-png/AMCL"));
        hyperlink.setFont(Fonts.t_f);

        v.setSpacing(10);
        v.getChildren().addAll(
                loadSingleNodeAnimate(setFont(new Label(Launcher.languageManager.get("ui.about.version.title")), Fonts.s_f)),
                loadSingleNodeAnimate(setFont(new Label(VersionInfo.launcher_full_version), Fonts.t_f)),
                loadSingleNodeAnimate(setFont(new Label(Launcher.languageManager.get("ui.about.java.title")), Fonts.s_f)),
                loadSingleNodeAnimate(setFont(new Label(System.getProperty("java.runtime.name", "null")), Fonts.t_f)),
                loadSingleNodeAnimate(setFont(new Label(System.getProperty("java.runtime.version", "null")), Fonts.t_f)),
                loadSingleNodeAnimate(setFont(new Label(Launcher.languageManager.get("ui.about.opensource.title")), Fonts.s_f)),
                loadSingleNodeAnimate(hyperlink),
                loadSingleNodeAnimate(setFont(new Label(Launcher.languageManager.get("ui.about.depencies.title")), Fonts.s_f)),
                new DepencyItem("JavaFX", "Copyright (c) 2013, 2021, Oracle and/or its affiliates.", "Licensed under the GPL 2 with Classpath Exception.").toMaterial(),
                new DepencyItem("JFoenix", "Copyright (c) 2016 JFoenix", "Licensed under the MIT Lincense").toMaterial(),
                new DepencyItem("Gson", "Copyright 2008 Google Inc.", "Licensed under the Apache 2.0 License").toMaterial(),
                new DepencyItem("Minecraft JFX Skin", "Copyright (c) 2016 InfinityStudio", "Licensed under the GPL 3").toMaterial(),
                new DepencyItem("FastJson", "Copyright 1999-2020 Alibaba Group Holding Ltd.", "Fastjson is released under the Apache 2.0 license.").toMaterial(),
                new DepencyItem("Log4j2", "", "Apache Log4j 2 is distributed under the Apache License, version 2.0.").toMaterial(),
                new DepencyItem("Java Native Access", "", "This library is licensed under the LGPL, version 2.1 or later").toMaterial(),
                new DepencyItem("OSHI Core", "", "This project is licensed under the MIT License.").toMaterial()
        );

        ThemeManager.loadNodeAnimations(title, ok, checkUpdate, page);
        layout.setHeading(title);
        layout.setBody(page);
        layout.setActions(checkUpdate, ok);
        this.setContent(layout);
    }
}
