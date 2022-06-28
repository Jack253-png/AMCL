package com.mcreater.amcl.controls;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Application;
import com.mcreater.amcl.pages.interfaces.Fonts;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class UpdateItem extends VBox {
    Label type;
    Label url;
    JFXButton copy;
    public UpdateItem(String fileType, String url){
        this.type = new Label(String.format(".%s", fileType));
        this.type.setFont(Fonts.t_f);
        this.url = new Label(url);
        this.url.setFont(Fonts.t_f);
        copy = new JFXButton(Application.languageManager.get("ui.updateItem.copy.name"));
        copy.setFont(Fonts.t_f);
        copy.setOnAction(event -> {
            Runnable runnable = () -> {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable t = new StringSelection(this.url.getText());
                clipboard.setContents(t, (clipboard1, transferable) -> {});
            };
            new Thread(runnable).start();
        });
        this.getChildren().addAll(type, this.url, copy);
        BorderStroke bs = new BorderStroke(null,null, Color.BLACK,null, null,null, BorderStrokeStyle.SOLID,null,null, BorderWidths.DEFAULT,new Insets(2,2,2,2));
        this.setBorder(new Border(bs));
    }
}
