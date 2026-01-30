package com.qa.opencart.tests;

import com.qa.opencart.base.BaseClass;
import com.qa.opencart.constants.AppConstants;
import com.qa.opencart.listeners.ExtentLogger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class HomepageTests extends BaseClass {




    @Test(groups = {"smoke"})
    public void validateHomePageTitle(){
        String actualTitle = homepage.getHomepageTitle();
        Assert.assertEquals(actualTitle, AppConstants.HOME_PAGE_TITLE);
        //ExtentLogger.pass("Verification of Home page title is successful.");
    }

    @DataProvider
    public Object[][] provideProductData(){
        return new Object[][]{
                {"Macbook"},
                {"imac"},
                {"samsung"}
        };
    }

    @Test (dataProvider = "provideProductData", groups = {"regression"})
    public void validateSearchProductFromHomePage(String productName){
        String actualHeader = homepage.doSearch(productName);
        Assert.assertEquals(actualHeader, "Search - "+productName);
        //ExtentLogger.pass("Verification of Searching product From Homepage is successful.");
    }


}
