Feature: [IPCEISCOOD-510] Set Realizing Service correctly for cfs products

  Scenario: Delivery service sends realizing service details within the node to the orchestration management
    Given the system has a delivery order with factory order id "order1" for plan "plan1" with the following order item refs:
      | Product Order Item Id | Orchestration Node Id |
      | item1                 | node1                 |
    When the system should consume a serviceOrderStateChange event from topic "disco.service-order-management.serviceOrderStateChange-event" for service order ID "order1" and order item ID "item1" with status update "Completed" and the following service characteristics:
      | Name   | Value Type | Value | Service id | Service href |
      | volume | string     | 2h    | serviceId  | serviceHref  |
    Then the system should publish a Delivery Status event to topic "disco.delivery-management.deliveryOrderItemStatus-event" for the orchestration plan node with ID "node1", updating its state to "Completed" with the following realizing service details:
      | Order Id | realizing service id | realizing service href |
      | order1   | serviceId            | serviceHref            |
