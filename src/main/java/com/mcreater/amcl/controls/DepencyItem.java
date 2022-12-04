package com.mcreater.amcl.controls;

import com.jfoenix.effects.JFXDepthManager;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.concurrent.Callable;

public class DepencyItem extends VBox {
    public Label name;
    public Label copyrightUI;
    public Label lencinsesUI;
    public DepencyItem(String name, String copyright, String lencinses){
        this.name = new Label(name);
        this.name.setFont(Fonts.s_f);
        this.copyrightUI = new Label(copyright);
        this.copyrightUI.setFont(Fonts.t_f);
        this.lencinsesUI = new Label(lencinses);
        this.lencinsesUI.setFont(Fonts.t_f);
        this.setSpacing(8);
        this.getChildren().addAll(this.name, this.copyrightUI, this.lencinsesUI);
        ThemeManager.loadButtonAnimates(this);
        ThemeManager.addLis((observable, oldValue, newValue) -> {
            copyrightUI.textFillProperty().bind(Bindings.createObjectBinding((Callable<Paint>) () -> trans((Color) newValue, 0.75)));
            lencinsesUI.textFillProperty().bind(Bindings.createObjectBinding((Callable<Paint>) () -> trans((Color) newValue, 0.75)));
        });
    }
    private Color trans(Color c, double op) {
        return new Color(1D - c.getRed(), 1D - c.getGreen(), 1D - c.getBlue(), op);
    }
    public Node toMaterial() {
        return JFXDepthManager.createMaterialNode(this, 10);
    }
}
