package com.mcreater.amcl.pages.dialogs.commons;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import static com.mcreater.amcl.util.FXUtils.ColorUtil.reverse;

public class PopupMessage {
    static int contentHeight = 450;
    static Vector<Control> mess = new Vector<>();
    static boolean hasMessages = false;

    public static final int MESSAGE_BASE_HEIGHT = 450;
    public static final int MESSAGE_MAX_NUM = 18;
    public static final int MESSAGE_HEIGHT = 20;
    public enum MessageTypes{
        LABEL,
        HYPERLINK,
        BUTTON
    }
    public static Labeled createMessage(String text, MessageTypes type, @Nullable EventHandler<Event> handler) {
        return createMessage(text, type, handler, Color.BLACK);
    }
    public static Labeled createMessage(String text, MessageTypes type, @Nullable EventHandler<Event> handler, Paint paint){
        Labeled l;
        switch (type){
            case HYPERLINK:
                Hyperlink link = new Hyperlink();
                link.setFont(Fonts.t_f);
                link.setTextFill(paint);
                if (handler != null) link.setOnAction(handler::handle);
                l = link;
                break;
            case BUTTON:
                JFXButton button = new JFXButton();
                button.setFont(Fonts.t_f);
                button.setTextFill(paint);
                if (handler != null) button.setOnAction(handler::handle);
                l = button;
                break;
            default:
            case LABEL:
                Label label = new Label();
                label.setFont(Fonts.t_f);
                label.setTextFill(paint);
                l = label;
                break;
        }
        Labeled finalL = l;
        Platform.runLater(() -> createMessageInternal(finalL, text));
        ThemeManager.loadNodeAnimations(l);
        return finalL;
    }
    private static Labeled createMessageInternal(Labeled circle, String text) {
        ThemeManager.addLis((observable, oldValue, newValue) -> circle.setTextFill(reverse(newValue)));
        final Path path = new Path();
        int strWidth = 0;
        try {
            Object o = Class.forName("sun.font.FontDesignMetrics").getDeclaredMethod("getMetrics", Font.class).invoke(null, Fonts.awt_t_f);
            strWidth = ((FontMetrics) o).stringWidth(text);
        }
        catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
               IllegalAccessException exception){
            exception.printStackTrace();
        }
        if (contentHeight == MESSAGE_BASE_HEIGHT - MESSAGE_MAX_NUM * MESSAGE_HEIGHT && hasMessages){
            return circle;
        }
        else{
            if (contentHeight == MESSAGE_BASE_HEIGHT - MESSAGE_MAX_NUM * MESSAGE_HEIGHT) contentHeight = MESSAGE_BASE_HEIGHT;
            contentHeight -= MESSAGE_HEIGHT;
        }
        hasMessages = true;
        mess.add(circle);
        path.getElements().add(new MoveTo(-245 + (double) strWidth / 2 / 100 * 101, contentHeight));
        path.getElements().add(new LineTo(25 + (double) strWidth / 2 / 100 * 101, contentHeight));
        Launcher.p.getChildren().add(circle);
        final PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(4.0));
        pathTransition.setDelay(Duration.seconds(0));
        pathTransition.setPath(path);
        pathTransition.setNode(circle);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(2);
        pathTransition.setAutoReverse(true);
        pathTransition.play();
        pathTransition.setOnFinished(event -> {
            Launcher.p.getChildren().removeAll(circle);
            mess.remove(circle);
            if (mess.size() == 0){
                hasMessages = false;
                contentHeight = MESSAGE_BASE_HEIGHT;
            }
        });
        new Thread(() -> {
            Point2D p;
            do{
                p = circle.localToScene(0, 0);
            }
            while (p.getY() == 0 && p.getX() == 0);
            Platform.runLater(() -> {
                circle.setText(text);
            });
        }).start();
        return circle;
    }
}
