package com.mcreater.amcl.util.svg;

import com.mcreater.amcl.natives.ResourceGetter;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.concurrent.atomic.AtomicReference;

public interface Icons {
    String iconPath = "assets/icons/icon.png";
    Image swingIcon = Toolkit.getDefaultToolkit().createImage(ResourceGetter.getUrl(iconPath));
    AtomicReference<javafx.scene.image.Image> fxIcon = new AtomicReference<>();

    static void initFXIcon() {
        fxIcon.set(new javafx.scene.image.Image(iconPath));
    }
}
