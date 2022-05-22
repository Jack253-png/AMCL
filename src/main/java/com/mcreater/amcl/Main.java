package com.mcreater.amcl;

import com.mcreater.amcl.pages.FastInfomation;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Objects;

public class Main {
    static Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        logger.info("initlaze");
        logger.info("launching core with arguments : " + Arrays.toString(args));
        if (Objects.equals(System.getProperty("os.name"), "Windows 10")) {
            HelloApplication.main(args);
        }
        else{
            FastInfomation.create("System Version Checker", "Please Use Windows 10", "Launcher Will Exit", Alert.AlertType.WARNING);
        }
    }
}
