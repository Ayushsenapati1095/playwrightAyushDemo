package com.ayush.framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Store search at /search. */
public class SearchPage extends BasePage {

    public static final String PATH = "/search";

    public SearchPage(Page page) {
        super(page);
    }

    public Locator heading() {
        return mainHeading("Search Results");
    }

    public Locator searchInput() {
        return page.locator("input[name='q']").first();
    }

    /** Runs a search via the standard Shopify query URL. */
    @Step("Search for '{term}'")
    public SearchPage searchFor(String term) {
        navigateTo(PATH + "?q=" + URLEncoder.encode(term, StandardCharsets.UTF_8));
        return this;
    }

    /** Runs a search by typing into the on-page search box. */
    @Step("Search for '{term}' using the search box")
    public SearchPage searchUsingBox(String term) {
        searchInput().fill(term);
        searchInput().press("Enter");
        return this;
    }

    public Locator result(String productTitle) {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(productTitle).setExact(true)).first();
    }
}
