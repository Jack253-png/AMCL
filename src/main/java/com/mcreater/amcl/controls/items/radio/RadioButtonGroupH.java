package com.mcreater.amcl.controls.items.radio;

import com.jfoenix.controls.JFXRadioButton;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import javafx.scene.layout.HBox;

import java.util.Vector;

import static com.mcreater.amcl.util.FXUtils.ColorUtil.reverse;

public class RadioButtonGroupH extends HBox implements AbstractRadioButtonGroup {
    public final Vector<JFXRadioButton> items = new Vector<>();
    public RadioButtonGroupH(String... items) {
        boolean isFirst = true;
        for (String i : items) {
            JFXRadioButton button = new JFXRadioButton(i);
            if (isFirst) {
                button.setSelected(true);
                isFirst = false;
            }
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
            });
            this.items.add(button);
            getChildren().add(button);
        }
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
}
