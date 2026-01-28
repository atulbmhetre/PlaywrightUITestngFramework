package com.qa.opencart.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.qa.opencart.config.ConfigManager;
import com.qa.opencart.pages.Homepage;
import com.qa.opencart.playwrightfactory.PlaywrightFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.io.IOException;

public class BaseClass {

    static PlaywrightFactory pf;
    Page page;
    protected Homepage homepage;
    public static ThreadLocal<String> tlBrowserName = new ThreadLocal<>();
    private static final Logger log = LoggerFactory.getLogger(BaseClass.class);

    @Parameters({"browser"})
    @BeforeSuite(alwaysRun = true)
    public void startBrowser(@Optional String browserFromXML) throws IOException {
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

        tlBrowserName.set(browserName);
        pf.initBrowser(browserName);
    }

    @BeforeClass(alwaysRun = true)
    public void startSesson(){
        page = pf.initContext();
        page.navigate(ConfigManager.get("url"));
        log.info("Navigated to url : " + ConfigManager.get("url"));
        homepage = new Homepage(page);
    }

    @AfterClass(alwaysRun = true)
    public void closeSession(){
        page.context().close();
    }
    @AfterSuite(alwaysRun = true)
    public void tearDown(){
        //page.context().browser().close();
        PlaywrightFactory.getBrowser().close();
        PlaywrightFactory.getPlaywright().close();
        log.info("Browser is closed successful.");
    }

    public static String getBrowserName(){
        return tlBrowserName.get();
    }
}
