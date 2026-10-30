package com.ayush.framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/** Product detail page, e.g. /products/grey-jacket. */
public class ProductPage extends BasePage {

    public ProductPage(Page page) {
        super(page);
    }

    @Step("Open product page for handle '{handle}'")
    public ProductPage open(String handle) {
        navigateTo("/products/" + handle);
        return this;
    }

    public Locator title(String productTitle) {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(productTitle).setExact(true)).first();
    }

    public Locator price(String formattedPrice) {
        return page.getByText(formattedPrice).first();
    }

    /**
     * The add-to-cart submit button. The role locator covers both &lt;button&gt; and
     * &lt;input type=submit value="Add to Cart"&gt;; the CSS fallback covers themes
     * that use a different label on the Shopify cart form.
     */
    public Locator addToCartButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to Cart"))
                .or(page.locator("form[action*='/cart/add'] [type='submit']"))
                .first();
    }

    public Locator soldOutLabel() {
        return page.getByText("Sold Out").first();
    }

    @Step("Add product to cart")
    public ProductPage addToCart() {
        addToCartButton().click();
        return this;
    }
}
