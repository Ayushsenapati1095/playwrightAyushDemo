package com.ayush.framework.pages;

import com.ayush.framework.components.HeaderComponent;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Common behaviour for every page object.
 * <p>
 * Rules followed by all page objects in this framework:
 * <ul>
 *   <li>Locators prefer user-facing attributes (role, label, text) over CSS/XPath.</li>
 *   <li>Page objects expose actions and state; assertions live in tests.</li>
 *   <li>Navigation methods return the next page object to allow fluent chaining.</li>
 * </ul>
 */
public abstract class BasePage {

    protected final Page page;
    private final HeaderComponent header;

    protected BasePage(Page page) {
        this.page = page;
        this.header = new HeaderComponent(page);
    }

    public HeaderComponent header() {
        return header;
    }

    public Page page() {
        return page;
    }

    public String title() {
        return page.title();
    }

    public String url() {
        return page.url();
    }

    /** Main page heading (h1). Uses the role so it ignores footer/sidebar headings. */
    public Locator mainHeading(String name) {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(name).setLevel(1));
    }

    @Step("Navigate to '{path}'")
    protected void navigateTo(String path) {
        page.navigate(path);
    }
}
