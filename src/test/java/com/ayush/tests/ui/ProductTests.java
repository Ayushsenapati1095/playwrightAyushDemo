package com.ayush.tests.ui;

import com.ayush.framework.data.ProductData;
import com.ayush.framework.pages.CatalogPage;
import com.ayush.framework.pages.ProductPage;
import com.ayush.tests.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Epic("Sauce Demo Store")
@Feature("Product details")
public class ProductTests extends BaseTest {

    @DataProvider(name = "availableProducts", parallel = true)
    public Object[][] availableProducts() {
        return Arrays.stream(ProductData.values())
                .filter(ProductData::available)
                .map(p -> new Object[]{p})
                .toArray(Object[][]::new);
    }

    @Test(groups = {"regression", "ui"}, dataProvider = "availableProducts")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Data-driven: each in-stock product shows the correct title, price and an Add to Cart button")
    public void productDetailsAreCorrect(ProductData product) {
        ProductPage productPage = new ProductPage(page()).open(product.handle());

        assertThat(productPage.title(product.title())).isVisible();
        assertThat(productPage.price(product.displayPrice())).isVisible();
        assertThat(productPage.addToCartButton()).isEnabled();
    }

    @Test(groups = {"smoke", "regression", "ui"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Opening a product from the catalog lands on its detail page")
    public void openProductFromCatalog() {
        ProductData product = ProductData.NOIR_JACKET;
        ProductPage productPage = new CatalogPage(page()).open().openProduct(product.title());

        assertThat(page()).hasURL(Pattern.compile(".*/products/" + product.handle() + ".*"));
        assertThat(productPage.title(product.title())).isVisible();
        assertThat(productPage.price(product.displayPrice())).isVisible();
    }

    @Test(groups = {"regression", "ui"})
    @Severity(SeverityLevel.NORMAL)
    @Description("A sold-out product is labelled Sold Out and cannot be added to the cart")
    public void soldOutProductCannotBeAdded() {
        ProductPage productPage = new ProductPage(page()).open(ProductData.BROWN_SHADES.handle());

        assertThat(productPage.soldOutLabel()).isVisible();
        if (productPage.addToCartButton().count() > 0) {
            assertThat(productPage.addToCartButton()).isDisabled();
        }
    }
}
