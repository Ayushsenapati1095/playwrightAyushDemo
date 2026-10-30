package com.ayush.tests.ui;

import com.ayush.framework.data.TestDataFactory;
import com.ayush.framework.pages.HomePage;
import com.ayush.framework.pages.LoginPage;
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
@Feature("Customer account")
public class LoginTests extends BaseTest {

    @Test(groups = {"smoke", "regression", "ui"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Log In link opens the customer login form")
    public void loginPageOpensFromHeader() {
        LoginPage login = new HomePage(page()).open().header().goToLogin();

        assertThat(page()).hasURL(Pattern.compile(".*" + LoginPage.PATH + ".*"));
        assertThat(login.heading()).isVisible();
        assertThat(login.emailInput()).isEditable();
        assertThat(login.passwordInput()).isEditable();
    }

    /**
     * Note: Shopify may show a bot-protection challenge after repeated failed logins from the
     * same IP. If this test starts failing on a CAPTCHA page, run it less often (it is excluded
     * from the smoke suite for that reason) or use a test store with bot protection disabled.
     */
    @Test(groups = {"regression", "ui"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Logging in with an unknown account shows an error and stays on the login page")
    public void invalidLoginShowsError() {
        LoginPage login = new LoginPage(page()).open()
                .loginAs(TestDataFactory.uniqueEmail(), TestDataFactory.randomPassword());

        assertThat(login.errorMessage()).isVisible();
        assertThat(page()).hasURL(Pattern.compile(".*/account/login.*"));
    }

    @Test(groups = {"regression", "ui"})
    @Severity(SeverityLevel.NORMAL)
    @Description("'Forgot your password?' reveals the Reset Password form")
    public void forgotPasswordShowsResetForm() {
        LoginPage login = new LoginPage(page()).open().openForgotPassword();

        assertThat(login.resetPasswordHeading()).isVisible();
    }
}
