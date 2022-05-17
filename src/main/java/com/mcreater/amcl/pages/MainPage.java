package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.mcreater.amcl.HelloApplication;
import com.mcreater.amcl.config.ConfigWriter;
import com.mcreater.amcl.game.getMinecraftVersion;
import com.mcreater.amcl.game.launch.Launch;
import com.mcreater.amcl.game.versionTypeGetter;
import com.mcreater.amcl.util.SVG;
import com.mcreater.amcl.util.Vars;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public class MainPage extends AbstractAnimationPage {
    Label title;
    Label launch;
    JFXButton choose_version;
    JFXButton version_settings;
    VBox GameMenu;
    HBox LaunchTitle;
    Font b_f = new Font("Consolas",28);
    Font s_f = new Font("Consolas",22);
    Font t_f = new Font("Consolas",16);
    ConfigWriter configWriter;
    HBox SetTitle;
    Label set;
    JFXButton settings;
    JFXButton launchButton;
    VBox launchBox;
    boolean is_ee = false;
    public static boolean minecraft_running = false;
    static String log = "";
    public static long exit_code = -1;
    public MainPage(double width,double height,Background bg){
        this.setBackground(bg);
        set(this);
        in.play();

        launchButton = new JFXButton();
        launchButton.setFont(b_f);

        launchButton.setStyle("-fx-background-color: rgb(173,216,246);");
        launchButton.setOnAction(event -> {
            versionTypeGetter.get(configWriter.configModel.selected_minecraft_dir_index,configWriter.configModel.selected_version_index);
            new Thread(() -> new Launch().launch("java",configWriter.configModel.selected_minecraft_dir_index,configWriter.configModel.selected_version_index,is_ee, Vars.launcher_version)).start();
        });

        new Thread(() -> {
            while (true){
                launchButton.setDisable(minecraft_running);
                if (!minecraft_running){
                    // TODO 初次运行
                    if (!(exit_code == -1)){
                        // TODO 正常退出
                        if (!(exit_code == 0)){
                        }
                        // TODO minecraft进程被强制结束
                        if (!(exit_code == 1)){

                        }
                        exit_code = -1;
                    }
                }
            }
        }).start();

        launchBox = new VBox();
        launchBox.setAlignment(Pos.BOTTOM_LEFT);
        launchBox.setMaxSize(width / 2,height - 185);
        launchBox.getChildren().add(launchButton);

        try {
            configWriter = new ConfigWriter(new File(HelloApplication.config_base_path + "config.json"));
        }
        catch (IOException ignored){
            throw new IllegalStateException("Null Config File");
        }

        title = new Label("AMCL "+Vars.launcher_version);
        launch = new Label("Launch");
        set = new Label("Settings");
        choose_version = new JFXButton("Choose...");
        version_settings = new JFXButton();
        settings = new JFXButton(" Settings");

        title.setFont(b_f);
        launch.setFont(t_f);
        set.setFont(t_f);
        choose_version.setFont(s_f);
        version_settings.setFont(s_f);
        settings.setFont(s_f);

        choose_version.setOnAction(event -> HelloApplication.setPage(new VersionSelectPage(width,height,bg)));


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

        JFXToggleButton is_e = new JFXToggleButton();
        is_e.selectedProperty().addListener((observable, oldValue, newValue) -> is_ee = newValue);

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
                settings,
                is_e
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
        try {
            configWriter.write();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
