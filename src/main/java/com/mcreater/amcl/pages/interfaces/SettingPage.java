package com.mcreater.amcl.pages.interfaces;

import com.jfoenix.utils.JFXSmoothScroll;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.skin.ScrollPaneSkin;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class SettingPage extends ScrollPane implements SettingsAnimationPage {
    public double width, height;
    public Pane content;

    private final Region shadow = new Region();
    public SettingPage(double width, double height, VBox content, Object flag){
        super(content);
        FXUtils.ControlSize.set(this, width, height);
        FXUtils.ControlSize.set(content, width - 30, height - 10);
        this.width = width;
        this.height = height;
        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.content = content;
        init();
        JFXSmoothScroll.smoothScrolling(this, 0.8);
        ThemeManager.loadButtonAnimates(this.content);
    }
    public SettingPage(double width, double height, Pane content) {
        this (width, height, content, true);
    }
    public SettingPage(double width, double height, Pane content, boolean neededHeight) {
        super(content);
        FXUtils.ControlSize.set(this, width, height);
        if (neededHeight) {
            FXUtils.ControlSize.set(content, width - 30, height - 10);
        }
        else{
            FXUtils.ControlSize.setWidth(content, width - 30);
        }
        this.width = width;
        this.height = height;
        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.content = content;
        init();
        JFXSmoothScroll.smoothScrolling(this, 0.8);
        ThemeManager.loadButtonAnimates(this.content);
    }
    private void init() {
        skinProperty().addListener(it -> getChildren().addAll(shadow));

        setFitToWidth(true);

        shadow.setManaged(false);
        shadow.setStyle("-fx-pref-height: 10;-fx-background-color: rgba(0, 0, 0, .35);-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, .15), 20, 0.8, 0, 4);");
        shadow.getStyleClass().add("shadow");
        shadow.visibleProperty().bind(showShadowProperty());
        shadow.setMouseTransparent(true);
        shadow.visibleProperty().bind(vvalueProperty().greaterThan(0));

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);

        vvalueProperty().addListener(it -> {
            if (lastOffset != computeOffset()) {
                requestLayout();
            }
        });
        showShadowProperty().addListener(it -> requestLayout());
    }

    private final BooleanProperty showShadow = new SimpleBooleanProperty(this, "showShadow", true);

    public final BooleanProperty showShadowProperty() {
        return showShadow;
    }

    public final boolean isShowShadow() {
        return showShadow.get();
    }

    public final void setShowShadow(boolean show) {
        showShadow.set(show);
    }

    private final int SHADOW_HEIGHT = 30;

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        if (isShowShadow()) {
            Insets insets = getInsets();
            double w = getWidth();
            double h = getHeight();
            double offset = computeOffset();
            shadow.resizeRelocate(-10, insets.getTop() - shadow.prefHeight(-1) - SHADOW_HEIGHT + offset, w + 20, shadow.prefHeight(-1) - 1);
            lastOffset = offset;
        }
    }

    private double lastOffset = 0;

    private double computeOffset() {
        if (getContent() != null) {
            return Math.min(getVvalue() * getContent().prefHeight(-1), SHADOW_HEIGHT);
        }

        return 0;
    }

    public void setTypeAll(boolean t) {
        for (Node n : this.getChildrenUnmodifiable()) {
            n.setDisable(t);
        }
    }
    public boolean CanMovePage(){
//        return this.getOpacity() == 1;
        return true;
    }

}
