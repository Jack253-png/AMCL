package com.mcreater.amcl.util;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.skins.JFXTextFieldSkin;
import com.mcreater.amcl.pages.interfaces.AnimationPage;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

public class FXUtils {
    public static class ImageConverter {
        public static WritableImage convertToWritableImage(Image image){
            return new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
        }
    }
    public static <C extends Node> C toNodeClass(Node obj, Class<C> cClass) {
        return (C) obj;
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

    public static class Platform {
        public static void runLater(Runnable runnable) {
            System.out.println("Platform delegating : " + runnable);
            javafx.application.Platform.runLater(() -> {
                while (true) {
                    try {
                        runnable.run();
                        break;
                    }
                    catch (Exception ignored){}
                }
            });
        }
    }
    public static class ImagePreProcesser {
        public static Logger logger = LogManager.getLogger(ImagePreProcesser.class);
        public static Color noTransparent(Color ori) {
            return new Color(
                    ori.getRed(),
                    ori.getGreen(),
                    ori.getBlue(),
                    1.0
            );
        }

        public static WritableImage getColorImage(Color color, int width, int height) {
            WritableImage result = new WritableImage(width, height);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    result.getPixelWriter().setColor(x, y, color);
                }
            }
            return result;
        }
        @SafeVarargs
        public static void process(WritableImage image, SimpleFunctions.Arg2FuncNoReturn<ImageView, WritableImage>... func) {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(image.getWidth());
            imageView.setFitHeight(image.getHeight());

            for (SimpleFunctions.Arg2FuncNoReturn<ImageView, WritableImage> processor : func) {
                processor.run(imageView, image);
            }

            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            image = imageView.snapshot(parameters, image);
        }
        public static WritableImage cutImage(Image src, int x, int y, int w, int h) {
            WritableImage result = new WritableImage(w, h);
            if (src.getWidth() >= x + w || src.getHeight() >= y + h) {
                for (int natX = x; natX < x + w; natX++) {
                    for (int natY = y; natY < y + h; natY++) {
                        result.getPixelWriter().setColor(natX - x, natY - y, src.getPixelReader().getColor(natX, natY));
                    }
                }
            }
            else {
                return null;
            }

            return result;
        }
    }
    public static boolean gemotryInned(Point2D target, List<AnimationPage.NodeInfo> nodes) {
        for (AnimationPage.NodeInfo control : nodes) {
            if (control.size.contains(target)) {
                return true;
            }
        }
        return false;
    }
    public static Rectangle generateRect(double width, double height, double radius) {
        Rectangle rect = new Rectangle();
        rect.setWidth(width);
        rect.setHeight(height);
        rect.setArcWidth(radius);
        rect.setArcHeight(radius);
        return rect;
    }

    public static class AnimationUtils {
        public static <T> Timeline genSingleCycleAnimation(WritableValue<T> target, T value1, T value2, double startupDur, double runDur) {
            Timeline out1 = new Timeline();
            out1.setCycleCount(1);
            out1.getKeyFrames().clear();
            out1.getKeyFrames().add(new KeyFrame(Duration.millis(startupDur), new KeyValue(target, value1)));
            out1.getKeyFrames().add(new KeyFrame(new Duration(runDur), new KeyValue(target, value2)));
            return out1;
        }
        public static <T> void runSingleCycleAnimation(WritableValue<T> target, T value1, T value2, double startupDur, double runDur, @NotNull EventHandler<ActionEvent> finishedHandler) {
            Timeline out1 = FXUtils.AnimationUtils.genSingleCycleAnimation(target, value1, value2, startupDur, runDur);
            out1.setOnFinished(finishedHandler);
            out1.play();
        }

        public static Vector<KeyFrame> genDoublePercentKeyframes(double startValue, double endValue, double[] percents, DoubleProperty property, double duration) {
            boolean finalU = false;
            if (startValue < 0) startValue = 0;
            if (endValue < 0) {
                endValue = 0;
                finalU = true;
            }

            Vector<KeyFrame> frames = new Vector<>();
            if (startValue == endValue) return frames;
            for (double per : percents) {
                double num;
                if (startValue < endValue) {
                    num = startValue + (endValue - startValue) * per;
                }
                else {
                    num = endValue - (startValue - endValue ) * per;
                }
                frames.add(new KeyFrame(
                        new Duration(duration),
                        new KeyValue(property, num, Interpolator.EASE_BOTH)
                ));
                System.out.println(num);
            }
            if (finalU) {
                frames.add(new KeyFrame(
                        new Duration(duration),
                        new KeyValue(property, -1, Interpolator.DISCRETE)
                ));
            }

            return frames;
        }
    }
}
