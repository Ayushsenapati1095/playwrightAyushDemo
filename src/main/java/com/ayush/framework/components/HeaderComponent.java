package com.ayush.framework.components;

import com.ayush.framework.pages.AboutUsPage;
import com.ayush.framework.pages.CartPage;
import com.ayush.framework.pages.CatalogPage;
import com.ayush.framework.pages.LoginPage;
import com.ayush.framework.pages.SearchPage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Site header / navigation, shared by every page of the store.
 * Component objects keep repeated UI out of individual page classes.
 */
public class HeaderComponent {

    private static final Pattern CART_COUNT = Pattern.compile("My Cart \\((\\d+)\\)");

    private final Page page;

    public HeaderComponent(Page page) {
        this.page = page;
    }

    private Locator link(String name) {
        // The same link can appear in the header, the side menu and the footer: take the first visible one.
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(name).setExact(true))
                .filter(new Locator.FilterOptions().setVisible(true))
                .first();
    }

    /** The "My Cart (n)" link. The theme renders it twice (drawer toggle + link), so take the first. */
    public Locator cartLink() {
        return page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName(Pattern.compile("My Cart \\(\\d+\\)"))).first();
    }

    /** Current cart count as shown in the header. Prefer the web-first assertion on {@link #cartLink()} in tests. */
    public int cartCount() {
        Matcher m = CART_COUNT.matcher(cartLink().textContent());
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }

    @Step("Navigate to Catalog from header")
    public CatalogPage goToCatalog() {
        link("Catalog").click();
        return new CatalogPage(page);
    }

    @Step("Navigate to About Us from header")
    public AboutUsPage goToAboutUs() {
        link("About Us").click();
        return new AboutUsPage(page);
    }

    @Step("Navigate to Log In from header")
    public LoginPage goToLogin() {
        link("Log In").click();
        return new LoginPage(page);
    }

    @Step("Navigate to Search from header")
    public SearchPage goToSearch() {
        link("Search").click();
        return new SearchPage(page);
    }

    @Step("Open the cart page")
    public CartPage openCart() {
        // Navigating directly is more reliable than the drawer toggle, which only opens a mini-cart.
        page.navigate("/cart");
        return new CartPage(page);
    }
}
