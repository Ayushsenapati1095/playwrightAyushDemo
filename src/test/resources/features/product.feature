@ui @product
Feature: Product details
  As a shopper
  I want to see accurate product information
  So that I know what I am buying and what it costs

  @regression
  Scenario Outline: Product page shows the correct details for "<product>"
    Given I open the product page for "<product>"
    Then I should see the product title "<product>"
    And I should see the price "<price>"
    And the Add to Cart button should be enabled

    Examples:
      | product        | price  |
      | Grey jacket    | £55.00 |
      | Noir jacket    | £60.00 |
      | Striped top    | £50.00 |
      | Bronze sandals | £39.99 |
      | Black heels    | £45.00 |

  @smoke @regression
  Scenario: Open a product from the catalog
    Given I am on the catalog page
    When I open the product "Noir jacket"
    Then I should be on the product page for "Noir jacket"
    And I should see the price "£60.00"

  @regression
  Scenario: A sold-out product cannot be added to the cart
    Given I open the product page for "Brown Shades"
    Then the product should be shown as sold out
