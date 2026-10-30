package com.ayush.framework.listeners;

import com.ayush.framework.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test up to {@code retry.count} times (0 by default, set it to 1 in CI:
 * {@code -Dretry.count=1}). Every retry is logged so flaky tests stay visible instead of being hidden.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger(RetryAnalyzer.class);
    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        int max = ConfigManager.retryCount();
        if (attempt < max) {
            attempt++;
            LOG.warn("FLAKY? Retrying {} (attempt {}/{}) after: {}", result.getName(), attempt, max,
                    result.getThrowable() == null ? "unknown" : result.getThrowable().getMessage());
            return true;
        }
        return false;
    }
}
