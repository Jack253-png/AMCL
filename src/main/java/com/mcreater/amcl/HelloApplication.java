package com.mcreater.amcl;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.config.ConfigWriter;
import com.mcreater.amcl.lang.LanguageManager;
import com.mcreater.amcl.pages.ConfigPage;
import com.mcreater.amcl.pages.MainPage;
import com.mcreater.amcl.pages.VersionInfoPage;
import com.mcreater.amcl.pages.VersionSelectPage;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.SVG;
import com.mcreater.amcl.util.Vars;
import com.mcreater.amcl.util.multiThread.Run;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class HelloApplication extends Application {
    static Logger logger = LogManager.getLogger(HelloApplication.class);
    public static Scene s = new Scene(new Pane(), Color.TRANSPARENT);
    public static Stage stage;
    public static AbstractAnimationPage last;
    static boolean is_t;
    public static MainPage MAINPAGE;
    public static ConfigPage CONFIGPAGE;
    public static VersionSelectPage VERSIONSELECTPAGE;
    public static VersionInfoPage VERSIONINFOPAGE;
    public static ConfigWriter configReader;
    public static LanguageManager languageManager;
    static Background bg;
    static BackgroundSize bs;
    @Override
    public void start(Stage primaryStage) throws AWTException, IOException {
        if (is_t) {
            languageManager = new LanguageManager(null);
            stage = new Stage();
            setGeometry(stage, 800, 480);
            bs = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true);
            logger.info("Launcher Version : " + Vars.launcher_version);
            logger.info("getted background size : " + bs);
            try {
                File f = new File("AMCL");
                boolean b = true;
                if (!f.exists()){
                    b = f.mkdirs();
                }
                if (!b){
                    throw new IllegalStateException("Failed to read config");
                }
                configReader = new ConfigWriter(new File("AMCL/config.json"));
                configReader.check_and_write();
            } catch (Exception e) {
                logger.error("failed to read config", e);
            }

            languageManager.setLanguage(LanguageManager.valueOf(configReader.configModel.language));
            MAINPAGE = new MainPage(800, 480);
            CONFIGPAGE = new ConfigPage(800, 480);
            VERSIONSELECTPAGE = new VersionSelectPage(800, 480);
            VERSIONINFOPAGE = new VersionInfoPage(800, 480);

            setBackground();

            last = MAINPAGE;
            setPage(last, last);

            stage.initStyle(StageStyle.UNIFIED);
            refresh();
            stage.setScene(s);

            logger.info("created stage : " + stage);

            stage.getIcons().add(new Image("assets/grass.png"));

            stage.initStyle(StageStyle.TRANSPARENT);
            WindowMovement windowMovement = new WindowMovement();
            windowMovement.windowMove(s, stage);
            stage.show();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("System Version Checker");
            alert.setHeaderText("Please Use Windows");
            alert.setContentText("Launcher Will Exit");
            alert.showAndWait();
        }
    }
    public static void setPage(AbstractAnimationPage n, AbstractAnimationPage caller) {
        if (caller.getCanMovePage()) {
            last.setOut();
            last = n;
            last.setIn();
            last.setTypeAll(true);
            last.in.stop();
            last.setTypeAll(false);
            Service<String> refresh = Run.run(last::refresh);
            Service<String> refreshLanguage = Run.run(last::refreshLanguage);
            Service<String> refreshType = Run.run(last::refreshType);
            refresh.start();
            refreshLanguage.start();
            refreshType.start();
            refresh();

            setPageCore(n);
        }
    }
    public static void setPageCore(AbstractAnimationPage n){
        double t_size = 45;
        VBox top = new VBox();
        top.setStyle("-fx-background-color: rgb(173,216,246)");
        top.setPrefSize(800, t_size);

        GridPane title = new GridPane();
        title.setAlignment(Pos.CENTER);
        JFXButton close = new JFXButton();
        JFXButton min = new JFXButton();
        JFXButton back = new JFXButton();
        StackPane graphic = new StackPane();
        Node svg = SVG.close(Bindings.createObjectBinding(() -> Paint.valueOf("#000000")), t_size / 3 * 2, t_size / 3 * 2);
        StackPane graphic1 = new StackPane();
        Node svg1 = new Rectangle(t_size / 2.5, t_size / 15, Color.BLACK);
        StackPane graphic2 = new StackPane();
        Node svg2 = SVG.back(Bindings.createObjectBinding(() -> Paint.valueOf("#000000")), t_size / 3 * 2, t_size / 3 * 2);
        graphic.getChildren().setAll(svg);
        graphic1.getChildren().setAll(svg1);
        graphic2.getChildren().setAll(svg2);

        String round = "-fx-background-radius:" + t_size / 2 + ";-fx-border-radius: " + t_size / 2;

        close.setPrefWidth(t_size / 6 * 5);
        close.setPrefHeight(t_size / 6 * 5);
        close.setGraphic(graphic);
        close.setStyle(round);
        close.setButtonType(JFXButton.ButtonType.RAISED);
        close.setOnAction(event -> {
            last.setOut();
            stage.close();
            Platform.exit();
            System.exit(0);
        });
        min.setPrefWidth(t_size / 2.5);
        min.setPrefHeight(t_size / 2.5);
        min.setGraphic(graphic1);
        min.setStyle(round);
        min.setButtonType(JFXButton.ButtonType.RAISED);
        min.setOnAction(event -> HelloApplication.stage.setIconified(true));

        back.setPrefWidth(t_size / 2.5);
        back.setPrefHeight(t_size / 2.5);
        back.setGraphic(graphic2);
        back.setButtonType(JFXButton.ButtonType.RAISED);
        back.setStyle(round);
        AbstractAnimationPage lpa = last.l;
        Label ln = new Label();
        if (lpa == null) back.setDisable(true);
        else{
            back.setOnAction(event -> {
                configReader.write();
                setPage(lpa, lpa);
            });
            ln.setText(lpa.name);
        }
        ln.setFont(Fonts.s_f);
        set(back, t_size / 6 * 5);
        set(min, t_size / 6 * 5);
        set(close, t_size / 6 * 5);
        HBox b = new HBox(back, ln);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setMinSize(400, t_size);
        b.setMaxSize(400, t_size);
        title.add(b, 0, 0, 1, 1);
        HBox cl = new HBox(min, close);
        cl.setAlignment(Pos.CENTER_RIGHT);
        cl.setMinSize(400, t_size);
        cl.setMaxSize(400, t_size);
        title.add(cl, 1, 0, 1, 1);
        top.getChildren().add(title);

        VBox v = new VBox(top, last);
        v.setStyle("-fx-background-radius:25;-fx-border-radius:25");
        Pane p = new Pane();
        p.getChildren().addAll(v);
        setBackground();
        s.setFill(null);
        s.setRoot(p);
        s.setFill(Color.TRANSPARENT);
        stage.setScene(s);
        logger.info("set page : " + last.name);

    }
    public static void setAllPage(AbstractAnimationPage n){
        MAINPAGE.name = languageManager.get("ui.mainpage.name");
        VERSIONSELECTPAGE.name = languageManager.get("ui.versionselectpage.name");
        CONFIGPAGE.name = languageManager.get("ui.configpage.name");
        VERSIONINFOPAGE.name = languageManager.get("ui.versioninfopage.name");
        setPageCore(n);
    }
    public static void setGeometry(Stage s, double width, double height){
        s.setWidth(width);
        s.setHeight(height);
//        s.setResizable(false);
        logger.info("setted size (" + width+", "+height+") for stage " + s);
        logger.info(String.format("setted size (%f, %f) for stage %s", width, height, s));
    }
    public static void set(JFXButton n, double s){
        n.setMaxSize(s,s);
        n.setMinSize(s,s);
    }
    public static void refresh(){
        stage.setTitle(String.format(languageManager.get("ui.title"), Vars.launcher_version));
    }

    public static void startApplication(String[] args, boolean is_true) {
        is_t = is_true;
        launch(args);
    }
    public static void setBackground(){
        String wallpaper = "assets/background.jpg";

        bg = new Background(new BackgroundImage(new Image(wallpaper),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                bs));

        MAINPAGE.setBackground(bg);
        VERSIONSELECTPAGE.setBackground(bg);
        CONFIGPAGE.setBackground(bg);
        VERSIONINFOPAGE.setBackground(bg);
    }
}