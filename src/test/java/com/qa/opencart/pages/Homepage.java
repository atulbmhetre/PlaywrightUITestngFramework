package com.qa.opencart.pages;

import com.microsoft.playwright.Page;
import com.qa.opencart.listeners.ExtentLogger;
import com.qa.opencart.listeners.ExtentReportListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Homepage {

    private Page page;
    private String field_search = "//input[@name='search']";
    private String btn_search = "//div[@id='search']//button";
    private String txt_prodHeader = "//div[@id='content']/h1";
    private String link_myAccount = "//span[text()='My Account']";
    private String link_login = "//a[text()='Login']";
    private static final Logger log = LoggerFactory.getLogger(Homepage.class);

    public Homepage(Page page){
        this.page = page;
    }

    public String getHomepageTitle(){
        log.info("Home Page title = " + page.title());
        ExtentLogger.pass("Home Page title retrieved. title = "+ page.title());
        return page.title();
    }

    public String doSearch(String productName){
        page.fill(field_search,productName);
        page.click(btn_search);
        log.info("Search product Header = "+ page.textContent(txt_prodHeader));
        ExtentLogger.pass("Search product Header retrieved. Header = "+ page.textContent(txt_prodHeader));
        return page.textContent(txt_prodHeader);
    }

    public LoginPage navigateToLoginPage(){
        page.click(link_myAccount);
        page.click(link_login);
        log.info("User navigated to login page.");
        ExtentLogger.pass("User navigated to login page.");
        return new LoginPage(page);
    }

}
