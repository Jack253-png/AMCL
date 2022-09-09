package com.mcreater.amcl.util;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.skins.JFXTextFieldSkin;
import javafx.geometry.Insets;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

import java.lang.reflect.Field;

public class FXUtils {
    public static class ImageConverter {
        public static WritableImage convertToWritableImage(Image image){
            return new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
        }
    }
    public static class ControlSize {
        public static void set(Region n, double width, double height){
            n.setMinSize(width, height);
            n.setMaxSize(width, height);
        }
        public static void set(WebView n, double width, double height){
            n.setMinSize(width, height);
            n.setMaxSize(width, height);
        }
        public static void setAll(double width, double height, Region... n){
            for (Region n1 : n){
                n1.setMinSize(width, height);
                n1.setMaxSize(width, height);
            }
        }
        public static void setWidth(Region n, double width){
            n.setMaxWidth(width);
            n.setMinWidth(width);
        }
        public static void setHeight(Region n, double height){
            n.setMaxHeight(height);
            n.setMinHeight(height);
        }
        public static void setHeight(WebView v, double height){
            v.setMaxHeight(height);
            v.setMinHeight(height);
        }
        public static SplitPane setSplit(SplitPane s, double width){
            s.setMaxWidth(width);
            s.setMinWidth(width);
            return s;
        }
    }
    public static Border generateBorder(Paint topStroke, Paint rightStroke, Paint bottomStroke, Paint leftStroke, BorderStrokeStyle topStyle, BorderStrokeStyle rightStyle, BorderStrokeStyle bottomStyle, BorderStrokeStyle leftStyle, CornerRadii radii, BorderWidths widths, Insets insets){
        return new Border(new BorderStroke(topStroke,rightStroke, bottomStroke,leftStroke, topStyle,rightStyle, bottomStyle, leftStyle, radii, widths, insets));
    }
    public static Border generateBorder(Paint color, BorderStrokeStyle style, boolean top, boolean right, boolean bottom, boolean left, int width){
        return generateBorder(top ? color : null,
                              right ? color : null,
                              bottom ? color : null,
                              left ? color : null,
                              top ? style : null,
                              right ? style : null,
                              bottom ? style : null,
                              left ? style : null,
                              null, BorderWidths.DEFAULT, new Insets(width));
    }
    public static void fixJFXTextField(JFXTextField field) {
        try {
            JFXTextFieldSkin<?> sk = new JFXTextFieldSkin<>(field);
            field.setSkin(sk);
            Field f = JFXTextFieldSkin.class.getDeclaredField("textNode");
            f.setAccessible(true);
            f.set(sk, new Text());
        }
        catch (Exception ignored){}
    }
}
