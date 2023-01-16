package com.mcreater.amcl.controls.items.radio;

import com.jfoenix.controls.JFXRadioButton;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.Vector;
import java.util.function.Consumer;

import static com.mcreater.amcl.util.FXUtils.ColorUtil.reverse;

public class RadioButtonGroupV extends VBox implements AbstractRadioButtonGroup {
    public final Vector<JFXRadioButton> items = new Vector<>();
    Consumer<Integer> c = index -> {};
    public RadioButtonGroupV(String... items) {
        Arrays.stream(items).forEach(this::addItem);
        setSpacing(20);
    }
    public int getSelectedItem() {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isSelected()) return i;
        }
        return -1;
    }

    public void select(int index) {
        items.get(index).setSelected(true);
    }

    public void setOnChanged(Consumer<Integer> c) {
        this.c = c;
    }

    public void addItem(String s) {
        JFXRadioButton button = new JFXRadioButton(s);
        if (this.items.size() == 0) button.setSelected(true);
        button.setFont(Fonts.t_f);
        ThemeManager.addLis((observable, oldValue, newValue) -> button.setTextFill(reverse(newValue)));
        button.setOnAction(event -> {
            if (!button.isSelected()) button.setSelected(true);
            if (button.isSelected()) {
                this.items.forEach(jfxRadioButton -> {
                    if (jfxRadioButton != button) {
                        jfxRadioButton.setSelected(!button.isSelected());
                    }
                });
            }
            this.c.accept(getSelectedItem());
        });
        this.items.add(button);
        getChildren().add(button);
    }
}
