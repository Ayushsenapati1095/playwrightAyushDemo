package com.ayush.framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/** "Products" listing at /collections/all. */
public class CatalogPage extends BasePage {

    public static final String PATH = "/collections/all";

    public CatalogPage(Page page) {
        super(page);
    }

    @Step("Open catalog page")
    public CatalogPage open() {
        navigateTo(PATH);
        return this;
    }

    public Locator heading() {
        return mainHeading("Products");
    }

    public Locator productLink(String title) {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(title).setExact(true)).first();
    }

    @Step("Open product '{title}' from catalog")
    public ProductPage openProduct(String title) {
        productLink(title).click();
        return new ProductPage(page);
    }
}
