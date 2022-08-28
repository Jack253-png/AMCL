package com.mcreater.amcl.api.awtWrapper;

import com.mcreater.amcl.nativeInterface.ResourceGetter;

import java.awt.*;

public class MessageCenter {
    public static void pushNewMessage(String title, String text, TrayIcon.MessageType type) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage(new ResourceGetter().getUrl("assets/icons/grass.png"));

        TrayIcon trayIcon = new TrayIcon(image, "Abstract Minecraft Launcher");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Minecraft Launcher");
        tray.add(trayIcon);
        trayIcon.displayMessage(title, text, type);
    }
    public static void pushNewMessage(String title, String text) throws AWTException {
        pushNewMessage(title, text, TrayIcon.MessageType.INFO);
    }
}
