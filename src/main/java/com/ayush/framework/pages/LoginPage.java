package com.ayush.framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/** Customer login at /account/login (also hosts the "Reset Password" form). */
public class LoginPage extends BasePage {

    public static final String PATH = "/account/login";

    public LoginPage(Page page) {
        super(page);
    }

    @Step("Open login page")
    public LoginPage open() {
        navigateTo(PATH);
        return this;
    }

    public Locator heading() {
        return mainHeading("Customer Login");
    }

    public Locator emailInput() {
        return page.getByLabel("Email Address", new Page.GetByLabelOptions().setExact(true))
                .or(page.locator("#customer_email"))
                .first();
    }

    public Locator passwordInput() {
        return page.getByLabel("Password", new Page.GetByLabelOptions().setExact(true))
                .or(page.locator("#customer_password"))
                .first();
    }

    public Locator signInButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In"))
                .or(page.locator("#customer_login [type='submit']"))
                .first();
    }

    public Locator errorMessage() {
        return page.getByText("Incorrect email or password").first();
    }

    public Locator forgotPasswordLink() {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Forgot your password?"));
    }

    public Locator resetPasswordHeading() {
        return mainHeading("Reset Password");
    }

    @Step("Log in with email '{email}'")
    public LoginPage loginAs(String email, String password) {
        emailInput().fill(email);
        passwordInput().fill(password);
        signInButton().click();
        return this;
    }

    @Step("Open the reset password form")
    public LoginPage openForgotPassword() {
        forgotPasswordLink().click();
        return this;
    }
}
