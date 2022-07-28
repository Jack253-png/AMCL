package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.Sleeper;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

public class PopupMessage {
    static int contentHeight = 450;
    static Vector<Control> mess = new Vector<>();
    static boolean hasMessages = false;
    public enum MessageTypes{
        LABEL,
        HYPERLINK,
        BUTTON
    }
    public static Labeled createMessage(String text, MessageTypes type, @Nullable EventHandler<Event> handler){
        Labeled l;
        switch (type){
            case HYPERLINK:
                Hyperlink link = new Hyperlink();
                link.setFont(Fonts.t_f);
                if (handler != null) link.setOnAction(handler::handle);
                l = link;
                break;
            case BUTTON:
                JFXButton button = new JFXButton();
                button.setFont(Fonts.t_f);
                if (handler != null) button.setOnAction(handler::handle);
                l = button;
                break;
            default:
            case LABEL:
                Label circle = new Label();
                circle.setFont(Fonts.t_f);
                l = circle;
                break;
        }
        Labeled finalL = l;
        Platform.runLater(() -> createMessageInternal(finalL, text));
        ThemeManager.loadButtonAnimates(l);
        return finalL;
    }
    private static Labeled createMessageInternal(Labeled circle, String text){
        final Path path = new Path();
        int strWidth = 0;
        try {
            Font f = new Font(Fonts.t_f.getName(), Font.PLAIN, (int) Fonts.t_f.getSize());
            Object o = Class.forName("sun.font.FontDesignMetrics").getDeclaredMethod("getMetrics", Font.class).invoke(null, f);
            strWidth = ((FontMetrics) o).stringWidth(text);
        }
        catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
               IllegalAccessException exception){
            exception.printStackTrace();
        }
        if (contentHeight == 250 && hasMessages){
            return circle;
        }
        else{
            if (contentHeight == 250) contentHeight = 450;
            contentHeight -= 20;
        }
        circle.setTextFill(Color.TRANSPARENT);
        hasMessages = true;
        mess.add(circle);
        path.getElements().add(new MoveTo(-245 + (double) strWidth / 2 / 100 * 101, contentHeight));
        path.getElements().add(new LineTo(25 + (double) strWidth / 2 / 100 * 101, contentHeight));
        Launcher.p.getChildren().add(circle);
        final PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(4.0));
        pathTransition.setDelay(Duration.seconds(0.15));
        pathTransition.setPath(path);
        pathTransition.setNode(circle);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(2);
        pathTransition.setAutoReverse(true);
        pathTransition.play();
        circle.setText(text);
        pathTransition.setOnFinished(event -> {
            Launcher.p.getChildren().removeAll(circle);
            mess.remove(circle);
            if (mess.size() == 0){
                hasMessages = false;
                contentHeight = 450;
            }
        });
        new Thread(() -> {
            Point2D p;
            do{
                p = circle.localToScene(0, 0);
            }
            while (p.getY() == 0 && p.getX() == 0);
            Platform.runLater(() -> circle.setTextFill(Color.BLACK));
        }).start();
        return circle;
    }
}
