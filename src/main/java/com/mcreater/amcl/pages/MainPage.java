package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Application;
import com.mcreater.amcl.exceptions.LaunchException;
import com.mcreater.amcl.game.getMinecraftVersion;
import com.mcreater.amcl.game.launch.Launch;
import com.mcreater.amcl.audio.BGMManager;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.dialogs.ProcessDialog;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.ChangeDir;
import com.mcreater.amcl.util.SetSize;
import com.mcreater.amcl.util.Vars;
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

public class MainPage extends AbstractAnimationPage {
    Label title;
    Label launch;
    JFXButton choose_version;
    JFXButton version_settings;
    VBox GameMenu;
    HBox LaunchTitle;
    HBox SetTitle;
    Label set;
    JFXButton settings;
    JFXButton downloadMc;
    Label downloadTitle;
    public static JFXButton launchButton;
    VBox launchBox;
    public static boolean minecraft_running = false;
    public static String log = "";
    public static Long exit_code = null;
    public boolean is_vaild_minecraft_dir;
    Launch g;
    public static Logger logger = LogManager.getLogger(MainPage.class);
    public static boolean window_showed;
    public static ProcessDialog d;
    public static ProcessDialog l;
    public static JFXButton stop;
    public MainPage(double width,double height) {
        super(width, height);
        l = null;
        set();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (minecraft_running) {
                g.p.destroy();
            }
        }));

        launchButton = new JFXButton();
        launchButton.setId("launch-button");
        launchButton.setFont(Fonts.s_f);
        launchButton.setTextFill(Color.WHITE);
        launchButton.setOnAction(event -> {
            flush();
            ChangeDir.saveNowDir();
            if (!Objects.equals(launchButton.getText(), Application.languageManager.get("ui.mainpage.launchButton.noVersion"))) {
                Application.configReader.check_and_write();
                g = new Launch();
                d = new ProcessDialog(3, Application.languageManager.get("ui.mainpage.launch._01"));
                d.setV(0, 1, Application.languageManager.get("ui.mainpage.launch._02"));
                Thread la = new Thread(() -> {
                    try {
                        launchButton.setDisable(true);
                        if (new File(Application.configReader.configModel.selected_java_index).exists()) {
                            g.launch(Application.configReader.configModel.selected_java_index, Application.configReader.configModel.selected_minecraft_dir_index, Application.configReader.configModel.selected_version_index, Application.configReader.configModel.change_game_dir, Application.configReader.configModel.max_memory);
                            logger.info("started launch thread");
                        } else {
                            Application.configReader.configModel.selected_java.remove(Application.configReader.configModel.selected_java_index);
                            Application.configReader.configModel.selected_java_index = "";
                            Application.configReader.write();
                            Platform.runLater(() -> FastInfomation.create(Application.languageManager.get("ui.mainpage.launch.javaChecker.name"), Application.languageManager.get("ui.mainpage.launch.javaChecker.Headcontent"), ""));
                            launchButton.setDisable(false);
                        }
                    } catch (LaunchException | InterruptedException e) {
                        d.close();
                        logger.info("failed to launch", e);
                        launchButton.setDisable(false);
                        Platform.runLater(() -> FastInfomation.create(Application.languageManager.get("ui.mainpage.launch.launchFailed.name"), Application.languageManager.get("ui.mainpage.launch.launchFailed.Headcontent"), e.toString()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                la.setName("Launch Thread");
                la.start();
            } else {
                if (d != null) d.close();
                FastInfomation.create(Application.languageManager.get("ui.mainpage.launch.noVersion.name"), Application.languageManager.get("ui.mainpage.launch.noVersion.Headcontent"), Application.languageManager.get("ui.mainpage.launch.noVersion.content"));
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
            g.p.destroy();
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

        title.setFont(Fonts.b_f);
        launch.setFont(Fonts.t_f);
        set.setFont(Fonts.t_f);
        choose_version.setFont(Fonts.s_f);
        version_settings.setFont(Fonts.s_f);
        settings.setFont(Fonts.s_f);
        downloadMc.setFont(Fonts.s_f);
        downloadTitle.setFont(Fonts.t_f);

        settings.setOnAction(event -> Application.setPage(Application.CONFIGPAGE, this));
        version_settings.setOnAction(event -> Application.setPage(Application.VERSIONINFOPAGE, this));
        downloadMc.setOnAction(event -> Application.setPage(Application.DOWNLOADMCPAGE, this));

        is_vaild_minecraft_dir = Application.configReader.configModel.selected_minecraft_dir.contains(Application.configReader.configModel.selected_minecraft_dir_index) && new File(Application.configReader.configModel.selected_minecraft_dir_index).exists();

        choose_version.setOnAction(event -> {
            l = new ProcessDialog(1, Application.languageManager.get("ui.versionListLoad._02"));
            l.setV(0, 0, Application.languageManager.get("ui.mainpage.loadlist.name"));
            Application.setPage(Application.VERSIONSELECTPAGE, this);
        });

        version_settings.setGraphic(Application.getSVGManager().gear(Bindings.createObjectBinding(this::returnBlack), 25.0D, 25.0D));
        settings.setGraphic(Application.getSVGManager().gear(Bindings.createObjectBinding(this::returnBlack), 25.0D, 25.0D));
        downloadMc.setGraphic(Application.getSVGManager().downloadOutline(Bindings.createObjectBinding(this::returnBlack), 25.0D, 25.0D));

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

        choose_version.setMaxWidth(width / 4);
        version_settings.setMaxWidth(width / 4);
        settings.setMaxWidth(width / 4);
        downloadMc.setMaxWidth(width / 4);

        GameMenu = new VBox();
        GameMenu.setId("game-menu");
        SetSize.set(GameMenu, width / 4, height);
        GameMenu.setAlignment(Pos.TOP_CENTER);

        HBox hBox1 = new HBox();
        SetSize.set(hBox1, width / 5, height);
        HBox hBox2 = new HBox();
        SetSize.set(hBox2, width / 5, height);



        GameMenu.getChildren().addAll(
                title,
                LaunchTitle,
                SetSize.setSplit(new SplitPane(), width / 4 - 20),
                new Spacer(),
                choose_version,
                new Spacer(),
                SetTitle,
                SetSize.setSplit(new SplitPane(), width / 4 - 20),
                new Spacer(),
                version_settings,
                settings,
                new Spacer(),
                downloadTitle,
                SetSize.setSplit(new SplitPane(), width / 4 - 20),
                new Spacer(),
                downloadMc
        );

        this.add(GameMenu, 0, 1, 1, 1);
        this.add(hBox1, 1, 1, 1, 1);
        this.add(hBox2, 2, 1, 1, 1);
        this.add(launchBox, 3, 1, 1, 1);
//        this.getChildren().add(text);
//        this.add(text, 1, 2, 1, 1);
//        this.getChildren().add(text);
    }
    public static void check(){
        launchButton.setDisable(minecraft_running);
        if (!minecraft_running){
            if (exit_code != null){
                logger.info("Minecraft exited with code " + exit_code);
                if (exit_code != 0){

                    FastInfomation.create(Application.languageManager.get("ui.mainpage.minecraftExit.title"), Application.languageManager.get("ui.mainpage.minecraftExit.Headercontent"),String.format(Application.languageManager.get("ui.mainpage.minecraftExit.content"), exit_code));
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
        version_settings.setText(Application.languageManager.get("ui.mainpage.launchButton.noVersion"));
        launchButton.setText(Application.languageManager.get("ui.mainpage.launchButton.noVersion"));
        Application.configReader.configModel.selected_version_index = "";
        Application.configReader.write();
        version_settings.setDisable(true);
    }
    public void flush(){
        if (new File(Application.configReader.configModel.selected_minecraft_dir_index).exists()) {
            if (Application.configReader.configModel.selected_minecraft_dir.contains(Application.configReader.configModel.selected_minecraft_dir_index)) {
                if (Application.configReader.configModel.selected_version_index != null) {
                    if (Objects.requireNonNull(getMinecraftVersion.get(Application.configReader.configModel.selected_minecraft_dir_index)).contains(Application.configReader.configModel.selected_version_index)) {
                        version_settings.setText(" " + Application.configReader.configModel.selected_version_index);
                        launchButton.setText(Application.languageManager.get("ui.mainpage.launchButton.hasVersion"));
                        version_settings.setDisable(false);
                        downloadMc.setDisable(false);
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
        name = Application.languageManager.get("ui.mainpage.name");
        title.setText(String.format(Application.languageManager.get("ui.title"), Vars.launcher_name, Vars.launcher_version));
        launch.setText(Application.languageManager.get("ui.mainpage.launchTitle.launch.name"));
        set.setText(Application.languageManager.get("ui.mainpage.settings.name"));
        choose_version.setText(Application.languageManager.get("ui.mainpage.choose_version.name"));
        settings.setText(" "+ Application.languageManager.get("ui.mainpage.settings.name"));
        downloadTitle.setText(Application.languageManager.get("ui.mainpage.download.title"));
        downloadMc.setText(Application.languageManager.get("ui.mainpage.downloadMc.name"));
        stop.setText(Application.languageManager.get("ui.mainpage.stop"));
    }
}