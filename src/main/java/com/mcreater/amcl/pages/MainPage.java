package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.OffLineAuth;
import com.mcreater.amcl.api.auth.users.OffLineUser;
import com.mcreater.amcl.audio.BGMManager;
import com.mcreater.amcl.controls.skin.FunctionHelper;
import com.mcreater.amcl.controls.skin.SkinCanvasSupport;
import com.mcreater.amcl.controls.skin.SkinView;
import com.mcreater.amcl.controls.skin.animation.SkinAniRunning;
import com.mcreater.amcl.controls.skin.animation.SkinAniWavingArms;
import com.mcreater.amcl.exceptions.LaunchException;
import com.mcreater.amcl.game.getMinecraftVersion;
import com.mcreater.amcl.game.launch.Launch;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.dialogs.ProcessDialog;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.pages.stages.FXBrowserPage;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.VersionInfo;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public class MainPage extends AbstractAnimationPage {
    public static Label title;
    public static Label launch;
    public static JFXButton choose_version;
    public static JFXButton version_settings;
    public static VBox GameMenu;
    public static HBox LaunchTitle;
    public static HBox SetTitle;
    public static Label set;
    public static JFXButton settings;
    public static JFXButton downloadMc;
    public static Label downloadTitle;
    public static JFXButton launchButton;
    public static VBox launchBox;
    public static boolean minecraft_running = false;
    public static String log = "";
    public static Long exit_code = null;
    public boolean is_vaild_minecraft_dir;
    public static Launch g;
    public static Logger logger = LogManager.getLogger(MainPage.class);
    public static boolean window_showed;
    public static ProcessDialog d;
    public static ProcessDialog l;
    public static JFXButton stop;
    public static JFXButton users;
    public MainPage(double width,double height) {
        super(width, height);
        l = null;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (minecraft_running) {
                g.stop_process();
            }
        }));

        launchButton = new JFXButton();
        launchButton.setId("launch-button");
        launchButton.setFont(Fonts.s_f);
        launchButton.setTextFill(Color.WHITE);
        launchButton.setOnMouseEntered(event -> flush());
        launchButton.setOnAction(event -> {
            flush();
            FileUtils.ChangeDir.saveNowDir();
            if (!Objects.equals(launchButton.getText(), Launcher.languageManager.get("ui.mainpage.launchButton.noVersion"))) {
                Launcher.configReader.check_and_write();
                g = new Launch();
                d = new ProcessDialog(3, Launcher.languageManager.get("ui.mainpage.launch._01"));
                d.setV(0, 1, Launcher.languageManager.get("ui.mainpage.launch._02"));
                Thread la = new Thread(() -> {
                    try {
                        launchButton.setDisable(true);
                        if (new File(Launcher.configReader.configModel.selected_java_index).exists()) {
                            g.launch(
                                    Launcher.configReader.configModel.selected_java_index, Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index, Launcher.configReader.configModel.change_game_dir,
                                    Launcher.configReader.configModel.max_memory,
                                    UserSelectPage.user_object.get());
                            logger.info("started launch thread");
                        } else {
                            Launcher.configReader.configModel.selected_java.remove(Launcher.configReader.configModel.selected_java_index);
                            Launcher.configReader.configModel.selected_java_index = "";
                            Launcher.configReader.write();
                            Platform.runLater(() -> FastInfomation.create(Launcher.languageManager.get("ui.mainpage.launch.javaChecker.name"), Launcher.languageManager.get("ui.mainpage.launch.javaChecker.Headcontent"), ""));
                            launchButton.setDisable(false);
                        }
                    }
                    catch (Exception e) {
                        d.close();
                        logger.info("failed to launch", e);
                        launchButton.setDisable(false);
                        Platform.runLater(() -> FastInfomation.create(Launcher.languageManager.get("ui.mainpage.launch.launchFailed.name"), Launcher.languageManager.get("ui.mainpage.launch.launchFailed.Headcontent"), e.toString()));
                    }
                });
                la.setName("Launch Thread");
                la.start();
            } else {
                if (d != null) d.close();
                FastInfomation.create(Launcher.languageManager.get("ui.mainpage.launch.noVersion.name"), Launcher.languageManager.get("ui.mainpage.launch.noVersion.Headcontent"), Launcher.languageManager.get("ui.mainpage.launch.noVersion.content"));
            }
        });
        if (minecraft_running) {
            launchButton.setDisable(true);
        }
        stop = new JFXButton();
        stop.setDisable(true);
        stop.setId("launch-button");
        stop.setFont(Fonts.s_f);
        stop.setTextFill(Color.WHITE);
        stop.setOnAction(event -> {
            g.stop_process();
            stop.setDisable(true);
        });

        launchBox = new VBox();
        launchBox.setAlignment(Pos.BOTTOM_LEFT);
        launchBox.setMaxSize(width / 2, height - 185);
        launchBox.getChildren().addAll(stop, launchButton);

        title = new Label();
        launch = new Label();
        set = new Label();
        choose_version = new JFXButton();
        version_settings = new JFXButton();
        settings = new JFXButton();
        downloadMc = new JFXButton();
        downloadTitle = new Label();
        users = new JFXButton();

        title.setFont(Fonts.b_f);
        launch.setFont(Fonts.t_f);
        set.setFont(Fonts.t_f);
        choose_version.setFont(Fonts.s_f);
        version_settings.setFont(Fonts.s_f);
        settings.setFont(Fonts.s_f);
        downloadMc.setFont(Fonts.s_f);
        downloadTitle.setFont(Fonts.t_f);
        users.setFont(Fonts.s_f);

        settings.setOnAction(event -> Launcher.setPage(Launcher.CONFIGPAGE, this));
        version_settings.setOnAction(event -> Launcher.setPage(Launcher.VERSIONINFOPAGE, this));
        downloadMc.setOnAction(event -> Launcher.setPage(Launcher.DOWNLOADMCPAGE, this));
        users.setOnAction(event -> Launcher.setPage(Launcher.USERSELECTPAGE, this));

        is_vaild_minecraft_dir = Launcher.configReader.configModel.selected_minecraft_dir.contains(Launcher.configReader.configModel.selected_minecraft_dir_index) && new File(Launcher.configReader.configModel.selected_minecraft_dir_index).exists();

        choose_version.setOnAction(event -> {
            l = new ProcessDialog(1, Launcher.languageManager.get("ui.versionListLoad._02"));
            l.setV(0, 0, Launcher.languageManager.get("ui.mainpage.loadlist.name"));
            Launcher.setPage(Launcher.VERSIONSELECTPAGE, this);
        });

        version_settings.setGraphic(Launcher.getSVGManager().gear(Bindings.createObjectBinding(this::returnBlack), 25.0D, 25.0D));
        settings.setGraphic(Launcher.getSVGManager().gear(Bindings.createObjectBinding(this::returnBlack), 25.0D, 25.0D));
        downloadMc.setGraphic(Launcher.getSVGManager().downloadOutline(Bindings.createObjectBinding(this::returnBlack), 25.0D, 25.0D));

        LaunchTitle = new HBox();
        LaunchTitle.setAlignment(Pos.BOTTOM_CENTER);
        LaunchTitle.getChildren().add(launch);

        SetTitle = new HBox();
        SetTitle.setAlignment(Pos.BOTTOM_CENTER);
        SetTitle.getChildren().add(set);

        choose_version.setButtonType(JFXButton.ButtonType.RAISED);
        version_settings.setButtonType(JFXButton.ButtonType.RAISED);
        settings.setButtonType(JFXButton.ButtonType.RAISED);
        launchButton.setButtonType(JFXButton.ButtonType.RAISED);
        downloadMc.setButtonType(JFXButton.ButtonType.RAISED);
        users.setButtonType(JFXButton.ButtonType.RAISED);

        choose_version.setMaxWidth(width / 4);
        version_settings.setMaxWidth(width / 4);
        settings.setMaxWidth(width / 4);
        downloadMc.setMaxWidth(width / 4);
        users.setMaxWidth(width / 4);

        GameMenu = new VBox();
        GameMenu.setId("game-menu");
        FXUtils.ControlSize.set(GameMenu, width / 4, height);
        GameMenu.setAlignment(Pos.TOP_CENTER);

        HBox hBox1 = new HBox();
        FXUtils.ControlSize.set(hBox1, width / 5, height);
        HBox hBox2 = new HBox();
        FXUtils.ControlSize.set(hBox2, width / 5, height);

        GameMenu.getChildren().addAll(
                title,
                LaunchTitle,
                FXUtils.ControlSize.setSplit(new SplitPane(), width / 4 - 20),
                new Spacer(),
                choose_version,
                users,
                new Spacer(),
                SetTitle,
                FXUtils.ControlSize.setSplit(new SplitPane(), width / 4 - 20),
                new Spacer(),
                version_settings,
                settings,
                new Spacer(),
                downloadTitle,
                FXUtils.ControlSize.setSplit(new SplitPane(), width / 4 - 20),
                new Spacer(),
                downloadMc
        );

        this.add(GameMenu, 0, 1, 1, 1);
        this.add(hBox1, 1, 1, 1, 1);
        this.add(hBox2, 2, 1, 1, 1);
        this.add(launchBox, 3, 1, 1, 1);
    }
    public static void check(){
        launchButton.setDisable(minecraft_running);
        if (!minecraft_running){
            Platform.runLater(d::close);
            if (exit_code != null){
                logger.info("Minecraft exited with code " + exit_code);
                if (exit_code != 0){

                    FastInfomation.create(Launcher.languageManager.get("ui.mainpage.minecraftExit.title"), Launcher.languageManager.get("ui.mainpage.minecraftExit.Headercontent"),String.format(Launcher.languageManager.get("ui.mainpage.minecraftExit.content"), exit_code));
                }
                exit_code = null;
                window_showed = false;
                stop.setDisable(true);
                BGMManager.start();
            }
        }
    }
    public static void cleanLog(){
        log = "";
    }
    public static void addLog(String line){
        log += line + "\n";
    }
    public static class Spacer extends Label {
        public Spacer(){
            super();
            this.setText("");
        }
    }
    public void clean_null_version(){
        Platform.runLater(() -> {
            version_settings.setText(Launcher.languageManager.get("ui.mainpage.launchButton.noVersion"));
            launchButton.setText(Launcher.languageManager.get("ui.mainpage.launchButton.noVersion"));
            Launcher.configReader.configModel.selected_version_index = "";
            Launcher.configReader.write();
            version_settings.setDisable(true);
        });
    }
    public void flush(){
        if (new File(Launcher.configReader.configModel.selected_minecraft_dir_index).exists()) {
            if (Launcher.configReader.configModel.selected_minecraft_dir.contains(Launcher.configReader.configModel.selected_minecraft_dir_index)) {
                if (Launcher.configReader.configModel.selected_version_index != null) {
                    if (Objects.requireNonNull(getMinecraftVersion.get(Launcher.configReader.configModel.selected_minecraft_dir_index)).contains(Launcher.configReader.configModel.selected_version_index)) {
                        Platform.runLater(() -> {
                            version_settings.setText(" " + Launcher.configReader.configModel.selected_version_index);
                            launchButton.setText(Launcher.languageManager.get("ui.mainpage.launchButton.hasVersion"));
                            version_settings.setDisable(false);
                            downloadMc.setDisable(false);
                        });
                    } else {
                        clean_null_version();
                        downloadMc.setDisable(false);
                    }
                }
                else{
                    clean_null_version();
                    downloadMc.setDisable(false);
                }
            }
            else{
                clean_null_version();
                downloadMc.setDisable(true);
            }
        }
        else{
            clean_null_version();
            downloadMc.setDisable(true);
        }
    }
    public void refresh(){
        flush();
    }
    public void refreshType(){

    }

    public void onExitPage() {

    }

    public void refreshLanguage(){
        name = Launcher.languageManager.get("ui.mainpage.name");
        title.setText(String.format(Launcher.languageManager.get("ui.title"), VersionInfo.launcher_name, VersionInfo.launcher_version));
        launch.setText(Launcher.languageManager.get("ui.mainpage.launchTitle.launch.name"));
        set.setText(Launcher.languageManager.get("ui.mainpage.settings.name"));
        choose_version.setText(Launcher.languageManager.get("ui.mainpage.choose_version.name"));
        settings.setText(" "+ Launcher.languageManager.get("ui.mainpage.settings.name"));
        downloadTitle.setText(Launcher.languageManager.get("ui.mainpage.download.title"));
        downloadMc.setText(Launcher.languageManager.get("ui.mainpage.downloadMc.name"));
        stop.setText(Launcher.languageManager.get("ui.mainpage.stop"));
        users.setText(Launcher.languageManager.get("ui.mainpage.users.name"));
    }
}