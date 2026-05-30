Feature: [IPCEISCOOD-510] Set Realizing Service correctly for cfs products

  Scenario: Cpib product is updated with the node realizing service while consuming delivery status event
    Given the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | add               |
    And the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Active" main state and "Active" operational state returns success
    And the delivery order item state change event for orchestration plan node with id "node1" and state "Completed" has the following  details:
      | realizing service id | realizing service href | factory order id | factory order item id |
      | rSId                 | rSHref                 | factoryOrderID1  | factoryOrderItemID1   |
    When the system consumes delivery status event from topic "disco.delivery-management.deliveryOrderItemStatus-event"
    Then node "node1" is updated with the following realizing service data:
      | Order Id | realizing service id | realizing service href | related service order id | related service order item id |
      | order1   | rSId                 | rSHref                 | factoryOrderID1          | factoryOrderItemID1           |
