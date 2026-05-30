Feature: Process product order events with Address Characteristic type

  Scenario: 1 Process product order with Address characteristics
    Given There is no orchestration plan created
    And Product Catalog has no characteristics for spec "spec1"
    And Product Catalog has no characteristics for spec "spec2"
    And OM has product order "order1" with status "accepted"
    And  order item "orderItem1" with spec id "spec1" has the following characteristics
      | type                  | name          | addressId | country | city  | streetName  | postCode |
      | AddressCharacteristic | FranceAddress | 1         | France  | Paris | ParisStreet | 23456    |
    And  order item "orderItem2" with spec id "spec2" has the following characteristics
      | type                  | name         | addressId | country | city  | streetName    | postCode |
      | AddressCharacteristic | EgyptAddress | 2         | Egypt   | Cairo | TahreerStreet | 12345    |
    When the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan for order id "order1" with "Acknowledged" state
    And the plan with product order "order1" will have the following characteristics for order item "orderItem1"
      | type                  | name          | addressId | country | city  | streetName  | postCode |
      | AddressCharacteristic | FranceAddress | 1         | France  | Paris | ParisStreet | 23456    |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem2"
      | type                  | name         | addressId | country | city  | streetName    | postCode |
      | AddressCharacteristic | EgyptAddress | 2         | Egypt   | Cairo | TahreerStreet | 12345    |
