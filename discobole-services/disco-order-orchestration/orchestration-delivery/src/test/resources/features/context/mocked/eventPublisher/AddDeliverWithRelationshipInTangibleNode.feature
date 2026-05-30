Feature: [IPCEISCOOD-1062] Enhance Shipping - Evolve Orchestration Plan Creation with deliverWith

  Scenario: 1- Tangible product node receives shipping details via deliverWith relation
    Given There is no orchestration plan created
    And Product Catalog has no characteristics for spec "spec1"
    And Product Catalog has no characteristics for spec "spec2"
    And Product Catalog has no characteristics for spec "spec3"
    And Product Catalog has no characteristics for spec "spec4"
    And OM has product order "order1" with status "accepted"
    And product order "order1" has the following product order items
      | orderItemId | action | specificationId | relationType | relatedOrderItemId | atBaseType                   |
      | orderItem1  | add    | spec1           |              |                    |                              |
      | shipItem1   | add    | spec2           | requires     | orderItem1         | ShippingProductSpecification |
      | orderItem2  | add    | spec3           |              |                    |                              |
      | shipItem2   | add    | spec4           | requires     | orderItem2         | ShippingProductSpecification |
    And order item "orderItem1" with spec id "spec1" has the following characteristics
      | name | value    |
      | IMSI | IMSI#123 |
    And order item "shipItem1" with spec id "spec2" has the following characteristics
      | type                 | name                    | value                 |
      | stringCharacteristic | Shipping mode           | Instore               |
      | stringCharacteristic | Shipping Address        | Orange Canebière Shop |
      | dateCharacteristic   | Requested delivery date | 2025-12-25T11:55:49Z  |
    And order item "orderItem2" with spec id "spec3" has the following characteristics
      | name | value    |
      | IMSI | IMSI#123 |
    And  order item "shipItem2" with spec id "spec4" has the following characteristics
      | type                 | name                    | value                 |
      | stringCharacteristic | Shipping mode           | Instore               |
      | stringCharacteristic | Shipping Address        | Orange Canebière Shop |
      | dateCharacteristic   | Requested delivery date | 2025-12-25T11:55:49Z  |
    When the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And the plan with product order "order1" which contains order item "shipItem1" will have the following related products
      | Product Type    | Relationship Type | Order Item Id |
      | physicalProduct | delivers          | orderItem1    |
      | shipmentProduct | deliverWith       | shipItem1     |
    And the plan with product order "order1" will have the following characteristics for order item "shipItem1"
      | type                 | name                    | value                 |
      | StringCharacteristic | Shipping mode           | Instore               |
      | StringCharacteristic | Shipping Address        | Orange Canebière Shop |
      | DateCharacteristic   | Requested delivery date | 2025-12-25T11:55:49Z  |
    And the plan with product order "order1" which contains order item "shipItem2" will have the following related products
      | Product Type    | Relationship Type | Order Item Id |
      | physicalProduct | delivers          | orderItem2    |
      | shipmentProduct | deliverWith       | shipItem2     |
    And the plan with product order "order1" will have the following characteristics for order item "shipItem2"
      | type                 | name                    | value                 |
      | StringCharacteristic | Shipping mode           | Instore               |
      | StringCharacteristic | Shipping Address        | Orange Canebière Shop |
      | DateCharacteristic   | Requested delivery date | 2025-12-25T11:55:49Z  |
