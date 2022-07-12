package com.mcreater.amcl;

import com.sun.glass.utils.NativeLibLoader;
import com.sun.jna.platform.win32.User32;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;
public class Main{
    static String[] args;
    static {
        File f = new File("AMCL/logs/log.log");
        f.delete();
    }
    static Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        Main.args = args;
        new Main().start();
    }
    public void start() {
        try {
            logger.info("initialize");
            logger.info("launching core with arguments : " + Arrays.toString(args));
            NativeLibLoader.loadLibrary("prism_d3d");
            NativeLibLoader.loadLibrary("prism_sw");
            Application.startApplication(args, System.getProperty("os.name", "Unknow").contains("Windows"));
        }
        catch (Exception e) {
            logger.error("Error while launcher running", e);
            JOptionPane.showMessageDialog(null, "If tou want to see the infomation, please visit the log file.", "Exception", JOptionPane.ERROR_MESSAGE);
        }
        catch (Error e){
            logger.error("Error while loading native libs", e);
            JOptionPane.showMessageDialog(null, "If tou want to see the infomation, please visit the log file.", "Native Lib Exception", JOptionPane.ERROR_MESSAGE);
        }
    }
}
