package com.qa.opencart.listeners;

import com.aventstack.extentreports.Status;

public class ExtentLogger {

    public static void pass(String message) {
        ExtentReportListeners.getCurrentTest().log(Status.PASS, message);
    }

    public static void fail(String message) {
        ExtentReportListeners.getCurrentTest().log(Status.FAIL, message);
    }

    public static void info(String message) {
        ExtentReportListeners.getCurrentTest().log(Status.INFO, message);
    }
}
