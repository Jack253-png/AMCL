package com.mcreater.amcl;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.config.ConfigWriter;
import com.mcreater.amcl.lang.LanguageManager;
import com.mcreater.amcl.natives.OSInfo;
import com.mcreater.amcl.pages.AddModsPage;
import com.mcreater.amcl.pages.ConfigPage;
import com.mcreater.amcl.pages.DownloadAddonSelectPage;
import com.mcreater.amcl.pages.DownloadMcPage;
import com.mcreater.amcl.pages.MainPage;
import com.mcreater.amcl.pages.ModDownloadPage;
import com.mcreater.amcl.pages.UserSelectPage;
import com.mcreater.amcl.pages.VersionInfoPage;
import com.mcreater.amcl.pages.VersionSelectPage;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.dialogs.SimpleDialog;
import com.mcreater.amcl.pages.dialogs.commons.AboutDialog;
import com.mcreater.amcl.pages.dialogs.commons.PopupMessage;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.AnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.pages.stages.UpgradePage;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.Timer;
import com.mcreater.amcl.util.VersionChecker;
import com.mcreater.amcl.util.VersionInfo;
import com.mcreater.amcl.util.builders.ThreadBuilder;
import com.mcreater.amcl.util.concurrent.FXConcurrentPool;
import com.mcreater.amcl.util.math.Fraction;
import com.mcreater.amcl.util.svg.AbstractSVGIcons;
import com.mcreater.amcl.util.svg.DefaultSVGIcons;
import com.mcreater.amcl.util.svg.Icons;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Vector;

import static com.mcreater.amcl.util.FXUtils.ColorUtil.transparent;
import static com.mcreater.amcl.util.FileUtils.PathUtil.buildPath;

public class Launcher {
    static Logger logger = LogManager.getLogger(Launcher.class);
    public static Scene scene = new Scene(new Pane(), Color.TRANSPARENT);
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
    public static Pane wrapper = new Pane();
    public static StackPane topWrapper = new StackPane();
    public static final SimpleDoubleProperty radius = new SimpleDoubleProperty(30);
    public static VBox top = new VBox();
    public static final Vector<AbstractAnimationPage> pages = new Vector<>();

    private static Timeline onStageShow;
    private static Timeline onStageExit;

    public static void initConfig() {
        try {
            FileUtils.ChangeDir.saveNowDir();
            File f = new File(FileUtils.ChangeDir.dirs, "AMCL");
            boolean b = true;
            if (!f.exists()) {
                b = f.mkdirs();
            }
            if (!b) {
                throw new IllegalStateException("Failed to read config");
            }
            configReader = new ConfigWriter(new File(FileUtils.ChangeDir.dirs, buildPath("AMCL", "config.json")));
            configReader.check_and_write();
        } catch (Exception e) {
            logger.error("failed to read config", e);
        }
    }

    public static void initLanguageManager(LanguageManager.LanguageType type) {
        languageManager = new LanguageManager(type);
    }

    public static void basicInitialize() {
        initConfig();
        initLanguageManager(configReader.configModel.language);
    }

    public static void start(Stage primaryStage) throws Exception {
        Fonts.loadFont();
        Icons.initFXIcon();
        if (OSInfo.isWin() || OSInfo.isLinux()) {
            stage = primaryStage;
            setGeometry(stage, width, height);
            bs = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true);
            logger.info("Launcher Version : " + VersionInfo.launcher_version);
            basicInitialize();

            MAINPAGE = new MainPage(width, height);
            CONFIGPAGE = new ConfigPage(width, height);
            VERSIONSELECTPAGE = new VersionSelectPage(width, height);
            VERSIONINFOPAGE = new VersionInfoPage(width, height);
            ADDMODSPAGE = new AddModsPage(width, height);
            MODDOWNLOADPAGE = new ModDownloadPage(width, height);
            DOWNLOADMCPAGE = new DownloadMcPage(width, height);
            DOWNLOADADDONSELECTPAGE = new DownloadAddonSelectPage(width, height);
            USERSELECTPAGE = new UserSelectPage(width, height);

            pages.addAll(J8Utils.createList(MAINPAGE, CONFIGPAGE, VERSIONSELECTPAGE, VERSIONINFOPAGE, ADDMODSPAGE, MODDOWNLOADPAGE, DOWNLOADMCPAGE, DOWNLOADADDONSELECTPAGE, USERSELECTPAGE));
            languageManager.setListener(() -> pages.forEach(AbstractAnimationPage::refreshLanguage));

            ThemeManager.apply(pages);

            CONFIGPAGE.bar1.setOnMouseEntered(null);
            CONFIGPAGE.bar1.setOnMouseExited(null);
            CONFIGPAGE.bar2.setOnMouseEntered(null);
            CONFIGPAGE.bar2.setOnMouseExited(null);
            CONFIGPAGE.bar1.setOpacity(1);
            CONFIGPAGE.bar2.setOpacity(0.5);

            ThemeManager.loadButtonAnimateParent(CONFIGPAGE.p);
            last = MAINPAGE;
            setPage(last, last);

            stage.initStyle(StageStyle.TRANSPARENT);
            refresh();
            stage.setScene(scene);
            stage.getIcons().add(Icons.fxIcon.get());

            ThreadBuilder.createBuilder()
                    .runTarget(() -> VersionChecker.check(
                            (s, aBoolean) ->
                                    FXUtils.Platform.runLater(() ->
                                            PopupMessage.createMessage(s, aBoolean ? PopupMessage.MessageTypes.HYPERLINK : PopupMessage.MessageTypes.LABEL, aBoolean ? event -> new UpgradePage().open() : null)
                                    )
                    ))
                    .name("Version update checker thread")
                    .buildAndRun();

            Rectangle rect = FXUtils.generateRect(width, height, 0);
            rect.arcWidthProperty().bind(Launcher.radius);
            rect.arcHeightProperty().bind(Launcher.radius);
            topWrapper.setClip(rect);

            onStageShow = FXUtils.AnimationUtils.generateNodeInAnimation(topWrapper);
            onStageExit = FXUtils.AnimationUtils.generateNodeOutAnimation(topWrapper);

            topWrapper.setOpacity(0);
            topWrapper.setScaleX(0.8);
            topWrapper.setScaleY(0.8);
            stage.show();
            playStageAnimation(false, () -> {}, () -> StableMain.splashScreen.setVisible(false));
        } else {
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

    private static void playStageAnimation(boolean isExit, Runnable finisher, Runnable start) {
        start.run();
        if (isExit) {
            onStageShow.stop();
            onStageExit.setOnFinished(event -> finisher.run());
            onStageExit.playFromStart();
        } else {
            onStageExit.stop();
            onStageShow.setOnFinished(event -> finisher.run());
            onStageShow.playFromStart();
        }
    }

    public static void setPage(AbstractAnimationPage n, AbstractAnimationPage caller) {
        if (caller.getCanMovePage()) {
            configReader.write();
            last.onExitPage();
            last.out.setOnFinished(event -> {
                last = n;
                setPageCore();
                last.in.play();
                FXConcurrentPool.run(last::refresh).start();
                last.refreshLanguage();
                last.refreshType();

                refresh();
            });
            last.out.play();
        }
    }

    public static AbstractSVGIcons getSVGManager() {
        return new DefaultSVGIcons();
    }

    public static void setPageCore() {
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
        close.setOnAction(event -> playStageAnimation(true, () -> {
            stage.close();
            Platform.exit();
            System.exit(0);
        }, () -> {}));
        Rectangle rect = new Rectangle(t_size / 2.5, t_size / 15, Color.BLACK);
        rect.fillProperty().bind(ThemeManager.createPaintBinding());

        stage.setOnCloseRequest(event -> close.getOnAction().handle(new ActionEvent()));
        min.setPrefWidth(t_size / 2.5);
        min.setPrefHeight(t_size / 2.5);
        min.setGraphic(rect);
        min.setButtonType(JFXButton.ButtonType.RAISED);
        min.setOnAction(event -> stage.setIconified(true));

        back.setPrefWidth(t_size / 2.5);
        back.setPrefHeight(t_size / 2.5);
        back.setGraphic(getSVGManager().back(ThemeManager.createPaintBinding(), t_size / 3 * 2, t_size / 3 * 2));
        back.setButtonType(JFXButton.ButtonType.RAISED);
        AbstractAnimationPage lpa = last.l;
        ln = new Label();
        ln.setFont(Fonts.s_f);
        if (lpa == null) back.setDisable(true);
        else {
            back.setOnAction(event -> {
                configReader.write();
                setPage(lpa, lpa);
            });
        }
        JFXButton about = new JFXButton();
        about.setPrefWidth(t_size / 2.5);
        about.setPrefHeight(t_size / 2.5);
        about.setGraphic(getSVGManager().informationOutline(ThemeManager.createPaintBinding(), barSize / 3 * 2, barSize / 3 * 2));
        about.setButtonType(JFXButton.ButtonType.RAISED);
        about.setOnAction(event -> new AboutDialog().Create());

        JFXButton messages = new JFXButton();
        messages.setPrefWidth(t_size / 2.5);
        messages.setPrefHeight(t_size / 2.5);
        messages.setGraphic(getSVGManager().bell(ThemeManager.createPaintBinding(), barSize / 3 * 2, barSize / 3 * 2));
        messages.setButtonType(JFXButton.ButtonType.RAISED);
        messages.setOnAction(event -> PopupMessage.showDialog());

        DoubleProperty property = new SimpleDoubleProperty(1);
        property.addListener((observable, oldValue, newValue) -> messages.setGraphic(getSVGManager().bell(Bindings.createObjectBinding(() -> transparent(ThemeManager.createPaintBinding().get(), newValue.doubleValue())), barSize / 3 * 2, barSize / 3 * 2)));

        Timeline line = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(
                                property,
                                1
                        )
                ),
                new KeyFrame(
                        Duration.millis(300),
                        new KeyValue(
                                property,
                                0.5
                        )
                )
        );
        line.setCycleCount(Timeline.INDEFINITE);
        line.setAutoReverse(true);

        ChangeListener<Boolean> handler = (observable, oldValue, newValue) -> {
            if (newValue) line.playFromStart();
            else {
                line.stop();
                property.set(1);
            }
        };

        handler.changed(PopupMessage.unreadedProperty(), PopupMessage.unreadedProperty().get(), PopupMessage.unreadedProperty().get());
        PopupMessage.addUnreadedListener(handler);

        setTitle();
        FXUtils.ControlSize.setAll(t_size / 6 * 5, t_size / 6 * 5, about, back, min, close);
        HBox b = new HBox(back, ln);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setMinSize((double) width / 2, t_size);
        b.setMaxSize((double) width / 2, t_size);
        title.add(b, 0, 0, 1, 1);
        HBox cl = new HBox(messages, about, min, close);
        cl.setAlignment(Pos.CENTER_RIGHT);
        cl.setMinSize((double) width / 2, t_size);
        cl.setMaxSize((double) width / 2, t_size);
        title.add(cl, 1, 0, 1, 1);
        top.getChildren().add(title);

        FXUtils.WindowMovement.getInstance().windowMove(top, stage);

        ThemeManager.applyTopBar(top);
        VBox v = new VBox();
        v.getChildren().addAll(top, last);
        p.getChildren().removeIf(node -> node.getClass() == VBox.class);
        p.getChildren().add(0, v);
        wrapper.getChildren().clear();
        wrapper.getChildren().addAll(p);
        topWrapper.getChildren().clear();
        topWrapper.getChildren().addAll(wrapper, AbstractDialog.wrapper);
        topWrapper.setBackground(new Background(
                new BackgroundFill(
                        Color.TRANSPARENT,
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                )
        ));
        FXUtils.disableNodeKeyboard(topWrapper);

        scene.setFill(Color.TRANSPARENT);
        scene.setRoot(topWrapper);
        stage.setScene(scene);
        refreshBackground();
        scene.setOnKeyPressed(event -> System.out.println(event.getCode()));
    }

    public static void setTitle() {
        AbstractAnimationPage lpa = last.l;
        if (lpa != null) {
            ln.setText(lpa.name);
        }
    }

    private static void setGeometry(Stage s, double width, double height) {
        s.setWidth(width);
        s.setHeight(height);
        s.setResizable(false);
        logger.info("setted size (" + width + ", " + height + ") for stage " + s);
    }

    public static void refresh() {
        stage.setTitle(languageManager.get("ui.title", VersionInfo.launcher_name, VersionInfo.launcher_version));
    }

    public static void clearBgBuffer() {
        pages.forEach(page -> page.setBufferedBackground(null));
        refreshBackground();
    }

    public static void refreshBackground() {
        Timer timer = Timer.getInstance();
        String wallpaper = "assets/imgs/background.jpg";
        Image wap = new Image(wallpaper);

        double widthRadius = wap.getWidth() / (double) width;
        double heightRadius = wap.getHeight() / (double) height;

        boolean hasBinded = false;
        AbstractAnimationPage ha = last;

        for (AbstractAnimationPage page : last.bindedPageproperty().get()) {
            if (page != null && page.getBufferedBackground() != null) {
                hasBinded = true;
                ha = page;
            }
        }

        WritableImage result = FXUtils.ImageConverter.convertToWritableImage(wap);

        if (configReader.configModel.enable_blur) {
            if (last.getBufferedBackground() == null && !hasBinded) {
                WritableImage original = new WritableImage(result.getPixelReader(), (int) result.getWidth(), (int) result.getHeight());
                FXUtils.ImagePreProcesser.process(
                        result,
                        (view, image) -> view.setEffect(new GaussianBlur(Integer.MAX_VALUE)),
                        (view, image) -> {
                            Rectangle clip = new Rectangle(
                                    view.getFitWidth(),
                                    view.getFitHeight()
                            );
                            clip.setArcWidth(0);
                            clip.setArcHeight(0);
                            view.setClip(clip);
                        }
                );
                FXUtils.ImagePreProcesser.process(
                        result,
                        (view, image) -> {
                            Fraction widthRec = new Fraction(6, 7);
                            Fraction heightRec = new Fraction(11, 12);

                            List<AnimationPage.NodeInfo> nodes = new Vector<>(J8Utils.createList(new AnimationPage.NodeInfo(0, 0, width * widthRec.doubleValue(), barSize * heightRec.doubleValue())));
                            for (AnimationPage.NodeInfo box : last.nodes) {
                                if (box != null) {
                                    nodes.add(new AnimationPage.NodeInfo(
                                            box.size.getMinX(),
                                            box.size.getMinY(),
                                            box.size.getWidth() * widthRec.doubleValue(),
                                            box.size.getHeight() * heightRec.doubleValue()
                                    ));
                                } else {
                                    nodes.add(null);
                                }
                            }

                            for (double x = 0; x < image.getWidth(); x++) {
                                int tempX = (int) x;
                                for (double y = 0; y < image.getHeight(); y++) {
                                    if (!last.nodes.contains(null) &&
                                            !FXUtils.gemotryInned(
                                                    new Point2D(
                                                            x / widthRadius,
                                                            y / heightRadius
                                                    ), nodes)) {
                                        image.getPixelWriter().setColor(
                                                tempX, (int) y,
                                                original.getPixelReader().getColor(tempX, (int) y)
                                        );
                                    } else {
                                        Color color = image.getPixelReader().getColor(tempX, (int) y);
                                        if (color.getOpacity() < 1) {
                                            image.getPixelWriter().setColor(
                                                    tempX, (int) y,
                                                    transparent(color, 1.0)
                                            );
                                        }
                                    }
                                }
                            }
                        }
                );

                last.setBufferedBackground(result);
                for (AbstractAnimationPage pag : last.bindedPageproperty().get()) {
                    if (pag != null) pag.setBufferedBackground(result);
                }
            } else {
                result = ha.getBufferedBackground();
            }
        }

        BackgroundImage im = new BackgroundImage(
                result,
                BackgroundRepeat.ROUND,
                BackgroundRepeat.ROUND,
                BackgroundPosition.DEFAULT,
                bs);
        bg = new Background(im);
        p.setBackground(bg);
        logger.info("Background generate takes " + timer.getTimeString());
    }
}