package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.concurrent.Sleeper;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.Vector;

import static com.mcreater.amcl.Launcher.height;
import static com.mcreater.amcl.Launcher.width;

public abstract class AbstractDialog extends JFXAlert<String> {
    public static final Vector<AbstractDialog> dialogs = new Vector<>();
    SimpleObjectProperty<Thread> animationThread = new SimpleObjectProperty<>(new Thread(() -> {}));
    final double radius = 8;
    public AbstractDialog(Stage stage) {
        super(stage);
        this.setAnimation(JFXAlertAnimation.SMOOTH);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setOverlayClose(false);
        getDialogPane().setClip(FXUtils.generateRect(width, height, radius));
        setOnShowing(event -> {
            dialogs.add(this);
            inAnimation();
        });
        setOnCloseRequest(event -> {
            dialogs.remove(this);
            outAnimation();
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
        if (animationThread != null) animationThread.get().stop();
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
        if (animationThread != null) animationThread.get().stop();
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
        Platform.runLater(this::show);
    }
    public static Label setFont(Label l, Font font){
        l.setFont(font);
        return l;
    }
}