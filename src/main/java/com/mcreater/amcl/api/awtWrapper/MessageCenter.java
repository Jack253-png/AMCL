package com.mcreater.amcl.api.awtWrapper;

import com.mcreater.amcl.util.svg.Icons;

import java.awt.*;

public class MessageCenter {
    public static void pushNewMessage(String title, String text, TrayIcon.MessageType type) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon trayIcon = new TrayIcon(Icons.swingIcon, "Abstract Minecraft Launcher");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Abstract Minecraft Launcher");
        tray.add(trayIcon);
        trayIcon.displayMessage(title, text, type);
    }
    public static void pushNewMessage(String title, String text) throws AWTException {
        pushNewMessage(title, text, TrayIcon.MessageType.INFO);
    }
}
