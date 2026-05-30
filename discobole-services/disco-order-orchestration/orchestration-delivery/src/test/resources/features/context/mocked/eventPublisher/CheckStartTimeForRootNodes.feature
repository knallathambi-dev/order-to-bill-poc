Feature: [IPCEISCOOD-930] Evolve Execute OrchestrationPlan - Validate plan start date before execution

  Scenario: 1- Set an orchestration node to status InProgress when order item start date for root nodes equal current time or null
    Given  the current system timestamp is "2025-12-16T10:15:30Z"
    And the system has orchestration plan with id "plan1", state "InProgress" and order id "order1"
    And the plan with product order "order1" has the following order dates:
      | order start date     |
      | 2025-12-16T09:15:30Z |
    And the plan with id "plan1" has the following nodes:
      | Id    | State        | Order Id | order item start date |
      | node1 | Acknowledged | order1   | 2025-12-16T09:15:30Z  |
      | node2 | Acknowledged | order1   | 2025-12-17T09:15:30Z  |
      | node3 | Acknowledged | order1   |                       |
      | node4 | Acknowledged | order1   |                       |
      | node5 | Acknowledged | order1   | 2025-12-16T09:15:30Z  |
    And the node with id "node1" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product1 | physicalProduct | delivers          | orderItem1    | add               |
    And the node with id "node2" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product2 | physicalProduct | delivers          | orderItem2    | add               |
    And the node with id "node3" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product3 | CFS             | delivers          | orderItem3    | add               |
      | product1 | physicalProduct | reliesOn          | orderItem1    | add               |
    And the node with id "node4" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product4 | physicalProduct | delivers          | orderItem4    | add               |
    And the node with id "node5" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product5 | physicalProduct | delivers          | orderItem5    | add               |
  # order item start time = current time
    When the system consumes orchestration plan state change event on topic "disco.order-orchestration.orchestrationPlanStateChange-event" with plan id "plan1" and state "InProgress"
    Then the system orchestration plan node state with id "node1" will be "InProgress"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InProgress"
    And  the system orchestration plan node state with id "node4" still be "InProgress"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node5" and state "InProgress"
    And  the system orchestration plan node state with id "node5" will be "InProgress"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node5" and state "InProgress"
    And  the system orchestration plan node state with id "node2" still be "Acknowledged"
    And  the system orchestration plan node state with id "node3" still be "Acknowledged"

  Scenario: 2 - Set an orchestration node to status InProgress when order item start date for root node is in the past
    Given the current system timestamp is "2025-12-17T10:15:30Z"
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with product order "order1" has the following order dates:
      | order start date     |
      | 2025-12-16T10:15:30Z |
    And the plan with id "plan1" has the following nodes:
      | Id    | State        | Order Id | order item start date |
      | node1 | Completed    | order1   | 2025-12-16T09:15:30Z  |
      | node2 | Acknowledged | order1   | 2025-12-17T09:15:30Z  |
      | node3 | Acknowledged | order1   |                       |
      | node4 | Acknowledged | order1   | 2025-12-17T20:15:30Z  |
      | node5 | InProgress   | order1   | 2025-12-16T20:15:30Z  |
    And the node with id "node1" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product1 | physicalProduct | delivers          | orderItem1    | add               |
    And the node with id "node2" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product2 | physicalProduct | delivers          | orderItem2    | add               |
    And the node with id "node3" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product3 | CFS             | delivers          | orderItem3    | add               |
      | product1 | physicalProduct | reliesOn          | orderItem1    | add               |
    And the node with id "node4" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product4 | physicalProduct | delivers          | orderItem4    | add               |
    And the node with id "node5" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product5 | physicalProduct | delivers          | orderItem5    | add               |
  # order item start time = current time
    When the system cron job for changing delayed root nodes from acknowledged to inprogress runs
    Then  the system orchestration plan node state with id "node2" will be "InProgress"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node2" and state "InProgress"
    And  the system orchestration plan node state with id "node3" still be "Acknowledged"
    And  the system orchestration plan node state with id "node4" still be "Acknowledged"

 