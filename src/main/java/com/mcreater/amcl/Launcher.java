package com.mcreater.amcl;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.audio.BGMManager;
import com.mcreater.amcl.config.ConfigWriter;
import com.mcreater.amcl.lang.LanguageManager;
import com.mcreater.amcl.nativeInterface.OSInfo;
import com.mcreater.amcl.pages.*;
import com.mcreater.amcl.pages.dialogs.SimpleDialog;
import com.mcreater.amcl.pages.dialogs.commons.AboutDialog;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.AnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.*;
import com.mcreater.amcl.util.concurrent.FXConcurrentPool;
import com.mcreater.amcl.util.svg.AbstractSVGIcons;
import com.mcreater.amcl.util.svg.DefaultSVGIcons;
import com.mcreater.amcl.util.svg.Icons;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
import java.util.List;
import java.util.Vector;

public class Launcher extends javafx.application.Application {
    static Logger logger = LogManager.getLogger(Launcher.class);
    public static Scene s = new Scene(new Pane(), Color.TRANSPARENT);
    public static Stage stage;
    static AbstractAnimationPage last;

    public static MainPage MAINPAGE;
    public static ConfigPage CONFIGPAGE;
    public static VersionSelectPage VERSIONSELECTPAGE;
    public static VersionInfoPage VERSIONINFOPAGE;
    public static AddModsPage ADDMODSPAGE;
    public static ModDownloadPage MODDOWNLOADPAGE;
    public static DownloadMcPage DOWNLOADMCPAGE;
    public static DownloadAddonSelectPage DOWNLOADADDONSELECTPAGE;
    public static UserSelectPage USERSELECTPAGE;


    public static ConfigWriter configReader;
    public static LanguageManager languageManager;
    static Background bg;
    static BackgroundSize bs;
    public static double barSize = 45;
    public static int width = 800;
    public static int height = 480;
    public static Label ln;
    public static Pane p = new Pane();
    public static JFXButton close;
    public static JFXButton min;
    public static JFXButton back;
    public static AboutDialog aboutDialog;
    public static Pane wrapper = new Pane();
    public static final SimpleDoubleProperty radius = new SimpleDoubleProperty(30);
    public static VBox top = new VBox();
    public static void initConfig() {
        try {
            FileUtils.ChangeDir.saveNowDir();
            File f = new File(FileUtils.ChangeDir.dirs, "AMCL");
            boolean b = true;
            if (!f.exists()){
                b = f.mkdirs();
            }
            if (!b){
                throw new IllegalStateException("Failed to read config");
            }
            configReader = new ConfigWriter(new File(FileUtils.ChangeDir.dirs, "AMCL/config.json"));
            configReader.check_and_write();
        } catch (Exception e) {
            logger.error("failed to read config", e);
        }
    }
    public static void initLanguageManager(LanguageManager.LanguageType type) {
        languageManager = new LanguageManager(type);
    }
    public static void initLanguageManager() {
        initConfig();
        initLanguageManager(LanguageManager.LanguageType.valueOf(configReader.configModel.language));
    }
    public void start(Stage primaryStage) throws AWTException, IOException, IllegalAccessException, NoSuchFieldException, InterruptedException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        Fonts.loadFont();
        Icons.initFXIcon();
        if (OSInfo.isWin()) {
            stage = new Stage();
            setGeometry(stage, width, height);
            bs = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true);
            logger.info("Launcher Version : " + VersionInfo.launcher_version);
            initLanguageManager();

            MAINPAGE = new MainPage(width, height);
            CONFIGPAGE = new ConfigPage(width, height);
            VERSIONSELECTPAGE = new VersionSelectPage(width, height);
            VERSIONINFOPAGE = new VersionInfoPage(width, height);
            ADDMODSPAGE = new AddModsPage(width, height);
            MODDOWNLOADPAGE = new ModDownloadPage(width, height);
            DOWNLOADMCPAGE = new DownloadMcPage(width, height);
            DOWNLOADADDONSELECTPAGE = new DownloadAddonSelectPage(width, height);
            USERSELECTPAGE = new UserSelectPage(width, height);

            languageManager.bindAll(MAINPAGE, CONFIGPAGE, VERSIONSELECTPAGE, VERSIONINFOPAGE, ADDMODSPAGE, MODDOWNLOADPAGE, DOWNLOADMCPAGE, DOWNLOADADDONSELECTPAGE, USERSELECTPAGE);

            ThemeManager.apply(LanguageManager.bindedPages);

            CONFIGPAGE.bar1.setOnMouseEntered(event -> {});
            CONFIGPAGE.bar1.setOnMouseExited(event -> {});
            CONFIGPAGE.bar2.setOnMouseEntered(event -> {});
            CONFIGPAGE.bar2.setOnMouseExited(event -> {});
            CONFIGPAGE.bar1.setOpacity(0.5);
            CONFIGPAGE.bar2.setOpacity(1);

            ThemeManager.loadButtonAnimateParent(CONFIGPAGE.p);
            last = MAINPAGE;
            setPage(last, last);

            BGMManager.init();
            BGMManager.start();

            stage.initStyle(StageStyle.TRANSPARENT);
            refresh();
            stage.setScene(s);
            stage.getIcons().add(Icons.fxIcon.get());

            new Thread(VersionChecker::check).start();
            aboutDialog = new AboutDialog();

            Rectangle rect = FXUtils.generateRect(width, height, 0);
            rect.arcWidthProperty().bind(Launcher.radius);
            rect.arcHeightProperty().bind(Launcher.radius);
            wrapper.setClip(rect);

            stage.show();
            StableMain.splashScreen.setVisible(false);
        }
        else{
            SimpleDialog dialog = new SimpleDialog(
                    StableMain.manager.get("ui.system.check.title"),
                    StableMain.manager.get("ui.system.check.content"),
                    SimpleDialog.MessageType.QUIT,
                    event -> System.exit(1)
            );
            StableMain.splashScreen.setVisible(false);
            dialog.showAndWait();
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
                FXConcurrentPool.run(last::refresh).start();
                last.refreshLanguage();
                last.refreshType();

                refresh();
            };
            last.out.setOnFinished(event -> r.run());
            last.out.play();
        }
    }
    public static AbstractSVGIcons getSVGManager(){
        return new DefaultSVGIcons();
    }

    public static void setPageCore(){
        double t_size = barSize;
        top = new VBox();
        top.setId("top-bar");
        top.setPrefSize(width, t_size);

        GridPane title = new GridPane();
        title.setAlignment(Pos.CENTER);
        close = new JFXButton();
        min = new JFXButton();
        back = new JFXButton();
        close.setPrefWidth(t_size / 6 * 5);
        close.setPrefHeight(t_size / 6 * 5);
        close.setGraphic(getSVGManager().close(ThemeManager.createPaintBinding(), t_size / 3 * 2, t_size / 3 * 2));
        close.setButtonType(JFXButton.ButtonType.RAISED);
        close.setOnAction(event -> {
            last.out.setOnFinished(event1 -> {
                stage.close();
                System.exit(0);
            });
            last.out.play();
        });
        Rectangle rect = new Rectangle(t_size / 2.5, t_size / 15, Color.BLACK);
        rect.fillProperty().bind(ThemeManager.createPaintBinding());

        stage.setOnCloseRequest(event -> close.getOnAction().handle(new ActionEvent()));
        min.setPrefWidth(t_size / 2.5);
        min.setPrefHeight(t_size / 2.5);
        min.setGraphic(rect);
        min.setButtonType(JFXButton.ButtonType.RAISED);
        min.setOnAction(event -> Launcher.stage.setIconified(true));

        back.setPrefWidth(t_size / 2.5);
        back.setPrefHeight(t_size / 2.5);
        back.setGraphic(getSVGManager().back(ThemeManager.createPaintBinding(), t_size / 3 * 2, t_size / 3 * 2));
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
        JFXButton about = new JFXButton();
        about.setPrefWidth(t_size / 2.5);
        about.setPrefHeight(t_size / 2.5);
        about.setGraphic(getSVGManager().dotsVertical(ThemeManager.createPaintBinding(), barSize / 3 * 2, barSize / 3 * 2));
        about.setButtonType(JFXButton.ButtonType.RAISED);
        about.setOnAction(event -> aboutDialog.Create());

        setTitle();
        FXUtils.ControlSize.setAll(t_size / 6 * 5, t_size / 6 * 5, about, back, min, close);
        HBox b = new HBox(back, ln);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setMinSize(400, t_size);
        b.setMaxSize(400, t_size);
        title.add(b, 0, 0, 1, 1);
        HBox cl = new HBox(about, min, close);
        cl.setAlignment(Pos.CENTER_RIGHT);
        cl.setMinSize(400, t_size);
        cl.setMaxSize(400, t_size);
        title.add(cl, 1, 0, 1, 1);
        top.getChildren().add(title);

        new WindowMovement().windowMove(top, stage);

        ThemeManager.applyTopBar(top);
        VBox v = new VBox();
        v.getChildren().addAll(top, last);
        v.setStyle("-fx-background-color: transparent");
        p.getChildren().removeIf(node -> node.getClass() == VBox.class);
        p.getChildren().add(0, v);
        wrapper.getChildren().clear();
        wrapper.getChildren().add(p);
        wrapper.setStyle("-fx-background-color: transparent");

        s.setFill(Color.TRANSPARENT);
        s.setRoot(wrapper);
        stage.setScene(s);
        Timer timer = Timer.getInstance();
        refreshBackground();
        logger.info("background generate/set used " + timer.getTimeString());
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
        stage.setTitle(String.format(languageManager.get("ui.title"), VersionInfo.launcher_name, VersionInfo.launcher_version));
    }
    public static void startApplication(String[] args) {
        launch(args);
    }
    public static void clearBgBuffer() {
        LanguageManager.bindedPages.forEach(page -> page.setBufferedBackground(null));
        refreshBackground();
    }
    public static void refreshBackground(){
        String wallpaper = "assets/imgs/background.jpg";
        Image wap = new Image(wallpaper);

        double widthRadius = wap.getWidth() / (double) width;
        double heightRadius = wap.getHeight() / (double) height;

        boolean hasBinded = false;
        AbstractAnimationPage ha = last;

        for (AbstractAnimationPage page : last.bindedPageproperty().get()) {
            if (page != null) {
                if (page.getBufferedBackground() != null) {
                    hasBinded = true;
                    ha = page;
                }
            }
        }

        WritableImage result;
        if (last.getBufferedBackground() == null && !hasBinded) {
            result = FXUtils.ImageConverter.convertToWritableImage(wap);
            WritableImage original = FXUtils.ImagePreProcesser.cutImage(result, 0, 0, (int) result.getWidth(), (int) result.getHeight());

            FXUtils.ImagePreProcesser.process(
                    result,
                    (view, image) -> view.setEffect(new GaussianBlur(100)),
                    (view, image) -> {
                        Rectangle clip = new Rectangle(
                                view.getFitWidth() / 7 * 6,
                                view.getFitHeight() / 12 * 11
                        );
                        clip.setArcWidth(0);
                        clip.setArcHeight(0);
                        view.setClip(clip);
                    }
            );

            FXUtils.ImagePreProcesser.process(
                    result,
                    (view, image) -> {
                        List<AnimationPage.NodeInfo> nodes = new Vector<>();
                        for (AnimationPage.NodeInfo box : last.nodes) {
                            if (box != null) {
                                nodes.add(new AnimationPage.NodeInfo(
                                        box.size.getMinX(),
                                        box.size.getMinY(),
                                        box.size.getWidth(),
                                        box.size.getHeight()
                                ));
                            }
                            else {
                                nodes.add(null);
                            }
                        }

                        nodes.add(new AnimationPage.NodeInfo(0, 0, width, barSize));

                        for (AnimationPage.NodeInfo node : nodes) {
                            if (node != null) {
                                node.size = new BoundingBox(node.size.getMinX(), node.size.getMinY(), node.size.getWidth() * 0.8615384615384616, node.size.getHeight() * 0.9);
                            }
                        }

                        for (int x = 0; x < image.getWidth(); x++) {
                            for (int y = 0; y < image.getHeight(); y++) {
                                if (!last.nodes.contains(null) && !FXUtils.gemotryInned(new Point2D(x / widthRadius, y / heightRadius), nodes)) {
                                    image.getPixelWriter().setColor(
                                            x, y,
                                            original.getPixelReader().getColor(x, y)
                                    );
                                }
                                else {
                                    image.getPixelWriter().setColor(
                                            x, y,
                                            FXUtils.ImagePreProcesser.noTransparent(image.getPixelReader().getColor(x, y))
                                    );
                                }
                            }
                        }
                    }
            );

            last.setBufferedBackground(result);
            for (AbstractAnimationPage pag : last.bindedPageproperty().get()) {
                if (pag != null) pag.setBufferedBackground(result);
            }
        }
        else {
            result = ha.getBufferedBackground();
        }

        BackgroundImage im = new BackgroundImage(
                result,
                BackgroundRepeat.ROUND,
                BackgroundRepeat.ROUND,
                BackgroundPosition.DEFAULT,
                bs);
        bg = new Background(im);
        p.setBackground(bg);
    }
}