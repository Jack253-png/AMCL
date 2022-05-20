package com.mcreater.amcl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class Main {
    static Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        logger.info("initlaze");
        logger.info("launching core with arguments : " + Arrays.toString(args));
        HelloApplication.main(args);
    }
}
