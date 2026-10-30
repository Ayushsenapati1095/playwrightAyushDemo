@ui @login
Feature: Customer login
  As a returning customer
  I want to log in to my account
  So that I can see my orders and details

  @smoke @regression
  Scenario: Login page opens from the header
    Given I am on the home page
    When I navigate to "Log In" from the header
    Then I should be on the "/account/login" page
    And the login form should be displayed

  # Shopify can show a bot-protection challenge after many failed logins from one IP,
  # so this scenario is kept out of @smoke.
  @regression @negative
  Scenario: Logging in with an unregistered account shows an error
    Given I am on the login page
    When I log in with a random unregistered email and password
    Then I should see the login error "Incorrect email or password"
    And I should be on the "/account/login" page

  @regression
  Scenario: Forgot password reveals the reset form
    Given I am on the login page
    When I choose to reset my password
    Then I should see the page heading "Reset Password"
