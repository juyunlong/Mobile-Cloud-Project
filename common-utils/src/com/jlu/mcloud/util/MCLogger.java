package com.jlu.mcloud.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by koko on 17-3-19.
 */
public class MCLogger {

    private static Logger logger = Logger.getLogger("com.jlu.mcloud.util.MCLogger");


    public MCLogger() {
    }

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void log(String msg) {
        logger.log(Level.ALL, msg);
    }

    public static void warning(String msg) {
        logger.warning(msg);
    }
}