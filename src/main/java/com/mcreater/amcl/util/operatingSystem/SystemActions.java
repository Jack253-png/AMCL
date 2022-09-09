package com.mcreater.amcl.util.operatingSystem;

import com.mcreater.amcl.pages.stages.NativeBrowserPage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class SystemActions {
    public static void openBrowser(String url){
        new Thread(() -> {
            try {
                if (supportAction(Desktop.Action.BROWSE)){
                    getDesktop().browse(URI.create(url));
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        });
        NativeBrowserPage page = new NativeBrowserPage(url);
        page.open();
    }

    private static boolean supportAction(Desktop.Action action){
        return getDesktop().isSupported(action);
    }
    private static Desktop getDesktop(){
        return Desktop.getDesktop();
    }
}
