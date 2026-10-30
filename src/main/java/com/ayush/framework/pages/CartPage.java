package com.ayush.framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/** Full cart page at /cart. */
public class CartPage extends BasePage {

    public static final String PATH = "/cart";

    public CartPage(Page page) {
        super(page);
    }

    @Step("Open cart page")
    public CartPage open() {
        navigateTo(PATH);
        return this;
    }

    /** The cart line-items form (Shopify themes post it to /cart). */
    private Locator cartForm() {
        return page.locator("form[action='/cart'], form[action*='/cart']:not([action*='/cart/add'])").first();
    }

    public Locator lineItem(String productTitle) {
        return cartForm().getByRole(AriaRole.LINK,
                new Locator.GetByRoleOptions().setName(productTitle).setExact(true)).first();
    }

    /** Shopify removes a line by linking to /cart/change?line=N&amp;quantity=0. */
    public Locator removeLinkFor(int lineNumber) {
        return page.locator("a[href*='/cart/change'][href*='line=" + lineNumber + "'][href*='quantity=0']")
                .or(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Remove")))
                .first();
    }

    @Step("Remove line {lineNumber} from the cart")
    public CartPage removeLine(int lineNumber) {
        removeLinkFor(lineNumber).click();
        return this;
    }
}
