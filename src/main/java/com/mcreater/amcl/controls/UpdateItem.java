package com.mcreater.amcl.controls;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.builders.ThreadBuilder;
import com.mcreater.amcl.util.os.SystemActions;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class UpdateItem extends VBox {
    Label type;
    Label url;
    JFXButton copy;
    public UpdateItem(String fileType, String url){
        this.type = new Label(String.format(".%s", fileType));
        this.type.setFont(Fonts.t_f);
        this.url = new Label(url);
        this.url.setFont(Fonts.t_f);
        copy = new JFXButton(Launcher.languageManager.get("ui.updateItem.copy.name"));
        copy.setFont(Fonts.t_f);
        copy.setOnAction(event -> {
            ThreadBuilder.createBuilder()
                    .runTarget(() -> SystemActions.copyContent(this.url.getText()))
                    .buildAndRun();
        });
        this.getChildren().addAll(type, this.url, copy);
        BorderStroke bs = new BorderStroke(null,null, Color.BLACK,null, null,null, BorderStrokeStyle.SOLID,null,null, BorderWidths.DEFAULT,new Insets(2,2,2,2));
        this.setBorder(new Border(bs));
    }
}
