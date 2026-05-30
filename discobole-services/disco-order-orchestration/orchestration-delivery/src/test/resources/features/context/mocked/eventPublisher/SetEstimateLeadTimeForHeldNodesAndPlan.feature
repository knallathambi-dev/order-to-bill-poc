Feature: [IPCEISCOOD-1034] Updating estimated lead-time when node gets held

  Scenario: On node held set order estimate lead time and order item estimate lead time to undefined for node and plan
    Given the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State        | Order Id |
      | node1 | Completed    | order1   |
      | node2 | Held         | order1   |
      | node3 | Acknowledged | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product1 | physicalProduct | delivers          | orderItem1    | add               |
    And the node with id "node2" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product2 | CFS             | delivers          | orderItem2    | add               |
      | product1 | physicalProduct | reliesOn          | orderItem1    | add               |
    And the node with id "node3" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product3 | CFS          | delivers          | orderItem3    | add               |
      | product2 | CFS          | reliesOn          | orderItem1    | add               |
    And the plan with product order "order1" has the following order dates:
      | order start date     |
      | 2025-09-01T10:15:30Z |
    And the plan with product order "order1" has product order item "orderItem2" with order item start date "2025-09-01T10:15:30Z"
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node2" and state "Held"
    And orchestration plan node with id "node2" in plan "plan1" will have the following data
      | estimated order item delivery lead time | expected order item completion date |
      | 9223372036854775807                     | 9999-12-31T23:59:59.999Z            |
    And the plan with product order "order1" will have the following data
      | estimated order delivery lead time | expected order completion date |
      | 9223372036854775807                | 9999-12-31T23:59:59.999Z       |
