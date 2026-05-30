Feature: [IPCEISCOOD-929] Updating the Orchestration time parameters along the orchestration process

  Scenario: 1-  manage the time parameters for all orchestration plan nodes when status InProgress for immediate delivery
    Given the current system timestamp is "2025-09-01T10:15:30Z"
    And the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "Confirmed" operational state returns success
    And  the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "PendingActive" operational state returns success
    And the system has orchestration plan with id "plan1", state "InProgress" and order id "order1"
    And the plan with product order "order1" has the following order dates:
      | order start date     |
      | 2025-09-01T10:15:30Z |
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InProgress | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | add               |
    And the plan with product order "order1" has product order item "orderItem1" with order item start date "2025-09-01T10:15:30Z"
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InProgress"
    Then orchestration plan node with id "node1" in plan "plan1" will have the following data
      | order item start date | actual order item start date |
      | 2025-09-01T10:15:30Z  | 2025-09-01T10:15:30Z         |

  Scenario: 2-  manage the time parameters for orchestration plan when status InProgress for immediate delivery
    Given the current system timestamp is "2025-09-01T10:15:30Z"
    And the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "Confirmed" operational state returns success
    And the system has orchestration plan with id "plan1", state "Acknowledged" and order id "order1"
    And the plan with product order "order1" has the following order dates:
      | order start date     |
      | 2025-09-01T10:15:30Z |
    And the plan with id "plan1" has the following nodes:
      | Id    | State        | Order Id |
      | node1 | Acknowledged | order1   |
    And the node with id "node1" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1 | CFS          | delivers          | orderItem1    | add               |
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "PendingActive" operational state returns success
    And the plan with product order "order1" has product order item "orderItem1" with order item start date "2025-09-01T10:15:30Z"
    When the system consumes orchestration plan state change event on topic "disco.order-orchestration.orchestrationPlanStateChange-event" with plan id "plan1" and state "Acknowledged"
    Then the plan with product order "order1" will have the following data
      | order start date     | actual order start date |
      | 2025-09-01T10:15:30Z | 2025-09-01T10:15:30Z    |

  Scenario: 3-  manage the time parameters for all orchestration plan nodes status Completed for immediate delivery
    Given the current system timestamp is "2025-09-01T10:15:30Z"
    And   the system has orchestration plan with id "plan1", state "InProgress" and order id "order1"
    And   the plan with product order "order1" has the following order dates:
      | actual order start date | order start date     |
      | 2025-09-01T11:15:30Z    | 2025-09-01T10:15:30Z |
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InProgress | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | add               |
    And the plan with product order "order1" has the following product order item "orderItem1"
      | actual order item start date | order item start date |
      | 2025-09-01T11:15:30Z         | 2025-09-01T10:15:30Z  |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "Completed"
    Then the plan with product order "order1" will have the following data
      | order start date     | actual order start date | actual order completion date |
      | 2025-09-01T10:15:30Z | 2025-09-01T11:15:30Z    | 2025-09-01T10:15:30Z         |
    And  orchestration plan node with id "node1" in plan "plan1" will have the following data
      | order item start date | actual order item start date | actual order item completion date |
      | 2025-09-01T10:15:30Z  | 2025-09-01T11:15:30Z         | 2025-09-01T10:15:30Z              |
