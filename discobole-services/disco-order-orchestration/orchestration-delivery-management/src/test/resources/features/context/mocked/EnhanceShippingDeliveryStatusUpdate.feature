Feature: [IPCEISCOOD-1065] Enhance Shipping - Delivery status update

  Background:
    Given the system has a delivery order with factory order id "shippingOrderId" for plan "plan1" with the following order item refs:
      | Product Order Item Id | Orchestration Node Id |
      | shipItem1             | node1                 |
      | shipItem2             | node2                 |

  Scenario: 1- Update delivery & send order item status update event when shipping is completed
    And The shipping order state change event for order with id "shippingOrderId" has the following shipping order items
      | Id    | action | order id | order item id | quantity | requested delivery date | state     |
      | node1 | add    | order1   | shipItem1     | 3        | 2026-01-20T11:10:00Z    | completed |
      | node2 | add    | order1   | shipItem2     | 1        | 2026-01-20T11:10:00Z    | completed |
    When the system consumes shipping order state change event from the topic "disco.shipping-order-management.shippingOrderStateChange-event"
    Then delivery management will publish delivery order item status events with the following data:
      | Orchestration Node Id | Delivery Status | Node Status |
      | node1                 | executed        | Completed   |
      | node2                 | executed        | Completed   |
    And order item "shipItem1" in delivery order with factory order id "shippingOrderId" will have "executed" delivery status and "Completed" node status
    And order item "shipItem2" in delivery order with factory order id "shippingOrderId" will have "executed" delivery status and "Completed" node status


  Scenario: 2- Update delivery & send order item status update event when shipping is held
    And The shipping order state change event for order with id "shippingOrderId" has the following shipping order items
      | Id    | action | order id | order item id | quantity | requested delivery date | state |
      | node1 | add    | order1   | shipItem1     | 3        | 2026-01-20T11:10:00Z    | held  |
      | node2 | add    | order1   | shipItem2     | 1        | 2026-01-20T11:10:00Z    | held  |
    When the system consumes shipping order state change event from the topic "disco.shipping-order-management.shippingOrderStateChange-event"
    Then delivery management will publish delivery order item status events with the following data:
      | Orchestration Node Id | Delivery Status | Node Status |
      | node1                 | held            | Held        |
      | node2                 | held            | Held        |
    And order item "shipItem1" in delivery order with factory order id "shippingOrderId" will have "held" delivery status and "Held" node status
    And order item "shipItem2" in delivery order with factory order id "shippingOrderId" will have "held" delivery status and "Held" node status

  Scenario: 3- Update delivery & send order item status update event when shipping is failed
    And The shipping order state change event for order with id "shippingOrderId" has the following shipping order items
      | Id    | action | order id | order item id | quantity | requested delivery date | state  |
      | node1 | add    | order1   | shipItem1     | 3        | 2026-01-20T11:10:00Z    | failed |
      | node2 | add    | order1   | shipItem2     | 1        | 2026-01-20T11:10:00Z    | failed |
    When the system consumes shipping order state change event from the topic "disco.shipping-order-management.shippingOrderStateChange-event"
    Then delivery management will publish delivery order item status events with the following data:
      | Orchestration Node Id | Delivery Status | Node Status |
      | node1                 | executed        | Failed      |
      | node2                 | executed        | Failed      |
    And order item "shipItem1" in delivery order with factory order id "shippingOrderId" will have "executed" delivery status and "Failed" node status
    And order item "shipItem2" in delivery order with factory order id "shippingOrderId" will have "executed" delivery status and "Failed" node status
