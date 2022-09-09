package com.mcreater.amcl;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.lang.management.ManagementFactory;

public class WindowMovement {
    double x1;
    double y1;
    double x_stage;
    double y_stage;
    public void windowMove(Region var1, Stage var2) {
        var1.setOnMouseDragged(var2x -> {
            var2.setX(this.x_stage + var2x.getScreenX() - this.x1);
            var2.setY(this.y_stage + var2x.getScreenY() - this.y1);
        });
        var1.setOnDragEntered(null);
        var1.setOnMousePressed(var2x -> {
            this.x1 = var2x.getScreenX();
            this.y1 = var2x.getScreenY();
            this.x_stage = var2.getX();
            this.y_stage = var2.getY();
        });
    }
}