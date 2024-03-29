package com.mcreater.amcl.util;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.skins.JFXTextFieldSkin;
import com.mcreater.amcl.pages.interfaces.AnimationPage;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import static com.mcreater.amcl.Launcher.height;
import static com.mcreater.amcl.Launcher.width;

public class FXUtils {
    public static class WindowMovement {
        double x1;
        double y1;
        double x_stage;
        double y_stage;

        public static WindowMovement getInstance() {
            return new WindowMovement();
        }

        private WindowMovement() {
        }

        public <V extends Region, K extends Stage> void windowMove(V listenedObject, K stage) {
            Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
            listenedObject.setOnMouseDragged(event -> {
                double x = this.x_stage + event.getScreenX() - this.x1;
                double y = this.y_stage + event.getScreenY() - this.y1;
                if (x >= 0 && x <= scrSize.getWidth() - width) stage.setX(x);
                if (y >= 0 && y <= scrSize.getHeight() - height) stage.setY(y);
            });
            listenedObject.setOnDragEntered(null);
            listenedObject.setOnMousePressed(event -> {
                this.x1 = event.getScreenX();
                this.y1 = event.getScreenY();
                this.x_stage = stage.getX();
                this.y_stage = stage.getY();
            });
        }
    }

    public static class ImageConverter {
        public static WritableImage convertToWritableImage(Image image) {
            return new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
        }
    }

    public static class ControlSize {
        public static void set(Region n, double width, double height) {
            n.setMinSize(width, height);
            n.setMaxSize(width, height);
            n.setPrefSize(width, height);
        }

        public static void setAll(double width, double height, Region... n) {
            Arrays.stream(n).forEach(region -> set(region, width, height));
        }

        public static void setWidth(Region n, double width) {
            n.setMaxWidth(width);
            n.setMinWidth(width);
            n.setPrefWidth(width);
        }

        public static void setHeight(Region n, double height) {
            n.setMaxHeight(height);
            n.setMinHeight(height);
            n.setPrefHeight(height);
        }

        public static SplitPane setSplit(SplitPane s, double width) {
            s.setMaxWidth(width);
            s.setMinWidth(width);
            s.setPrefWidth(width);
            return s;
        }
    }

    public static Border generateBorder(Paint topStroke, Paint rightStroke, Paint bottomStroke, Paint leftStroke, BorderStrokeStyle topStyle, BorderStrokeStyle rightStyle, BorderStrokeStyle bottomStyle, BorderStrokeStyle leftStyle, CornerRadii radii, BorderWidths widths, Insets insets) {
        return new Border(new BorderStroke(topStroke, rightStroke, bottomStroke, leftStroke, topStyle, rightStyle, bottomStyle, leftStyle, radii, widths, insets));
    }

    public static Border generateBorder(Paint color, BorderStrokeStyle style, boolean top, boolean right, boolean bottom, boolean left, int width) {
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
        } catch (Exception ignored) {
        }
    }

    public static class Platform {
        public static void runLater(Runnable runnable) {
            javafx.application.Platform.runLater(() -> {
                while (true) {
                    try {
                        runnable.run();
                        break;
                    } catch (Exception ignored) {
                    }
                }
            });
        }
        public static void runUntil(Runnable runnable) {
            CountDownLatch latch = new CountDownLatch(1);
            javafx.application.Platform.runLater(() -> {
                while (true) {
                    try {
                        runnable.run();
                        latch.countDown();
                        break;
                    } catch (Exception ignored) {
                    }
                }
            });
            try {
                latch.await();
            }
            catch (Exception ignored) {}
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
            } else {
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
                } else {
                    num = endValue - (startValue - endValue) * per;
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

        public static Timeline generateNodeInAnimation(Node node) {
            Timeline timeline = new Timeline(
                    new KeyFrame(
                            Duration.millis(100),
                            new KeyValue(
                                    node.opacityProperty(),
                                    0,
                                    Interpolator.EASE_BOTH
                            ),
                            new KeyValue(
                                    node.scaleXProperty(),
                                    0.8,
                                    Interpolator.EASE_BOTH
                            ),
                            new KeyValue(
                                    node.scaleYProperty(),
                                    0.8,
                                    Interpolator.EASE_BOTH
                            )
                    ),
                    new KeyFrame(
                            Duration.seconds(2),
                            new KeyValue(
                                    node.opacityProperty(),
                                    1,
                                    Interpolator.EASE_BOTH
                            )
                    ),
                    new KeyFrame(
                            Duration.seconds(2.5),
                            new KeyValue(
                                    node.scaleXProperty(),
                                    1,
                                    Interpolator.EASE_BOTH
                            ),
                            new KeyValue(
                                    node.scaleYProperty(),
                                    1,
                                    Interpolator.EASE_BOTH
                            )
                    )
            );
            timeline.setCycleCount(1);
            timeline.setAutoReverse(false);
            return timeline;
        }

        public static Timeline generateNodeOutAnimation(Node node) {
            Timeline timeline = new Timeline(
                    new KeyFrame(
                            Duration.millis(50),
                            new KeyValue(
                                    node.opacityProperty(),
                                    1,
                                    Interpolator.EASE_BOTH
                            ),
                            new KeyValue(
                                    node.scaleXProperty(),
                                    1,
                                    Interpolator.EASE_BOTH
                            ),
                            new KeyValue(
                                    node.scaleYProperty(),
                                    1,
                                    Interpolator.EASE_BOTH
                            )
                    ),
                    new KeyFrame(
                            Duration.seconds(1),
                            new KeyValue(
                                    node.opacityProperty(),
                                    0,
                                    Interpolator.EASE_BOTH
                            )
                    ),
                    new KeyFrame(
                            Duration.seconds(0.5),
                            new KeyValue(
                                    node.scaleXProperty(),
                                    0.8,
                                    Interpolator.EASE_BOTH
                            ),
                            new KeyValue(
                                    node.scaleYProperty(),
                                    0.8,
                                    Interpolator.EASE_BOTH
                            )
                    )
            );
            timeline.setCycleCount(1);
            timeline.setAutoReverse(false);
            return timeline;
        }
    }

    public static class ColorUtil {
        public static Color transparent(Color src, double op) {
            return new Color(
                    src.getRed(),
                    src.getGreen(),
                    src.getBlue(),
                    op
            );
        }

        public static Color reverse(Color src) {
            return new Color(
                    1D - src.getRed(),
                    1D - src.getGreen(),
                    1D - src.getBlue(),
                    src.getOpacity()
            );
        }
    }

    public static void disableNodeKeyboard(ButtonBase buttonBase) {
        buttonBase.addEventHandler(KeyEvent.KEY_PRESSED, Event::consume);
        buttonBase.addEventHandler(KeyEvent.KEY_RELEASED, Event::consume);
        buttonBase.addEventHandler(KeyEvent.KEY_TYPED, Event::consume);

        buttonBase.addEventFilter(KeyEvent.KEY_PRESSED, Event::consume);
        buttonBase.addEventFilter(KeyEvent.KEY_RELEASED, Event::consume);
        buttonBase.addEventFilter(KeyEvent.KEY_TYPED, Event::consume);
    }

    public static void disableNodeKeyboard(Pane pane) {
        ThemeManager.GetAllNodes(pane).forEach(
                node -> {
                    if (node instanceof ButtonBase) disableNodeKeyboard((ButtonBase) node);
                }
        );
    }
}
