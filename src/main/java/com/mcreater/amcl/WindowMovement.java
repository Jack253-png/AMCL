package com.mcreater.amcl;

import com.sun.javafx.tk.Toolkit;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import net.querz.mca.MCAFile;
import net.querz.mca.MCAUtil;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;

public class WindowMovement {
    double x1;
    double y1;
    double x_stage;
    double y_stage;
    public <V extends Region, K extends Stage> void windowMove(V listenedObject, K stage) {
        listenedObject.setOnMouseDragged(event -> {
            stage.setX(this.x_stage + event.getScreenX() - this.x1);
            stage.setY(this.y_stage + event.getScreenY() - this.y1);
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