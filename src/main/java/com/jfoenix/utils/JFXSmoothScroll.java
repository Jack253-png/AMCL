package com.jfoenix.utils;

import com.jfoenix.skins.JFXListViewSkin;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.css.CssMetaData;
import javafx.css.Selector;
import javafx.css.Styleable;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

import java.util.function.Function;

public class JFXSmoothScroll {
    private static ScrollBar getScrollbarComponent(ListView<?> control, Orientation orientation) {
        Node n = control.lookup(".scroll-bar");
        if (n instanceof ScrollBar) {
            final ScrollBar bar = (ScrollBar) n;
            if (bar.getOrientation().equals(orientation)) {
                return bar;
            }
        }
        return null;
    }

    public static void smoothScrollingListView(ListView<?> listView, double speed) {
        smoothScrollingListView(listView, speed, Orientation.VERTICAL, Bounds::getHeight);
    }

    public static void smoothHScrollingListView(ListView<?> listView, double speed) {
        smoothScrollingListView(listView, speed, Orientation.HORIZONTAL, Bounds::getHeight);
    }

    private  static void smoothScrollingListView(ListView<?> listView, double speed, Orientation orientation, Function<Bounds, Double> sizeFunc) {
        ScrollBar scrollBar = getScrollbarComponent(listView, orientation);
        if (scrollBar == null) {
            return;
        }
        scrollBar.setUnitIncrement(5);
        final double[] frictions = {0.99, 0.1, 0.05, 0.04, 0.03, 0.02, 0.01, 0.04, 0.01, 0.008, 0.008, 0.008, 0.008, 0.0006, 0.0005, 0.00003, 0.00001};
        final double[] pushes = {speed};
        final double[] derivatives = new double[frictions.length];
        final double[] lastVPos = {0};
        Timeline timeline = new Timeline();
        final EventHandler<MouseEvent> dragHandler = event -> timeline.stop();
        final EventHandler<ScrollEvent> scrollHandler = event -> {
            scrollBar.valueProperty().set(lastVPos[0]);
            if (event.getEventType() == ScrollEvent.SCROLL) {
                double direction = event.getDeltaY() > 0 ? -1 : 1;
                for (int i = 0; i < pushes.length; i++) {
                    derivatives[i] += direction * pushes[i];
                }
                if (timeline.getStatus() == Animation.Status.STOPPED) {
                    timeline.play();
                }

            }
            event.consume();
        };
        if (scrollBar.getParent() != null) {
            scrollBar.getParent().addEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
            scrollBar.getParent().addEventHandler(ScrollEvent.ANY, scrollHandler);
        }
        scrollBar.parentProperty().addListener((o,oldVal, newVal)->{
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
            double size = sizeFunc.apply(scrollBar.getLayoutBounds());
            scrollBar.valueProperty().set(Math.min(Math.max(scrollBar.getValue() + dy / size, 0), 1));
            lastVPos[0] = scrollBar.getValue();
            if (Math.abs(dy) < 1) {
                if (Math.abs(dy) < 0.001) {
                    timeline.stop();
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }
}
