package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.transitions.CachedTransition;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.concurrent.Sleeper;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Vector;

import static com.mcreater.amcl.Launcher.height;
import static com.mcreater.amcl.Launcher.width;

public abstract class AbstractDialog extends JFXAlert<String> {
    public static final Vector<AbstractDialog> dialogs = new Vector<>();
    public static final SimpleDoubleProperty dialogRadius = new SimpleDoubleProperty(30);
    final SimpleObjectProperty<Thread> animationThread = new SimpleObjectProperty<>(new Thread(() -> {}));
    final double radius = 8;
    boolean cliped;
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
                return new CachedTransition(contentContainer, new Timeline(
                        new KeyFrame(Duration.millis(1000),
                                new KeyValue(contentContainer.scaleXProperty(), 1, Interpolator.EASE_OUT),
                                new KeyValue(contentContainer.scaleYProperty(), 1, Interpolator.EASE_OUT),
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
        this.initModality(Modality.APPLICATION_MODAL);
        this.setOverlayClose(false);
        getDialogPane().setClip(FXUtils.generateRect(width, height, Launcher.radius));
        setOnShowing(event -> {
            dialogs.add(this);
            inAnimation();
        });
        setOnCloseRequest(event -> {
            dialogs.remove(this);
            outAnimation();
        });
        setOnShown(event -> {
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            Node par;

            try {
                par = ((Pane) getDialogPane().getContent()).getChildren().get(0);
            } catch (Exception e) {
                return;
            }

            WritableImage image = par.snapshot(parameters, null);
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            if (!cliped) {
                width -= 10;
                height -= 10;
                cliped = true;
            }
            par.setClip(FXUtils.generateRect(width, height, dialogRadius.get()));
        });

        setHideOnEscape(false);
    }

    public double getBlurRadius() {
        Effect effect = Launcher.wrapper.getEffect();
        if (effect != null) {
            if (effect instanceof GaussianBlur) {
                GaussianBlur blur = (GaussianBlur) effect;
                return blur.getRadius();
            }
            else {
                return 0;
            }
        }
        else {
            return 0;
        }
    }
    public void outAnimation() {
        if (animationThread != null && animationThread.get() != null) animationThread.get().stop();
        animationThread.set(new Thread(() -> {
            for (double i2 = getBlurRadius(); i2 >= 0; i2 -= radius / 80) {
                double finalI = i2;
                FXUtils.Platform.runLater(() -> Launcher.wrapper.setEffect(new GaussianBlur(finalI)));
                Sleeper.sleep(1);
            }
            FXUtils.Platform.runLater(() -> Launcher.wrapper.setEffect(null));
        }));
        if (dialogs.size() == 0) animationThread.get().start();
    }
    public void inAnimation() {
        if (animationThread != null && animationThread.get() != null) animationThread.get().stop();
        animationThread.set(new Thread(() -> {
            FXUtils.Platform.runLater(() -> Launcher.wrapper.setEffect(null));
            for (double i2 = getBlurRadius(); i2 <= radius; i2 += radius / 80) {
                double finalI = i2;
                FXUtils.Platform.runLater(() -> Launcher.wrapper.setEffect(new GaussianBlur(finalI)));
                Sleeper.sleep(1);
            }
        }));
        if (dialogs.size() == 1) animationThread.get().start();
    }

    public void Create(){
        FXUtils.Platform.runLater(this::show);
    }
    public static Label setFont(Label l, Font font){
        l.setFont(font);
        return l;
    }
}