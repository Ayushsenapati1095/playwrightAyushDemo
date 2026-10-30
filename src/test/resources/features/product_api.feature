@api
Feature: Product API
  As an integrating system
  I want the storefront product API to return correct data
  So that product information is consistent across channels

  @smoke @regression
  Scenario Outline: Product API returns the catalogue data for "<handle>"
    When I request the product "<handle>" from the product API
    Then the response status should be 200
    And the product title should be "<title>"
    And the product price should be <price> pence
    And the product availability should be <available>
    And the product should have at least one variant

    Examples:
      | handle             | title          | price | available |
      | grey-jacket        | Grey jacket    | 5500  | true      |
      | noir-jacket        | Noir jacket    | 6000  | true      |
      | striped-top        | Striped top    | 5000  | true      |
      | bronze-sandals     | Bronze sandals | 3999  | true      |
      | flower-print-jeans | Black heels    | 4500  | true      |
      | brown-shades       | Brown Shades   | 2000  | false     |
      | white-sandals      | White sandals  | 2500  | false     |

  @regression @negative
  Scenario: Requesting an unknown product returns 404
    When I request the product "this-product-does-not-exist-12345" from the product API
    Then the response status should be 404
