package com.jfoenix.utils;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

import java.util.function.Function;

public class JFXSmoothScroll {
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

    public static void smoothScrolling(ScrollPane scrollPane, double speed) {
        customScrolling(scrollPane, scrollPane.vvalueProperty(), Bounds::getHeight, speed);
        customScrolling(scrollPane, scrollPane.hvalueProperty(), Bounds::getWidth, speed);
    }
}
