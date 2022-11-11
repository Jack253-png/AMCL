package com.mcreater.amcl.controls;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.users.AbstractUser;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.concurrent.Callable;

public class AccountInfoItem extends VBox {
    Label userName;
    ImageView headImage;
    JFXButton modify;
    JFXButton delete;
    public JFXRadioButton selector;
    public final AbstractUser user;
    public void setModify(EventHandler<ActionEvent> handler) {
        modify.setOnAction(handler);
    }
    public void setDelete(EventHandler<ActionEvent> handler) {
        delete.setOnAction(handler);
    }
    public AccountInfoItem(AbstractUser user, double width) {
        this.user = user;
        userName = new Label(user.username);
        userName.setFont(Fonts.s_f);
        headImage = new ImageView();

        modify = new JFXButton();
        delete = new JFXButton();

        FXUtils.ControlSize.setAll(30, 30, modify, delete);

        modify.setGraphic(Launcher.getSVGManager().accountEdit(Bindings.createObjectBinding((Callable<Paint>) () -> Color.BLACK), 30, 30));
        delete.setGraphic(Launcher.getSVGManager().delete(Bindings.createObjectBinding((Callable<Paint>) () -> Color.BLACK), 30, 30));

        selector = new JFXRadioButton();
        selector.selectedProperty().addListener((observable, oldValue, newValue) -> user.active = newValue);

        HBox box = new HBox(modify, delete);
        box.setSpacing(0);

        HBox left = new HBox(selector, userName);
        HBox right = new HBox(box);

        left.setAlignment(Pos.TOP_LEFT);
        right.setAlignment(Pos.TOP_RIGHT);

        HBox topG = new HBox(left, right);
        FXUtils.ControlSize.setWidth(topG, width);

        FXUtils.ControlSize.setWidth(left, width - 65);
        FXUtils.ControlSize.setWidth(right, 65);

        getChildren().add(topG);
    }
}
