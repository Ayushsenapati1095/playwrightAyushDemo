package com.ayush.framework.utils;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Helpers that put evidence (screenshots, traces, API calls) into the Allure report. */
public final class AllureAttachments {

    private static final Logger LOG = LoggerFactory.getLogger(AllureAttachments.class);
    private static final int MAX_BODY_CHARS = 20_000;

    private AllureAttachments() {
    }

    public static void screenshot(Page page, String name) {
        try {
            byte[] png = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(png), "png");
        } catch (Exception e) {
            LOG.warn("Could not capture screenshot", e);
        }
    }

    public static void pageUrl(Page page) {
        try {
            text("Page URL", page.url());
        } catch (Exception ignored) {
            // page may already be closed
        }
    }

    public static void trace(Path traceZip) {
        if (traceZip == null || !Files.exists(traceZip)) {
            return;
        }
        try (InputStream in = Files.newInputStream(traceZip)) {
            Allure.addAttachment("Playwright trace (open at trace.playwright.dev)", "application/zip", in, "zip");
        } catch (Exception e) {
            LOG.warn("Could not attach trace {}", traceZip, e);
        }
    }

    public static void text(String name, String content) {
        Allure.addAttachment(name, "text/plain", content == null ? "" : content);
    }

    public static void apiCall(String method, String path, String requestBody, APIResponse response) {
        StringBuilder sb = new StringBuilder()
                .append(method).append(' ').append(response.url()).append('\n')
                .append("Status: ").append(response.status()).append(' ').append(response.statusText()).append('\n');
        if (requestBody != null) {
            sb.append("\nRequest body:\n").append(requestBody).append('\n');
        }
        String body;
        try {
            body = response.text();
        } catch (Exception e) {
            body = "<unreadable body: " + e.getMessage() + ">";
        }
        if (body.length() > MAX_BODY_CHARS) {
            body = body.substring(0, MAX_BODY_CHARS) + "\n...<truncated>";
        }
        sb.append("\nResponse body:\n").append(body);
        Allure.addAttachment(method + " " + path, "text/plain",
                new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)), "txt");
    }
}
