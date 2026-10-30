package com.ayush.framework.api;

import com.ayush.framework.config.ConfigManager;
import com.ayush.framework.core.PlaywrightManager;
import com.ayush.framework.utils.AllureAttachments;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.util.Map;

/**
 * Thin wrapper around Playwright's {@link APIRequestContext}.
 * <p>
 * Service-specific clients (e.g. {@link ProductApiClient}) extend this and expose
 * typed methods, so tests never build raw URLs or parse JSON by hand.
 * Every request/response is attached to the Allure report.
 */
public abstract class ApiClient implements AutoCloseable {

    protected static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    protected final APIRequestContext request;

    protected ApiClient() {
        this(Map.of("Accept", "application/json"));
    }

    protected ApiClient(Map<String, String> headers) {
        this.request = PlaywrightManager.playwright().request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(ConfigManager.apiBaseUrl())
                .setExtraHTTPHeaders(headers)
                .setTimeout(ConfigManager.navigationTimeoutMs()));
    }

    protected APIResponse get(String path) {
        APIResponse response = request.get(path);
        AllureAttachments.apiCall("GET", path, null, response);
        return response;
    }

    protected APIResponse post(String path, Object body) {
        String json = toJson(body);
        APIResponse response = request.post(path, RequestOptions.create()
                .setHeader("Content-Type", "application/json")
                .setData(json));
        AllureAttachments.apiCall("POST", path, json, response);
        return response;
    }

    protected <T> T as(APIResponse response, Class<T> type) {
        try {
            return MAPPER.readValue(response.text(), type);
        } catch (Exception e) {
            throw new IllegalStateException("Could not parse response as " + type.getSimpleName()
                    + ". Status " + response.status() + ", body: " + response.text(), e);
        }
    }

    private static String toJson(Object body) {
        try {
            return body instanceof String s ? s : MAPPER.writeValueAsString(body);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not serialise request body", e);
        }
    }

    @Override
    public void close() {
        request.dispose();
    }
}
