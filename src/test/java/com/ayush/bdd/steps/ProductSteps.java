package com.ayush.bdd.steps;

import com.ayush.bdd.context.TestContext;
import com.ayush.framework.data.ProductData;
import com.ayush.framework.pages.ProductPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ProductSteps {

    private final TestContext context;

    public ProductSteps(TestContext context) {
        this.context = context;
    }

    @Given("I open the product page for {string}")
    public void iOpenTheProductPageFor(String title) {
        ProductData product = ProductData.fromTitle(title);
        context.setCurrentProduct(product);
        context.productPage().open(product.handle());
    }

    @When("I open the product {string}")
    public void iOpenTheProduct(String title) {
        context.setCurrentProduct(ProductData.fromTitle(title));
        context.catalogPage().openProduct(title);
    }

    @Then("I should be on the product page for {string}")
    public void iShouldBeOnTheProductPageFor(String title) {
        ProductData product = ProductData.fromTitle(title);
        assertThat(context.page()).hasURL(Pattern.compile(".*/products/" + Pattern.quote(product.handle()) + ".*"));
        assertThat(context.productPage().title(product.title())).isVisible();
    }

    @Then("I should see the product title {string}")
    public void iShouldSeeTheProductTitle(String title) {
        assertThat(context.productPage().title(title)).isVisible();
    }

    @Then("I should see the price {string}")
    public void iShouldSeeThePrice(String price) {
        assertThat(context.productPage().price(price)).isVisible();
    }

    @Then("the Add to Cart button should be enabled")
    public void theAddToCartButtonShouldBeEnabled() {
        assertThat(context.productPage().addToCartButton()).isEnabled();
    }

    @Then("the product should be shown as sold out")
    public void theProductShouldBeShownAsSoldOut() {
        ProductPage productPage = context.productPage();
        assertThat(productPage.soldOutLabel()).isVisible();
        // Themes either hide the button or disable it for sold-out products
        if (productPage.addToCartButton().count() > 0) {
            assertThat(productPage.addToCartButton()).isDisabled();
        }
    }
}
