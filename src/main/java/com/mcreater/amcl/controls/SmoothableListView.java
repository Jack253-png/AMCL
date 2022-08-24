package com.mcreater.amcl.controls;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.controls.items.ListItem;
import com.mcreater.amcl.pages.interfaces.SettingPage;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.sun.javafx.scene.EventHandlerProperties;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
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
    public Vector<JFXButton> bs = new Vector<>();
    public SettingPage page;
    public T selectedItem = null;
    public JFXButton selectedButton = null;
    public ObjectProperty<Runnable> onActionProperty = new SimpleObjectProperty<>(() -> {});
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
    }
    public void setOnAction(Runnable r){
        onActionProperty.set(r);
    }
    public void addItem(T item){
        vecs.add(item);
        JFXButton button = new JFXButton();
        button.setGraphic(item);
        Border b1 = FXUtils.generateBorder(new Color(0, 0, 0, 0.25), BorderStrokeStyle.SOLID, true, true, true, true, 1);
        Border b2 = FXUtils.generateBorder(Color.BLACK, BorderStrokeStyle.SOLID, false, false, false, false, 1);
        button.setOnMouseEntered(event -> button.setBorder(b1));
        button.setOnMouseExited(event -> button.setBorder(b2));
        button.setButtonType(JFXButton.ButtonType.RAISED);

        bs.add(button);

        ThemeManager.loadButtonAnimates(button);

        button.setOnAction(event -> {
            bs.forEach(e -> e.setStyle("-fx-background-color: transparent"));
            button.setStyle("-fx-background-color: rgba(0, 0, 0, 0.15)");
            selectedItem = item;
            selectedButton = button;
            onActionProperty.get().run();
        });
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
