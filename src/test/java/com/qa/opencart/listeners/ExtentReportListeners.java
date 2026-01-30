package com.qa.opencart.listeners;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.microsoft.playwright.Page;
import com.qa.opencart.base.BaseClass;
import com.qa.opencart.utilities.ScreenCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class ExtentReportListeners implements ITestListener, ISuiteListener {

    private static final Logger log = LoggerFactory.getLogger(ExtentReportListeners.class);

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> tlStepNode = new ThreadLocal<>();
    private static Map<String, ExtentTest> parentMap = new HashMap<>();

    public static ExtentTest getCurrentTest() {
        return tlStepNode.get();
    }

    // =================== SUITE ====================
    @Override
    public void onStart(ISuite suite) {
        log.info("=================================================");
        log.info("SUITE STARTED  : {}", suite.getName());
        log.info("Parallel Mode  : {}", suite.getXmlSuite().getParallel());
        log.info("Thread Count   : {}", suite.getXmlSuite().getThreadCount());
        log.info("=================================================");
    }

    @Override
    public void onFinish(ISuite suite) {
        int passed = suite.getResults().values().stream()
                .mapToInt(r -> r.getTestContext().getPassedTests().size()).sum();
        int failed = suite.getResults().values().stream()
                .mapToInt(r -> r.getTestContext().getFailedTests().size()).sum();
        int skipped = suite.getResults().values().stream()
                .mapToInt(r -> r.getTestContext().getSkippedTests().size()).sum();
        int duration = suite.getResults().values().stream()
                .mapToInt(r -> (int)(r.getTestContext().getEndDate().getTime() - r.getTestContext().getStartDate().getTime()) / 1000).sum();

        log.info("=================================================");
        log.info("SUITE FINISHED : {}", suite.getName());
        log.info("Total Passed   : {}", passed);
        log.info("Total Failed   : {}", failed);
        log.info("Total Skipped  : {}", skipped);
        log.info("Total Duration : {} seconds", duration);
        log.info("=================================================");
    }

    // =================== TEST CONTEXT ====================
    @Override
    public void onStart(ITestContext context) {
        if (extent == null) {
            String browser = BaseClass.getBrowserName();
            if (browser == null || browser.isBlank()) browser = "default";

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportName = "Test_Extent_Report_" + browser + "_" + timestamp + ".html";
            String path = System.getProperty("user.dir") + "/test-output/" + reportName;

            ExtentSparkReporter spark = new ExtentSparkReporter(path);
            spark.config().setReportName("Playwright UI Automation");
            spark.config().setDocumentTitle("Automation Report");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Browser", browser);
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("User", System.getProperty("user.name"));
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
        tlStepNode.remove();
        parentMap.clear();
    }

    // =================== TEST METHODS ====================
    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        ExtentTest parent;

        // ✅ Only one parent per @Test method
        if (!parentMap.containsKey(methodName)) {
            parent = extent.createTest(methodName);
            parentMap.put(methodName, parent);
        } else {
            parent = parentMap.get(methodName);
        }

        // ✅ Parameter node
        String paramText = (result.getParameters().length > 0)
                ? Arrays.toString(result.getParameters())
                : null;

        ExtentTest node = (paramText != null)
                ? parent.createNode("Parameters: " + paramText)
                : parent;

        tlStepNode.set(node); // Important: Step node for ExtentLogger

        log.info("Test Started: {}", methodName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        attachScreenshot();
        tlStepNode.get().pass("Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        attachScreenshot();
        tlStepNode.get().fail(result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        attachScreenshot();
        tlStepNode.get().skip("Test Skipped");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) { }

    // =================== Utilities ====================
    private void attachScreenshot() {
        Page page = com.qa.opencart.playwrightfactory.PlaywrightFactory.getPage();
        String base64 = ScreenCapture.capture(page);
        if (base64 != null) {
            tlStepNode.get().addScreenCaptureFromBase64String(base64);
        }
    }
}
