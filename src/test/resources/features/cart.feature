@ui @cart
Feature: Shopping cart
  As a shopper
  I want to add and remove products in my cart
  So that I can control what I will buy

  @smoke @regression
  Scenario: Add a single product to the cart
    Given I open the product page for "Grey jacket"
    When I add the product to the cart
    Then the cart should show 1 item
    When I open the cart
    Then the cart should contain "Grey jacket"

  @regression
  Scenario: Add several products to the cart
    Given I have added the following products to the cart:
      | Grey jacket |
      | Striped top |
    Then the cart should show 2 items
    When I open the cart
    Then the cart should contain the following products:
      | Grey jacket |
      | Striped top |

  @regression
  Scenario: Remove the only product from the cart
    Given I have added the following products to the cart:
      | Noir jacket |
    When I open the cart
    And I remove line 1 from the cart
    Then the cart should show 0 items
    And the cart should not contain "Noir jacket"
