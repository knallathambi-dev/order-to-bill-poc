Feature: [IPCEISCOOD-1104] send service order request after consuming delivery order event

  Scenario: Delivery service create a service order for delivery order
    Given The delivery order event for order with id "order1" and the delivery factory type is "ServiceOrderManagement" has the following order item refs:
      | Product Order Item Id | Orchestration Node Id | Service Specification Id | action |
      | item1                 | node1                 | serviceSpec1             | add    |
    And the service delivery order has the following related parties:
      | Id           | name             |
      | relatedParty | relatedPartyName |
    And order item ref with product order item Id "item1" has the following characteristics:
      | type                 | name   | value |
      | stringCharacteristic | volume | 1h    |
    And the delivery order request has the following data:
      | Requested Delivery Date | Start Date           | Product Order Id | Orchestration Plan Id |
      | 2026-01-20T11:10:00Z    | 2026-01-20T11:10:00Z | order1           | plan1                 |
    And service order response returns service order "deliveryOrder1" with the following order items:
      | Id           | action | quantity |
      | serviceItem1 | add    | 3        |
    When the system consumes delivery order event on topic "disco.delivery-management.deliveryOrder-event" with factory order id "factoryOrderId" and state "InDelivery"
    Then the system will create a service order request with the following service orders:
      | service order item action | service specification id |
      | add                       | serviceSpec1             |
    And the service order request will have the following related parties:
      | Id           | name             |
      | relatedParty | relatedPartyName |
    And the service order request will have the following characteristics:
      | type                 | name   | value |
      | StringCharacteristic | volume | 1h    |
    And the delivery order "order1" will have factory order id "deliveryOrder1"
    And order item "item1" in delivery order "order1" will have factory order item id "serviceItem1"

