Feature: [IPCEISCOOD-1105] Evolve the tangible delivery management and follow up to use the deliveryOrder Model

  Scenario: Orchestration node is updated with the related supply chain while consuming delivery status event
    Given the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    |    State   | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product1   | physicalProduct | delivers          | orderItem1    | add               |
    And the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Sold" main state and "Sold" operational state returns success
    And the delivery order item state change event for orchestration plan node with id "node1" and state "Completed" has the following factory details:
      | factory order id | factory order item id |
      | factoryOrderID1  | factoryOrderItemID1   |
    When the system consumes delivery status event from topic "disco.delivery-management.deliveryOrderItemStatus-event"
    Then node "node1" is updated with the following related supply chain order data:
      | related supply chain order id | related supply chain order item id  |
      | factoryOrderID1               | factoryOrderItemID1                 |
