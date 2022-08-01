package com.mcreater.amcl.util.operatingSystem;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class BrowserHelper {
    public static void open(String url){
        new Thread(() -> {
            try {
                URI url1 = URI.create(url);
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)){
                    desktop.browse(url1);
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }).start();

    }
}
