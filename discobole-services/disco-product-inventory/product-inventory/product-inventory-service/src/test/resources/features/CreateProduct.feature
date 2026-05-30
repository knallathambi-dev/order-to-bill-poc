Feature: Create new product


  Scenario Outline: Creating a new product with status
    Given I have body with <status>
    When I make a POST request to "/tmf-api/productManagement/v4/product"
    Then the response status code should be <status_code>
    And the response body should contain product resource
    Examples:
      | status     | status_code |
      | null       | 400         |
      | ""         | 400         |
      | C          | 400         |
      | CREATED    | 201         |
      | CANCELLED  | 400         |
      | ACTIVE     | 400         |
      | TERMINATED | 400         |
      | ABORTED    | 400         |

#  Scenario Outline: Creating a new product with status and billingAccount
#    Given I have body with valid status
#    And  I have body with billingAccount
#    And  It has a  <billingAccountId>
#    When I make a POST request to "/productManagementInventory/v1/productCreation"
#    Then the response status code should be <status_code>
#    And the response body should contain product resource
#    Examples:
#      | billingAccountId   | status_code |
#      |                    | 400         |
#      | ""                 | 400         |
#      | "billingAccountId" | 201         |
#
#  Scenario Outline: Creating a new product with status and agreement
#    Given I have body with valid status
#    And  I have body with agreement
#    And  It has a  <agreementId>
#    When I make a POST request to "/productManagementInventory/v1/productCreation"
#    Then the response status code should be <status_code>
#    And the response body should contain product resource
#    Examples:
#      | agreementId   | status_code |
#      |               | 400         |
#      | ""            | 400         |
#      | "agreementId" | 201         |
#
#  Scenario Outline: Creating a new product with status and productOffering
#    Given I have body with valid status
#    And  I have body with productOffering
#    And  It has a  <productOfferingId>
#    When I make a POST request to "/productManagementInventory/v1/productCreation"
#    Then the response status code should be <status_code>
#    And the response body should contain product resource
#    Examples:
#      | productOfferingId   | status_code |
#      |                     | 400         |
#      | ""                  | 400         |
#      | "productOfferingId" | 201         |
#
#  Scenario Outline: Creating a new product with status and relatedParty
#    Given I have body with valid status
#    And  I have body with relatedParty
#    And  It has a  <relatedPartyId> and a  <relatedPartyReferredType>
#    When I make a POST request to "/productManagementInventory/v1/productCreation"
#    Then the response status code should be <status_code>
#    And the response body should contain product resource
#    Examples:
#      | relatedPartyId   | relatedPartyReferredType   | status_code |
#      |                  |                            | 400         |
#      | ""               | ""                         | 400         |
#      | "relatedPartyId" | ""                         | 400         |
#      | ""               | "relatedPartyReferredType" | 400         |
#      | "relatedPartyId" |                            | 400         |
#      |                  | "relatedPartyReferredType" | 400         |
#      | "relatedPartyId" | "relatedPartyReferredType" | 200         |
#
#  Scenario Outline: Creating a new product with status and realizingService
#    Given I have body with valid status
#    And  I have body with realizingService
#    And  It has a  <realizingServiceId>
#    When I make a POST request to "/productManagementInventory/v1/productCreation"
#    Then the response status code should be <status_code>
#    And the response body should contain product resource
#    Examples:
#      | realizingServiceId   | status_code |
#      |                      | 400         |
#      | ""                   | 400         |
#      | "realizingServiceId" | 201         |
#
#  Scenario Outline: Creating a new product with status and realizingResource
#    Given I have body with valid status
#    And  I have body with realizingResource
#    And  It has a  <realizingResourceId>
#    When I make a POST request to "/productManagementInventory/v1/productCreation"
#    Then the response status code should be <status_code>
#    And the response body should contain product resource
#    Examples:
#      | realizingResourceId   | status_code |
#      |                       | 400         |
#      | ""                    | 400         |
#      | "realizingResourceId" | 201         |
#
#  Scenario Outline: Creating a new product with status and productRelationship
#    Given I have body with valid status
#    And  I have body with productRelationship
#    And  It has a  <productRelationshipType> and a  <productRelationshipProduct>
#    When I make a POST request to "/productManagementInventory/v1/productCreation"
#    Then the response status code should be <status_code>
#    And the response body should contain product resource
#    Examples:
#      | productRelationshipType   | productRelationshipProduct   | status_code |
#      |                           |                              | 400         |
#      | ""                        | ""                           | 400         |
#      | "productRelationshipType" | ""                           | 400         |
#      | ""                        | "productRelationshipProduct" | 400         |
#      | "productRelationshipType" |                              | 400         |
#      |                           | "productRelationshipProduct" | 400         |
#      | "productRelationshipType" | "productRelationshipProduct" | 200         |
#
#  Scenario Outline: Creating a new product with status and productSpecification
#    Given I have body with valid status
#    And  I have body with productSpecification
#    And  It has a  <productSpecificationId>
#    When I make a POST request to "/productManagementInventory/v1/productCreation"
#    Then the response status code should be <status_code>
#    And the response body should contain product resource
#    Examples:
#      | productSpecificationId   | status_code |
#      |                          | 400         |
#      | ""                       | 400         |
#      | "productSpecificationId" | 201         |
#
#  Scenario Outline: Creating a new product with status and relatedPlace
#    Given I have body with valid status
#    And  I have body with relatedPlace
#    And  It has a  <relatedPlaceRole> and a  <relatedPlaceReferredType>
#    When I make a POST request to "/productManagementInventory/v1/productCreation"
#    Then the response status code should be <status_code>
#    And the response body should contain product resource
#    Examples:
#      | relatedPlaceRole   | relatedPlaceReferredType   | status_code |
#      |                    |                            | 400         |
#      | ""                 | ""                         | 400         |
#      | "relatedPlaceRole" | ""                         | 400         |
#      | ""                 | "relatedPlaceReferredType" | 400         |
#      | "relatedPlaceRole" |                            | 400         |
#      |                    | "relatedPlaceReferredType" | 400         |
#      | "relatedPlaceRole" | "relatedPlaceReferredType" | 200         |
#
#  Scenario Outline: Creating a new product with status and productOrderItem
#    Given I have body with valid status
#    And  I have body with productOrderItem
#    And  It has a  <productOrderItemId>
#    When I make a POST request to "/productManagementInventory/v1/productCreation"
#    Then the response status code should be <status_code>
#    And the response body should contain product resource
#    Examples:
#      | productOrderItemId | status_code |
#      |                    | 400         |
#      | ""                 | 400         |
#      | "productOrderItem" | 201         |
#
#  Scenario Outline: Creating a new product with status and productPrice
#    Given I have body with valid status
#    And  I have body with productOrderItem
#    And  It has a  <productPriceType>
#    When I make a POST request to "/productManagementInventory/v1/productCreation"
#    Then the response status code should be <status_code>
#    And the response body should contain product resource
#    Examples:
#      | productPriceType   | status_code |
#      |                    | 400         |
#      | ""                 | 400         |
#      | "productPriceType" | 201         |