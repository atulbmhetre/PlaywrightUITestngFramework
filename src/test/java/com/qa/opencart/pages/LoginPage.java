package com.qa.opencart.pages;

import com.microsoft.playwright.Page;
import com.qa.opencart.listeners.ExtentLogger;
import com.qa.opencart.listeners.ExtentManager;
import com.qa.opencart.listeners.ExtentReportListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPage {
    Page page;
    private String field_email = "//input[@name='email']";
    private String field_pwd = "//input[@name='password']";
    private String btn_login = "//input[@value='Login']";
    private static final Logger log = LoggerFactory.getLogger(LoginPage.class);

    public LoginPage(Page page){
        this.page = page;
    }

    public void doLogin(String userName, String password){
        page.fill(field_email,userName);
        page.fill(field_pwd,password);
        log.info("User Creds Entered = {} : {}",  userName, password);
        page.click(btn_login);
        log.info("Login action performed.");
        ExtentLogger.pass("User performed login execution.");
    }

    public String getLoginPageTitle(){
        log.info("Login Page title = " + page.title());
        ExtentLogger.pass("Login Page title retrieved. title = "+ page.title());
        return page.title();
    }

}
