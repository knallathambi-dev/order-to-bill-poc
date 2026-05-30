Feature: [IPCEISCOOD-746] Fix Quantity characteristic value format

  Scenario: Update Quantity characteristic value to be in json format
    Given There is no orchestration plan created
    And Product Catalog has the following characteristics for spec "spec1"
      | name     | value | unitOfMeasure | type                 |
      | Quantity | 1     | GB            | ObjectCharacteristic |
      | Level    | Basic |               | StringCharacteristic |
    And Product Catalog has the following characteristics for spec "spec2"
      | name                  | value | unitOfMeasure | type                 |
      | Validity Of Rollovers | 2     | month         | StringCharacteristic |
      | Quantity              | 100   | Days          | ObjectCharacteristic |
    And OM has product order "order1" with status "accepted"
    And  order item "orderItem1" with spec id "spec1" has the following characteristics
      | specificationId | name   | value | relationType | relatedNodeId |
      | spec1           | volume | 1hr   |              |               |
    And  order item "orderItem2" with spec id "spec2" has the following characteristics
      | specificationId | name  | value      | relationType | relatedNodeId |
      | spec2           | ICCID | ICCIDValue |              |               |
    When the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan for order id "order1" with "Acknowledged" state
    And the plan with product order "order1" will have the following characteristics for order item "orderItem1"
      | name     | value | unitOfMeasure | type                 |
      | Quantity | 1     | GB            | ObjectCharacteristic |
      | Level    | Basic |               | StringCharacteristic |
      | volume   | 1hr   |               | StringCharacteristic |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem2"
      | name                  | value      | unitOfMeasure | type                 |
      | Validity Of Rollovers | 2month     |               | StringCharacteristic |
      | ICCID                 | ICCIDValue |               | StringCharacteristic |
      | Quantity              | 100        | Days          | ObjectCharacteristic |