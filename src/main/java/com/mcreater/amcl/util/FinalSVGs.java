package com.mcreater.amcl.util;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;

import java.util.Stack;

public abstract class FinalSVGs {
    public static StackPane InPage;
    public static StackPane OutPage;
    public static StackPane addNode;
    public static StackPane refreshNode;
    private static final double t_size = 45;
    static {
        InPage = new StackPane();
        Node in = SVG.arrowRight(Bindings.createObjectBinding(() -> Paint.valueOf("#000000")), t_size / 3 * 2, t_size / 3 * 2);
        InPage.getChildren().setAll(in);

        OutPage = new StackPane();
        Node out = SVG.createSVGPath("", Bindings.createObjectBinding(() -> Paint.valueOf("#000000")), t_size / 3 * 2, t_size / 3 * 2);
        OutPage.getChildren().setAll(out);

        addNode = new StackPane();
        Node add = SVG.plus(Bindings.createObjectBinding(() -> Paint.valueOf("#000000")), t_size, t_size);
        addNode.getChildren().setAll(add);

        refreshNode = new StackPane();
        Node refresh = SVG.refresh(Bindings.createObjectBinding(() -> Paint.valueOf("#000000")), t_size, t_size);
        refreshNode.getChildren().setAll(refresh);
    }
}
