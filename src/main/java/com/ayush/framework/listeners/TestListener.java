package com.ayush.framework.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

/** Logs a clear start/finish line per test, which makes parallel CI logs readable. */
public class TestListener implements ITestListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestListener.class);

    private static String name(ITestResult r) {
        return r.getTestClass().getRealClass().getSimpleName() + "." + r.getMethod().getMethodName();
    }

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("▶ START  {}", name(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("✔ PASS   {} ({} ms)", name(result), result.getEndMillis() - result.getStartMillis());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("✘ FAIL   {} -> {}", name(result),
                result.getThrowable() == null ? "" : result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("↷ SKIP   {}", name(result));
    }
}
