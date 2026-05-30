Feature: [IPCEISCOOD-1104] Consume service order status change event and update order item status

  Background:
    Given the system has a delivery order with factory order id "factoryOrderId" for plan "plan1" with the following order item refs:
      | Product Order Item Id | Orchestration Node Id |
      | orderItem1            | node1                 |

  Scenario: 1- Update delivery & send order item status update event when service is completed
    And The service order state change event for order with id "factoryOrderId" has the following order items
      | action | order item id | quantity | state     |
      | add    | orderItem1    | 3        | Completed |
    When the system consumes service order state change event from the topic "disco.service-order-management.serviceOrderStateChange-event"
    Then delivery management will publish delivery order item status event with the following data:
      | Orchestration Node Id | Delivery Status | Node Status |
      | node1                 | executed        | Completed   |
    And order item "orderItem1" in delivery order with factory order id "factoryOrderId" will have "executed" delivery status and "Completed" node status

  Scenario: 2- Update delivery & send order item status update event when service is held
    And The service order state change event for order with id "factoryOrderId" has the following order items
      | action | order item id | quantity | state |
      | add    | orderItem1    | 3        | Held  |
    When the system consumes service order state change event from the topic "disco.service-order-management.serviceOrderStateChange-event"
    Then delivery management will publish delivery order item status event with the following data:
      | Orchestration Node Id | Delivery Status | Node Status |
      | node1                 | held            | Held        |
    And order item "orderItem1" in delivery order with factory order id "factoryOrderId" will have "held" delivery status and "Held" node status

  Scenario: 3- Update delivery & send order item status update event when service is failed
    And The service order state change event for order with id "factoryOrderId" has the following order items
      | action | order item id | quantity | state  |
      | add    | orderItem1    | 3        | Failed |
    When the system consumes service order state change event from the topic "disco.service-order-management.serviceOrderStateChange-event"
    Then delivery management will publish delivery order item status event with the following data:
      | Orchestration Node Id | Delivery Status | Node Status |
      | node1                 | executed        | Failed      |
    And order item "orderItem1" in delivery order with factory order id "factoryOrderId" will have "executed" delivery status and "Failed" node status
