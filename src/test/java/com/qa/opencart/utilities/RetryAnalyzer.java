package com.qa.opencart.utilities;

import com.qa.opencart.listeners.ExtentReportListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private int count = 0;
    private static final int maxTry = 1;
    private final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);

    @Override
    public boolean retry(ITestResult result) {
        if (count < maxTry) {
            count++;

            log.info("Retrying test: {} | Attempt: {}",
                    result.getMethod().getMethodName(), count);

            result.setAttribute("retry", true);
            return true;
        }

        return false;
    }

}
