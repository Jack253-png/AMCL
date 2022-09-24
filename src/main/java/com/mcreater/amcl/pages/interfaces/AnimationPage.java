package com.mcreater.amcl.pages.interfaces;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;
import java.util.Vector;

public interface AnimationPage {
    class NodeInfo {
        public final BoundingBox size;
        public NodeInfo(double x, double y, double w, double h){
            this.size = new BoundingBox(x, y, w, h);
        }
    }
    Timeline in = new Timeline();
    Timeline out = new Timeline();
    long delay = 300;
    default void set(DoubleProperty property){
        in.setCycleCount(1);
        in.getKeyFrames().clear();
        in.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(property, 0)));
        in.getKeyFrames().add(new KeyFrame(new Duration(delay), new KeyValue(property, 1)));

        out.setCycleCount(1);
        out.getKeyFrames().clear();
        out.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(property, 1)));
        out.getKeyFrames().add(new KeyFrame(new Duration(delay), new KeyValue(property, 0)));
    }
}
