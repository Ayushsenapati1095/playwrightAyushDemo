package com.ayush.bdd.context;

import com.ayush.framework.api.ProductApiClient;
import com.ayush.framework.core.BrowserContextManager;
import com.ayush.framework.data.ProductData;
import com.ayush.framework.pages.CartPage;
import com.ayush.framework.pages.CatalogPage;
import com.ayush.framework.pages.GenericPage;
import com.ayush.framework.pages.HomePage;
import com.ayush.framework.pages.LoginPage;
import com.ayush.framework.pages.ProductPage;
import com.ayush.framework.pages.SearchPage;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Page;

/**
 * Scenario-scoped state shared between step definition classes.
 * <p>
 * PicoContainer creates one TestContext per scenario and injects it into every
 * step class and hook that asks for it in its constructor, so steps in different
 * classes can share the current page, product, API client and last response
 * without static fields (which would break parallel runs).
 */
public class TestContext {

    private ProductApiClient productApi;
    private APIResponse lastResponse;
    private ProductData currentProduct;

    // ---- Browser ----

    public Page page() {
        return BrowserContextManager.page();
    }

    public HomePage homePage() {
        return new HomePage(page());
    }

    public CatalogPage catalogPage() {
        return new CatalogPage(page());
    }

    public ProductPage productPage() {
        return new ProductPage(page());
    }

    public CartPage cartPage() {
        return new CartPage(page());
    }

    public LoginPage loginPage() {
        return new LoginPage(page());
    }

    public SearchPage searchPage() {
        return new SearchPage(page());
    }

    public GenericPage currentPage() {
        return new GenericPage(page());
    }

    // ---- Scenario data ----

    public ProductData currentProduct() {
        if (currentProduct == null) {
            throw new IllegalStateException("No product selected yet in this scenario");
        }
        return currentProduct;
    }

    public void setCurrentProduct(ProductData product) {
        this.currentProduct = product;
    }

    // ---- API ----

    /** Created lazily so UI-only scenarios never open an API context. */
    public ProductApiClient productApi() {
        if (productApi == null) {
            productApi = new ProductApiClient();
        }
        return productApi;
    }

    public APIResponse lastResponse() {
        if (lastResponse == null) {
            throw new IllegalStateException("No API request has been made in this scenario");
        }
        return lastResponse;
    }

    public void setLastResponse(APIResponse response) {
        this.lastResponse = response;
    }

    /** Releases API resources; called from the @After hook. */
    public void disposeApi() {
        if (productApi != null) {
            productApi.close();
            productApi = null;
        }
    }
}
