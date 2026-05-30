Feature: [IPCEISCOOD-932] Evolve Create Orchestration Plan - add time parameters for immediate delivery orders

  Scenario: 1- Immediate delivery order - plan, root nodes, and execution start immediately
    Given the current system timestamp is "2025-09-01T10:15:30Z"
    And Product Catalog has no characteristics for spec "spec1"
    And Product Catalog has no characteristics for spec "spec2"
    And OM has product order "order1" with status "accepted" and requested delivery date is "2025-09-01T10:15:30Z"
    And order item "orderItem1" with spec id "spec1" has no characteristics
    And order item "orderItem2" with spec id "spec2" has no characteristics
    When the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And the plan with product order "order1" has order start date "2025-09-01T10:15:30Z"
    And the plan with product order "order1" will have product order item "orderItem1" with order item start date "2025-09-01T10:15:30Z"
    And the plan with product order "order1" will have product order item "orderItem2" with order item start date "2025-09-01T10:15:30Z"


  Scenario: 2- Immediate delivery for child nodes when parent node state changes to Completed
    Given the current system timestamp is "2025-09-01T10:15:30Z"
    And the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State        | Order Id |
      | node1 | Completed    | order1   |
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
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Completed"
    Then the system orchestration plan node state with id "node2" will be "InProgress"
    And the system orchestration plan node state with id "node3" will be "InProgress"
    And the plan with product order "order1" will have product order item "orderItem2" with order item start date "2025-09-01T10:15:30Z"
    And the plan with product order "order1" will have product order item "orderItem3" with order item start date "2025-09-01T10:15:30Z"
