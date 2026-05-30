Feature: [IPCEISCOOD-575] correct CPIB product state update for tangible product

  Scenario: Update CPIB operational status  for tangible to "Locked"  when node status is held
    Given  the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "PendingDelivery" operational state
    And    the system has orchestration plan with id "plan1" and state "InProgress"
    And    the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InProgress | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product1   | physicalProduct | delivers          | orderItem1    | add               |
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "Locked" operational state returns success
    When  the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event-dlt" with node id "node1" and state "Held"
    Then  the CPIB installed product with id "product1" and type "Product" main state will be "Created" and operational state will be "Locked"
    And the system orchestration plan node state with id "node1" will be "Held"
