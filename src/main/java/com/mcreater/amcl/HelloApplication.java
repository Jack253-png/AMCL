package com.mcreater.amcl;

import com.mcreater.amcl.config.ConfigModel;
import com.mcreater.amcl.config.ConfigReader;
import com.mcreater.amcl.pages.AbstractAnimationPage;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;

import java.io.File;
import java.net.MalformedURLException;

public class HelloApplication extends Application {
    static Logger logger = LogManager.getLogger(HelloApplication.class);
    static Scene s;
    public static Stage stage;
    boolean use_classic;
    String wallpaper;
    public static String config_base_path;
    @Override
    public void start(Stage primaryStage){
        BackgroundSize bs = new BackgroundSize(BackgroundSize.AUTO,BackgroundSize.AUTO,true,true,false,true);
        logger.info("Launcher Version : " + Vars.launcher_version);
        logger.info("getted background size : " + bs);
        try {
            config_base_path = "C:\\Users\\Administrator\\AppData\\Roaming\\.amcl\\";
            logger.info("try to load config : " + config_base_path);
            File p = new File(config_base_path);
            if (!p.exists()){
                logger.info("making config dir : " + config_base_path);
                p.mkdirs();
            }
            ConfigReader configReader = new ConfigReader(new File(config_base_path + "config.json"));
            ConfigModel result = configReader.read();
            use_classic = result.use_classic_wallpaper;
            logger.info("use classic wallpaper : " + use_classic);
        }
        catch (Exception e){
            logger.error("failed to read config", e);
        }
        if (!use_classic){
            wallpaper = "assets/background.jpg";
        }
        else{
            wallpaper = "assets/background-classic.jpg";
        }
        logger.info("use wallpaper path : " + wallpaper);

        Background bg = new Background(new BackgroundImage(new Image(wallpaper),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                bs));

        logger.info("created background image : " + bg);

        stage = new Stage();
        setGeometry(stage,800,480);
        setPage(new MainPage(800,480,bg));

        stage.initStyle(StageStyle.UNIFIED);
        stage.setTitle("AMCL " + Vars.launcher_version);
        stage.setScene(s);

        logger.info("created stage : " + stage);

        stage.show();
    }
    public static void setPage(AbstractAnimationPage n){
        s = new Scene(n);
        s.setFill(Color.TRANSPARENT);
        stage.setScene(s);
        logger.info("setted page : " + n.name);
    }
    public static void setGeometry(Stage s, double width, double height){
        s.setWidth(width);
        s.setHeight(height);
        s.setResizable(false);
        logger.info("setted size (" + width+", "+height+") for stage " + s);
    }

    public static void main(String[] args) {
        launch(args);
    }
}