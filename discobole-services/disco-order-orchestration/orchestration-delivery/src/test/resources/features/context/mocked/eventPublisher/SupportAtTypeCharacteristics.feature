Feature: [IPCEISCOOD-900]Support @type characteristics

  Scenario: 1- support StringCharacteristic type with same format as received from OM
    Given There is no orchestration plan created
    And  Product Catalog has no characteristics for spec "spec1"
    And OM has product order "order1" with status "accepted"
    And  order item "orderItem1" with spec id "spec1" has the following characteristics
      | type                 | name | value   |
      | StringCharacteristic | Tone | ToneVIP |
    When the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan for order id "order1" with "Acknowledged" state
    And the plan with product order "order1" will have the following characteristics for order item "orderItem1"
      | type                 | name | value   |
      | StringCharacteristic | Tone | ToneVIP |

  Scenario: 2- support ObjectCharacteristic type with same format as received from catalog
    Given There is no orchestration plan created
    And Product Catalog has the following characteristics for spec "spec1"
      | type                 | name     | value | unitOfMeasure |
      | ObjectCharacteristic | Quantity | 9999  | Hour          |
    And OM has product order "order1" with status "accepted"
    And order item "orderItem1" with spec id "spec1" has no characteristics
    When the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan for order id "order1" with "Acknowledged" state
    And the plan with product order "order1" will have the following characteristics for order item "orderItem1"
      | type                 | name     | value | unitOfMeasure |
      | ObjectCharacteristic | Quantity | 9999  | Hour          |

  Scenario: 3- support ValidityCharacteristic type with same format as received from OM and catalog
    Given There is no orchestration plan created
    And Product Catalog has the following characteristics for spec "spec1"
      | type                   | name     | value | unitOfMeasure |
      | ValidityCharacteristic | Validity | 30    | Days          |
    And Product Catalog has the following characteristics for spec "spec2"
      | type                   | name     | valid from               | valid to                 |
      | ValidityCharacteristic | Validity | 2025-07-16T08:35:51.963Z | 2025-09-16T08:35:51.963Z |
    And OM has product order "order1" with status "accepted"
    And  order item "orderItem1" with spec id "spec1" has the following characteristics
      | type                 | name   | value |
      | StringCharacteristic | volume | 1hr   |
    And  order item "orderItem2" with spec id "spec2" has the following characteristics
      | type                 | name  | value      |
      | StringCharacteristic | ICCID | ICCIDValue |
    When the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan for order id "order1" with "Acknowledged" state
    And the plan with product order "order1" will have the following characteristics for order item "orderItem1"
      | type                   | name     | value | unitOfMeasure |
      | ValidityCharacteristic | Validity | 30    | Days          |
      | StringCharacteristic   | volume   | 1hr   |               |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem2"
      | type                   | name     | valid from               | valid to                 | value      |
      | ValidityCharacteristic | Validity | 2025-07-16T08:35:51.963Z | 2025-09-16T08:35:51.963Z |            |
      | StringCharacteristic   | ICCID    |                          |                          | ICCIDValue |

  Scenario: 4- support AddressCharacteristic type with same format as received from OM
    Given There is no orchestration plan created
    And Product Catalog has no characteristics for spec "spec1"
    And OM has product order "order1" with status "accepted"
    And  order item "orderItem1" with spec id "spec1" has the following characteristics
      | type                  | name          | addressId | country | city  | streetName  | postCode |
      | AddressCharacteristic | FranceAddress | 1         | France  | Paris | ParisStreet | 23456    |
    When the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan for order id "order1" with "Acknowledged" state
    And the plan with product order "order1" will have the following characteristics for order item "orderItem1"
      | type                  | name          | addressId | country | city  | streetName  | postCode |
      | AddressCharacteristic | FranceAddress | 1         | France  | Paris | ParisStreet | 23456    |







