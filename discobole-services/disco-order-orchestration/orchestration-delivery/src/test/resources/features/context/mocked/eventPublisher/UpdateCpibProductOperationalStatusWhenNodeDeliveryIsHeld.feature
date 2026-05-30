#Feature: [IPCEISCOOD-657]  Update CPIB operational status to Locked when node status is changed from InDelivery to held
Feature: [IPCEISCOOD-810] Update CPIB state before Node state (Held)

  Scenario: 1 Update CPIB operational status to "Locked" when when node status is held and node has action add
    Given  the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "PendingActive" operational state
    And    the system has orchestration plan with id "plan1" and state "InProgress"
    And    the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | add               |
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "Locked" operational state returns success
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Held"
    Then  the CPIB installed product with id "product1" and type "Product" main state will be "Created" and operational state will be "Locked"
    And the system orchestration plan node state with id "node1" will be "Held"

  Scenario: 2 Update CPIB operational status to Locked when node status held and node has action migrate
    Given  the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "PendingActive" operational state
    And    the system has orchestration plan with id "plan1" and state "InProgress"
    And    the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | migrate           |
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "Locked" operational state returns success
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Held"
    Then  the CPIB installed product with id "product1" and type "Product" main state will be "Created" and operational state will be "Locked"
    And the system orchestration plan node state with id "node1" will be "Held"

  Scenario: 3 Update CPIB operational status to LockedActive when node status held  and node has action modify
    Given  the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Active" main state and "PendingModification" operational state
    And    the system has orchestration plan with id "plan1" and state "InProgress"
    And    the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | modify            |
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Active" main state and "LockedActive" operational state returns success
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Held"
    Then  the CPIB installed product with id "product1" and type "Product" main state will be "Active" and operational state will be "LockedActive"
    And the system orchestration plan node state with id "node1" will be "Held"


  Scenario: 4 Update CPIB operational status  to LockedActive when delivery starts and node has action delete
    Given  the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Active" main state and "PendingTerminate" operational state
    And    the system has orchestration plan with id "plan1" and state "InProgress"
    And    the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | delete            |
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Active" main state and "LockedActive" operational state returns success
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Held"
    Then  the CPIB installed product with id "product1" and type "Product" main state will be "Active" and operational state will be "LockedActive"
    And the system orchestration plan node state with id "node1" will be "Held"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Held"

  Scenario: 5 Node state update to held  because of  CPIB update failure due to internal server error
    Given  the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "PendingActive" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "Locked" operational state returns success
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Active" main state and "Active" operational state returns service unavailable
    And    the system has orchestration plan with id "plan1" and state "InProgress"
    And    the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1 | CFS          | delivers          | orderItem1    | add               |
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Completed"
    Then the system orchestration plan node state with id "node1" will be "Held"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Held"


  Scenario: 6 Update CPIB operational status to "Locked" when delivery status  is "Held" and node has action add for physicalProduct
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Locked" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "Locked" operational state returns success
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product1 | physicalProduct | delivers          | orderItem1    | add               |
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Held"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Created" and operational state will be "Locked"
    And the system orchestration plan node state with id "node1" will be "Held"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Held"

  Scenario: 7 Node state update to held  because of  CPIB update failure due to internal server error for physicalProduct
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "PendingActive" operational state
    And the PATCH request for CPIB installed product with id "product1" returns service unavailable
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "Locked" operational state returns success
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product1 | physicalProduct | delivers          | orderItem1    | add               |
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Completed"
    Then the system orchestration plan node state with id "node1" will be "Held"

