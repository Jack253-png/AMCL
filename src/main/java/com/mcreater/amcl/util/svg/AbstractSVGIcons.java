package com.mcreater.amcl.util.svg;

import javafx.beans.binding.ObjectBinding;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

public abstract class AbstractSVGIcons {
    AbstractSVGIcons(){}
    public Node createSVGPath(String d, ObjectBinding<? extends Paint> fill, double width, double height){
        SVGPath path = new SVGPath();
        path.getStyleClass().add("svg");
        path.setContent(d);
        if (fill != null) {
            path.fillProperty().bind(fill);
        }

        if (!(width < 0.0D) && !(height < 0.0D)) {
            Group svg = new Group(path);
            double scale = Math.min(width / 24.0D, height / 24.0D);
            svg.setScaleX(scale);
            svg.setScaleY(scale);
            return svg;
        } else {
            StackPane pane = new StackPane(path);
            pane.setAlignment(Pos.CENTER);
            return pane;
        }
    }
    public abstract Node close(ObjectBinding<? extends Paint> fill, double width, double height);
    public abstract Node back(ObjectBinding<? extends Paint> fill, double width, double height);
    public abstract Node gear(ObjectBinding<? extends Paint> fill, double width, double height);
    public abstract Node plus(ObjectBinding<? extends Paint> fill, double width, double height);
    public abstract Node refresh(ObjectBinding<? extends Paint> fill, double width, double height);
    public abstract Node wrench(ObjectBinding<? extends Paint> fill, double width, double height);
    public abstract Node downloadOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    public abstract Node delete(ObjectBinding<? extends Paint> fill, double width, double height);
}
