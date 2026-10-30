package com.ayush.bdd.hooks;

import com.ayush.bdd.context.TestContext;
import com.ayush.framework.core.BrowserContextManager;
import com.ayush.framework.utils.AllureAttachments;
import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Scenario lifecycle.
 * <ul>
 *   <li>UI scenarios (anything not tagged {@code @api}) get a fresh, isolated browser context.</li>
 *   <li>On failure: full-page screenshot (shown in Cucumber HTML and Allure), page URL and Playwright trace.</li>
 *   <li>API resources are always released.</li>
 * </ul>
 */
public class Hooks {

    private static final Logger LOG = LoggerFactory.getLogger(Hooks.class);

    private final TestContext context;

    public Hooks(TestContext context) {
        this.context = context;
    }

    @Before(order = 0)
    public void logStart(Scenario scenario) {
        LOG.info("▶ START  {} {}", scenario.getName(), scenario.getSourceTagNames());
    }

    @Before(value = "not @api", order = 1)
    public void startBrowser() {
        BrowserContextManager.start(null);
    }

    @After(value = "not @api", order = 1)
    public void stopBrowser(Scenario scenario) {
        boolean failed = scenario.isFailed();
        if (failed && BrowserContextManager.context() != null) {
            try {
                Page page = BrowserContextManager.page();
                byte[] png = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                scenario.attach(png, "image/png", "Screenshot on failure");
                scenario.log("Failed on URL: " + page.url());
            } catch (Exception e) {
                LOG.warn("Could not capture failure evidence", e);
            }
        }
        Path trace = BrowserContextManager.stop(failed ? traceName(scenario) : null);
        if (failed) {
            AllureAttachments.trace(trace);
        }
    }

    @After(order = 0)
    public void cleanUpAndLog(Scenario scenario) {
        context.disposeApi();
        LOG.info("{} {} -> {}", scenario.isFailed() ? "✘ FAIL  " : "✔ PASS  ", scenario.getName(), scenario.getStatus());
    }

    private static String traceName(Scenario scenario) {
        return scenario.getName().replaceAll("[^a-zA-Z0-9]+", "_") + "_" + System.currentTimeMillis();
    }
}
