package com.mcreater.amcl.controls;

import com.mcreater.amcl.patcher.ClassPathInjector;
import javafx.scene.control.Skin;

public class JFXProgressBar extends com.jfoenix.controls.JFXProgressBar {
    public JFXProgressBar(double i) {
        super(i);
    }

    public JFXProgressBar() {
        super();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new com.mcreater.amcl.controls.JFXProgressBarSkin(this);
    }

    public static com.jfoenix.controls.JFXProgressBar createProgressBar(double value){
        if (ClassPathInjector.version < 9) return new com.jfoenix.controls.JFXProgressBar(value);
        else return new JFXProgressBar(value);
    }
    public static com.jfoenix.controls.JFXProgressBar createProgressBar(){
        if (ClassPathInjector.version < 9) return new com.jfoenix.controls.JFXProgressBar();
        else return new JFXProgressBar();
    }
}
