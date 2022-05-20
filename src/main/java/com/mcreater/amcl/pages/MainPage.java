package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.HelloApplication;
import com.mcreater.amcl.config.ConfigWriter;
import com.mcreater.amcl.game.getMinecraftVersion;
import com.mcreater.amcl.game.launch.Launch;
import com.mcreater.amcl.util.SVG;
import com.mcreater.amcl.util.Vars;
import com.sun.javafx.application.PlatformImpl;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
    public static ConfigWriter configWriter;
    HBox SetTitle;
    Label set;
    JFXButton settings;
    public static JFXButton launchButton;
    VBox launchBox;
    boolean is_ee;
    public static boolean minecraft_running = false;
    static String log = "";
    public static long exit_code = -1;
    public boolean is_vaild_minecraft_dir;
    Launch g;
    static Logger logger = LogManager.getLogger(MainPage.class);
    public MainPage(double width,double height,Background bg){
        name = "Main Page";
        this.setBackground(bg);
        set(this);
        in.play();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (minecraft_running){
                g.p.destroy();
            }
        }));

        try {
            configWriter = new ConfigWriter(new File(HelloApplication.config_base_path + "config.json"));
            configWriter.check_and_write();
        }
        catch (IOException ignored){
            throw new IllegalStateException("Null Config File");
        }

        is_ee = configWriter.configModel.change_game_dir;

        launchButton = new JFXButton();
        launchButton.setFont(Fonts.b_f);
        launchButton.setStyle("-fx-background-color: rgb(173,216,246);");
        launchButton.setOnAction(event -> {
            if (!Objects.equals(launchButton.getText(), "No Version")) {
                g = new Launch();
                new Thread(() -> {
                    try {
                        if (new File(configWriter.configModel.selected_java_index).exists()) {
                            g.launch(configWriter.configModel.selected_java_index, configWriter.configModel.selected_minecraft_dir_index, configWriter.configModel.selected_version_index, is_ee, Vars.launcher_version);
                            logger.info("started launch thread");
                        }
                        else{
                            configWriter.configModel.selected_java.remove(configWriter.configModel.selected_java_index);
                            configWriter.configModel.selected_java_index = "";
                            configWriter.write();
                            FastInfomation.create("Java Checker", "Java has been removed", "");
                        }
                    }
                    catch (IllegalStateException e){
                        logger.info("failed to launch", e);
                    }
                }).start();
                launchButton.setDisable(true);
            }
            else{
                FastInfomation.create("Select Version","No Selected Version","Please Choose A Version");
            }
        });

        launchBox = new VBox();
        launchBox.setAlignment(Pos.BOTTOM_LEFT);
        launchBox.setMaxSize(width / 2,height - 185);
        launchBox.getChildren().add(launchButton);

        title = new Label("AMCL "+Vars.launcher_version);
        launch = new Label("Launch");
        set = new Label("Settings");
        choose_version = new JFXButton("Choose...");
        version_settings = new JFXButton();
        settings = new JFXButton(" Settings");

        title.setFont(Fonts.b_f);
        launch.setFont(Fonts.t_f);
        set.setFont(Fonts.t_f);
        choose_version.setFont(Fonts.s_f);
        version_settings.setFont(Fonts.s_f);
        settings.setFont(Fonts.s_f);

        settings.setOnAction(event -> runLater(() -> HelloApplication.setPage(new ConfigPage(width, height, bg))));

        is_vaild_minecraft_dir = configWriter.configModel.selected_minecraft_dir.contains(configWriter.configModel.selected_minecraft_dir_index) && new File(configWriter.configModel.selected_minecraft_dir_index).exists();

        choose_version.setOnAction(event -> runLater(() -> HelloApplication.setPage(new VersionSelectPage(width,height,bg))));


        StackPane graphic = new StackPane();
        Node svg = SVG.gear(Bindings.createObjectBinding(this::returnBlack), 25.0D, 25.0D);

        StackPane graphic1 = new StackPane();
        Node svg1 = SVG.gear(Bindings.createObjectBinding(this::returnBlack),25.0D,25.0D);

        StackPane.setAlignment(svg, Pos.CENTER_RIGHT);
        graphic.getChildren().setAll(svg);
        graphic1.getChildren().setAll(svg1);

        version_settings.setGraphic(graphic);
        settings.setGraphic(graphic1);

        if (new File(configWriter.configModel.selected_minecraft_dir_index).exists()) {
            if (configWriter.configModel.selected_minecraft_dir.contains(configWriter.configModel.selected_minecraft_dir_index)) {
                if (Objects.requireNonNull(getMinecraftVersion.get(configWriter.configModel.selected_minecraft_dir_index)).contains(configWriter.configModel.selected_version_index)) {
                    version_settings.setText(" " + configWriter.configModel.selected_version_index);
                    launchButton.setText("Launch");
                } else {
                    clean_null_version();
                }
            }
            else{
                clean_null_version();
            }
        }
        else{
            clean_null_version();
        }

        LaunchTitle = new HBox();
        LaunchTitle.setAlignment(Pos.BOTTOM_CENTER);
        LaunchTitle.getChildren().add(launch);

        SetTitle = new HBox();
        SetTitle.setAlignment(Pos.BOTTOM_CENTER);
        SetTitle.getChildren().add(set);

        GameMenu = new VBox();
        GameMenu.setMinHeight(height);
        GameMenu.setMinWidth(width / 4);
        GameMenu.setStyle("-fx-background-color: rgba(255,255,255,0.75);");
        GameMenu.setAlignment(Pos.TOP_CENTER);
        GameMenu.getChildren().addAll(
                title,
                LaunchTitle,
                new SplitPane(),
                new Spacer(),
                choose_version,
                new Spacer(),
                SetTitle,
                new SplitPane(),
                new Spacer(),
                version_settings,
                settings
        );

        HBox hBox1 = new HBox();
        hBox1.setMinSize(width / 5,height);
        hBox1.setMaxSize(width / 5,height);

        HBox hBox2 = new HBox();
        hBox2.setMinSize(width / 5,height);
        hBox2.setMaxSize(width / 5,height);



        this.add(GameMenu,0,0,1,1);
        this.add(hBox1,1,0,1,1);
        this.add(hBox2,2,0,1,1);
        this.add(launchBox,3,0,1,1);

    }
    public static void check(){
        launchButton.setDisable(minecraft_running);
        if (!minecraft_running){
            // TODO 初次运行
            if (!(exit_code == -1)){
                logger.info("Minecraft exited with code " + exit_code);
                // TODO 正常退出
                if (exit_code == 0){
                    FastInfomation.create("Minecraft Exit","Minecraft Exited Normally\n"+log,"Exit Code : 0");
                }
                // TODO minecraft崩溃
                else{
                    FastInfomation.create("Minecraft Exit","Minecraft Crashed\n"+log,"Exit Code : "+exit_code);
                }
                exit_code = -1;
            }
        }

    }
    public static void runLater(Runnable runnable){
        PlatformImpl.runLater(runnable);
    }
    public static void cleanLog(){
        log = "";
    }
    public static void addLog(String line){
        log += line + "\n";
    }
    public Color returnBlack(){
        return Color.BLACK;
    }
    public static class Spacer extends Label {
        public Spacer(){
            super();
            this.setText("");
        }
    }
    public void clean_null_version(){
        version_settings.setText(" No Version");
        launchButton.setText("No Version");
        configWriter.configModel.selected_version_index = "";
        configWriter.write();
    }
}
