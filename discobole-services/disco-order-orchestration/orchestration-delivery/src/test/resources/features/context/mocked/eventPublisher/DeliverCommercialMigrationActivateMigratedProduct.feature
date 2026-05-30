Feature: [IPCEISCOOD-561] Deliver Commercial Migration: Activate Migrated Product

  Scenario: 2- Migration action Update orchestration plan node from InProgress to InDelivery in case of operational migration
    Given the system has orchestration plan with id "plan1", state "InProgress" and order id "order1"
    And the plan with id "plan1" has the following nodes:
      | Id    |   State    | Order Id |
      | node1 | InProgress | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | migrate           |
      | product2   | CFS          | migratedFrom      | orderItem2    | migrate           |
    And the product with id "product1" has the following characteristics:
      | Name   | Value Type | Value  |
      | chara1 | string     | value1 |
      | chara2 | string     | value2 |
    And the product with id "product2" has the following characteristics:
      | Name   | Value Type | Value  |
      | chara1 | string     | value1 |
      | chara2 | string     | value2 |
      | chara3 | string     | value2 |
    And the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "PendingActive" operational state returns success
    And the CPIB has installed product with id "product2" and type "Product" associated to order id "order1" and order item id "orderItem2" in "Active" main state and "PendingMigrate" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Active" main state and "PendingMigrate" operational state returns success
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InProgress"
    Then the system orchestration plan node state with id "node1" will be "InDelivery"
    And The system is going to have node with node id "node1" and the following related products
      | RelationType | ProductId |
      | delivers     | product1  |
      | migratedFrom | product2  |
