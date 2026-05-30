Feature: [IPCEISCOOD-887] Mobile Package Max Plus - Validity in the Product Characteristics is displayed without unitOfMeasure

  Scenario: Update Validity characteristic value to be in json format
    Given There is no orchestration plan created
    And Product Catalog has the following characteristics for spec "spec1"
      | name     | value | unitOfMeasure |
      | Validity | 30    | Days          |
    And Product Catalog has the following characteristics for spec "spec2"
      | name     | valid from               | valid to                 |
      | Validity | 2025-07-16T08:35:51.963Z | 2025-09-16T08:35:51.963Z |
    And Product Catalog has the following characteristics for spec "spec3"
      | name     | value | unitOfMeasure |
      | Validity |       | Days          |
    And OM has product order "order1" with status "accepted"
    And  order item "orderItem1" with spec id "spec1" has the following characteristics
      | specificationId | name   | value | relationType | relatedNodeId |
      | spec1           | volume | 1hr   |              |               |
    And  order item "orderItem2" with spec id "spec2" has the following characteristics
      | specificationId | name  | value      | relationType | relatedNodeId |
      | spec2           | ICCID | ICCIDValue |              |               |
    And  order item "orderItem3" with spec id "spec3" has the following characteristics
      | specificationId | name     | value         | relationType | relatedNodeId |
      | spec3           | quantity | quantityValue |              |               |
    When the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan for order id "order1" with "Acknowledged" state
    And the plan with product order "order1" will have the following characteristics for order item "orderItem1"
      | name     | value | type                   | unitOfMeasure |
      | Validity | 30    | ValidityCharacteristic | Days          |
      | volume   | 1hr   |                        |               |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem2"
      | name     | value      | type                   | validFrom                | validTo                  |
      | ICCID    | ICCIDValue |                        |                          |                          |
      | Validity |            | ValidityCharacteristic | 2025-07-16T08:35:51.963Z | 2025-09-16T08:35:51.963Z |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem3"
      | name     | value         | type                   | unitOfMeasure |
      | Validity |               | ValidityCharacteristic | Days          |
      | quantity | quantityValue |                        |               |
