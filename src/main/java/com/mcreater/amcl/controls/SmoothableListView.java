package com.mcreater.amcl.controls;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Vector;

public class SmoothableListView<T extends Region> extends VBox {
    public Vector<T> vecs = new Vector<>();
    private final Vector<JFXButton> bs = new Vector<>();
    public AdvancedScrollPane page;
    public T selectedItem = null;
    public JFXButton selectedButton = null;
    public ObjectProperty<Runnable> onActionProperty = new SimpleObjectProperty<>(() -> {});
    public ObjectProperty<Runnable> onReleasedProperty = new SimpleObjectProperty<>(() -> {});

    public void select(int index) {
        try {
            bs.get(index).getOnAction().handle(new ActionEvent());
        } catch (Exception ignored) {
        }
    }

    public SmoothableListView(double width, double height) {
        page = new AdvancedScrollPane(width, height, this, false);
        page.setId("opc");
        this.setSpacing(5);
        ThemeManager.loadNodeAnimations(this);

        ThemeManager.applyNode(page, "SmoothableListView");
        FXUtils.ControlSize.setWidth(this, width - 15);
    }

    public void setOnAction(@NotNull Runnable r) {
        onActionProperty.set(r);
    }

    public void addItem(T item) {
        vecs.add(item);
        JFXButton button = new JFXButton();
        button.setGraphic(item);
        button.setButtonType(JFXButton.ButtonType.RAISED);

        bs.add(button);

        ThemeManager.loadNodeAnimations(button);

        button.setOnAction(event -> {
            selectedItem = item;
            selectedButton = button;
        });
        button.setOnMouseClicked(event -> onActionProperty.get().run());
        button.setOnMouseReleased(event -> onReleasedProperty.get().run());

        FXUtils.ControlSize.setWidth(button, this.getMaxWidth());
        this.getChildren().add(button);
        FXUtils.ControlSize.setWidth(this, page.width - 15);
    }

    public void addItem(T item, int index) {
        vecs.add(index, item);
        JFXButton button = new JFXButton();
        button.setGraphic(item);
        button.setButtonType(JFXButton.ButtonType.RAISED);

        bs.add(index, button);

        ThemeManager.loadNodeAnimations(button);

        button.setOnAction(event -> {
            selectedItem = item;
            selectedButton = button;
        });
        button.setOnMouseClicked(event -> onActionProperty.get().run());
        button.setOnMouseReleased(event -> onReleasedProperty.get().run());

        FXUtils.ControlSize.setWidth(button, this.getMaxWidth());
        this.getChildren().add(index, button);
        FXUtils.ControlSize.setWidth(this, page.width - 15);
    }

    public void clear() {
        vecs.clear();
        bs.clear();
        getChildren().clear();
        selectedItem = null;
        selectedButton = null;
    }

    public void removeItem(T item) {
        if (vecs.contains(item)) {
            item.setOnMouseReleased(event -> {});
            int index = vecs.indexOf(item);
            vecs.remove(item);

            JFXButton tempButton = bs.get(index);
            bs.remove(index);
            FXUtils.ControlSize.setWidth(this, page.width - 15);
            if (item == selectedItem) selectedItem = null;
            if (tempButton == selectedButton) selectedButton = null;
            Timeline timeline = new Timeline(
                    new KeyFrame(
                            Duration.ZERO,
                            new KeyValue(
                                    tempButton.opacityProperty(),
                                    1
                            )
                    ),
                    new KeyFrame(
                            Duration.millis(350),
                            new KeyValue(
                                    tempButton.opacityProperty(),
                                    0
                            )
                    )
            );
            timeline.setCycleCount(1);
            timeline.setAutoReverse(false);
            timeline.setOnFinished(event -> getChildren().remove(tempButton));
            timeline.playFromStart();
        }
    }

    public final void removeItems(List<T> items) {
        items.forEach(this::removeItem);
    }
}
