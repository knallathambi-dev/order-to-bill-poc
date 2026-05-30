Feature: [IPCEISCOOD-839] the state of the plan is held, although some nodes are still in the delivery state

  Scenario: Update orchestration plan to InProgress if one of its nodes is inDelivery
    Given the CPIB has installed product with id "product2" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Locked" operational state
    And the PATCH request for the CPIB installed product with id "product2" and type "Product" and "Created" main state and "Locked" operational state returns success
    And the system has orchestration plan with id "plan1" and state "Held"
    And the plan with id "plan1" has the following nodes:
      | Id    | State        | Order Id |
      | node1 | Completed    | order1   |
      | node2 | Held         | order1   |
      | node3 | Held         | order1   |
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
    When the orchestration plan node with id "node2" in plan "plan1" is updated to state "InDelivery"
    And the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node2" and state "InDelivery"
    And The orchestration plan "plan1" is going to be updated with status "InProgress"