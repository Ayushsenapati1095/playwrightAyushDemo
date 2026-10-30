package com.ayush.tests.ui;

import com.ayush.framework.data.ProductData;
import com.ayush.framework.pages.HomePage;
import com.ayush.framework.pages.SearchPage;
import com.ayush.tests.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Epic("Sauce Demo Store")
@Feature("Search")
public class SearchTests extends BaseTest {

    @Test(groups = {"regression", "ui"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Search link opens the search page")
    public void searchPageOpensFromHeader() {
        SearchPage search = new HomePage(page()).open().header().goToSearch();

        assertThat(page()).hasURL(Pattern.compile(".*" + SearchPage.PATH + ".*"));
        assertThat(search.heading()).isVisible();
    }

    @Test(groups = {"smoke", "regression", "ui"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Searching for 'jacket' returns both jackets and not unrelated products")
    public void searchReturnsMatchingProducts() {
        SearchPage search = new SearchPage(page()).searchFor("jacket");

        assertThat(search.result(ProductData.GREY_JACKET.title())).isVisible();
        assertThat(search.result(ProductData.NOIR_JACKET.title())).isVisible();
        assertThat(search.result(ProductData.BRONZE_SANDALS.title())).not().isVisible();
    }
}
