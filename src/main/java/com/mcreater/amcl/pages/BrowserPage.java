package com.mcreater.amcl.pages;

import com.mcreater.amcl.theme.ThemeManager;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;

public class BrowserPage extends Stage {
    WebView w;
    String title;
    Thread updateTitle;
    public BrowserPage(String url){
        super();
        w = new WebView();
        w.setMaxSize(800, 600);
        w.setMinSize(800, 600);
        WebEngine webEngine = w.getEngine();
        webEngine.load(url);
        webEngine.setUserDataDirectory(new File("."));
        webEngine.setOnError(event -> event.getException().printStackTrace());
        w.getStylesheets().add(String.format(ThemeManager.getPath(), "BrowserPage"));
        this.getIcons().add(new Image("assets/grass.png"));
        updateTitle = new Thread(() -> {
            while (true){
                if (webEngine.getLocation().length() < 40){title = webEngine.getLocation();}
                else{title = webEngine.getLocation().substring(0, 40) + "...";}

                if (webEngine.getTitle() != null){
                    title += String.format(" - %s", webEngine.getTitle());
                }
                Platform.runLater(() -> this.setTitle(title));
                try {Thread.sleep(100);}
                catch (InterruptedException ignored) {}
            }
        });
        updateTitle.start();
        this.setOnCloseRequest(event -> updateTitle.stop());
    }
    public void open(){
        Scene scene = new Scene(w);
        this.setWidth(800);
        this.setHeight(600);
        this.setScene(scene);
        this.show();
    }
}
