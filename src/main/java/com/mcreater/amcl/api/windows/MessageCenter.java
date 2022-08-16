package com.mcreater.amcl.api.windows;

import com.mcreater.amcl.nativeInterface.ResourceGetter;

import java.awt.*;

public class MessageCenter {
    public static void pushNewMessage(String title, String text, TrayIcon.MessageType type) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage(new ResourceGetter().getUrl("assets/icons/grass.png"));

        TrayIcon trayIcon = new TrayIcon(image, "Abstract Minecraft Launcher");
        char[] c = type.toString().toLowerCase().toCharArray();
        c[0] = toUpperCase(c[0]);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("A Launcher " + String.valueOf(c));
        tray.add(trayIcon);
        trayIcon.displayMessage(title, text, type);
    }
    public static void pushNewMessage(String title, String text) throws AWTException {
        pushNewMessage(title, text, TrayIcon.MessageType.INFO);
    }
    private static char toUpperCase(char s){
        if (97 <= s && s < 122){
            s ^= 32;
        }
        return s;
    }
}
