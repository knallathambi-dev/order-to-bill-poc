Feature: Deserialization error is not handled in http calls

  Scenario: Consumer InProgress Orchestration plan node state change event with Cpib installed product statues is invalid
    Given the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    |    State   | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id |  Product Type   | Relationship Type | Order Item Id |
      | product1   | physicalProduct | delivers          | orderItem1    |
    And the CPIB has installed product wih id "product1" associated with order id "order1" and order item id "orderItem1" and invalid product type
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Completed"
    Then Fallout incident will be created with error code "COOD_DECODING_EXCEPTION"

