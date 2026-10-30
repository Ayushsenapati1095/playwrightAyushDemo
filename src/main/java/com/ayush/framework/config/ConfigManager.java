package com.ayush.framework.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central configuration.
 * <p>
 * Resolution order for every key (first match wins):
 * <ol>
 *   <li>JVM system property, e.g. {@code -Dbrowser=firefox}</li>
 *   <li>Environment variable, upper-cased with dots replaced by underscores, e.g. {@code BROWSER}</li>
 *   <li>{@code config/<env>.properties} on the classpath, where env comes from {@code -Denv=qa} (default: qa)</li>
 * </ol>
 * Secrets (passwords, API keys) must only ever come from 1 or 2, injected by Jenkins credentials
 * or an Azure DevOps variable group linked to Key Vault.
 */
public final class ConfigManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigManager.class);
    private static final Properties PROPS = new Properties();
    private static final String ENV;

    static {
        ENV = resolveRaw("env", "qa");
        String file = "config/" + ENV + ".properties";
        try (InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                throw new IllegalStateException("Config file not found on classpath: " + file);
            }
            PROPS.load(in);
            LOG.info("Loaded configuration for environment '{}'", ENV);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read " + file, e);
        }
    }

    private ConfigManager() {
    }

    private static String resolveRaw(String key, String fallback) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys.trim();
        }
        String env = System.getenv(key.toUpperCase().replace('.', '_'));
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        return fallback;
    }

    public static String get(String key) {
        String value = resolveRaw(key, PROPS.getProperty(key));
        if (value == null) {
            throw new IllegalStateException("Missing configuration key: " + key);
        }
        return value.trim();
    }

    public static String get(String key, String defaultValue) {
        String value = resolveRaw(key, PROPS.getProperty(key, defaultValue));
        return value == null ? null : value.trim();
    }

    public static int getInt(String key, int defaultValue) {
        return Integer.parseInt(get(key, String.valueOf(defaultValue)));
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
    }

    public static String env() {
        return ENV;
    }

    // ---- Typed accessors for commonly used keys ----

    public static String baseUrl() {
        return get("base.url");
    }

    public static String apiBaseUrl() {
        return get("api.base.url", baseUrl());
    }

    public static String browser() {
        return get("browser", "chromium").toLowerCase();
    }

    public static boolean headless() {
        return getBoolean("headless", true);
    }

    public static int slowMo() {
        return getInt("slow.mo", 0);
    }

    public static int defaultTimeoutMs() {
        return getInt("timeout.ms", 15000);
    }

    public static int navigationTimeoutMs() {
        return getInt("navigation.timeout.ms", 30000);
    }

    public static int viewportWidth() {
        return getInt("viewport.width", 1440);
    }

    public static int viewportHeight() {
        return getInt("viewport.height", 900);
    }

    public static int retryCount() {
        return getInt("retry.count", 0);
    }

    public static boolean traceOnFailure() {
        return getBoolean("trace.on.failure", true);
    }

    public static boolean videoOnFailure() {
        return getBoolean("video.on.failure", false);
    }
}
