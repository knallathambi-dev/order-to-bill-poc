#Feature: [IPCEISCOOD-654] Update CFS CPIB products operational status when delivery starts
Feature:  [IPCEISCOOD-809] Update CPIB state before Node state (In Delivery)

  Scenario: Before delivery, set CPIB CFS product status to PendingActive when action is add
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "PendingActive" operational state returns success
    And the system has orchestration plan with id "plan1", state "InProgress" and order id "order1"
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InProgress | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1 | CFS          | delivers          | orderItem1    | add               |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InProgress"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Created" and operational state will be "PendingActive"
    And the system orchestration plan node state with id "node1" will be "InDelivery"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InDelivery"


  Scenario: Before delivery, set CPIB CFS product status to PendingActive when action is migrate
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "PendingActive" operational state returns success
    And the CPIB has installed product with id "product2" and type "Product" associated to order id "order1" and order item id "orderItem2" in "Active" main state and "PendingMigrate" operational state
    And the system has orchestration plan with id "plan1", state "InProgress" and order id "order1"
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InProgress | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | migrate           |
      | product2   | CFS          | migratedFrom      | orderItem2    | migrate           |
    And the product with id "product1" has the following characteristics:
      | Name   | Value Type | Value |
      | volume | string     | 1h    |
    And the product with id "product2" has the following characteristics:
      | Name   | Value Type | Value |
      | volume | string     | 2h    |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InProgress"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Created" and operational state will be "PendingActive"
    And the system orchestration plan node state with id "node1" will be "InDelivery"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InDelivery"


  Scenario: Before delivery, set CPIB CFS product status to PendingModification when action is modify
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Active" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Active" main state and "PendingModification" operational state returns success
    And the system has orchestration plan with id "plan1", state "InProgress" and order id "order1"
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InProgress | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1 | CFS          | delivers          | orderItem1    | modify            |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InProgress"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Active" and operational state will be "PendingModification"
    And the system orchestration plan node state with id "node1" will be "InDelivery"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InDelivery"


  Scenario: Before delivery, set CPIB CFS product status to PendingTerminate when action is delete
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Active" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Active" main state and "PendingTerminate" operational state returns success
    And the system has orchestration plan with id "plan1", state "InProgress" and order id "order1"
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InProgress | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1 | CFS          | delivers          | orderItem1    | delete            |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InProgress"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Active" and operational state will be "PendingTerminate"
    And the system orchestration plan node state with id "node1" will be "InDelivery"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InDelivery"

  Scenario: Before delivery, set CPIB physicalProduct status to PendingDelivery when action is add
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "PendingDelivery" operational state returns success
    And the system has orchestration plan with id "plan1", state "InProgress" and order id "order1"
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InProgress | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product1 | physicalProduct | delivers          | orderItem1    | add               |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InProgress"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Created" and operational state will be "PendingDelivery"
    And the system orchestration plan node state with id "node1" will be "InDelivery"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InDelivery"

  Scenario: For commercial migration, set CPIB CFS product status to Active and node state to completed
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Active" main state and "Active" operational state returns success
    And the CPIB has installed product with id "product2" and type "Product" associated to order id "order1" and order item id "orderItem2" in "Active" main state and "PendingMigrate" operational state
    And the system has orchestration plan with id "plan1", state "InProgress" and order id "order1"
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InProgress | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | migrate           |
      | product2   | CFS          | migratedFrom      | orderItem2    | migrate           |
    And the product with id "product1" has the following characteristics:
      | Name   | Value Type | Value |
      | volume | string     | 1h    |
    And the product with id "product2" has the following characteristics:
      | Name   | Value Type | Value |
      | volume | string     | 1h    |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InProgress"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Active" and operational state will be "Active"
    And the system orchestration plan node state with id "node1" will be "Completed"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Completed"




