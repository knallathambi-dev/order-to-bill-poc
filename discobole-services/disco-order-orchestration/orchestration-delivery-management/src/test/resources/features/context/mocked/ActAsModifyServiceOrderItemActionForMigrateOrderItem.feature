Feature: [IPCEISCOOD-564] Act As Modify ServiceOrderItem Action For Migrate OrderItem

  Scenario: Delivery service create a service order for migrate order item with modify service order item
    Given The delivery order event for order with id "order1" and the delivery factory type is "ServiceOrderManagement" has the following order item refs:
      | Product Order Item Id | Orchestration Node Id | Service Specification Id | action  |
      | item1                 | node1                 | serviceSpec1             | migrate |
    And the delivery order request has the following data:
      | Requested Delivery Date | Start Date           | Product Order Id | Orchestration Plan Id |
      | 2026-01-20T11:10:00Z    | 2026-01-20T11:10:00Z | order1           | plan1                 |
    When the system consumes delivery order event on topic "disco.delivery-management.deliveryOrder-event" with factory order id "factoryOrderId" and state "InDelivery"
    Then the system will create a service order request with the following service orders:
      | service order item action | service specification id |
      | modify                    | serviceSpec1             |
