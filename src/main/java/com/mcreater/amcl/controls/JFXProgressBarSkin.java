package com.mcreater.amcl.controls;

import com.jfoenix.utils.JFXNodeUtils;
import com.sun.javafx.scene.NodeHelper;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.skin.ProgressIndicatorSkin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class JFXProgressBarSkin extends ProgressIndicatorSkin {
    private StackPane track;
    private StackPane secondaryBar;
    private StackPane bar;
    private double barWidth = 0.0;
    private double secondaryBarWidth = 0.0;
    private Animation indeterminateTransition;
    private Region clip;
    boolean wasIndeterminate = false;

    public JFXProgressBarSkin(JFXProgressBar bar) {
        super(bar);
        bar.widthProperty().addListener((observable) -> {
            this.updateProgress();
            this.updateSecondaryProgress();
        });
        this.registerChangeListener(bar.progressProperty(), (obs) -> {
            this.updateProgress();
        });
        this.registerChangeListener(bar.secondaryProgressProperty(), (obs) -> {
            this.updateSecondaryProgress();
        });
        this.registerChangeListener(bar.visibleProperty(), (obs) -> {
            this.updateAnimation();
        });
        this.registerChangeListener(bar.parentProperty(), (obs) -> {
            this.updateAnimation();
        });
        this.registerChangeListener(bar.sceneProperty(), (obs) -> {
            this.updateAnimation();
        });
        this.registerChangeListener(bar.indeterminateProperty(), (obs) -> {
            this.initialize();
        });
        this.initialize();
        ((ProgressIndicator)this.getSkinnable()).requestLayout();
    }

    protected void initialize() {
        this.track = new StackPane();
        this.track.getStyleClass().setAll(new String[]{"track"});
        this.bar = new StackPane();
        this.bar.getStyleClass().setAll(new String[]{"bar"});
        this.secondaryBar = new StackPane();
        this.secondaryBar.getStyleClass().setAll(new String[]{"secondary-bar"});
        this.clip = new Region();
        this.clip.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)}));
        this.bar.backgroundProperty().addListener((observable) -> {
            JFXNodeUtils.updateBackground(this.bar.getBackground(), this.clip);
        });
        this.getChildren().setAll(new Node[]{this.track, this.secondaryBar, this.bar});
    }

    public double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
        return Double.NEGATIVE_INFINITY;
    }

    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(100.0, leftInset + this.bar.prefWidth(((ProgressIndicator)this.getSkinnable()).getWidth()) + rightInset);
    }

    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + this.bar.prefHeight(width) + bottomInset;
    }

    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return ((ProgressIndicator)this.getSkinnable()).prefWidth(height);
    }

    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return ((ProgressIndicator)this.getSkinnable()).prefHeight(width);
    }

    protected void layoutChildren(double x, double y, double w, double h) {
        this.track.resizeRelocate(x, y, w, h);
        this.secondaryBar.resizeRelocate(x, y, this.secondaryBarWidth, h);
        this.bar.resizeRelocate(x, y, ((ProgressIndicator)this.getSkinnable()).isIndeterminate() ? w : this.barWidth, h);
        this.clip.resizeRelocate(0.0, 0.0, w, h);
        if (((ProgressIndicator)this.getSkinnable()).isIndeterminate()) {
            this.createIndeterminateTimeline();
            if (NodeHelper.isTreeShowing(this.getSkinnable())) {
                this.indeterminateTransition.play();
            }

            this.bar.setClip(this.clip);
        } else if (this.indeterminateTransition != null) {
            this.clearAnimation();
            this.bar.setClip((Node)null);
        }

    }

    protected void updateSecondaryProgress() {
        JFXProgressBar control = (JFXProgressBar) this.getSkinnable();
        this.secondaryBarWidth = (double)((int)(control.getWidth() - this.snappedLeftInset() - this.snappedRightInset()) * 2) * Math.min(1.0, Math.max(0.0, control.getSecondaryProgress())) / 2.0;
        control.requestLayout();
    }

    protected void pauseTimeline(boolean pause) {
        if (this.getSkinnable().isIndeterminate()) {
            if (this.indeterminateTransition == null) {
                this.createIndeterminateTimeline();
            }

            if (pause) {
                this.indeterminateTransition.pause();
            } else {
                this.indeterminateTransition.play();
            }
        }

    }

    private void updateAnimation() {
        ProgressIndicator control = (ProgressIndicator)this.getSkinnable();
        boolean isTreeVisible = control.isVisible() && control.getParent() != null && control.getScene() != null;
        if (this.indeterminateTransition != null) {
            this.pauseTimeline(!isTreeVisible);
        } else if (isTreeVisible) {
            this.createIndeterminateTimeline();
        }

    }

    private void updateProgress() {
        ProgressIndicator control = (ProgressIndicator)this.getSkinnable();
        boolean isIndeterminate = control.isIndeterminate();
        if (!isIndeterminate || !this.wasIndeterminate) {
            this.barWidth = (double)((int)(control.getWidth() - this.snappedLeftInset() - this.snappedRightInset()) * 2) * Math.min(1.0, Math.max(0.0, control.getProgress())) / 2.0;
            control.requestLayout();
        }

        this.wasIndeterminate = isIndeterminate;
    }

    private void createIndeterminateTimeline() {
        if (this.indeterminateTransition != null) {
            this.clearAnimation();
        }

        double dur = 1.0;
        ProgressIndicator control = (ProgressIndicator)this.getSkinnable();
        double w = control.getWidth() - (this.snappedLeftInset() + this.snappedRightInset());
        this.indeterminateTransition = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(this.clip.scaleXProperty(), 0.0, Interpolator.EASE_IN), new KeyValue(this.clip.translateXProperty(), -w / 2.0, Interpolator.LINEAR)), new KeyFrame(Duration.seconds(0.5 * dur), new KeyValue(this.clip.scaleXProperty(), 0.4, Interpolator.LINEAR)), new KeyFrame(Duration.seconds(0.9 * dur), new KeyValue(this.clip.translateXProperty(), w / 2.0, Interpolator.LINEAR)), new KeyFrame(Duration.seconds(dur), new KeyValue(this.clip.scaleXProperty(), 0.0, Interpolator.EASE_OUT)));
        this.indeterminateTransition.setCycleCount(-1);
    }

    private void clearAnimation() {
        this.indeterminateTransition.stop();
        ((Timeline)this.indeterminateTransition).getKeyFrames().clear();
        this.indeterminateTransition = null;
    }

    public void dispose() {
        super.dispose();
        if (this.indeterminateTransition != null) {
            this.clearAnimation();
        }

    }
}
