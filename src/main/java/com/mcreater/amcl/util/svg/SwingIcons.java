package com.mcreater.amcl.util.svg;

import com.mcreater.amcl.nativeInterface.ResourceGetter;

import java.awt.*;

public interface SwingIcons {

    java.awt.Image swingIcon = Toolkit.getDefaultToolkit().createImage(new ResourceGetter().getUrl("assets/icons/grass.png"));
}
