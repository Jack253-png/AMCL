package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.transitions.CachedTransition;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import javafx.animation.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import static com.mcreater.amcl.Launcher.*;

public abstract class AbstractDialog extends JFXAlert<String> {
    public static final ObservableList<AbstractDialog> dialogs = FXCollections.observableArrayList();
    public static final SimpleDoubleProperty dialogRadius = new SimpleDoubleProperty(30);
    public static final double blurRadius = 8;
    public static final SimpleDoubleProperty nowRadius = new SimpleDoubleProperty(0);
    public static final SimpleDoubleProperty exceptedRadius = new SimpleDoubleProperty(0);

    public final SimpleDoubleProperty dialogNowRadius = new SimpleDoubleProperty(0);

    public final SimpleDoubleProperty dialogExceptedRadius = new SimpleDoubleProperty(0);
    private final SimpleDoubleProperty dialogWidth = new SimpleDoubleProperty(-1);
    private final SimpleDoubleProperty dialogHeight = new SimpleDoubleProperty(-1);

    static {
        dialogs.addListener((ListChangeListener<AbstractDialog>) c -> exceptedRadius.set(c.getList().size() == 0 ? 0 : blurRadius));
        nowRadius.addListener((observable, oldValue, newValue) -> wrapper.setEffect(new GaussianBlur(newValue.intValue())));
        new Thread("Widget blur calc thread") {
            public void run() {
                while (true) {
                    if (nowRadius.getValue().intValue() != exceptedRadius.getValue().intValue()) {
                        double now = nowRadius.get();
                        nowRadius.set(now < exceptedRadius.get() ? now + blurRadius / 20 : now - blurRadius / 20);
                    }
                    try {
                        dialogs.forEach(abstractDialog -> {
                            if (abstractDialog.dialogNowRadius.getValue().intValue() != abstractDialog.dialogExceptedRadius.getValue().intValue()) {
                                double now2 = abstractDialog.dialogNowRadius.get();
                                abstractDialog.dialogNowRadius.set(now2 < abstractDialog.dialogExceptedRadius.get() ? now2 + blurRadius / 20 : now2 - blurRadius / 20);
                            }
                        });
                    }
                    catch (Exception ignored) {

                    }
                }
            }
        }.start();
    }

    public double getDialogWidth() {
        return dialogWidth.get();
    }
    public double getDialogHeight() {
        return dialogHeight.get();
    }

    boolean cliped;
    public AbstractDialog(Stage stage) {
        super(stage);
//        getDialogPane().setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setAnimation(new JFXAlertAnimation() {
            public void initAnimation(Node contentContainer, Node overlay) {
                overlay.setOpacity(0);
                contentContainer.setScaleX(.80);
                contentContainer.setScaleY(.80);
            }
            public Animation createShowingAnimation(Node contentContainer, Node overlay) {
                return new CachedTransition(contentContainer, new Timeline(
                        new KeyFrame(Duration.millis(1000),
                                new KeyValue(contentContainer.scaleXProperty(), 1, Interpolator.EASE_OUT),
                                new KeyValue(contentContainer.scaleYProperty(), 1, Interpolator.EASE_OUT),
                                new KeyValue(overlay.opacityProperty(), 1, Interpolator.EASE_BOTH)
                        ))) {
                    {
                        setCycleDuration(Duration.millis(400));
                        setDelay(Duration.seconds(0));
                    }
                };
            }
            public Animation createHidingAnimation(Node contentContainer, Node overlay) {
                return new CachedTransition(contentContainer, new Timeline(
                        new KeyFrame(Duration.millis(1000),
                                new KeyValue(overlay.opacityProperty(), 0, Interpolator.EASE_BOTH)
                        ))) {
                    {
                        setCycleDuration(Duration.millis(400));
                        setDelay(Duration.seconds(0));
                    }
                };
            }
        });
        this.initModality(Modality.APPLICATION_MODAL);
        this.setOverlayClose(false);
        Rectangle r = FXUtils.generateRect(width, height, 0);
        r.arcWidthProperty().bind(Launcher.radius);
        r.arcHeightProperty().bind(Launcher.radius);
        getDialogPane().setClip(r);
        setOnShowing(event -> dialogs.add(this));
        setOnCloseRequest(event -> dialogs.remove(this));
        setOnShown(event -> {
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            Node par;

            try {
                par = ((Pane) getDialogPane().getContent()).getChildren().get(0);
            } catch (Exception e) {
                return;
            }

            WritableImage image = par.snapshot(parameters, null);
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            if (!cliped) {
                width -= 10;
                height -= 10;
                cliped = true;
            }
            dialogWidth.set(width);
            dialogHeight.set(height);
            par.setClip(FXUtils.generateRect(width, height, dialogRadius.get()));
        });

        dialogNowRadius.addListener((observable, oldValue, newValue) -> getDialogPane().setEffect(new GaussianBlur(newValue.intValue())));
        dialogs.addListener((ListChangeListener<AbstractDialog>) c -> onDialogListChange());

        setHideOnEscape(false);
    }
    public void setContent(Node... content) {
        ThemeManager.addLis((observable, oldValue, newValue) -> FXUtils.toNodeClass(
                FXUtils.toNodeClass(
                        getDialogPane().getContent(), Pane.class
                ).getChildren().get(0),
                Region.class
        ).setBackground(new Background(
                new BackgroundFill(
                        getColorTrans((Color) newValue),
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                )
        )));

        super.setContent(content);
    }
    private Color getColorTrans(Color src) {
        return new Color(src.getRed(), src.getGreen(), src.getBlue(), 0.85);
    }
    private void onDialogListChange() {
        dialogExceptedRadius.set(dialogs.indexOf(this) + 1 == dialogs.size() ? 0 : blurRadius);
    }
    public void Create() {
        FXUtils.Platform.runLater(this::show);
    }
    public static Label setFont(Label l, Font font){
        l.setFont(font);
        return l;
    }
}