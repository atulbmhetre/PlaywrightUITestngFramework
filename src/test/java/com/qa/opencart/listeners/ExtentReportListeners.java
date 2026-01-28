package com.qa.opencart.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.microsoft.playwright.Page;
import com.qa.opencart.base.BaseClass;
import com.qa.opencart.playwrightfactory.PlaywrightFactory;
import com.qa.opencart.utilities.RetryAnalyzer;
import com.qa.opencart.utilities.ScreenCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ExtentReportListeners implements ISuiteListener, ITestListener {

    private static final String OUTPUT_FOLDER = System.getProperty("user.dir") + "/test-output/Test-ExtentReport/";
    private static final String FILE_NAME =
            "TestExecutionReport_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                    .format(new Date()) + ".html";
    private static ExtentReports extent = init();
    public static ThreadLocal<ExtentTest> test = new ThreadLocal<ExtentTest>();
    private static ExtentReports extentReports;
    private static final Map<String, ExtentTest> extentTestMap = new ConcurrentHashMap<>();
    private final Logger log = LoggerFactory.getLogger(ExtentReportListeners.class);


    private static ExtentReports init() {

        Path path = Paths.get(OUTPUT_FOLDER);
        // if directory exists?
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                // fail to create directory
                e.printStackTrace();
            }
        }

        extentReports = new ExtentReports();
        ExtentSparkReporter reporter = new ExtentSparkReporter(OUTPUT_FOLDER + FILE_NAME);
        reporter.config().setReportName("Open Cart Automation Test Results");
        extentReports.attachReporter(reporter);
        extentReports.setSystemInfo("System", "Windows");
        extentReports.setSystemInfo("Author", "SamTech AutomationLabs");
        extentReports.setSystemInfo("Build#", "1.1");
        extentReports.setSystemInfo("Team", "QA");
        extentReports.setSystemInfo("Customer Name", "N/A");

        //extentReports.setSystemInfo("ENV NAME", System.getProperty("env"));

        return extentReports;
    }

    @Override
    public synchronized void onStart(ISuite iSuite) {
        log.info("Test Suite "+ iSuite.getName() +" started");
        String env = System.getProperty("env","dev");
        if (env == null || env.isBlank() || env.isEmpty()) {
            env = "dev";
        }
        extent.setSystemInfo("Environment", env);

    }

    @Override
    public synchronized void onFinish(ISuite iSuite) {
        log.info(("Test Suite "+ iSuite.getName() +" ended"));
        extent.flush();
        test.remove();
    }

    @Override
    public synchronized void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String uniqueTestName = result.getMethod().getQualifiedName();

        log.info("Test started: {}", methodName);

        // If test already exists (retry case), reuse it
        if (extentTestMap.containsKey(uniqueTestName)) {
            test.set(extentTestMap.get(uniqueTestName));
            return;
        }

        String qualifiedName = result.getMethod().getQualifiedName();
        int last = qualifiedName.lastIndexOf(".");
        int mid = qualifiedName.substring(0, last).lastIndexOf(".");
        String className = qualifiedName.substring(mid + 1, last);
        String browser = BaseClass.getBrowserName();

        ExtentTest extentTest = extent.createTest(
                methodName,
                result.getMethod().getDescription());

        extentTest.assignCategory(result.getTestContext().getSuite().getName());
        extentTest.assignCategory(className);
        extentTest.assignCategory(browser);

        extentTestMap.put(uniqueTestName, extentTest);
        test.set(extentTest);

        test.get().getModel().setStartTime(getTime(result.getStartMillis()));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("Test Passed: {}", result.getMethod().getMethodName());
        MediaEntityBuilder media = captureScreenshot();
        if (media != null) {
            test.get().pass("Test Passed", media.build());
        }
        test.get().getModel().setEndTime(getTime(result.getEndMillis()));
    }

    @Override
    public synchronized void onTestFailure(ITestResult result) {
        // If this failure will be retried → do nothing
        if (result.getStatus() == ITestResult.SKIP) {
            return;
        }

        log.error("Test Failed: {}", result.getMethod().getMethodName());

        MediaEntityBuilder media = captureScreenshot();
        if (media != null) {
            test.get().fail(result.getThrowable(), media.build());
        } else {
            test.get().fail(result.getThrowable());
        }

        test.get().getModel().setEndTime(getTime(result.getEndMillis()));
    }

    @Override
    public synchronized void onTestSkipped(ITestResult result) {
        // Skip due to retry → ignore completely
        if (result.getThrowable() != null &&
                result.getThrowable().getMessage() != null &&
                result.getThrowable().getMessage().contains("retry")) {
            return;
        }

        log.info("Test Skipped: {}", result.getMethod().getMethodName());

        MediaEntityBuilder media = captureScreenshot();
        if (media != null) {
            test.get().skip(result.getThrowable(), media.build());
        } else {
            test.get().skip(result.getThrowable());
        }

        test.get().getModel().setEndTime(getTime(result.getEndMillis()));
    }

    @Override
    public synchronized void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log.info(("onTestFailedButWithinSuccessPercentage for " + result.getMethod().getMethodName()));
    }
    @Override
    public void onFinish(ITestContext context) {

        removeDuplicateResults(context.getFailedTests());
        removeDuplicateResults(context.getSkippedTests());
    }

    private void removeDuplicateResults(IResultMap resultMap) {

        Set<ITestResult> allResults = resultMap.getAllResults();
        Map<String, ITestResult> latestResultMap = new HashMap<>();

        // Keep only the latest result for each test method
        for (ITestResult result : allResults) {
            String methodName = result.getMethod().getQualifiedName();

            if (!latestResultMap.containsKey(methodName)) {
                latestResultMap.put(methodName, result);
            } else {
                ITestResult existing = latestResultMap.get(methodName);
                if (result.getEndMillis() > existing.getEndMillis()) {
                    latestResultMap.put(methodName, result);
                }
            }
        }

        // Clear all
        allResults.clear();

        // Add back only latest results
        allResults.addAll(latestResultMap.values());
    }

    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }
    private MediaEntityBuilder captureScreenshot() {

        Page page = PlaywrightFactory.getPage();
        String base64 = ScreenCapture.capture(page);

        if (base64 != null) {
            return MediaEntityBuilder
                    .createScreenCaptureFromBase64String(base64);
        }

        return null;
    }

}
