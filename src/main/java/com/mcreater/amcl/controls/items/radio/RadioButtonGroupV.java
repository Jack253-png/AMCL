package com.mcreater.amcl.controls.items.radio;

import com.jfoenix.controls.JFXRadioButton;
import com.mcreater.amcl.pages.interfaces.Fonts;
import javafx.scene.layout.VBox;

import java.util.Vector;

public class RadioButtonGroupV extends VBox implements AbstractRadioButtonGroup {
    public final Vector<JFXRadioButton> items = new Vector<>();
    public RadioButtonGroupV(String... items) {
        for (String i : items) {
            JFXRadioButton button = new JFXRadioButton(i);
            button.setFont(Fonts.t_f);
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
