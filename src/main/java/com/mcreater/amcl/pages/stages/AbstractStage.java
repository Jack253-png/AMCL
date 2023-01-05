package com.mcreater.amcl.pages.stages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import static com.mcreater.amcl.Launcher.barSize;
import static com.mcreater.amcl.Launcher.getSVGManager;

public abstract class AbstractStage extends Stage {
    public static VBox top = new VBox();
    public static JFXButton close;
    public static JFXButton min;
    public static Scene s;
    VBox v = new VBox();
    private static Timeline onStageShow;
    private static Timeline onStageExit;
    private static Pane wrapper;

    public AbstractStage(double width, double height) {
        initStyle(StageStyle.TRANSPARENT);
        initOwner(Launcher.stage);

        setWidth(width);
        setHeight(height);

        s = new Scene(new Region());
        wrapper = new Pane();

        double t_size = barSize;
        top = new VBox();
        top.setId("top-bar");
        top.setPrefSize(width, t_size);

        GridPane title = new GridPane();
        title.setAlignment(Pos.CENTER);
        close = new JFXButton();
        min = new JFXButton();
        close.setPrefWidth(t_size / 6 * 5);
        close.setPrefHeight(t_size / 6 * 5);
        close.setGraphic(getSVGManager().close(ThemeManager.createPaintBinding(), t_size / 3 * 2, t_size / 3 * 2));
        close.setButtonType(JFXButton.ButtonType.RAISED);
        close.setOnAction(event -> playStageAnimation(true, this::close));
        Rectangle rect = new Rectangle(t_size / 2.5, t_size / 15, Color.BLACK);
        rect.fillProperty().bind(ThemeManager.createPaintBinding());

        setOnCloseRequest(event -> close.getOnAction().handle(new ActionEvent()));
        min.setPrefWidth(t_size / 2.5);
        min.setPrefHeight(t_size / 2.5);
        min.setGraphic(rect);
        min.setButtonType(JFXButton.ButtonType.RAISED);
        min.setOnAction(event -> this.setIconified(true));

        FXUtils.ControlSize.setAll(t_size / 6 * 5, t_size / 6 * 5, min, close);
        HBox cl = new HBox(min, close);
        cl.setAlignment(Pos.CENTER_RIGHT);
        cl.setMinSize(width, t_size);
        cl.setMaxSize(width, t_size);
        title.add(cl, 1, 0, 1, 1);
        top.getChildren().add(title);

        FXUtils.WindowMovement.getInstance().windowMove(top, this);

        ThemeManager.applyTopBar(top);

        v.getChildren().add(top);
        wrapper.getChildren().clear();
        wrapper.getChildren().add(v);

        s.setFill(Color.TRANSPARENT);
        s.setRoot(wrapper);
        setScene(s);
        s.setOnKeyPressed(event -> System.out.println(event.getCode()));

        onStageShow = new Timeline(
                new KeyFrame(
                        Duration.millis(100),
                        new KeyValue(
                                wrapper.opacityProperty(),
                                0,
                                Interpolator.EASE_BOTH
                        ),
                        new KeyValue(
                                wrapper.scaleXProperty(),
                                0.8,
                                Interpolator.EASE_BOTH
                        ),
                        new KeyValue(
                                wrapper.scaleYProperty(),
                                0.8,
                                Interpolator.EASE_BOTH
                        )
                ),
                new KeyFrame(
                        Duration.seconds(2.5),
                        new KeyValue(
                                wrapper.opacityProperty(),
                                1,
                                Interpolator.EASE_BOTH
                        )
                ),
                new KeyFrame(
                        Duration.seconds(2),
                        new KeyValue(
                                wrapper.scaleXProperty(),
                                1,
                                Interpolator.EASE_BOTH
                        ),
                        new KeyValue(
                                wrapper.scaleYProperty(),
                                1,
                                Interpolator.EASE_BOTH
                        )
                )
        );
        onStageShow.setCycleCount(1);
        onStageShow.setAutoReverse(false);

        onStageExit = new Timeline(
                new KeyFrame(
                        Duration.millis(50),
                        new KeyValue(
                                wrapper.opacityProperty(),
                                1,
                                Interpolator.EASE_BOTH
                        ),
                        new KeyValue(
                                wrapper.scaleXProperty(),
                                1,
                                Interpolator.EASE_BOTH
                        ),
                        new KeyValue(
                                wrapper.scaleYProperty(),
                                1,
                                Interpolator.EASE_BOTH
                        )
                ),
                new KeyFrame(
                        Duration.seconds(1),
                        new KeyValue(
                                wrapper.opacityProperty(),
                                0,
                                Interpolator.EASE_BOTH
                        )
                ),
                new KeyFrame(
                        Duration.seconds(0.5),
                        new KeyValue(
                                wrapper.scaleXProperty(),
                                0.8,
                                Interpolator.EASE_BOTH
                        ),
                        new KeyValue(
                                wrapper.scaleYProperty(),
                                0.8,
                                Interpolator.EASE_BOTH
                        )
                )
        );
        onStageExit.setCycleCount(1);
        onStageExit.setAutoReverse(false);

        wrapper.setOpacity(0);
        wrapper.setScaleX(0.8);
        wrapper.setScaleY(0.8);

        Rectangle rect2 = FXUtils.generateRect(width, height, Launcher.radius.get());
        rect2.arcWidthProperty().bind(Launcher.radius);
        rect2.arcHeightProperty().bind(Launcher.radius);
        wrapper.setClip(rect2);

    }
    public void setContent(Node content) {
        v.getChildren().add(content);
    }
    private static void playStageAnimation(boolean isExit, Runnable finisher) {
        if (isExit) {
            onStageShow.stop();
            onStageExit.setOnFinished(event -> finisher.run());
            onStageExit.playFromStart();
        }
        else {
            onStageExit.stop();
            onStageShow.setOnFinished(event -> finisher.run());
            onStageShow.playFromStart();
        }
    }

    public void open() {
        initStyle(StageStyle.TRANSPARENT);
        show();
        playStageAnimation(false, () -> {});
    }
}
