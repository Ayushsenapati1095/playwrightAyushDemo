package com.ayush.framework.api;

import com.ayush.framework.api.models.Product;
import com.microsoft.playwright.APIResponse;
import io.qameta.allure.Step;

/**
 * Shopify storefront product endpoints.
 * Every Shopify store exposes {@code /products/<handle>.js} as public JSON,
 * which makes it a convenient API layer for the demo store.
 */
public class ProductApiClient extends ApiClient {

    @Step("API: GET product '{handle}'")
    public APIResponse getProductRaw(String handle) {
        return get("/products/" + handle + ".js");
    }

    @Step("API: GET product '{handle}' as model")
    public Product getProduct(String handle) {
        APIResponse response = getProductRaw(handle);
        if (!response.ok()) {
            throw new IllegalStateException("GET product '" + handle + "' failed with status " + response.status());
        }
        return as(response, Product.class);
    }

    /** Parses a response previously returned by {@link #getProductRaw(String)}. */
    public Product parse(APIResponse response) {
        return as(response, Product.class);
    }
}
