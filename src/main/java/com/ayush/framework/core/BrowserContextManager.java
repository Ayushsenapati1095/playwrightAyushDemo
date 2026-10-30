package com.ayush.framework.core;

import com.ayush.framework.config.ConfigManager;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Creates one isolated BrowserContext + Page per test, per thread.
 * Handles tracing and (optionally) video so failures can be diagnosed from CI artifacts.
 */
public final class BrowserContextManager {

    private static final Logger LOG = LoggerFactory.getLogger(BrowserContextManager.class);
    public static final Path TRACE_DIR = Paths.get("target", "traces");
    public static final Path VIDEO_DIR = Paths.get("target", "videos");

    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private BrowserContextManager() {
    }

    /**
     * Starts a fresh context and page for the current test.
     *
     * @param storageStatePath optional path to a saved login state (cookies + local storage); may be null
     */
    public static Page start(Path storageStatePath) {
        Browser browser = PlaywrightManager.browser();

        Browser.NewContextOptions options = new Browser.NewContextOptions()
                .setBaseURL(ConfigManager.baseUrl())
                .setViewportSize(ConfigManager.viewportWidth(), ConfigManager.viewportHeight())
                .setIgnoreHTTPSErrors(true);

        if (storageStatePath != null) {
            options.setStorageStatePath(storageStatePath);
        }
        if (ConfigManager.videoOnFailure()) {
            options.setRecordVideoDir(VIDEO_DIR);
        }

        BrowserContext context = browser.newContext(options);
        context.setDefaultTimeout(ConfigManager.defaultTimeoutMs());
        context.setDefaultNavigationTimeout(ConfigManager.navigationTimeoutMs());

        if (ConfigManager.traceOnFailure()) {
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(false));
        }

        Page page = context.newPage();
        CONTEXT.set(context);
        PAGE.set(page);
        return page;
    }

    public static Page page() {
        Page page = PAGE.get();
        if (page == null) {
            throw new IllegalStateException("No page for this thread. Did BaseTest#setUp run?");
        }
        return page;
    }

    public static BrowserContext context() {
        return CONTEXT.get();
    }

    /**
     * Stops tracing and closes the context.
     *
     * @param traceName when non-null the trace is saved to target/traces/&lt;traceName&gt;.zip
     * @return path of the saved trace, or null if none was saved
     */
    public static Path stop(String traceName) {
        BrowserContext context = CONTEXT.get();
        Path savedTrace = null;
        if (context != null) {
            try {
                if (ConfigManager.traceOnFailure()) {
                    if (traceName != null) {
                        savedTrace = TRACE_DIR.resolve(traceName + ".zip");
                        context.tracing().stop(new Tracing.StopOptions().setPath(savedTrace));
                    } else {
                        context.tracing().stop();
                    }
                }
            } catch (Exception e) {
                LOG.warn("Could not stop tracing", e);
                savedTrace = null;
            } finally {
                context.close();
            }
        }
        CONTEXT.remove();
        PAGE.remove();
        return savedTrace;
    }
}
