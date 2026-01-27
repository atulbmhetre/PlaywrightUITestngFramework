package com.qa.opencart.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

    private static Properties prop;

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
            System.err.println("Environment config file not found: " + path);
            System.exit(1);
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            prop = new Properties();
            prop.load(fis);
        } catch (IOException e) {
            System.err.println("Failed to load config file.");
            System.exit(1);
        }
    }

    public static String get(String key) {
        return prop.getProperty(key);
    }
}

