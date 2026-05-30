Feature: [IPCEISCOOD-862] Allow external resolution for held nodes for delivery factory failure

  Scenario: CPIB product operational state is updated to pendingActive then to Active for cfs product
    Given the system has orchestration plan with id "plan1" and state "Held"
    And the plan with id "plan1" has the following nodes:
      | Id    | State | Order Id |
      | node1 | Held  | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | add               |
    And the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Locked" operational state
    And  the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "PendingActive" operational state returns success
    And  the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Active" main state and "Active" operational state returns success
    When the system consumes delivery status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Completed" and service order state "Completed"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Created" and operational state will be "PendingActive"
    And the CPIB installed product with id "product1" and type "Product" main state will be "Active" and operational state will be "Active"
    And the system orchestration plan node state with id "node1" will be "Completed"