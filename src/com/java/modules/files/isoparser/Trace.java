package com.java.modules.files.isoparser;

import org.apache.logging.log4j.LogManager;

import com.java.modules.files.isoparser.configuration.AppConfig;

public class Trace {
    public static AppConfig cfg = null;

    public static void log(String who, String msg) {
        if (cfg == null)
            cfg = AppConfig.get();

        if (cfg != null && !cfg.trace)
            return;

        System.out.println(String.format("[%s]: %s", who, msg));
        LogManager.getLogger().debug(String.format("[%s]: %s", who, msg));
    };

    public static void error(String who, String msg) {
        System.err.println(String.format("[error] [%s]: %s", who, msg));
        LogManager.getLogger().error(String.format("[%s]: %s", who, msg));
    }
}
