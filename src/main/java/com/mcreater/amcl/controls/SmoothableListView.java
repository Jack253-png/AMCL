package com.mcreater.amcl.controls;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.util.Vector;

public class SmoothableListView<T extends Region> extends VBox{
    public Vector<T> vecs = new Vector<>();
    public Vector<JFXButton> bs = new Vector<>();
    public SettingPage page;
    public T selectedItem = null;
    public JFXButton selectedButton = null;
    public ObjectProperty<Runnable> onActionProperty = new SimpleObjectProperty<>(() -> {});
    public ObjectProperty<Runnable> onReleasedProperty = new SimpleObjectProperty<>(() -> {});
    public void select(int index){
        try {
            bs.get(index).getOnAction().handle(new ActionEvent());
        }
        catch (Exception ignored){}
    }
    public SmoothableListView(double width, double height) {
        page = new SettingPage(width, height, this, false);
        this.setSpacing(5);
        ThemeManager.loadButtonAnimates(this);

        ThemeManager.applyNode(page);
        FXUtils.ControlSize.setWidth(this, width - 15);
    }
    public void setOnAction(@NotNull Runnable r){
        onActionProperty.set(r);
    }
    public void addItem(T item){
        vecs.add(item);
        JFXButton button = new JFXButton();
        button.setGraphic(item);
        button.setButtonType(JFXButton.ButtonType.RAISED);

        bs.add(button);

        ThemeManager.loadButtonAnimates(button);

        button.setOnAction(event -> {
            selectedItem = item;
            selectedButton = button;
            onActionProperty.get().run();
        });
        button.setOnMouseReleased(event -> onReleasedProperty.get().run());
        FXUtils.ControlSize.setWidth(button, this.getMaxWidth());
        this.getChildren().add(button);
        FXUtils.ControlSize.setWidth(this, page.width - 15);
    }

    public void clear(){
        vecs.forEach(node -> node.setOnMouseReleased(event -> {}));
        vecs.clear();
        bs.clear();
        this.getChildren().clear();
        FXUtils.ControlSize.setWidth(this, page.width - 15);
        selectedItem = null;
    }
    public enum MouseEventTypes {
        PRESSED,
        RELEASED,
        ENTERED,
        EXITED
    }
}
