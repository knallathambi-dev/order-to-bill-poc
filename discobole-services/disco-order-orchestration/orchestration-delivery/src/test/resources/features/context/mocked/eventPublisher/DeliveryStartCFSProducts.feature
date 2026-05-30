Feature: [IPCEISCOOD-1104] Evolve the service delivery management

  Scenario: Start bulk delivery when all eligible tangible nodes are in InDelivery state
    Given the system has orchestration plan with id "plan1", state "InProgress" and order id "order1"
    And the plan with product order "order1" has the following order dates:
      | order start date     |
      | 2025-12-16T10:15:30Z |
    And  the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InProgress | order1   |
    And  the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | add               |
    And the product with id "product1" has the following characteristics:
      | Name   | Value Type | Value |
      | volume | string     | 1h    |
    And the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "PendingActive" operational state returns success
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InProgress"
    Then the system orchestration plan node state with id "node1" will be "InDelivery"
    And the system will fire delivery start event on topic "disco.delivery-management.deliveryOrder-event" with "ServiceOrderManagement" delivery factory type, "2025-12-16T10:15:30Z" start date and the following node ids:
      | Id    |
      | node1 |
    And the delivery start event will have the following characteristics for node id "node1"
      | type                 | name   | value | Value Type |
      | StringCharacteristic | volume | 1h    | string     |
