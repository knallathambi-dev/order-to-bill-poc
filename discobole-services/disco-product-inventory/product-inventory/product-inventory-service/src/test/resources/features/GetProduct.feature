Feature: Get product

  Scenario Outline: Get existing product by RelatedPartyId
    Given I have queryParam relatedPartyId <relatedPartyId>
    And I have queryParam offset <offset>
    And  I have queryParam limit <limit>
    When I make a GET request to "/tmf-api/productManagement/v4/product"
    Then the response status should be <status>
    And the list product  should be returned
    Examples:
      | relatedPartyId | offset | limit | status |
      | 45hj-8888      | 1      | 5     | 206    |
      | ""             | 1      | 5     | 206    |
Feature: Get product by ID
  I want to retrieve a product by its ID

  Scenario: Get an existing product by ID
    Given there is a product with ID "6458dcfc3adce83e94de37d4"
    When I request the product with ID "6458dcfc3adce83e94de37d4"
    Then the response status should be 200
    And the product details should be returned
      | ID                       | description         | href |
      | 6458dcfc3adce83e94de37d4 | PS_Product_Instance |      |

  Scenario: Get a non-existing product by ID
    Given there is no product with ID "6458dcfc3adce83e94de3"
    When I request the product with ID "6458dcfc3adce83e94de3"
    Then the response status should be 404
    And an error message should be returned with "Product not found with id 6458dcfc3adce83e94de3"
