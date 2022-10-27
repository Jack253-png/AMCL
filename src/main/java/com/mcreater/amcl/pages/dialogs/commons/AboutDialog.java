package com.mcreater.amcl.pages.dialogs.commons;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.DepencyItem;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.controls.SettingPage;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.VersionChecker;
import com.mcreater.amcl.util.VersionInfo;
import com.mcreater.amcl.util.operatingSystem.SystemActions;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import static com.mcreater.amcl.theme.ThemeManager.loadSingleNodeAnimate;

public class AboutDialog extends AbstractDialog {
    JFXButton ok;
    JFXButton checkUpdate;
    public AboutDialog() {
        super(Launcher.stage);
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
            new Thread(() -> {
                VersionChecker.check();
                this.close();
                checkUpdate.setDisable(false);
            }).start();
        });

        VBox v = new VBox();
        SettingPage page = new SettingPage(400, 300, v, false);
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
                new DepencyItem("JavaFX", "Copyright (c) 2013, 2021, Oracle and/or its affiliates.", "Licensed under the GPL 2 with Classpath Exception."),
                new DepencyItem("JFoenix", "Copyright (c) 2016 JFoenix", "Licensed under the MIT Lincense"),
                new DepencyItem("Gson", "Copyright 2008 Google Inc.", "Licensed under the Apache 2.0 License"),
                new DepencyItem("Minecraft JFX Skin", "Copyright (c) 2016 InfinityStudio", "Licensed under the GPL 3"),
                new DepencyItem("FastJson", "Copyright 1999-2020 Alibaba Group Holding Ltd.", "Fastjson is released under the Apache 2.0 license."),
                new DepencyItem("Log4j2", "", "Apache Log4j 2 is distributed under the Apache License, version 2.0."),
                new DepencyItem("Java Native Access", "", "This library is licensed under the LGPL, version 2.1 or later"),
                new DepencyItem("OSHI Core", "", "This project is licensed under the MIT License."));

        ThemeManager.loadButtonAnimates(title, ok, checkUpdate, page);
        layout.setHeading(title);
        layout.setBody(page);
        layout.setActions(checkUpdate, ok);
        this.setContent(layout);
    }
}
