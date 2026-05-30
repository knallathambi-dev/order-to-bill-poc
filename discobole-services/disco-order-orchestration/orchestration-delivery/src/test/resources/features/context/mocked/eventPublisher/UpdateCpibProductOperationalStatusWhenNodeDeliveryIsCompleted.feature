Feature: IPCEISCOOD-813 Update CPIB state before Node state (Completed)

  Scenario: 1 Update CPIB operational status to Active when delivery factory is completed and node has action add
    Given  the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "PendingActive" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Active" main state and "Active" operational state returns success
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | add               |
    When the system consumes delivery status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Completed" with the following realizing services:
      | realizing service id | realizing service href |
      | rSId                 | rSHref                 |
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Active" and operational state will be "Active" with the following realizing service data:
      | Order Id | realizing service id | realizing service href |
      | order1   | rSId                 | rSHref                 |
    And the system orchestration plan node state with id "node1" will be "Completed"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Completed"

  Scenario: 2 Update CPIB operational status to sold for physicalProduct  when delivery factory is completed and node has action add
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "PendingActive" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Sold" main state and "Sold" operational state returns success
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product1 | physicalProduct | delivers          | orderItem1    | add               |
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Completed"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Sold" and operational state will be "Sold"
    And the system orchestration plan node state with id "node1" will be "Completed"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Completed"


  Scenario: 3  Update CPIB operational status to Active when delivery factory is completed and node has action migrate
    Given  the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "PendingActive" operational state
    And  the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Active" main state and "Active" operational state returns success
    And  the system has orchestration plan with id "plan1" and state "InProgress"
    And  the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | migrate           |
    When the system consumes delivery status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Completed" with the following realizing services:
      | Order Id | realizing service id | realizing service href |
      | order1   | rSId                 | rSHref                 |
    Then  the CPIB installed product with id "product1" and type "Product" main state will be "Active" and operational state will be "Active" with the following realizing service data:
      | Order Id | realizing service id | realizing service href |
      | order1   | rSId                 | rSHref                 |
    And the system orchestration plan node state with id "node1" will be "Completed"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Completed"

  Scenario: 4 Update CPIB operational status to Active and modify characteristics when delivery factory is completed and node has action modify
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Active" main state and "PendingModification" operational state and the following characteristics
      | id | name     |
      | 1  | validity |
      | 2  | quantity |
    And  the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Active" main state and "Active" operational state returns success
    And  the system has orchestration plan with id "plan1" and state "InProgress"
    And  the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | modify            |
    And the node "node1" actual related product has the following characteristics:
      | id    | name     |
      | 10000 | quantity |
    When the system consumes delivery status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Completed" with the following realizing services:
      | Order Id | realizing service id | realizing service href |
      | order1   | rSId                 | rSHref                 |
    Then  the CPIB installed product with id "product1" and type "Product" main state will be "Active" and operational state will be "Active" with the following realizing service data:
      | Order Id | realizing service id | realizing service href |
      | order1   | rSId                 | rSHref                 |
    And the CPIB installed product "product1" will be patched with the following characteristics:
      | id    | name     |
      | 1     | validity |
      | 10000 | quantity |
    And the system orchestration plan node state with id "node1" will be "Completed"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Completed"

  Scenario: 5 Update Node state to completed when CPIB  main state is "Active" and operational state is "Active" and node has action delete
    Given  the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Active" main state and "PendingTerminate" operational state
    And  the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Terminated" main state and "Terminated" operational state returns success
    And  the system has orchestration plan with id "plan1" and state "InProgress"
    And  the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | delete            |
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Completed"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Terminated" and operational state will be "Terminated"
    And the system orchestration plan node state with id "node1" will be "Completed"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Completed"
