package com.ayush.tests.ui;

import com.ayush.framework.data.ProductData;
import com.ayush.framework.pages.AboutUsPage;
import com.ayush.framework.pages.CatalogPage;
import com.ayush.framework.pages.HomePage;
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
@Feature("Home page & navigation")
public class HomePageTests extends BaseTest {

    @Test(groups = {"smoke", "regression", "ui"})
    @Severity(SeverityLevel.BLOCKER)
    @Description("Home page loads with title, branding and an empty cart")
    public void homePageLoads() {
        HomePage home = new HomePage(page()).open();

        assertThat(page()).hasTitle(Pattern.compile("Sauce Demo", Pattern.CASE_INSENSITIVE));
        assertThat(home.tagline()).isVisible();
        assertThat(home.header().cartLink()).containsText("My Cart (0)");
    }

    @Test(groups = {"smoke", "regression", "ui"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Featured products are shown on the home page")
    public void featuredProductsAreDisplayed() {
        HomePage home = new HomePage(page()).open();

        for (ProductData p : new ProductData[]{ProductData.GREY_JACKET, ProductData.NOIR_JACKET, ProductData.STRIPED_TOP}) {
            assertThat(home.featuredProduct(p.title())).isVisible();
        }
    }

    @Test(groups = {"regression", "ui"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Catalog link opens the full product listing with every product")
    public void catalogShowsAllProducts() {
        CatalogPage catalog = new HomePage(page()).open().header().goToCatalog();

        assertThat(page()).hasURL(Pattern.compile(".*/collections/all.*"));
        assertThat(catalog.heading()).isVisible();
        for (ProductData p : ProductData.values()) {
            assertThat(catalog.productLink(p.title())).isVisible();
        }
    }

    @Test(groups = {"regression", "ui"})
    @Severity(SeverityLevel.MINOR)
    @Description("About Us link opens the About Us page")
    public void aboutUsPageOpens() {
        AboutUsPage about = new HomePage(page()).open().header().goToAboutUs();

        assertThat(page()).hasURL(Pattern.compile(".*" + AboutUsPage.PATH + ".*"));
        assertThat(about.heading()).isVisible();
    }
}
