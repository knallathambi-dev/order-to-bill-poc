Feature: Get product filter

  Scenario Outline: Get existing product by filter
    Given I have request filter:
      | fields | <fields> |
      | offset | <offset> |
      | limit  | <limit>  |
    When I make a GET request to filter "/tmf-api/productManagement/v4/product"
    Then the response status filter should be <status>
    And the page list product should be returned

    Examples:
      | fields           | offset | limit | status |
      | name             | 1      | 5     | 206    |
      | name,description | 1      | 5     | 206    |


  Scenario Outline: Get existing product by filter without fields
    Given I have request filter:
      | offset | <offset> |
      | limit  | <limit>  |
    And I have additional URL parameters:
      | parameter         | value               |
      | relatedParty.partyOrPartyRole.id   | <relatedPartyId>    |
      | operationalStatus | <operationalStatus> |
    When I make a GET request to filter "/tmf-api/productManagement/v4/product"
    Then the response status filter should be <status>
    And the page list product should be returned

    Examples:
      | offset | limit | relatedPartyId | operationalStatus | status |
      | 1      | 5     | 45hj-8888      | CREATED           | 206    |



