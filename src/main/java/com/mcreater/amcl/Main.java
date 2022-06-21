package com.mcreater.amcl;

import com.mcreater.amcl.redirect.log4jOut;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JOptionPane;
import java.io.File;
import java.util.Arrays;
public class Main{
    static String[] args;
    static {
        File f = new File("AMCL/logs/log.log");
        f.delete();
    }
    static Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) throws Exception {
        Main.args = args;
        new Main().start();
    }

    public void start() throws Exception {
        try {
            log4jOut.redirect();
            logger.info("initialize");
            logger.info("launching core with arguments : " + Arrays.toString(args));
            Application.startApplication(args, System.getProperty("os.name").contains("Windows"));
        }
        catch (Exception e) {
            logger.error("Error while launcher running", e);
            JOptionPane.showMessageDialog(null, "If tou want to see the infomation, please visit the log file.", "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }
}
