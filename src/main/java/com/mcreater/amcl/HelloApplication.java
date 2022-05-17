package com.mcreater.amcl;

import com.mcreater.amcl.config.ConfigModel;
import com.mcreater.amcl.config.ConfigReader;
import com.mcreater.amcl.util.Vars;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.mcreater.amcl.pages.MainPage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.MalformedURLException;

public class HelloApplication extends Application {
    static Scene s;
    public static Stage stage;
    boolean use_classic;
    String wallpaper;
    public static String config_base_path;
    @Override
    public void start(Stage primaryStage) throws MalformedURLException {
        BackgroundSize bs = new BackgroundSize(BackgroundSize.AUTO,BackgroundSize.AUTO,true,true,false,true);
        try {
            config_base_path = "C:\\Users\\Administrator\\AppData\\Roaming\\.amcl\\";
            File p = new File(config_base_path);
            if (!p.exists()){
                p.mkdirs();
            }
            ConfigReader configReader = new ConfigReader(new File(config_base_path + "config.json"));
            ConfigModel result = configReader.read();
            use_classic = result.use_classic_wallpaper;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if (!use_classic){
            wallpaper = "assets/background.jpg";
        }
        else{
            wallpaper = "assets/background-classic.jpg";
        }
        Background bg = new Background(new BackgroundImage(new Image(wallpaper),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                bs));

        stage = new Stage();
        setGeometry(stage,800,480);
        setPage(new MainPage(800,480,bg));

        stage.initStyle(StageStyle.UNIFIED);
        stage.setTitle("AMCL " + Vars.launcher_version);
        stage.setScene(s);

        stage.show();
    }
    public static void setPage(Parent n){
        s = new Scene(n);
        s.setFill(Color.TRANSPARENT);
        stage.setScene(s);
    }
    public static void setGeometry(Stage s, double width, double height){
        s.setWidth(width);
        s.setHeight(height);
        s.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}