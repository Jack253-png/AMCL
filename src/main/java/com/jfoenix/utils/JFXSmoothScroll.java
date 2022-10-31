package com.jfoenix.utils;

import com.mcreater.amcl.controls.JFXProgressBar;
import com.mcreater.amcl.util.concurrent.Sleeper;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.function.Function;

public class JFXSmoothScroll {
    private static final double[] percents = {0.01, 0.02, 0.04, 0.08, 0.16, 0.32, 0.64, 0.8, 0.86,
    0.9, 0.92, 0.94, 0.96, 1.00};
    public static final Map<ProgressBar, BarUpdateThread> barMap = new HashMap<>();
    private static void customScrolling(ScrollPane scrollPane, DoubleProperty scrollDriection, Function<Bounds, Double> sizeFunc, double speed) {
        final double[] frictions = {0.99, 0.1, 0.05, 0.04, 0.03, 0.02, 0.01, 0.04, 0.01, 0.008, 0.008, 0.008, 0.008, 0.0006, 0.0005, 0.00003, 0.00001};
        final double[] pushes = {speed};
        final double[] derivatives = new double[frictions.length];

        Timeline timeline = new Timeline();
        final EventHandler<MouseEvent> dragHandler = event -> timeline.stop();
        final EventHandler<ScrollEvent> scrollHandler = event -> {
            if (event.getEventType() == ScrollEvent.SCROLL) {
                int direction = event.getDeltaY() > 0 ? -1 : 1;
                for (int i = 0; i < pushes.length; i++) {
                    derivatives[i] += direction * pushes[i];
                }
                if (timeline.getStatus() == Animation.Status.STOPPED) {
                    timeline.play();
                }
                event.consume();
            }
        };
        if (scrollPane.getContent().getParent() != null) {
            scrollPane.getContent().getParent().addEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
            scrollPane.getContent().getParent().addEventHandler(ScrollEvent.ANY, scrollHandler);
        }
        scrollPane.getContent().parentProperty().addListener((o,oldVal, newVal)->{
            if (oldVal != null) {
                oldVal.removeEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
                oldVal.removeEventHandler(ScrollEvent.ANY, scrollHandler);
            }
            if (newVal != null) {
                newVal.addEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
                newVal.addEventHandler(ScrollEvent.ANY, scrollHandler);
            }
        });
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(3), (event) -> {
            for (int i = 0; i < derivatives.length; i++) {
                derivatives[i] *= frictions[i];
            }
            for (int i = 1; i < derivatives.length; i++) {
                derivatives[i] += derivatives[i - 1];
            }
            double dy = derivatives[derivatives.length - 1];
            double size = sizeFunc.apply(scrollPane.getContent().getLayoutBounds());
            scrollDriection.set(Math.min(Math.max(scrollDriection.get() + dy / size, 0), 1));
            if (Math.abs(dy) < 0.001) {
                timeline.stop();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }
    public static void scrollToUnder(ScrollPane pane) {
        new Thread(() -> {
            double nowValue = pane.getVvalue();
            double value = 1D;
            for (double percent : percents){
                Platform.runLater(() -> {
                    if (nowValue < value){
                        pane.setVvalue(nowValue + (value - nowValue) * percent);
                    }
                    else if (nowValue > value) {
                        pane.setVvalue(nowValue - (nowValue - value) * percent);
                    }
                });
                Sleeper.sleep(10);
            }
            Platform.runLater(() -> pane.setVvalue(value));
        }).start();
    }

    public static void smoothScrolling(ScrollPane scrollPane, double speed) {
        customScrolling(scrollPane, scrollPane.vvalueProperty(), Bounds::getHeight, speed);
        customScrolling(scrollPane, scrollPane.hvalueProperty(), Bounds::getWidth, speed);
    }
    public static void smoothScrollBarToValue(ProgressBar bar, double value){
        if (barMap.containsKey(bar)) barMap.get(bar).setTarget(value);
        else {
            BarUpdateThread t = new BarUpdateThread(bar, value);
            barMap.put(bar, t);
            barMap.get(bar).setTarget(value);
            barMap.get(bar).start();
        }
    }
    public static class BarUpdateThread extends Thread {
        ProgressBar bar;
        double target;
        public BarUpdateThread(ProgressBar bar, double target) {
            this.bar = bar;
            this.target = target;
        }
        public void setTarget(double target) {
            this.target = target;
        }
        public void run() {
            while (true) {
                double nowValue = bar.getProgress();
                if (nowValue == target) continue;
                for (double percent : percents) {
                    if (nowValue < target) {
                        Platform.runLater(() -> bar.setProgress(nowValue + (target - nowValue) * percent));
                    } else {
                        Platform.runLater(() -> bar.setProgress(nowValue - (nowValue - target) * percent));
                    }
                    Sleeper.sleep(10);
                }
            }
        }
    }
}
