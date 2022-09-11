package com.mcreater.amcl;

import javafx.scene.layout.Region;
import javafx.stage.Stage;
import net.querz.mca.MCAFile;
import net.querz.mca.MCAUtil;

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

    public static void main(String[] args) throws Throwable {
        MCAFile file = MCAUtil.read("C:\\Users\\Administrator\\Desktop\\r.0.0.mcr");
        System.out.println(file.getChunk(0, 1).data);
    }
}