#Feature: [IPCEISCOOD-656] Update CFS CPIB products operational status when Node delivery is Failed
Feature: [IPCEISCOOD-812] Update CPIB state before Node state (Failed)

  Scenario: 1 Update CPIB operational status to Aborted when delivery factory is Failed and node has action add
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "PendingActive" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Aborted" main state and "Aborted" operational state returns success
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    |    State   | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | add               |
    And  the node with id "node1" has the following realizing services:
      | Id     | realizing service id | realizing service href |
      | order1 | rSId                 | rSHref                 |
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Failed"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Aborted" and operational state will be "Aborted"
    And  the system orchestration plan node state with id "node1" will be "Failed"
    And  the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Failed"


  Scenario: 2 Update CPIB operational status to Aborted when delivery factory is Failed and node has action migrate
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "PendingActive" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Aborted" main state and "Aborted" operational state returns success
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    |    State   | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | migrate           |
    And  the node with id "node1" has the following realizing services:
      | Id     | realizing service id | realizing service href |
      | order1 | rSId                 | rSHref                 |
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Failed"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Aborted" and operational state will be "Aborted"
    And  the system orchestration plan node state with id "node1" will be "Failed"
    And  the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Failed"


  Scenario: 3 Update CPIB operational status to Active when delivery factory is Failed and node has action modify
    Given  the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "PendingModification" operational state
    And     the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Active" main state and "Active" operational state returns success
    And    the system has orchestration plan with id "plan1" and state "InProgress"
    And    the plan with id "plan1" has the following nodes:
      | Id    |    State   | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | modify            |
    And  the node with id "node1" has the following realizing services:
      | Id     | realizing service id | realizing service href |
      | order1 | rSId                 | rSHref                 |
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Failed"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Active" and operational state will be "Active"
    And  the system orchestration plan node state with id "node1" will be "Failed"
    And  the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Failed"

  Scenario: 4 Update CPIB operational status to Active when delivery factory is Failed and node has action delete
    Given  the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "PendingTerminate" operational state
    And     the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Active" main state and "Active" operational state returns success
    And    the system has orchestration plan with id "plan1" and state "InProgress"
    And    the plan with id "plan1" has the following nodes:
      | Id    |    State   | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | delete            |
    And  the node with id "node1" has the following realizing services:
      | Id     | realizing service id | realizing service href |
      | order1 | rSId                 | rSHref                 |
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Failed"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Active" and operational state will be "Active"
    And  the system orchestration plan node state with id "node1" will be "Failed"
    And  the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Failed"

Scenario: 5 Update CPIB operational status to Aborted for physicalProduct when delivery factory is Failed and node has action add
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "PendingActive" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Aborted" main state and "Aborted" operational state returns success
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    |    State   | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1 | physicalProduct| delivers          | orderItem1    | add               |
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Failed"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Aborted" and operational state will be "Aborted"
    And  the system orchestration plan node state with id "node1" will be "Failed"
    And  the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Failed"




