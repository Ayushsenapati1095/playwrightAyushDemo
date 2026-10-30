package com.ayush.tests.api;

import com.ayush.framework.api.ProductApiClient;
import com.ayush.framework.api.models.Product;
import com.ayush.framework.data.ProductData;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import com.microsoft.playwright.APIResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;

import com.microsoft.playwright.assertions.PlaywrightAssertions;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * API tests run without a browser, using Playwright's APIRequestContext.
 * They are fast, so they make good smoke checks before the slower UI suite.
 */
@Epic("Sauce Demo Store")
@Feature("Product API")
public class ProductApiTests {

    private ProductApiClient productApi;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        productApi = new ProductApiClient();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (productApi != null) {
            productApi.close();
        }
    }

    @DataProvider(name = "allProducts")
    public Object[][] allProducts() {
        return Arrays.stream(ProductData.values()).map(p -> new Object[]{p}).toArray(Object[][]::new);
    }

    @Test(groups = {"smoke", "regression", "api"}, dataProvider = "allProducts")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Product JSON matches the expected catalogue: title, handle, price and availability")
    public void productContractMatchesCatalogue(ProductData expected) {
        Product product = productApi.getProduct(expected.handle());

        assertThat(product.title()).isEqualTo(expected.title());
        assertThat(product.handle()).isEqualTo(expected.handle());
        assertThat(product.price()).isEqualTo(expected.priceInPence());
        assertThat(product.available()).isEqualTo(expected.available());
        assertThat(product.variants()).as("every product has at least one variant").isNotEmpty();
    }

    @Test(groups = {"regression", "api"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Product endpoint returns a JSON body with HTTP 200")
    public void productEndpointReturnsJson() {
        APIResponse response = productApi.getProductRaw(ProductData.GREY_JACKET.handle());

        PlaywrightAssertions.assertThat(response).isOK();
        assertThat(response.text().trim()).as("response body is a JSON object").startsWith("{");
    }

    @Test(groups = {"regression", "api"})
    @Severity(SeverityLevel.NORMAL)
    @Description("An unknown product handle returns 404")
    public void unknownProductReturns404() {
        APIResponse response = productApi.getProductRaw("this-product-does-not-exist-12345");

        assertThat(response.status()).isEqualTo(404);
    }
}
