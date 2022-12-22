package com.mcreater.amcl;

import com.mcreater.amcl.util.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.Arrays;

public class Main {
    static String[] args;
    static {
        FileUtils.OperateUtil.deleteFile("AMCL/logs/log.log");
    }
    public static Logger logger = LogManager.getLogger(Main.class);
    public static void start() {
        try {
            args = new String[]{};
            logger.info("initialize");
            logger.info("launching core with arguments : " + Arrays.toString(args));
            JavaFXApplication.startApplication(args);
        }
        catch (Exception e) {
            logger.error("Error while launcher running", e);
            StableMain.splashScreen.setVisible(false);
            JOptionPane.showMessageDialog(null, "If tou want to see the infomation, please visit the log file.", "Exception", JOptionPane.ERROR_MESSAGE);
        }
        catch (Error e){
            logger.error("Error while loading native libs", e);
            StableMain.splashScreen.setVisible(false);
            JOptionPane.showMessageDialog(null, "If tou want to see the infomation, please visit the log file.", "Native Lib Exception", JOptionPane.ERROR_MESSAGE);
        }
    }
}
