package com.mcreater.amcl.controls;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.controls.items.ListItem;
import com.mcreater.amcl.pages.interfaces.SettingPage;
import com.mcreater.amcl.util.FXUtils;
import com.sun.javafx.scene.EventHandlerProperties;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Vector;

public class SmoothableListView<T extends Region> extends VBox{
    public Vector<T> vecs = new Vector<>();
    public SettingPage page;
    public T selectedItem = null;
    public JFXButton selectedButton = null;
    public ObjectProperty<Runnable> onActionProperty = new SimpleObjectProperty<>();
    public SmoothableListView(double width, double height) {
        page = new SettingPage(width, height, this, false);
        this.setSpacing(5);
    }
    public void setOnAction(Runnable r){
        onActionProperty.set(r);
    }
    public void addItem(T item){
        addItem(item, item.onMousePressedProperty());
    }
    public void addItem(T item, ObjectProperty<EventHandler<? super MouseEvent>> type){
        vecs.add(item);
        JFXButton button = new JFXButton();
        button.setGraphic(item);
        Border b1 = FXUtils.generateBorder(new Color(0, 0, 0, 0.25), BorderStrokeStyle.SOLID, true, true, true, true, 1);
        Border b2 = FXUtils.generateBorder(Color.BLACK, BorderStrokeStyle.SOLID, false, false, false, false, 1);
        button.setOnMouseEntered(event -> button.setBorder(b1));
        button.setOnMouseExited(event -> button.setBorder(b2));

        type.set(event -> {
            selectedItem = item;
            selectedButton = button;
            onActionProperty.get().run();
        });
        FXUtils.ControlSize.setWidth(button, this.getMaxWidth());
        this.getChildren().add(button);
        FXUtils.ControlSize.setWidth(this, page.width - 50);
    }

    public void clear(){
        vecs.forEach(node -> node.setOnMouseReleased(event -> {}));
        vecs.clear();
        this.getChildren().clear();
        FXUtils.ControlSize.setWidth(this, page.width - 50);
        selectedItem = null;
    }
    public enum MouseEventTypes {
        PRESSED,
        RELEASED,
        ENTERED,
        EXITED
    }
}
