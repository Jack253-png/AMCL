package com.mcreater.amcl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Objects;

public class Main {
    static Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        logger.info("initialize");
        logger.info("launching core with arguments : " + Arrays.toString(args));
        HelloApplication.main(args, Objects.equals(System.getProperty("os.name"), "Windows 10"));
    }
}
