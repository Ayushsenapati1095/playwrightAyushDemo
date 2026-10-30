package com.ayush.bdd.steps;

import com.ayush.bdd.context.TestContext;
import com.ayush.framework.api.models.Product;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

/** Steps for @api scenarios. These run without a browser. */
public class ApiSteps {

    private final TestContext context;

    public ApiSteps(TestContext context) {
        this.context = context;
    }

    private Product product() {
        return context.productApi().parse(context.lastResponse());
    }

    @When("I request the product {string} from the product API")
    public void iRequestTheProductFromTheProductApi(String handle) {
        context.setLastResponse(context.productApi().getProductRaw(handle));
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int status) {
        assertThat(context.lastResponse().status()).as("HTTP status").isEqualTo(status);
    }

    @Then("the product title should be {string}")
    public void theProductTitleShouldBe(String title) {
        assertThat(product().title()).isEqualTo(title);
    }

    @Then("the product price should be {long} pence")
    public void theProductPriceShouldBe(long pence) {
        assertThat(product().price()).isEqualTo(pence);
    }

    @Then("the product availability should be {word}")
    public void theProductAvailabilityShouldBe(String available) {
        assertThat(product().available()).isEqualTo(Boolean.parseBoolean(available));
    }

    @Then("the product should have at least one variant")
    public void theProductShouldHaveAtLeastOneVariant() {
        assertThat(product().variants()).isNotEmpty();
    }
}
