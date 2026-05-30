Feature: [IPCEISCOOD-1105] Evolve Tangible Products Delivery - Deliver Grouped Shipment To Shipping Factory

  Scenario: 1- Check that when a delivery order with multiple tangible order items is received, only one shipping order request is created
    Given a delivery order with id "deliveryOrder1" and product order id "order1" and the "ShippingOrderManagement" delivery factory type
    And the delivery order has the following order items:
      | Order Item Id | action | quantity |
      | orderItem1    | add    | 3        |
      | orderItem2    | add    | 1        |
    And order item "orderItem1" has the following characteristics:
      | type                 | name                    | value                                                  |
      | stringCharacteristic | Shipping mode           | Home delivery                                          |
      | stringCharacteristic | Shipping Address        | 111 Quai Du President Roosevelt, France, Paris - 92449 |
      | dateCharacteristic   | requested delivery date | 2026-01-20T11:10:00Z                                   |
    And order item "orderItem2" has the following characteristics:
      | type                 | name                    | value                                                  |
      | stringCharacteristic | Shipping mode           | Home delivery                                          |
      | stringCharacteristic | Shipping Address        | 111 Quai Du President Roosevelt, France, Paris - 92449 |
      | dateCharacteristic   | requested delivery date | 2026-01-20T11:10:00Z                                   |
    And the delivery order has the following related parties:
      | Id           | name             |
      | relatedParty | relatedPartyName |
   And shipping order response returns shipping order "shippingOrderId" with the following order items:
      | Id        | action | order id | order item id | quantity | requested delivery date |
      | shipItem1 | add    | order1   | orderItem1    | 3        | 2026-01-20T11:10:00Z    |
      | shipItem2 | add    | order1   | orderItem2    | 1        | 2026-01-20T11:10:00Z    |
    When delivery management consumes the delivery order event
    Then delivery management will send a shipping order request with the following order items:
      | action | order id | order item id | quantity | requested delivery date |
      | add    | order1   | orderItem1    | 3        | 2026-01-20T11:10:00Z    |
      | add    | order1   | orderItem2    | 1        | 2026-01-20T11:10:00Z    |
    And the shipping order request will have the following related parties:
      | Id           | name             |
      | relatedParty | relatedPartyName |
    And the shipping order request will have the following characteristics:
      | type                 | name                    | value                                                  |
      | stringCharacteristic | Shipping mode           | Home delivery                                          |
      | stringCharacteristic | Shipping Address        | 111 Quai Du President Roosevelt, France, Paris - 92449 |
      | dateCharacteristic   | requested delivery date | 2026-01-20T11:10:00Z                                   |
    And the delivery order "deliveryOrder1" will have factory order id "shippingOrderId"
    And order item "orderItem1" in delivery order "deliveryOrder1" will have factory order item id "shipItem1"
    And order item "orderItem2" in delivery order "deliveryOrder1" will have factory order item id "shipItem2"
