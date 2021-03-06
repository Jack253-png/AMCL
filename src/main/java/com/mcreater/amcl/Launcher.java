package com.mcreater.amcl;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.api.githubApi.GithubReleases;
import com.mcreater.amcl.audio.BGMManager;
import com.mcreater.amcl.config.ConfigWriter;
import com.mcreater.amcl.lang.LanguageManager;
import com.mcreater.amcl.pages.*;
import com.mcreater.amcl.pages.dialogs.PopupMessage;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.pages.stages.UpgradePage;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.ChangeDir;
import com.mcreater.amcl.util.SetSize;
import com.mcreater.amcl.util.Vars;
import com.mcreater.amcl.util.multiThread.Run;
import com.mcreater.amcl.util.svg.AbstractSVGIcons;
import com.mcreater.amcl.util.svg.SVGIcons;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
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
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

public class Launcher extends javafx.application.Application {
    static Logger logger = LogManager.getLogger(Launcher.class);
    static Scene s = new Scene(new Pane(), Color.TRANSPARENT);
    public static Stage stage;
    static AbstractAnimationPage last;
    static boolean is_t;
    public static MainPage MAINPAGE;
    public static ConfigPage CONFIGPAGE;
    public static VersionSelectPage VERSIONSELECTPAGE;
    public static VersionInfoPage VERSIONINFOPAGE;
    public static AddModsPage ADDMODSPAGE;
    public static ModDownloadPage MODDOWNLOADPAGE;
    public static DownloadMcPage DOWNLOADMCPAGE;
    public static DownloadAddonSelectPage DOWNLOADADDONSELECTPAGE;
    public static ThemeManager themeManager;
    public static ConfigWriter configReader;
    public static LanguageManager languageManager;
    static Background bg;
    static BackgroundSize bs;
    public static double barSize = 45;
    public static int width = 800;
    public static int height = 480;
    public static Label ln;
    public static Pane p = new Pane();
    public void start(Stage primaryStage) throws AWTException, IOException, IllegalAccessException, NoSuchFieldException, InterruptedException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        Fonts.loadFont();
        if (is_t) {
            languageManager = new LanguageManager(null);
            themeManager = new ThemeManager();
            stage = new Stage();
            setGeometry(stage, width, height);
            bs = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true);
            logger.info("Launcher Version : " + Vars.launcher_version);
            try {
                ChangeDir.saveNowDir();
                File f = new File(ChangeDir.dirs, "AMCL");
                boolean b = true;
                if (!f.exists()){
                    b = f.mkdirs();
                }
                if (!b){
                    throw new IllegalStateException("Failed to read config");
                }
                configReader = new ConfigWriter(new File(ChangeDir.dirs, "AMCL/config.json"));
                configReader.check_and_write();
            } catch (Exception e) {
                logger.error("failed to read config", e);
            }
            languageManager.setLanguage(LanguageManager.valueOf(configReader.configModel.language));
            MAINPAGE = new MainPage(width, height);
            CONFIGPAGE = new ConfigPage(width, height);
            VERSIONSELECTPAGE = new VersionSelectPage(width, height);
            VERSIONINFOPAGE = new VersionInfoPage(width, height);
            ADDMODSPAGE = new AddModsPage(width, height);
            MODDOWNLOADPAGE = new ModDownloadPage(width, height);
            DOWNLOADMCPAGE = new DownloadMcPage(width, height);
            DOWNLOADADDONSELECTPAGE = new DownloadAddonSelectPage(width, height);

            languageManager.bindAll(MAINPAGE, CONFIGPAGE, VERSIONSELECTPAGE, VERSIONINFOPAGE, ADDMODSPAGE, MODDOWNLOADPAGE, DOWNLOADMCPAGE, DOWNLOADADDONSELECTPAGE);

            themeManager.apply(this);

            refreshBackground();

            last = MAINPAGE;
            setPage(last, last);

            BGMManager.init();
            BGMManager.start();

            stage.initStyle(StageStyle.TRANSPARENT);
            refresh();
            stage.setScene(s);
            stage.getIcons().add(new Image("assets/icons/grass.png"));

            stage.initStyle(StageStyle.TRANSPARENT);
            WindowMovement windowMovement = new WindowMovement();
            windowMovement.windowMove(s, stage);
            stage.show();
            stage.setOnHidden(event -> {
                last.out.play();
                last.out.setOnFinished(event1 -> System.exit(0));
            });
            new Thread(() -> {
                try {
                    if (GithubReleases.isDevelop()) {
                        PopupMessage.createMessage(languageManager.get("ui.mainpage.versionChecker.inDevelope"), PopupMessage.MessageTypes.LABEL, null);
                    } else if (GithubReleases.outDated()) {
                        Runnable show = () -> new UpgradePage().open();
                        Platform.runLater(() -> {
                            Hyperlink link = (Hyperlink) PopupMessage.createMessage(languageManager.get("ui.mainpage.versionChecker.outDated"), PopupMessage.MessageTypes.HYPERLINK, null);
                            link.setOnAction(event -> show.run());
                        });
                    }
                }
                catch (IllegalStateException e){
                    Platform.runLater(() -> PopupMessage.createMessage(languageManager.get("ui.mainpage.versionChecker.checkFailed.name"), PopupMessage.MessageTypes.LABEL, null));
                }
            }).start();
            StableMain.initPlugins(StableMain.intros);
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
            configReader.write();
            last.onExitPage();
            Runnable r = () -> {
                last = n;
                setPageCore();
                last.in.play();
                Run.run(last::refresh).start();
                last.refreshLanguage();
                last.refreshType();

                refresh();
            };
            last.out.setOnFinished(event -> r.run());
            last.out.play();
        }
    }
    public static AbstractSVGIcons getSVGManager(){
        return new SVGIcons();
    }

    private static void setPageCore(){
        double t_size = barSize;
        VBox top = new VBox();
        top.setId("top-bar");
        top.setPrefSize(width, t_size);

        GridPane title = new GridPane();
        title.setAlignment(Pos.CENTER);
        JFXButton close = new JFXButton();
        JFXButton min = new JFXButton();
        JFXButton back = new JFXButton();
        close.setPrefWidth(t_size / 6 * 5);
        close.setPrefHeight(t_size / 6 * 5);
        close.setGraphic(getSVGManager().close(Bindings.createObjectBinding(() -> Paint.valueOf("#000000")), t_size / 3 * 2, t_size / 3 * 2));
        close.setButtonType(JFXButton.ButtonType.RAISED);
        close.setOnAction(event -> stage.close());
        min.setPrefWidth(t_size / 2.5);
        min.setPrefHeight(t_size / 2.5);
        min.setGraphic(new Rectangle(t_size / 2.5, t_size / 15, Color.BLACK));
        min.setButtonType(JFXButton.ButtonType.RAISED);
        min.setOnAction(event -> Launcher.stage.setIconified(true));

        back.setPrefWidth(t_size / 2.5);
        back.setPrefHeight(t_size / 2.5);
        back.setGraphic(getSVGManager().back(Bindings.createObjectBinding(() -> Paint.valueOf("#000000")), t_size / 3 * 2, t_size / 3 * 2));
        back.setButtonType(JFXButton.ButtonType.RAISED);
        AbstractAnimationPage lpa = last.l;
        ln = new Label();
        ln.setFont(Fonts.s_f);
        if (lpa == null) back.setDisable(true);
        else{
            back.setOnAction(event -> {
                configReader.write();
                setPage(lpa, lpa);
            });
        }
        setTitle();
        SetSize.setAll(t_size / 6 * 5, t_size / 6 * 5, back, min, close);
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

        themeManager.applyTopBar(top);
        VBox v = new VBox();
        v.getChildren().addAll(top, last);
        v.setStyle("-fx-background-color: transparent");
        p = new Pane();
        p.setStyle("-fx-background-color: transparent");
        p.getChildren().add(0, v);
        refreshBackground();
        s.setFill(null);
        s.setRoot(p);
        s.setFill(null);
        stage.setScene(s);
        s.setOnKeyPressed(event -> System.out.println(event.getCode()));
    }
    public static void setTitle(){
        AbstractAnimationPage lpa = last.l;
        if (lpa != null){
            ln.setText(lpa.name);
        }
    }
    private static void setGeometry(Stage s, double width, double height){
        s.setWidth(width);
        s.setHeight(height);
        s.setResizable(false);
        logger.info("setted size (" + width+", "+height+") for stage " + s);
    }
    public static void refresh(){
        stage.setTitle(String.format(languageManager.get("ui.title"), Vars.launcher_name, Vars.launcher_version));
    }
    public static void startApplication(String[] args, boolean is_true) {
        is_t = is_true;
        launch(args);
    }
    public static void refreshBackground(){
        String wallpaper = "assets/imgs/background.jpg";

        bg = new Background(new BackgroundImage(new Image(wallpaper),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                bs));

        MAINPAGE.setBackground(bg);
        VERSIONSELECTPAGE.setBackground(bg);
        CONFIGPAGE.setBackground(bg);
        VERSIONINFOPAGE.setBackground(bg);
        ADDMODSPAGE.setBackground(bg);
        MODDOWNLOADPAGE.setBackground(bg);
        DOWNLOADMCPAGE.setBackground(bg);
        DOWNLOADADDONSELECTPAGE.setBackground(bg);
    }
}