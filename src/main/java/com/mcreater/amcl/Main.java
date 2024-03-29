package com.mcreater.amcl;

import com.mcreater.amcl.util.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.Arrays;

import static com.mcreater.amcl.util.FileUtils.PathUtil.buildPath;

public class Main {
    static String[] args;
    static {
        FileUtils.OperateUtil.deleteFile(buildPath("AMCL/logs/log.log"));
    }
    public static Logger logger = LogManager.getLogger(Main.class);
    public static void start(String[] args) {
        try {
            logger.info("initialize");
            logger.info("launching core with arguments : " + Arrays.toString(args));
            JavaFXApplication.startApplication(args);
        }
        catch (Throwable e) {
            logger.error("Error while launcher running", e);
            StableMain.splashScreen.setVisible(false);
            JOptionPane.showMessageDialog(null, "If you want to see the infomation, please visit the log file.", "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }
}
