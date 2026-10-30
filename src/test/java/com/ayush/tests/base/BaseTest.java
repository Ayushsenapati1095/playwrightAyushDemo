package com.ayush.tests.base;

import com.ayush.framework.core.BrowserContextManager;
import com.ayush.framework.utils.AllureAttachments;
import com.microsoft.playwright.Page;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.nio.file.Path;

/**
 * Base class for UI tests.
 * <p>
 * Each test method gets a brand-new BrowserContext (clean cookies, storage and cache),
 * so tests are independent and can run in parallel. On failure the screenshot, URL
 * and Playwright trace are attached to the Allure report and kept under target/.
 */
public abstract class BaseTest {

    /** Override to start tests already logged in, e.g. return Paths.get(".auth/customer.json"). */
    protected Path storageState() {
        return null;
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        BrowserContextManager.start(storageState());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        boolean failed = result.getStatus() == ITestResult.FAILURE
                || (result.getStatus() == ITestResult.SKIP && result.getThrowable() != null);
        if (failed && BrowserContextManager.context() != null) {
            Page page = BrowserContextManager.page();
            AllureAttachments.screenshot(page, "Screenshot on failure");
            AllureAttachments.pageUrl(page);
        }
        String traceName = failed
                ? result.getTestClass().getRealClass().getSimpleName() + "_" + result.getMethod().getMethodName()
                  + "_" + System.currentTimeMillis()
                : null;
        Path trace = BrowserContextManager.stop(traceName);
        if (failed) {
            AllureAttachments.trace(trace);
        }
    }

    /** The page for the current test thread. */
    protected Page page() {
        return BrowserContextManager.page();
    }
}
