package com.mcreater.amcl;

import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.redirect.log4jOut;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
public class Main {
    static {
        File f = new File("AMCL/logs/log.log");
        f.delete();
    }
    static Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) throws IOException {
        try {
            log4jOut.redirect();
            logger.info("initialize");
            logger.info("launching core with arguments : " + Arrays.toString(args));
            HelloApplication.startApplication(args, true);
        }
        catch (Exception e){
            logger.error("Error while launcher running", e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);
            e.printStackTrace(pw);
            String errorMessage = sw.getBuffer().toString();
            JFrame frame = new JFrame();
            InputStream st = new ResourceGetter().get("assets/grass.png");
            BufferedImage i = ImageIO.read(st);
            frame.setIconImage(i);
            frame.setTitle("Error");
            frame.setVisible(true);
            JTextArea area = new JTextArea();
            area.setText(errorMessage);
            area.setEditable(false);
            area.setTabSize(4);
            frame.add(area);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(400, 600);
            frame.setResizable(false);
        }
    }
}
