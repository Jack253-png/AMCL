package com.mcreater.amcl;

import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.awt.*;

import static com.mcreater.amcl.Launcher.barSize;
import static com.mcreater.amcl.Launcher.height;
import static com.mcreater.amcl.Launcher.width;

public class WindowMovement {
    double x1;
    double y1;
    double x_stage;
    double y_stage;
    public <V extends Region, K extends Stage> void windowMove(V listenedObject, K stage) {
        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        listenedObject.setOnMouseDragged(event -> {
            double x = this.x_stage + event.getScreenX() - this.x1;
            double y = this.y_stage + event.getScreenY() - this.y1;
            if (x >= 0 && x <= scrSize.getWidth() - width) stage.setX(x);
            if (y >= 0 && y <= scrSize.getHeight() - height) stage.setY(y);
        });
        listenedObject.setOnDragEntered(null);
        listenedObject.setOnMousePressed(event -> {
            this.x1 = event.getScreenX();
            this.y1 = event.getScreenY();
            this.x_stage = stage.getX();
            this.y_stage = stage.getY();
        });

    }
}