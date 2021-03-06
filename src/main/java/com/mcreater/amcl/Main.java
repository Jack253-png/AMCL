package com.mcreater.amcl;

import com.sun.glass.utils.NativeLibLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class Main{
    static String[] args;
    static {
        File f = new File("AMCL/logs/log.log");
        f.delete();
    }
    static Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ParserConfigurationException, IOException, InterruptedException, ClassNotFoundException, SAXException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
        start();
    }
    public static void start() {
        try {
            args = new String[]{};
            logger.info("initialize");
            logger.info("launching core with arguments : " + Arrays.toString(args));
            NativeLibLoader.loadLibrary("prism_d3d");
            NativeLibLoader.loadLibrary("prism_sw");
            Launcher.startApplication(args, System.getProperty("os.name", "Unknow").contains("Windows"));
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
