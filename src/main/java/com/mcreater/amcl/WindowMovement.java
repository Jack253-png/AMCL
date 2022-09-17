package com.mcreater.amcl;

import javafx.scene.layout.Region;
import javafx.stage.Stage;
import net.querz.mca.MCAFile;
import net.querz.mca.MCAUtil;
import net.querz.nbt.io.NBTInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import net.querz.nbt.NBTUtils;

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
        MCAFile f = MCAUtil.read(new File("D:\\mods\\util\\.minecraft\\versions\\1.12.2\\saves\\新的世界\\region\\r.mca"));
        System.out.println(f);
    }
}