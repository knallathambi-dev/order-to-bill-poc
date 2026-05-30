Feature: [IPCEISCOOD-940] Support DateCharacteristic type

  Scenario: 1- support DateCharacteristic type with same format as received from OM
    Given There is no orchestration plan created
    And  Product Catalog has no characteristics for spec "spec1"
    And  OM has product order "order1" with status "accepted"
    And  order item "orderItem1" with spec id "spec1" has the following characteristics
      | type               | name                        | value                    |
      | DateCharacteristic | Requested installation date | 2025-09-16T08:35:51.963Z |
    When the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan for order id "order1" with "Acknowledged" state
    And the plan with product order "order1" will have the following characteristics for order item "orderItem1"
      | type               | name                        | value                    |
      | DateCharacteristic | Requested installation date | 2025-09-16T08:35:51.963Z |

  Scenario: 2- support DateCharacteristic type with same format as received from catalog
    Given There is no orchestration plan created
    And Product Catalog has the following characteristics for spec "spec1"
      | type               | name                        | value                    |
      | DateCharacteristic | Requested installation date | 2025-09-16T08:35:51.963Z |
    And  OM has product order "order1" with status "accepted"
    And order item "orderItem1" with spec id "spec1" has no characteristics
    When the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan for order id "order1" with "Acknowledged" state
    And the plan with product order "order1" will have the following characteristics for order item "orderItem1"
      | type               | name                        | value                    |
      | DateCharacteristic | Requested installation date | 2025-09-16T08:35:51.963Z |

  Scenario: 3- support DateCharacteristic type with same format as received from OM and catalog with different  value
    Given There is no orchestration plan created
    And Product Catalog has the following characteristics for spec "spec1"
      | type               | name                        | value                    |
      | DateCharacteristic | Requested installation date | 2024-09-16T08:35:51.963Z |
    And  OM has product order "order1" with status "accepted"  
    And  order item "orderItem1" with spec id "spec1" has the following characteristics
      | type               | name                        | value                   |
      | DateCharacteristic | Requested installation date | 2025-01-01T08:35:51.963Z |
    When  the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan for order id "order1" with "Acknowledged" state
    And the plan with product order "order1" will have the following characteristics for order item "orderItem1"
      | type               | name                        | value                   |
      | DateCharacteristic | Requested installation date | 2025-01-01T08:35:51.963Z |