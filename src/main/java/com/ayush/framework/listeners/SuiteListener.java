package com.ayush.framework.listeners;

import com.ayush.framework.config.ConfigManager;
import com.ayush.framework.core.PlaywrightManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Suite-level lifecycle:
 * <ul>
 *   <li>Writes Allure's environment.properties so the report shows env/browser/base URL.</li>
 *   <li>Closes every Playwright instance once all tests are done.</li>
 * </ul>
 */
public class SuiteListener implements ISuiteListener {

    private static final Logger LOG = LoggerFactory.getLogger(SuiteListener.class);

    @Override
    public void onStart(ISuite suite) {
        LOG.info("Starting suite '{}' | env={} | browser={} | headless={} | baseUrl={}",
                suite.getName(), ConfigManager.env(), ConfigManager.browser(),
                ConfigManager.headless(), ConfigManager.baseUrl());
        writeAllureEnvironment();
    }

    @Override
    public void onFinish(ISuite suite) {
        PlaywrightManager.closeAll();
        LOG.info("Finished suite '{}'", suite.getName());
    }

    private void writeAllureEnvironment() {
        Path dir = Paths.get(System.getProperty("allure.results.directory", "target/allure-results"));
        Properties props = new Properties();
        props.setProperty("Environment", ConfigManager.env());
        props.setProperty("Browser", ConfigManager.browser());
        props.setProperty("Headless", String.valueOf(ConfigManager.headless()));
        props.setProperty("Base URL", ConfigManager.baseUrl());
        props.setProperty("Java", System.getProperty("java.version"));
        props.setProperty("OS", System.getProperty("os.name"));
        try {
            Files.createDirectories(dir);
            try (OutputStream out = Files.newOutputStream(dir.resolve("environment.properties"))) {
                props.store(out, "Allure environment");
            }
        } catch (IOException e) {
            LOG.warn("Could not write Allure environment.properties", e);
        }
    }
}
