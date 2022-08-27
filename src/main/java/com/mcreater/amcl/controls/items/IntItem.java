package com.mcreater.amcl.controls.items;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.skins.JFXSliderSkin;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.lang.reflect.Field;

public class IntItem extends HBox {
    public Label name;
    public JFXSlider cont;
    public IntItem(String name, double width){
        this.name = new Label(name);
        this.name.setFont(Fonts.t_f);
        cont = new JFXSlider();
        FXUtils.ControlSize.setWidth(cont, width / 40 * 28);
        HBox left = new HBox(this.name);
        left.setAlignment(Pos.CENTER_LEFT);
        FXUtils.ControlSize.setWidth(left, width / 4);
        HBox right = new HBox(cont);
        right.setAlignment(Pos.CENTER);
        FXUtils.ControlSize.setWidth(right, width / 4 * 3);
        this.getChildren().addAll(left, right);

        JFXSliderSkin skin = new JFXSliderSkin(cont);
        cont.setSkin(skin);

        try {
            Field f = JFXSliderSkin.class.getDeclaredField("sliderValue");
            f.setAccessible(true);
            Text text = (Text) f.get(skin);
            text.setFont(Fonts.ts_f);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
