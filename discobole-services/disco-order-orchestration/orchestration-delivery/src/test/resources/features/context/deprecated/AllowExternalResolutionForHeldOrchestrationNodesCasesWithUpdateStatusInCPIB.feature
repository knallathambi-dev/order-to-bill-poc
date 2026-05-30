Feature: [IPCEISCOOD-526] Allow external resolution for held orchestration nodes cases with update status in (CPIB)

  Scenario: 2 External resolution for held node with add action for cfs product
    Given the system has orchestration plan with id "plan1" and state "Held"
    And the plan with id "plan1" has the following nodes:
      | Id    | State | Order Id |
      | node1 | Held  | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | add               |
    And the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Locked" operational state
    And  the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "PendingActive" operational state returns success
    And  the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Active" main state and "Active" operational state returns success
    When the system consumes delivery status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Completed" and service order state "Completed"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Active" and operational state will be "Active"
    And the system orchestration plan node state with id "node1" will be "Completed"

  Scenario: 3 External resolution for held nodes with delete action for cfs products
    Given the system has orchestration plan with id "plan1" and state "Held"
    And the plan with id "plan1" has the following nodes:
      | Id    | State | Order Id |
      | node1 | Held  | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | delete            |
    And the CPIB has installed product with id "product1" and type "PhysicalProduct" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Locked" operational state
    And  the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Terminated" main state and "Terminated" operational state returns success
    When the system consumes delivery status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Completed" and service order state "Completed"
    Then the CPIB installed product with id "product1" and type "PhysicalProduct" main state will be "Terminated" and operational state will be "Terminated"
    And the system orchestration plan node state with id "node1" will be "Completed"

  Scenario: 4 External resolution for held nodes with modify action for cfs products
    Given the system has orchestration plan with id "plan1" and state "Held"
    And the plan with id "plan1" has the following nodes:
      | Id    | State | Order Id |
      | node1 | Held  | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | modify            |
    And the product with id "product1" has the following characteristics:
      | Name   | Value Type | Value |
      | volume | string     | 1h    |
    And an installed product with id "product1" exists in the CPIB, associated with order id "order1" and order item id "orderItem1", having state "Created" and operational state "Locked", and the following characteristics:
      | Name   | Value Type | Value |
      | volume | string     | 1h    |
    When the system consumes delivery status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Completed" and service order state "Completed" and the following service characteristics:
      | Name   | Value Type | Value |
      | volume | string     | 1h    |
    Then the system installed product with id "product1" state will be "Created" and operational state will be "PendingActive" and has the following characteristics:
      | Name   | Value Type | Value |
      | volume | string     | 1h    |
    And the CPIB installed product with id "product1" and type "Product" main state will be "Active" and operational state will be "Active"
    And the system orchestration plan node state with id "node1" will be "Completed"