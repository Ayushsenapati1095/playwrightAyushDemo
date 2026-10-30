@ui @search
Feature: Product search
  As a shopper
  I want to search the store
  So that I can find products quickly

  @regression
  Scenario: Search page opens from the header
    Given I am on the home page
    When I navigate to "Search" from the header
    Then I should be on the "/search" page
    And I should see the page heading "Search Results"

  @smoke @regression
  Scenario Outline: Searching for "<term>" returns only matching products
    When I search for "<term>"
    Then the search results should include "<expected>"
    And the search results should not include "<unexpected>"

    Examples:
      | term    | expected       | unexpected     |
      | jacket  | Grey jacket    | Bronze sandals |
      | jacket  | Noir jacket    | Striped top    |
      | sandals | Bronze sandals | Grey jacket    |
