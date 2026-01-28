package com.qa.opencart.base;

import com.microsoft.playwright.Page;
import com.qa.opencart.config.ConfigManager;
import com.qa.opencart.pages.Homepage;
import com.qa.opencart.playwrightfactory.PlaywrightFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.util.Properties;

public class BaseClass {

    PlaywrightFactory pf;
    Page page;
    protected Homepage homepage;
    public static ThreadLocal<String> tlBrowserName = new ThreadLocal<>();

    @Parameters({"browser"})
    @BeforeClass(alwaysRun = true)
    public void setUp(@Optional String browserFromXML) throws IOException {
        pf = new PlaywrightFactory();
        String browserName = null;
        if (browserFromXML != null && !browserFromXML.isBlank()) {
            browserName = browserFromXML;
        }
        else if (System.getProperty("browser") != null && !System.getProperty("browser").isBlank()) {
            browserName = System.getProperty("browser");
        }
        else {
            browserName = ConfigManager.get("browser");
        }

        System.out.println("DEBUG: Final Browser decided: " + browserName);

        tlBrowserName.set(browserName);
        page = pf.intiBrowser(browserName);
        homepage = new Homepage(page);
    }

    @AfterClass
    public void tearDown(){
        //page.context().browser().close();
        PlaywrightFactory.getBrowser().close();
        PlaywrightFactory.getPlaywright().close();
    }

    public static String getBrowserName(){
        return tlBrowserName.get();
    }
}
