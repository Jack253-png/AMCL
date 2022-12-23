package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.transitions.CachedTransition;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.concurrent.Sleeper;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import static com.mcreater.amcl.Launcher.height;
import static com.mcreater.amcl.Launcher.width;
import static com.mcreater.amcl.Launcher.wrapper;
import static com.mcreater.amcl.util.FXUtils.ColorUtil.transparent;

public abstract class AbstractDialog extends JFXAlert<String> {
    public static final ObservableList<AbstractDialog> dialogs = FXCollections.observableArrayList();
    public static final SimpleDoubleProperty dialogRadius = new SimpleDoubleProperty(30);
    public static final double blurRadius = 8;
    public static final SimpleDoubleProperty nowRadius = new SimpleDoubleProperty(0);
    public static final SimpleDoubleProperty exceptedRadius = new SimpleDoubleProperty(0);

    public final SimpleDoubleProperty dialogNowRadius = new SimpleDoubleProperty(0);

    public final SimpleDoubleProperty dialogExceptedRadius = new SimpleDoubleProperty(0);

    static {
        dialogs.addListener((ListChangeListener<AbstractDialog>) c -> exceptedRadius.set(c.getList().size() == 0 ? 0 : blurRadius));
        nowRadius.addListener((observable, oldValue, newValue) -> wrapper.setEffect(new GaussianBlur(newValue.intValue())));

        new Thread("Widget blur calc thread") {
            public void run() {
                while (true) {
                    if (nowRadius.getValue().intValue() != exceptedRadius.getValue().intValue()) {
                        double now = nowRadius.get();
                        nowRadius.set(now < exceptedRadius.get() ? now + blurRadius / 40 : now - blurRadius / 40);
                    }
                    try {
                        dialogs.forEach(abstractDialog -> {
                            if (abstractDialog.dialogNowRadius.getValue().intValue() != abstractDialog.dialogExceptedRadius.getValue().intValue()) {
                                double now2 = abstractDialog.dialogNowRadius.get();
                                abstractDialog.dialogNowRadius.set(now2 < abstractDialog.dialogExceptedRadius.get() ? now2 + blurRadius / 40 : now2 - blurRadius / 40);
                            }
                        });
                    }
                    catch (Exception ignored) {

                    }
                    Sleeper.sleep(5);
                }
            }
        }.start();
    }
    public AbstractDialog(Stage stage) {
        super(stage);
//        getDialogPane().setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setAnimation(new JFXAlertAnimation() {
            public void initAnimation(Node contentContainer, Node overlay) {
                overlay.setOpacity(0);
                contentContainer.setScaleX(.80);
                contentContainer.setScaleY(.80);
            }
            public Animation createShowingAnimation(Node contentContainer, Node overlay) {
                contentContainer.setScaleX(1);
                contentContainer.setScaleY(1);
                return new CachedTransition(contentContainer, new Timeline(
                        new KeyFrame(Duration.millis(1000),
                                new KeyValue(overlay.opacityProperty(), 1, Interpolator.EASE_BOTH)
                        ))) {
                    {
                        setCycleDuration(Duration.millis(400));
                        setDelay(Duration.seconds(0));
                    }
                };
            }
            public Animation createHidingAnimation(Node contentContainer, Node overlay) {
                return new CachedTransition(contentContainer, new Timeline(
                        new KeyFrame(Duration.millis(1000),
                                new KeyValue(overlay.opacityProperty(), 0, Interpolator.EASE_BOTH)
                        ))) {
                    {
                        setCycleDuration(Duration.millis(400));
                        setDelay(Duration.seconds(0));
                    }
                };
            }
        });
        this.initModality(Modality.WINDOW_MODAL);
        this.setOverlayClose(false);
        Rectangle r = FXUtils.generateRect(width, height, 0);
        r.arcWidthProperty().bind(Launcher.radius);
        r.arcHeightProperty().bind(Launcher.radius);
        getDialogPane().setClip(r);
        setOnShowing(event -> dialogs.add(this));
        setOnCloseRequest(event -> dialogs.remove(this));
        setOnShown(event -> FXUtils.toNodeClass(getDialogPane().getContent(), Pane.class).getChildren().forEach(node -> {
            updateBounds(node);
            node.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> updateBounds(node));
        }));

        dialogNowRadius.addListener((observable, oldValue, newValue) -> getDialogPane().setEffect(new GaussianBlur(newValue.intValue())));
        dialogs.addListener((ListChangeListener<AbstractDialog>) c -> onDialogListChange());

        setHideOnEscape(false);
    }
    private void updateBounds(Node item) {
        Bounds bound = item.getLayoutBounds();
        if (bound.getWidth() > 0 && bound.getHeight() > 0) {
            getDialogPane().getContent().setClip(FXUtils.generateRect(
                    bound.getWidth(),
                    bound.getHeight(),
                    dialogRadius.get()
            ));
        }
    }
    public void setContent(Node... content) {
        ThemeManager.addLis((observable, oldValue, newValue) -> FXUtils.toNodeClass(
                getDialogPane().getContent(), Pane.class
        ).getChildren().forEach(node -> {
            if (node instanceof Region)
                ((Region) node).setBackground(new Background(
                        new BackgroundFill(
                                transparent(newValue, 0.8),
                                CornerRadii.EMPTY,
                                Insets.EMPTY
                        )
                ));
        }));

        super.setContent(content);
    }
    private void onDialogListChange() {
        dialogExceptedRadius.set(dialogs.indexOf(this) + 1 == dialogs.size() ? 0 : blurRadius);
    }
    public void Create() {
        FXUtils.Platform.runLater(this::show);
    }
    public static Label setFont(Label l, Font font){
        l.setFont(font);
        return l;
    }
}