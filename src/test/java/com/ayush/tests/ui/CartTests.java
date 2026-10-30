package com.ayush.tests.ui;

import com.ayush.framework.data.ProductData;
import com.ayush.framework.pages.CartPage;
import com.ayush.framework.pages.ProductPage;
import com.ayush.tests.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Epic("Sauce Demo Store")
@Feature("Shopping cart")
public class CartTests extends BaseTest {

    @Test(groups = {"smoke", "regression", "ui"})
    @Severity(SeverityLevel.BLOCKER)
    @Description("Adding a product updates the cart count and the item appears in the cart")
    public void addProductToCart() {
        ProductData product = ProductData.GREY_JACKET;

        ProductPage productPage = new ProductPage(page()).open(product.handle()).addToCart();
        assertThat(productPage.header().cartLink()).containsText("My Cart (1)");

        CartPage cart = productPage.header().openCart();
        assertThat(cart.lineItem(product.title())).isVisible();
    }

    @Test(groups = {"regression", "ui"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Adding two different products shows both in the cart")
    public void addMultipleProductsToCart() {
        ProductPage productPage = new ProductPage(page())
                .open(ProductData.GREY_JACKET.handle()).addToCart();
        assertThat(productPage.header().cartLink()).containsText("My Cart (1)");

        productPage.open(ProductData.STRIPED_TOP.handle()).addToCart();
        assertThat(productPage.header().cartLink()).containsText("My Cart (2)");

        CartPage cart = productPage.header().openCart();
        assertThat(cart.lineItem(ProductData.GREY_JACKET.title())).isVisible();
        assertThat(cart.lineItem(ProductData.STRIPED_TOP.title())).isVisible();
    }

    @Test(groups = {"regression", "ui"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Removing the only item empties the cart")
    public void removeProductFromCart() {
        ProductPage productPage = new ProductPage(page()).open(ProductData.NOIR_JACKET.handle()).addToCart();
        assertThat(productPage.header().cartLink()).containsText("My Cart (1)");

        CartPage cart = productPage.header().openCart().removeLine(1);

        assertThat(cart.header().cartLink()).containsText("My Cart (0)");
        assertThat(cart.lineItem(ProductData.NOIR_JACKET.title())).not().isVisible();
    }
}
