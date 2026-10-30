package com.ayush.bdd.steps;

import com.ayush.bdd.context.TestContext;
import com.ayush.framework.data.ProductData;
import com.ayush.framework.pages.ProductPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CartSteps {

    private final TestContext context;

    public CartSteps(TestContext context) {
        this.context = context;
    }

    @When("I add the product to the cart")
    public void iAddTheProductToTheCart() {
        context.productPage().addToCart();
    }

    @Given("I have added the following products to the cart:")
    public void iHaveAddedTheFollowingProductsToTheCart(List<String> titles) {
        int expectedCount = 0;
        for (String title : titles) {
            ProductData product = ProductData.fromTitle(title);
            ProductPage productPage = context.productPage().open(product.handle()).addToCart();
            expectedCount++;
            // Wait for the cart to update before adding the next product
            assertThat(productPage.header().cartLink()).containsText("My Cart (" + expectedCount + ")");
        }
    }

    @Then("the cart should show {int} item(s)")
    public void theCartShouldShowItems(int count) {
        assertThat(context.currentPage().header().cartLink()).containsText("My Cart (" + count + ")");
    }

    @When("I open the cart")
    public void iOpenTheCart() {
        context.currentPage().header().openCart();
    }

    @Then("the cart should contain {string}")
    public void theCartShouldContain(String title) {
        assertThat(context.cartPage().lineItem(title)).isVisible();
    }

    @Then("the cart should contain the following products:")
    public void theCartShouldContainTheFollowingProducts(List<String> titles) {
        for (String title : titles) {
            assertThat(context.cartPage().lineItem(title)).isVisible();
        }
    }

    @When("I remove line {int} from the cart")
    public void iRemoveLineFromTheCart(int line) {
        context.cartPage().removeLine(line);
    }

    @Then("the cart should not contain {string}")
    public void theCartShouldNotContain(String title) {
        assertThat(context.cartPage().lineItem(title)).not().isVisible();
    }
}
