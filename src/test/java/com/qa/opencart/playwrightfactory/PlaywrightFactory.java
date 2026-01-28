package com.qa.opencart.playwrightfactory;

import com.microsoft.playwright.*;
import com.qa.opencart.config.ConfigManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;

public class PlaywrightFactory {

    private static ThreadLocal<Playwright> tlPlaywright = new ThreadLocal<>();
    private static ThreadLocal<BrowserType> tlBrowserType = new ThreadLocal<>();
    private static ThreadLocal<Browser> tlBrowser = new ThreadLocal<>();
    private static ThreadLocal<BrowserContext> tlBrowserContext = new ThreadLocal<>();
    private static ThreadLocal<Page> tlPage = new ThreadLocal<>();

    public static Playwright getPlaywright() {
        return tlPlaywright.get();
    }

    public static Browser getBrowser() {
        return tlBrowser.get();
    }

    public static BrowserContext getBrowserContext() {
        return tlBrowserContext.get();
    }

    public static Page getPage() {
        return tlPage.get();
    }


    public Page intiBrowser(String browserName){
        tlPlaywright.set(Playwright.create());

        String name = browserName.toLowerCase().trim();
        switch (name) {
            case "chrome":     // Added this
            case "chromium":
                tlBrowserType.set(tlPlaywright.get().chromium());
                break;
            case "firefox":
                tlBrowserType.set(tlPlaywright.get().firefox());
                break;
            case "safari":
            case "webkit":     // Added this for completeness
                tlBrowserType.set(tlPlaywright.get().webkit());
                break;
            default:
                throw new IllegalArgumentException("Browser type not supported: " + name);
        }
        tlBrowser.set(tlBrowserType.get().launch(new BrowserType.LaunchOptions().setHeadless(Boolean.parseBoolean(ConfigManager.get("isHeadless")))));
        tlBrowserContext.set(tlBrowser.get().newContext());
        tlPage.set(tlBrowserContext.get().newPage());
        tlPage.get().navigate(ConfigManager.get("url"));

        return getPage();
    }

//    public Properties intiProperties() throws IOException {
//        String env = System.getProperty("env", "dev");
//
//        String fileName = "config/" + env + ".config.properties";
//
//        InputStream is = getClass()
//                .getClassLoader()
//                .getResourceAsStream(fileName);
//
//        if (is == null) {
//            throw new IllegalStateException("Environment config file not found: " + fileName);
//        }
//
//        Properties prop = new Properties();
//        prop.load(is);
//        return prop;
//
//
//    }


}
