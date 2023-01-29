package com.mcreater.amcl;

import com.mcreater.amcl.natives.ResourceGetter;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JavaFXApplication extends Application {
    static Logger logger = LogManager.getLogger(JavaFXApplication.class);

    public void start(Stage primaryStage) throws Exception {
        Launcher.start(primaryStage);
    }

    public static void startApplication(String[] args) {
        try {
            java.util.logging.LogManager.getLogManager().readConfiguration(ResourceGetter.get("logging.properties"));
        } catch (Exception e) {
            logger.warn("Failed to disable java logging", e);
        }
        launch(args);
    }
}
