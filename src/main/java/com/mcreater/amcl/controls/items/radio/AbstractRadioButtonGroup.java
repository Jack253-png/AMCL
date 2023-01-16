package com.mcreater.amcl.controls.items.radio;

import java.util.function.Consumer;

public interface AbstractRadioButtonGroup {
    int getSelectedItem();
    void select(int index);
    void setOnChanged(Consumer<Integer> c);
    void addItem(String s);
}
