Feature: Process product order events with various product characteristic types

  Scenario: 1 Process product order with validity characteristics
    Given There is no orchestration plan created
    And Product Catalog has no characteristics for spec "spec1"
    And Product Catalog has no characteristics for spec "spec2"
    And Product Catalog has no characteristics for spec "spec3"
    And OM has product order "order1" with status "accepted"
    And  order item "orderItem1" with spec id "spec1" has the following characteristics
      | type                   | name   | value | unitOfMeasure |
      | ValidityCharacteristic | volume | 1     | hr            |
    And  order item "orderItem2" with spec id "spec2" has the following characteristics
      | type                   | name   | validFrom                | validTo                  |
      | ValidityCharacteristic | volume | 2024-02-10T12:34:56.789Z | 2024-02-12T12:34:56.789Z |
    And  order item "orderItem3" with spec id "spec3" has the following characteristics
      | type                   | name   | validTo                  |
      | ValidityCharacteristic | volume | 2024-02-12T12:34:56.789Z |
    When the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan for order id "order1" with "Acknowledged" state
    And the plan with product order "order1" will have the following characteristics for order item "orderItem1"
      | name   | value | unitOfMeasure | type                   |
      | volume | 1     | hr            | ValidityCharacteristic |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem2"
      | name   | type                   | validFrom                | validTo                  |
      | volume | ValidityCharacteristic | 2024-02-10T12:34:56.789Z | 2024-02-12T12:34:56.789Z |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem3"
      | name   | type                   | validTo                  |
      | volume | ValidityCharacteristic | 2024-02-12T12:34:56.789Z |
