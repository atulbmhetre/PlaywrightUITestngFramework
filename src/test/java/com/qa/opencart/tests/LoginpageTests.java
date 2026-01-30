package com.qa.opencart.tests;

import com.qa.opencart.base.BaseClass;
import com.qa.opencart.config.ConfigManager;
import com.qa.opencart.constants.AppConstants;
import com.qa.opencart.listeners.ExtentLogger;
import com.qa.opencart.pages.LoginPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LoginpageTests extends BaseClass {

    LoginPage loginPage;

    @BeforeClass(alwaysRun = true)
    public void navigateToLoginPage(){
        loginPage = homepage.navigateToLoginPage();
    }

    @Test(groups = {"smoke"})
    public void verifyLoginForValidUser(){
        loginPage.doLogin(ConfigManager.get("username"), ConfigManager.get("password"));
        String actualTitle = loginPage.getLoginPageTitle();
        Assert.assertEquals(actualTitle, AppConstants.LOGIN_PAGE_TITLE);
        //ExtentLogger.pass("Verification of Login with valid user is suucessful.");
    }
}
