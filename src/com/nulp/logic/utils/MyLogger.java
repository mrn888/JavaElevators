package com.nulp.logic.utils;

import java.io.FileInputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MyLogger {
    private static Logger LOGGER;
    private static String LOG_CONF_PATH = "log.config";

    static {
        try(FileInputStream ins = new FileInputStream(LOG_CONF_PATH)){
            LogManager.getLogManager().readConfiguration(ins);
            LOGGER = Logger.getLogger(MyLogger.class.getName());
        }catch (Exception ignore){
            ignore.printStackTrace();
        }
    }

    /**
     * Returns a single instance of java.utils.Logger,
     * created in static initializer
     *
     * @return java.utils.Logger.
     */
    public static Logger getLOGGER() {
        return LOGGER;
    }
}
