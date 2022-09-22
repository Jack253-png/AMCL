package com.mcreater.amcl.util;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.skins.JFXTextFieldSkin;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.Arrays;

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

    public static class Platform {
        public static void runLater(Runnable runnable) {
//            try {
//                Field f = Toolkit.class.getDeclaredField("fxUserThread");
//                f.setAccessible(true);
//                Thread CURRENT_THREAD = (Thread) f.get(Toolkit.getToolkit());
//                f.set(Toolkit.getToolkit(), Thread.currentThread());
//
//                runnable.run();
//
//                f.set(Toolkit.getToolkit(), CURRENT_THREAD);
//            }
//            catch (Throwable e) {
//                javafx.application.Platform.runLater(runnable);
//            }
            javafx.application.Platform.runLater(runnable);
        }
    }
    public static class ImagePreProcesser {
        @SafeVarargs
        public static void process(WritableImage image, SimpleFunctions.Arg2FuncNoReturn<ImageView, WritableImage>... func) {
            ImageView imageView = new ImageView(image);
//            imageView.setFitWidth(image.getWidth() / 7 * 6);
//            imageView.setFitHeight(image.getHeight() / 12 * 11);
            imageView.setFitWidth(image.getWidth());
            imageView.setFitHeight(image.getHeight());
//            System.out.printf("%f, %f\n", image.getWidth(), image.getHeight());

            for (SimpleFunctions.Arg2FuncNoReturn<ImageView, WritableImage> processor : func) {
                processor.run(imageView, image);
            }

            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            image = imageView.snapshot(parameters, image);
//            System.out.printf("%f, %f\n", image.getWidth(), image.getHeight());
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

        public static void copyImage(WritableImage operateImg, Image src, int x, int y, int w, int h) {
            if (operateImg != null && src != null) {
                if (operateImg.getWidth() >= x + w || operateImg.getHeight() >= y + h) {
                    for (int natX = x; natX < x + w; natX++) {
                        for (int natY = y; natY < y + h; natY++) {
                            operateImg.getPixelWriter().setColor(natX, natY, src.getPixelReader().getColor(natX - x, natY - y));
                        }
                    }
                }
            }
        }

        public static BufferedImage toSwingImage(Image image) {
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            Color[] pixels = new Color[width * height];

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Color c = image.getPixelReader().getColor(x, y);
                    pixels[y * width + x] = c;
                }
            }

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // There may be better ways to do this
                    // You'll need to make sure your image's format is correct here
                    Color pixel = pixels[y * width + x];
                    int r = (int) (pixel.getRed() * 255);
                    int g = (int) (pixel.getGreen() * 255);
                    int b = (int) (pixel.getBlue() * 255);
                    bufferedImage.getRaster().setPixel(x, y, new int[]{r, g, b});
                }
            }
            return bufferedImage;
        }

        public static WritableImage fromSwingImage(BufferedImage image) {
            int w = image.getWidth();
            int h = image.getHeight();
            WritableImage result = new WritableImage(w, h);

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    java.awt.Color c = new java.awt.Color(image.getRGB(x, y));
                    result.getPixelWriter().setColor(x, y, new Color((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255, 1));
                }
            }
            return result;
        }
        public static class SwingImageGaussian {
            public static void generate(BufferedImage src, int radius) {
                System.out.println(new java.awt.Color(src.getRGB(0, 0)));
                int height = src.getHeight();
                int width = src.getWidth();
                int[][] martrix = new int[3][3];
                int[] values = new int[9];
                for (int i = 0; i < width; i++)
                    for (int j = 0; j < height; j++) {
                        readPixel(src, i, j, values);
                        fillMatrix(martrix, values);
                        src.setRGB(i, j, avgMatrix(martrix));
                    }
                System.out.println(new java.awt.Color(src.getRGB(0, 0)));
            }

            private static void readPixel(BufferedImage img, int x, int y, int[] pixels) {
                int xStart = x - 1;
                int yStart = y - 1;
                int current = 0;
                for (int i = xStart; i < 3 + xStart; i++)
                    for (int j = yStart; j < 3 + yStart; j++) {
                        int tx = i;
                        if (tx < 0) {
                            tx = -tx;

                        } else if (tx >= img.getWidth()) {
                            tx = x;
                        }
                        int ty = j;
                        if (ty < 0) {
                            ty = -ty;
                        } else if (ty >= img.getHeight()) {
                            ty = y;
                        }
                        pixels[current++] = img.getRGB(tx, ty);

                    }
            }

            private static void fillMatrix(int[][] matrix, int[] values) {
                int filled = 0;
                for (int[] x : matrix) {
                    for (int j = 0; j < x.length; j++) {
                        x[j] = values[filled++];
                    }
                }
            }

            private static int avgMatrix(int[][] matrix) {
                int r = 0;
                int g = 0;
                int b = 0;
                for (int[] x : matrix) {
                    for (int j = 0; j < x.length; j++) {
                        if (j == 1) {
                            continue;
                        }
                        java.awt.Color c = new java.awt.Color(x[j]);
                        r += c.getRed();
                        g += c.getGreen();
                        b += c.getBlue();
                    }
                }
                r = (int) (r / 0.75);
                g = (int) (g / 0.75);
                b = (int) (b / 0.75);
                return new java.awt.Color(r / 8 , g / 8, b / 8).getRGB();

            }
        }
    }
}
