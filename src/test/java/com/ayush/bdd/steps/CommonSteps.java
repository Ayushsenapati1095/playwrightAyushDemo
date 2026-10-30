package com.ayush.bdd.steps;

import com.ayush.bdd.context.TestContext;
import com.ayush.framework.data.ProductData;
import com.ayush.framework.pages.CatalogPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/** Navigation and page-level steps shared by all UI features. */
public class CommonSteps {

    private final TestContext context;

    public CommonSteps(TestContext context) {
        this.context = context;
    }

    @Given("I am on the home page")
    public void iAmOnTheHomePage() {
        context.homePage().open();
    }

    @Given("I am on the catalog page")
    public void iAmOnTheCatalogPage() {
        context.catalogPage().open();
    }

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        context.loginPage().open();
    }

    @When("I navigate to {string} from the header")
    public void iNavigateFromTheHeader(String linkText) {
        context.currentPage().header().clickNavLink(linkText);
    }

    @Then("I should be on the {string} page")
    public void iShouldBeOnThePage(String path) {
        assertThat(context.page()).hasURL(Pattern.compile(".*" + Pattern.quote(path) + ".*"));
    }

    @Then("I should see the page heading {string}")
    public void iShouldSeeThePageHeading(String heading) {
        assertThat(context.currentPage().mainHeading(heading)).isVisible();
    }

    @Then("the page title should contain {string}")
    public void thePageTitleShouldContain(String text) {
        assertThat(context.page()).hasTitle(Pattern.compile(".*" + Pattern.quote(text) + ".*", Pattern.CASE_INSENSITIVE));
    }

    @Then("the store tagline should be visible")
    public void theStoreTaglineShouldBeVisible() {
        assertThat(context.homePage().tagline()).isVisible();
    }

    @Then("the following featured products should be visible:")
    public void theFollowingFeaturedProductsShouldBeVisible(List<String> titles) {
        for (String title : titles) {
            assertThat(context.homePage().featuredProduct(title)).isVisible();
        }
    }

    @Then("the catalog should list all products")
    public void theCatalogShouldListAllProducts() {
        CatalogPage catalog = context.catalogPage();
        for (ProductData product : ProductData.values()) {
            assertThat(catalog.productLink(product.title())).isVisible();
        }
    }
}
