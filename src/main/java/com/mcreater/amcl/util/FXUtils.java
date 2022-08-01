package com.mcreater.amcl.util;

import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;

public class FXUtils {
    public static class ControlSize {
        public static void set(Region n, double width, double height){
            n.setMinSize(width, height);
            n.setMaxSize(width, height);
        }
        public static void setAll(double width, double height, Region... n){
            for (Region n1 : n){
                n1.setMinSize(width, height);
                n1.setMaxSize(width, height);
            }
        }
        public static void setWidth(Region n, double width){
            n.setMaxWidth(width);
            n.setMinWidth(width);
        }
        public static void setHeight(Region n, double height){
            n.setMaxHeight(height);
            n.setMinHeight(height);
        }
        public static SplitPane setSplit(SplitPane s, double width){
            s.setMaxWidth(width);
            s.setMinWidth(width);
            return s;
        }
    }
}
