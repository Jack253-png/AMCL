package com.mcreater.amcl.util.svg;

import com.mcreater.amcl.nativeInterface.ResourceGetter;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public interface Icons {
    String iconPath = "assets/icons/icon.png";
    java.awt.Image swingIcon = Toolkit.getDefaultToolkit().createImage(ResourceGetter.getUrl(iconPath));
    AtomicReference<javafx.scene.image.Image> fxIcon = new AtomicReference<>();
    static void initFXIcon() {
        fxIcon.set(new javafx.scene.image.Image(iconPath));
    }
}
