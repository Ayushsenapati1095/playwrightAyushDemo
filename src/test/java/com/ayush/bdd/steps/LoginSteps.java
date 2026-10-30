package com.ayush.bdd.steps;

import com.ayush.bdd.context.TestContext;
import com.ayush.framework.data.TestDataFactory;
import com.ayush.framework.pages.LoginPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginSteps {

    private final TestContext context;

    public LoginSteps(TestContext context) {
        this.context = context;
    }

    @Then("the login form should be displayed")
    public void theLoginFormShouldBeDisplayed() {
        LoginPage login = context.loginPage();
        assertThat(login.heading()).isVisible();
        assertThat(login.emailInput()).isEditable();
        assertThat(login.passwordInput()).isEditable();
        assertThat(login.signInButton()).isVisible();
    }

    @When("I log in with a random unregistered email and password")
    public void iLogInWithARandomUnregisteredAccount() {
        context.loginPage().loginAs(TestDataFactory.uniqueEmail(), TestDataFactory.randomPassword());
    }

    @Then("I should see the login error {string}")
    public void iShouldSeeTheLoginError(String message) {
        assertThat(context.page().getByText(message).first()).isVisible();
    }

    @When("I choose to reset my password")
    public void iChooseToResetMyPassword() {
        context.loginPage().openForgotPassword();
    }
}
