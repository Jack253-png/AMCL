package com.mcreater.amcl;

import javafx.application.Application;
import javafx.stage.Stage;

public class JavaFXApplication extends Application {
    public void start(Stage primaryStage) throws Exception {
        Launcher.start(primaryStage);
    }
    public static void startApplication(String[] args) {
        launch(args);
    }
}
