package com.mcreater.amcl.util.os;

import com.mcreater.amcl.natives.OSInfo;
import com.mcreater.amcl.util.builders.ThreadBuilder;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class SystemActions {
    private static final String[] linuxBrowsers = {
            "xdg-open",
            "google-chrome",
            "firefox",
            "microsoft-edge",
            "opera",
            "konqueror",
            "mozilla"
    };

    public static void openBrowser(String url) {
        ThreadBuilder.createBuilder()
                .runTarget(() -> {
                    try {
                        if (supportAction(Desktop.Action.BROWSE)) {
                            getDesktop().browse(URI.create(url));
                        } else {
                            if (OSInfo.isWin()) {
                                Runtime.getRuntime().exec(new String[]{"rundll32.exe", "url.dll,FileProtocolHandler", url});
                            } else if (OSInfo.isLinux()) {
                                for (String browser : linuxBrowsers) {
                                    try (final InputStream is = Runtime.getRuntime().exec(new String[]{"which", browser}).getInputStream()) {
                                        if (is.read() != -1) {
                                            Runtime.getRuntime().exec(new String[]{browser, url});
                                            return;
                                        }
                                    } catch (Throwable e) {
                                        throw new IOException(e);
                                    }
                                }
                            } else if (OSInfo.isMac()) {
                                Runtime.getRuntime().exec(new String[]{"/usr/bin/open", url});
                            } else {
                                throw new IOException("Can't open default web browser.");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .name("URL process thread")
                .buildAndRun();
    }

    public static void copyContent(String content) {
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(content), (clipboard1, transferable) -> {});
    }


    private static boolean supportAction(Desktop.Action action) {
        return getDesktop().isSupported(action);
    }

    private static Desktop getDesktop() {
        return Desktop.getDesktop();
    }
}
