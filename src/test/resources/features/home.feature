@ui @home
Feature: Home page and navigation
  As a shopper
  I want the store home page and navigation to work
  So that I can find the products I am looking for

  Background:
    Given I am on the home page

  @smoke @regression
  Scenario: Home page loads with branding and an empty cart
    Then the page title should contain "Sauce Demo"
    And the store tagline should be visible
    And the cart should show 0 items

  @smoke @regression
  Scenario: Featured products are shown on the home page
    Then the following featured products should be visible:
      | Grey jacket |
      | Noir jacket |
      | Striped top |

  @regression
  Scenario: Catalog lists every product in the store
    When I navigate to "Catalog" from the header
    Then I should be on the "/collections/all" page
    And I should see the page heading "Products"
    And the catalog should list all products

  @regression
  Scenario: About Us page opens from the header
    When I navigate to "About Us" from the header
    Then I should be on the "/pages/about-us" page
    And I should see the page heading "About Us"
