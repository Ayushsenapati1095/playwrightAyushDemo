package com.ayush.framework.core;

import com.ayush.framework.config.ConfigManager;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Thread-safe owner of Playwright and Browser instances.
 * <p>
 * Playwright Java objects are NOT thread-safe, so every test thread gets its own
 * {@link Playwright} and {@link Browser}. They are created lazily on first use and
 * reused for every test that runs on that thread (launching a browser is the
 * expensive part). Each test still gets a fresh, isolated BrowserContext via
 * {@link BrowserContextManager}.
 * <p>
 * All instances are tracked so {@link #closeAll()} can shut them down once at the
 * end of the suite (called by {@code SuiteListener}).
 */
public final class PlaywrightManager {

    private static final Logger LOG = LoggerFactory.getLogger(PlaywrightManager.class);

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final Queue<Playwright> ALL_INSTANCES = new ConcurrentLinkedQueue<>();

    private PlaywrightManager() {
    }

    public static Playwright playwright() {
        Playwright pw = PLAYWRIGHT.get();
        if (pw == null) {
            pw = Playwright.create();
            PLAYWRIGHT.set(pw);
            ALL_INSTANCES.add(pw);
            LOG.debug("Created Playwright instance for thread {}", Thread.currentThread().getName());
        }
        return pw;
    }

    public static Browser browser() {
        Browser browser = BROWSER.get();
        if (browser == null || !browser.isConnected()) {
            browser = launch(ConfigManager.browser());
            BROWSER.set(browser);
        }
        return browser;
    }

    private static Browser launch(String name) {
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(ConfigManager.headless())
                .setSlowMo(ConfigManager.slowMo());

        Playwright pw = playwright();
        LOG.info("Launching '{}' (headless={}) on thread {}", name, ConfigManager.headless(),
                Thread.currentThread().getName());

        return switch (name) {
            case "chromium" -> pw.chromium().launch(options);
            // Branded browsers use the chromium engine with a channel
            case "chrome" -> pw.chromium().launch(options.setChannel("chrome"));
            case "msedge", "edge" -> pw.chromium().launch(options.setChannel("msedge"));
            case "firefox" -> pw.firefox().launch(options);
            case "webkit", "safari" -> pw.webkit().launch(options);
            default -> throw new IllegalArgumentException("Unsupported browser: " + name
                    + ". Use chromium, chrome, msedge, firefox or webkit.");
        };
    }

    /** Closes every Playwright instance created during the run. Safe to call more than once. */
    public static void closeAll() {
        Playwright pw;
        while ((pw = ALL_INSTANCES.poll()) != null) {
            try {
                pw.close();
            } catch (Exception e) {
                LOG.warn("Error while closing Playwright instance", e);
            }
        }
        PLAYWRIGHT.remove();
        BROWSER.remove();
    }
}
