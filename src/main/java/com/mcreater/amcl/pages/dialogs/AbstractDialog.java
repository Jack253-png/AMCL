package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.controls.JFXDialog;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.builders.ThreadBuilder;
import com.mcreater.amcl.util.concurrent.Sleeper;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.lang.reflect.Field;

import static com.mcreater.amcl.Launcher.height;
import static com.mcreater.amcl.Launcher.width;
import static com.mcreater.amcl.util.FXUtils.ColorUtil.transparent;

public abstract class AbstractDialog extends JFXDialog {
    public static final ObservableList<AbstractDialog> dialogs = FXCollections.observableArrayList();
    public static final SimpleDoubleProperty dialogRadius = new SimpleDoubleProperty(30);
    public static final double blurRadius = 8;
    public static final SimpleDoubleProperty nowRadius = new SimpleDoubleProperty(0);
    public static final SimpleDoubleProperty exceptedRadius = new SimpleDoubleProperty(0);
    public static final StackPane wrapper = new StackPane();
    public final SimpleDoubleProperty dialogNowRadius = new SimpleDoubleProperty(0);
    public final SimpleDoubleProperty dialogExceptedRadius = new SimpleDoubleProperty(0);
    private Runnable onShow = () -> {
    };
    private Runnable onClose = () -> {
    };

    static {
        FXUtils.disableNodeKeyboard(wrapper);
        FXUtils.ControlSize.set(wrapper, 0, 0);
        dialogs.addListener((ListChangeListener<AbstractDialog>) c -> {
            exceptedRadius.set(c.getList().isEmpty() ? 0 : blurRadius);
            FXUtils.ControlSize.set(
                    wrapper,
                    c.getList().isEmpty() ? 0 : width,
                    c.getList().isEmpty() ? 0 : height
            );
        });
        nowRadius.addListener((observable, oldValue, newValue) -> Launcher.wrapper.setEffect(new GaussianBlur(newValue.intValue())));
        ThreadBuilder.createBuilder()
                .runTarget(() -> {
                    while (true) {
                        if (nowRadius.getValue().intValue() != exceptedRadius.getValue().intValue()) {
                            double now = nowRadius.get();
                            nowRadius.set(now < exceptedRadius.get() ? now + blurRadius / 40 : now - blurRadius / 40);
                        }
                        try {
                            dialogs.forEach(abstractDialog -> {
                                if (abstractDialog.dialogNowRadius.getValue().intValue() != abstractDialog.dialogExceptedRadius.getValue().intValue()) {
                                    double now2 = abstractDialog.dialogNowRadius.get();
                                    abstractDialog.dialogNowRadius.set(now2 < abstractDialog.dialogExceptedRadius.get() ? now2 + blurRadius / 40 : now2 - blurRadius / 40);
                                }
                            });
                        } catch (Exception ignored) {

                        }
                        Sleeper.sleep(5);
                    }
                })
                .name("Dialog blur calc thread")
                .buildAndRun();
    }

    public void show() {
        onShow.run();
        super.show();
        runInAnimation(() -> {
        });
    }

    public void close() {
        if (dialogs.size() <= 1) exceptedRadius.set(0);
        runOutAnimation(() -> {
            super.close();
            onClose.run();
        });
    }

    public AbstractDialog() {
        onShow = () -> {
            dialogs.add(this);
            if (getContent() != null) {
                getContent().layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
                    try {
                        updateBounds(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                ThemeManager.addLis((observable, oldValue, newValue) -> {
                    try {
                        getWrapper(this).setBackground(new Background(
                                new BackgroundFill(
                                        Color.TRANSPARENT,
                                        CornerRadii.EMPTY,
                                        Insets.EMPTY
                                )
                        ));
                        getContent().setBackground(new Background(
                                new BackgroundFill(
                                        transparent(newValue, 0.8),
                                        CornerRadii.EMPTY,
                                        Insets.EMPTY
                                )
                        ));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                dialogNowRadius.addListener((observable, oldValue, newValue) -> getContent().setEffect(new GaussianBlur(newValue.intValue())));
            }
        };
        onClose = () -> dialogs.remove(this);
        setTransitionType(DialogTransition.NONE);
        setDialogContainer(wrapper);
        setOverlayClose(false);

        dialogs.addListener((ListChangeListener<AbstractDialog>) c -> onDialogListChange());
        setBackground(new Background(
                new BackgroundFill(
                        Color.TRANSPARENT,
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                )
        ));
        FXUtils.disableNodeKeyboard(this);
    }

    private void runInAnimation(Runnable runnable) {
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(
                                this.opacityProperty(),
                                0,
                                Interpolator.EASE_BOTH
                        )
                ),
                new KeyFrame(
                        Duration.millis(300),
                        new KeyValue(
                                this.opacityProperty(),
                                1,
                                Interpolator.EASE_BOTH
                        )
                )
        );
        timeline.setAutoReverse(false);
        timeline.setCycleCount(1);
        timeline.setOnFinished(event -> runnable.run());
        timeline.playFromStart();
    }

    private void runOutAnimation(Runnable runnable) {
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(
                                this.opacityProperty(),
                                1,
                                Interpolator.EASE_BOTH
                        )
                ),
                new KeyFrame(
                        Duration.millis(300),
                        new KeyValue(
                                this.opacityProperty(),
                                0,
                                Interpolator.EASE_BOTH
                        )
                )
        );
        timeline.setAutoReverse(false);
        timeline.setCycleCount(1);
        timeline.setOnFinished(event -> runnable.run());
        timeline.playFromStart();
    }

    private static StackPane getWrapper(JFXDialog content) throws Exception {
        Field field = JFXDialog.class.getDeclaredField("contentHolder");
        field.setAccessible(true);
        return (StackPane) field.get(content);
    }

    private static void updateBounds(JFXDialog item) throws Exception {
        Bounds bound = item.getContent().getLayoutBounds();
        if (bound.getWidth() > 0 && bound.getHeight() > 0) {
            getWrapper(item).setClip(FXUtils.generateRect(
                    bound.getWidth(),
                    bound.getHeight(),
                    dialogRadius.get()
            ));
            item.getContent().setClip(FXUtils.generateRect(
                    bound.getWidth(),
                    bound.getHeight(),
                    dialogRadius.get()
            ));
        }
    }

    private void onDialogListChange() {
        dialogExceptedRadius.set(dialogs.indexOf(this) + 1 == dialogs.size() ? 0 : blurRadius);
    }

    public void Create() {
        FXUtils.Platform.runLater(this::show);
    }

    public static Label setFont(Label l, Font font) {
        l.setFont(font);
        return l;
    }

    public static <T extends Label> T setFont(Label l, Font font, Class<T> clazz) {
        l.setFont(font);
        return (T) l;
    }
}
