package com.ayush.framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

public class HomePage extends BasePage {

    public HomePage(Page page) {
        super(page);
    }

    @Step("Open home page")
    public HomePage open() {
        navigateTo("/");
        return this;
    }

    /** Store logo / site name link. */
    public Locator logo() {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Sauce Demo")).first();
    }

    public Locator tagline() {
        return page.getByText("Just a demo site showing off what Sauce can do.").first();
    }

    /** A featured product link on the home page, matched by its visible title. */
    public Locator featuredProduct(String title) {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(title).setExact(true)).first();
    }

    @Step("Open featured product '{title}'")
    public ProductPage openFeaturedProduct(String title) {
        featuredProduct(title).click();
        return new ProductPage(page);
    }
}
