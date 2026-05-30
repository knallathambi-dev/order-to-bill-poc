#Feature: [IPCEISCOOD-660] Update CPIB operational status when Orchestration Node is aborted
Feature: [IPCEISCOOD-811] Update CPIB state before Node state (Aborted)

  Scenario: 1 Update CPIB operational status to "Aborted" when the parent node is Failed and has action add
    Given the CPIB has installed product with id "product2" and type "Product" associated to order id "order1" and order item id "orderItem2" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product2" and type "Product" and "Aborted" main state and "Aborted" operational state returns success
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State        | Order Id |
      | node1 | Failed       | order1   |
      | node2 | Acknowledged | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1 | CFS          | delivers          | orderItem1    | add               |
    And the node with id "node2" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product2 | CFS          | delivers          | orderItem2    | add               |
      | product1 | CFS          | reliesOn          | orderItem1    | add               |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Failed"
    Then the CPIB installed product with id "product2" and type "Product" main state will be "Aborted" and operational state will be "Aborted"
    And the system orchestration plan node state with id "node2" will be "Aborted"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node2" and state "Aborted"

  Scenario: 2 Update CPIB operational status to "Aborted" when the parent node is Failed and has action migrate
    Given the CPIB has installed product with id "product2" and type "Product" associated to order id "order1" and order item id "orderItem2" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product2" and type "Product" and "Aborted" main state and "Aborted" operational state returns success
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State        | Order Id |
      | node1 | Failed       | order1   |
      | node2 | Acknowledged | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1 | CFS          | delivers          | orderItem1    | migrate           |
    And the node with id "node2" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product2 | CFS          | delivers          | orderItem2    | migrate           |
      | product1 | CFS          | reliesOn          | orderItem1    | migrate           |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Failed"
    Then the CPIB installed product with id "product2" and type "Product" main state will be "Aborted" and operational state will be "Aborted"
    And the system orchestration plan node state with id "node2" will be "Aborted"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node2" and state "Aborted"

  Scenario: 3 Update CPIB operational status to "Aborted" when the parent node is Failed and has action modify
    Given the CPIB has installed product with id "product2" and type "Product" associated to order id "order1" and order item id "orderItem2" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product2" and type "Product" and "Created" main state and "Created" operational state returns success
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State        | Order Id |
      | node1 | Failed       | order1   |
      | node2 | Acknowledged | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1 | CFS          | delivers          | orderItem1    | modify            |
    And the node with id "node2" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product2 | CFS          | delivers          | orderItem2    | modify            |
      | product1 | CFS          | reliesOn          | orderItem1    | modify            |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Failed"
    Then the CPIB installed product with id "product2" will not be updated
    And the system orchestration plan node state with id "node2" will be "Aborted"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node2" and state "Aborted"


  Scenario: 4 Update CPIB operational status to "Aborted" when the parent node is Failed and has action delete
    Given the CPIB has installed product with id "product2" and type "Product" associated to order id "order1" and order item id "orderItem2" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product2" and type "Product" and "Created" main state and "Created" operational state returns success
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State        | Order Id |
      | node1 | Failed       | order1   |
      | node2 | Acknowledged | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1 | CFS          | delivers          | orderItem1    | delete            |
    And the node with id "node2" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product2 | CFS          | delivers          | orderItem2    | delete            |
      | product1 | CFS          | reliesOn          | orderItem1    | delete            |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Failed"
    Then the CPIB installed product with id "product2" will not be updated
    And the system orchestration plan node state with id "node2" will be "Aborted"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node2" and state "Aborted"

  Scenario: 5 Update CPIB operational status to "Aborted" when the parent node is Aborted and has action add
    Given the CPIB has installed product with id "product2" and type "Product" associated to order id "order1" and order item id "orderItem2" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product2" and type "Product" and "Aborted" main state and "Aborted" operational state returns success
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State        | Order Id |
      | node1 | Aborted      | order1   |
      | node2 | Acknowledged | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1 | CFS          | delivers          | orderItem1    | add               |
    And the node with id "node2" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product2 | CFS          | delivers          | orderItem2    | add               |
      | product1 | CFS          | reliesOn          | orderItem1    | add               |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Aborted"
    Then the CPIB installed product with id "product2" and type "Product" main state will be "Aborted" and operational state will be "Aborted"
    And the system orchestration plan node state with id "node2" will be "Aborted"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node2" and state "Aborted"


  Scenario: 6 Update CPIB operational status to "Aborted" for all children nodes when the parent node is Failed and has action add
    Given the CPIB has installed product with id "product2" and type "Product" associated to order id "order1" and order item id "orderItem2" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product2" and type "Product" and "Aborted" main state and "Aborted" operational state returns success
    And the CPIB has installed product with id "product3" and type "Product" associated to order id "order1" and order item id "orderItem3" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product3" and type "Product" and "Aborted" main state and "Aborted" operational state returns success
    And the CPIB has installed product with id "product4" and type "Product" associated to order id "order1" and order item id "orderItem4" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product4" and type "Product" and "Aborted" main state and "Aborted" operational state returns success
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State        | Order Id |
      | node1 | Failed       | order1   |
      | node2 | Acknowledged | order1   |
      | node3 | Acknowledged | order1   |
      | node4 | Acknowledged | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1 | CFS          | delivers          | orderItem1    | add               |
    And the node with id "node2" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product2 | CFS          | delivers          | orderItem2    | add               |
      | product1 | CFS          | reliesOn          | orderItem1    | add               |
    And the node with id "node3" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product3 | CFS          | delivers          | orderItem3    | add               |
      | product1 | CFS          | reliesOn          | orderItem1    | add               |
    And the node with id "node4" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product4 | CFS          | delivers          | orderItem4    | add               |
      | product1 | CFS          | reliesOn          | orderItem1    | add               |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Failed"
    Then the CPIB installed product with id "product2" and type "Product" main state will be "Aborted" and operational state will be "Aborted"
    And the system orchestration plan node state with id "node2" will be "Aborted"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node2" and state "Aborted"
    And the system orchestration plan node state with id "node3" will be "Aborted"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node3" and state "Aborted"
    And the system orchestration plan node state with id "node4" will be "Aborted"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node4" and state "Aborted"


  Scenario: 7 Ensure CPIB update failure in one child node does not impact parent or sibling nodes
    Given the CPIB has installed product with id "product2" and type "Product" associated to order id "order1" and order item id "orderItem2" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product2" and type "Product" and "Aborted" main state and "Aborted" operational state returns success
    And the CPIB has installed product with id "product3" and type "Product" associated to order id "order1" and order item id "orderItem3" in "Created" main state and "Confirmed" operational state
    And the PATCH request for CPIB installed product with id "product3" returns service unavailable
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State        | Order Id |
      | node1 | Failed       | order1   |
      | node2 | Acknowledged | order1   |
      | node3 | Acknowledged | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1 | CFS          | delivers          | orderItem1    | add               |
    And the node with id "node2" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product2 | CFS          | delivers          | orderItem2    | add               |
      | product1 | CFS          | reliesOn          | orderItem1    | add               |
    And the node with id "node3" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product3 | CFS          | delivers          | orderItem3    | add               |
      | product1 | CFS          | reliesOn          | orderItem1    | add               |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Failed"
    Then the CPIB installed product with id "product2" and type "Product" main state will be "Aborted" and operational state will be "Aborted"
    And the system orchestration plan node state with id "node2" will be "Aborted"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node2" and state "Aborted"
    And the system orchestration plan node state with id "node3" will be "Acknowledged"