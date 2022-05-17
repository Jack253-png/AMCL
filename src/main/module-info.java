module com.mcreater.amcl {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.xml;
    requires java.desktop;
    requires com.jfoenix;
    requires com.google.gson;

    opens com.mcreater.amcl to javafx.fxml;
    exports com.mcreater.amcl;
    exports com.mcreater.amcl.config;
    exports com.mcreater.amcl.pages;
    exports com.mcreater.amcl.util;
}