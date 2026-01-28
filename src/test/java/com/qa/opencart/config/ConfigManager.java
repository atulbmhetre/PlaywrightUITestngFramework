package com.qa.opencart.config;

import com.qa.opencart.listeners.ExtentReportListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

    private static Properties prop;
    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);

    static {
        loadProperties();
    }

    private static void loadProperties() {

        String env = System.getProperty("env", "dev");

        String path = System.getProperty("user.dir")
                + "/src/test/resources/config/"
                + env + ".config.properties";

        File file = new File(path);

        if (!file.exists()) {
            log.error("Environment config file not found: " + path);
            System.exit(1);
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            log.info("Config file to Load : " + file.getPath());
            prop = new Properties();
            prop.load(fis);
        } catch (IOException e) {
            log.error("Failed to load config file for env : "+ env);
            System.exit(1);
        }
    }

    public static String get(String key) {
        String value = prop.getProperty(key);
        return (value != null) ? value.trim() : null;
    }
}

