Feature: [IPCEISCOOD-861] Fallout resolution for held nodes

  Scenario: Fallout resolution for held nodes updates CPIB product state and node state then sends node state update event
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Locked" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "PendingActive" operational state returns success
    And the system has orchestration plan with id "plan1" and state "Held"
    And the plan with id "plan1" has the following nodes:
      | Id    | State | Order Id | previous state |
      | node1 | Held  | order1   | InDelivery     |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | add               |
    And the fallout service has the following fallout incidents:
      | Related Entity Id | Process Id | Task Id |
      | node1             | process1   | task1   |
    When the system consumes fallout incident state change event from topic "disco.order-orchestration-fallout.falloutIncidentStateChange-event" with id "fId", state "Completed", resolution state "resolved" and initiator node id "node1"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Created" and operational state will be "PendingActive"
    And the system orchestration plan node state with id "node1" will be "InDelivery"
    And the system will fire orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InDelivery"


