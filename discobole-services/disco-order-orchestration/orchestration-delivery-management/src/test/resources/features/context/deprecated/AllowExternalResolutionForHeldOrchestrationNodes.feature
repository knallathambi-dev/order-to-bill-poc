Feature: [IPCEISCOOD-526] Allow external resolution for held orchestration nodes

  Scenario: 1 External resolution for held nodes for tangible product
   Given the system has a delivery order with factory order id "supplyChain1" for plan "plan1" with the following order item refs:
      | Product Order Item Id | Orchestration Node Id |
      | item1                 | node1                 |
    When The system consumes shippingOrderStateChange event from topic "disco.shipping-order-management.shippingOrderStateChange-event" for shipping order id "supplyChain1" and order item id "item1" with status update "completed"
    Then Delivery order item status event will be published to topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration node id "node1" with delivery status "executed" and node status "Completed"

  Scenario: 2 External resolution for held node  for cfs product
    Given the system has a delivery order with factory order id "order1" for plan "plan1" with the following order item refs:
      | Product Order Item Id | Orchestration Node Id |
      | item1                 | node1                 |
    When The system consumes serviceOrderStateChange event from topic "disco.service-order-management.serviceOrderStateChange-event" for service order id "order1" and order item id "item1" with status update "Completed"
    Then Delivery order item status event will be published to topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration node id "node1" with delivery status "executed" and node status "Completed"