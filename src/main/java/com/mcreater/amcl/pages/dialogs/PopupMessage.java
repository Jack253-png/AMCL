package com.mcreater.amcl.pages.dialogs;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.pages.MainPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

public class PopupMessage {
    static Pane screen;
    static int contentHeight = 450;
    static Vector<Text> mess = new Vector<>();
    static Vector<Double> useableHeights = new Vector<>();
    static Vector<Double> usedHeights = new Vector<>();
    public static void init(final Pane screen){
        PopupMessage.screen = screen;
    }
    public static void createMessage(String text){
        final Path path = new Path();
        double tempHeight = contentHeight;
        if (useableHeights.size() > 0){
            tempHeight = useableHeights.get(0);
            useableHeights.remove(0);
        }
        else{
            PopupMessage.contentHeight -= 20;
            if (PopupMessage.contentHeight < 50){
                PopupMessage.contentHeight = 450;
            }
        }
        if (usedHeights.contains(tempHeight)){
            MainPage.logger.warn(String.format("failed to show popup message : %s", text));
            return;
        }
        MessageLabel circle = new MessageLabel(tempHeight);
        circle.setFont(Fonts.t_f);

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

        mess.add(circle);
        usedHeights.add(tempHeight);
        path.getElements().add(new MoveTo(-245 + (double) strWidth / 2 / 100 * 101, tempHeight));
        path.getElements().add(new LineTo(25 + (double) strWidth / 2 / 100 * 101, tempHeight));
        Launcher.p.getChildren().add(circle);
        final PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(4.0));
        pathTransition.setDelay(Duration.seconds(.95));
        pathTransition.setPath(path);
        pathTransition.setNode(circle);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(2);
        pathTransition.setAutoReverse(true);
        pathTransition.play();
        pathTransition.setOnFinished(event -> {
            Launcher.p.getChildren().removeAll(circle);
            mess.remove(circle);
            if (circle.Height != PopupMessage.contentHeight) {
                useableHeights.add(circle.Height);
                usedHeights.remove(circle.Height);
            }
            PopupMessage.contentHeight += 20;
            if (PopupMessage.contentHeight > 450) {
                PopupMessage.contentHeight = 50;
            }
        });
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> circle.setText(text));
        }).start();
    }
    public static class MessageLabel extends Text{
        public double Height;

        public MessageLabel(String text) {
            super(text);
        }
        public MessageLabel(String text, double height){
            super(text);
            this.Height = height;
        }
        public MessageLabel(double height){
            this.Height = height;
        }
        public String toString(){
            return String.valueOf(this.Height);
        }
    }
}
