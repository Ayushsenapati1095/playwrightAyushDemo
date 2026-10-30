package com.ayush.bdd.steps;

import com.ayush.bdd.context.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class SearchSteps {

    private final TestContext context;

    public SearchSteps(TestContext context) {
        this.context = context;
    }

    @When("I search for {string}")
    public void iSearchFor(String term) {
        context.searchPage().searchFor(term);
    }

    @Then("the search results should include {string}")
    public void theSearchResultsShouldInclude(String title) {
        assertThat(context.searchPage().result(title)).isVisible();
    }

    @Then("the search results should not include {string}")
    public void theSearchResultsShouldNotInclude(String title) {
        assertThat(context.searchPage().result(title)).not().isVisible();
    }
}
