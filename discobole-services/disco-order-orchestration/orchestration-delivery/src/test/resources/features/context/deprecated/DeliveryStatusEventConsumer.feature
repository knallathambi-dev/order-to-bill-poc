Feature: Delivery status event consumer feature

  Scenario: Consume completed orchestration plan node delivery status
    Given the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product1   | physicalProduct | delivers          | orderItem1    | add               |
    And the CPIB has installed product with id "product1" and type "PhysicalProduct" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Confirmed" operational state
    And  the PATCH request for the CPIB installed product with id "product1" and type "PhysicalProduct" and "Sold" main state and "Sold" operational state returns success
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Completed"
    Then the CPIB installed product with id "product1" and type "PhysicalProduct" main state will be "Sold" and operational state will be "Sold"
    And the system orchestration plan node state with id "node1" will be "Completed"

  Scenario: Consume held orchestration plan node delivery status
    Given the system has orchestration plan with id "plan2" and state "InProgress"
    And the plan with id "plan2" has the following nodes:
      | Id    | State      | Order Id |
      | node2 | InDelivery | order1   |
    And the node with id "node2" has the following related products:
      | Product Id | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product1   | physicalProduct | delivers          | orderItem1    | add               |
    And the CPIB has installed product with id "product1" and type "PhysicalProduct" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Confirmed" operational state
    And the POST request for the process flow returns success
    When the PATCH request for the CPIB installed product with id "product1" and type "PhysicalProduct" and "Created" main state and "Locked" operational state returns success
    And the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node2" and state "Held"
    Then  the CPIB installed product with id "product1" and type "PhysicalProduct" main state will be "Created" and operational state will be "Locked"
    And the system orchestration plan node state with id "node2" will be "Held"
