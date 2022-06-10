package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRippler;
import com.mcreater.amcl.HelloApplication;
import com.mcreater.amcl.exceptions.LaunchException;
import com.mcreater.amcl.game.getMinecraftVersion;
import com.mcreater.amcl.game.launch.Launch;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.dialogs.ProcessDialog;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.SVG;
import com.mcreater.amcl.util.Vars;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
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
    public static JFXButton launchButton;
    VBox launchBox;
    boolean is_ee;
    public static boolean minecraft_running = false;
    public static String log = "";
    public static Long exit_code = null;
    public boolean is_vaild_minecraft_dir;
    Launch g;
    public static Logger logger = LogManager.getLogger(MainPage.class);
    public static boolean window_showed;
    public static ProcessDialog d;
    public static ProcessDialog l;
    public MainPage(double width,double height){
        super(width, height);
        l = null;
        set();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (minecraft_running){
                g.p.destroy();
            }
        }));

        is_ee = HelloApplication.configReader.configModel.change_game_dir;

        launchButton = new JFXButton();
        launchButton.setFont(Fonts.s_f);
        launchButton.setStyle("-fx-background-color: rgb(173,216,246);");
        launchButton.setOnAction(event -> {
            flush();
            if (!Objects.equals(launchButton.getText(), HelloApplication.languageManager.get("ui.mainpage.launchButton.noVersion"))) {
                HelloApplication.configReader.check_and_write();
                g = new Launch();
                d = new ProcessDialog(2, HelloApplication.languageManager.get("ui.mainpage.launch._01"));
                d.setV(0, 1, HelloApplication.languageManager.get("ui.mainpage.launch._02"));
                new Thread(() -> {
                    try {
                        launchButton.setDisable(true);
                        if (new File(HelloApplication.configReader.configModel.selected_java_index).exists()) {
                            g.launch(HelloApplication.configReader.configModel.selected_java_index, HelloApplication.configReader.configModel.selected_minecraft_dir_index, HelloApplication.configReader.configModel.selected_version_index, is_ee, Vars.launcher_version, HelloApplication.configReader.configModel.max_memory);
                            logger.info("started launch thread");
                        }
                        else{
                            HelloApplication.configReader.configModel.selected_java.remove(HelloApplication.configReader.configModel.selected_java_index);
                            HelloApplication.configReader.configModel.selected_java_index = "";
                            HelloApplication.configReader.write();
                            Platform.runLater(() -> FastInfomation.create(HelloApplication.languageManager.get("ui.mainpage.launch.javaChecker.name"), HelloApplication.languageManager.get("ui.mainpage.launch.javaChecker.Headcontent"), ""));
                            launchButton.setDisable(false);
                        }
                    }
                    catch (LaunchException | InterruptedException e){
                        d.close();
                        logger.info("failed to launch", e);
                        launchButton.setDisable(false);
                        Platform.runLater(() -> FastInfomation.create(HelloApplication.languageManager.get("ui.mainpage.launch.launchFailed.name"), HelloApplication.languageManager.get("ui.mainpage.launch.launchFailed.Headcontent"), e.toString()));
                    }
                }).start();

            }
            else{
                if (d != null) d.close();
                FastInfomation.create(HelloApplication.languageManager.get("ui.mainpage.launch.noVersion.name"),HelloApplication.languageManager.get("ui.mainpage.launch.noVersion.Headcontent"),HelloApplication.languageManager.get("ui.mainpage.launch.noVersion.content"));
            }
        });
        if (minecraft_running){
            launchButton.setDisable(true);
        }

        launchBox = new VBox();
        launchBox.setAlignment(Pos.BOTTOM_LEFT);
        launchBox.setMaxSize(width / 2,height - 185);
        launchBox.getChildren().add(launchButton);

        title = new Label();
        launch = new Label();
        set = new Label();
        choose_version = new JFXButton();
        version_settings = new JFXButton();
        settings = new JFXButton();

        title.setFont(Fonts.b_f);
        launch.setFont(Fonts.t_f);
        set.setFont(Fonts.t_f);
        choose_version.setFont(Fonts.s_f);
        version_settings.setFont(Fonts.s_f);
        settings.setFont(Fonts.s_f);

        settings.setOnAction(event ->{
            HelloApplication.setPage(HelloApplication.CONFIGPAGE, this);
        });
        version_settings.setOnAction(event -> {
            HelloApplication.setPage(HelloApplication.VERSIONINFOPAGE, this);
        });

        is_vaild_minecraft_dir = HelloApplication.configReader.configModel.selected_minecraft_dir.contains(HelloApplication.configReader.configModel.selected_minecraft_dir_index) && new File(HelloApplication.configReader.configModel.selected_minecraft_dir_index).exists();

        choose_version.setOnAction(event -> {
            l = new ProcessDialog(1, HelloApplication.languageManager.get("ui.versionListLoad._02"));
            l.setV(0, 0, "Sending Load Version Event");
            HelloApplication.setPage(HelloApplication.VERSIONSELECTPAGE, this);

        });

        StackPane graphic = new StackPane();
        Node svg = SVG.gear(Bindings.createObjectBinding(this::returnBlack), 25.0D, 25.0D);

        StackPane graphic1 = new StackPane();
        Node svg1 = SVG.gear(Bindings.createObjectBinding(this::returnBlack),25.0D,25.0D);

        StackPane.setAlignment(svg, Pos.CENTER_RIGHT);
        graphic.getChildren().setAll(svg);
        graphic1.getChildren().setAll(svg1);

        version_settings.setGraphic(graphic);
        settings.setGraphic(graphic1);

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

        choose_version.setMaxWidth(width / 4);
        version_settings.setMaxWidth(width / 4);
        settings.setMaxWidth(width / 4);

        choose_version.setStyle("-fx-background-radius:25;-fx-border-radius:25");
        version_settings.setStyle("-fx-background-radius:25;-fx-border-radius:25");
        settings.setStyle("-fx-background-radius:25;-fx-border-radius:25");

        GameMenu = new VBox();
        GameMenu.setMaxHeight(height);
        GameMenu.setMaxWidth(width / 4);
        GameMenu.setMinHeight(height);
        GameMenu.setMinWidth(width / 4);
        GameMenu.setStyle("-fx-background-color: rgba(255,255,255,0.5);");
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
                new Spacer(),
                new JFXRippler()
        );

        HBox hBox1 = new HBox();
        hBox1.setMinSize(width / 5,height);
        hBox1.setMaxSize(width / 5,height);
        
        HBox hBox2 = new HBox();
        hBox2.setMinSize(width / 5,height);
        hBox2.setMaxSize(width / 5,height);

        this.add(GameMenu,0,1,1,1);
        this.add(hBox1,1,1,1,1);
        this.add(hBox2,2,1,1,1);
        this.add(launchBox,3,1,1,1);
    }
    public static void check(){
        launchButton.setDisable(minecraft_running);
        if (!minecraft_running){
            if (exit_code != null){
                logger.info("Minecraft exited with code " + exit_code);
                if (exit_code != 0){

                    FastInfomation.create(HelloApplication.languageManager.get("ui.mainpage.minecraftExit.title"),HelloApplication.languageManager.get("ui.mainpage.minecraftExit.Headercontent"),String.format(HelloApplication.languageManager.get("ui.mainpage.minecraftExit.content"), exit_code));
                }
                exit_code = null;
                window_showed = false;
            }
        }
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
        version_settings.setText(HelloApplication.languageManager.get("ui.mainpage.launchButton.noVersion"));
        launchButton.setText(HelloApplication.languageManager.get("ui.mainpage.launchButton.noVersion"));
        HelloApplication.configReader.configModel.selected_version_index = "";
        HelloApplication.configReader.write();
    }
    public void flush(){
        if (new File(HelloApplication.configReader.configModel.selected_minecraft_dir_index).exists()) {
            if (HelloApplication.configReader.configModel.selected_minecraft_dir.contains(HelloApplication.configReader.configModel.selected_minecraft_dir_index)) {
                if (HelloApplication.configReader.configModel.selected_version_index != null) {
                    if (Objects.requireNonNull(getMinecraftVersion.get(HelloApplication.configReader.configModel.selected_minecraft_dir_index)).contains(HelloApplication.configReader.configModel.selected_version_index)) {
                        version_settings.setText(" " + HelloApplication.configReader.configModel.selected_version_index);
                        launchButton.setText(HelloApplication.languageManager.get("ui.mainpage.launchButton.hasVersion"));
                    } else {
                        clean_null_version();
                    }
                }
            }
            else{
                clean_null_version();
            }
        }
        else{
            clean_null_version();
        }
    }
    public void refresh(){
        flush();
    }
    public void refreshType(){

    }
    public void refreshLanguage(){
        name = HelloApplication.languageManager.get("ui.mainpage.name");
        title.setText(String.format(HelloApplication.languageManager.get("ui.title"), Vars.launcher_version));
        launch.setText(HelloApplication.languageManager.get("ui.mainpage.launchTitle.launch.name"));
        set.setText(HelloApplication.languageManager.get("ui.mainpage.settings.name"));
        choose_version.setText(HelloApplication.languageManager.get("ui.mainpage.choose_version.name"));
        settings.setText(" "+HelloApplication.languageManager.get("ui.mainpage.settings.name"));
    }
}
