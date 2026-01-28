package com.qa.opencart.playwrightfactory;

import com.microsoft.playwright.*;
import com.qa.opencart.config.ConfigManager;
import com.qa.opencart.listeners.ExtentReportListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(ExtentReportListeners.class);

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


    public Browser initBrowser(String browserName) {
        log.info("Launching browser "+browserName+" is initiated.");
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
        log.info(browserName + " is launched successfully.");
        return tlBrowser.get();
    }
    public Page initContext(){
        tlBrowserContext.set(tlBrowser.get().newContext());
        tlPage.set(tlBrowserContext.get().newPage());
        return getPage();
    }


}
