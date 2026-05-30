Feature: [IPCEISCOOD-1063] Enhance Shipping - Evolve Deliver Selected Node

  Scenario: Start bulk delivery when all eligible tangible nodes are in InDelivery state
    Given the system has orchestration plan with id "plan1", state "InProgress" and order id "order1"
    And the plan with product order "order1" has the following order dates:
      | order start date     |
      | 2025-12-16T10:15:30Z |
    And  the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
      | node2 | InDelivery | order1   |
    And  the node with id "node1" has the following related products:
      | Product Id | Product Type    | Relationship Type | Order Item Id |
      | product1   | physicalProduct | delivers          | orderItem1    |
      | product2   | shipmentProduct | deliverWith       | shipItem1     |
    And the product with id "product1" has the following characteristics:
      | Name   | Value Type | Value  |
      | chara1 | string     | value1 |
      | chara2 | string     | value2 |      
    And the node with id "node1" and order item id "shipItem1" has the fallowing shipping characteristics:
      | Shipping Mode | Shipping Address      | Requested delivery date |
      | Instore       | Orange Canebière Shop | 2025-12-25T11:55:49Z    |
    And the node with id "node2" has the following related products:
      | Product Id | Product Type    | Relationship Type | Order Item Id |
      | product3   | physicalProduct | delivers          | orderItem2    |
      | product4   | shipmentProduct | deliverWith       | shipItem2     |
    And the product with id "product3" has the following characteristics:
      | Name   | Value Type | Value  |
      | chara1 | string     | value1 |
      | chara2 | string     | value2 |
      | chara3 | string     | value2 |      
    And the node with id "node2" and order item id "shipItem2" has the fallowing shipping characteristics:
      | Shipping Mode | Shipping Address      | Requested delivery date |
      | Instore       | Orange Canebière Shop | 2025-12-25T11:55:49Z    |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InDelivery"
    Then the system will fire bulk delivery start event on topic "disco.delivery-management.deliveryOrder-event" with "ShippingOrderManagement" delivery factory type, "2025-12-16T10:15:30Z" start date and the following node ids:
      | Id    |
      | node1 |
      | node2 |
    And the delivery start event will have the following characteristics for node id "node1"
      | type                 | name                    | value                 |
      | StringCharacteristic | Shipping Address        | Orange Canebière Shop |
      | StringCharacteristic | Shipping mode           | Instore               |
      | DateCharacteristic   | Requested delivery date | 2025-12-25T11:55:49Z  |
    And the delivery start event will have the following characteristics for node id "node2"
      | type                 | name                    | value                 |
      | StringCharacteristic | Shipping Address        | Orange Canebière Shop |
      | StringCharacteristic | Shipping mode           | Instore               |
      | DateCharacteristic   | Requested delivery date | 2025-12-25T11:55:49Z  |
    And the system marks the following nodes as part of a bulk delivery batch:
      | Id    |
      | node1 |
      | node2 |

  Scenario: Do not start bulk delivery when at least one tangible node is already InProgress
    Given the system has orchestration plan with id "plan1", state "InProgress" and order id "order1"
    And the plan with product order "order1" has the following order dates:
      | order start date     |
      | 2025-12-16T10:15:30Z |
    And  the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
      | node2 | InProgress | order1   |
    And   the node with id "node1" has the following related products:
      | Product Type    | Relationship Type | Order Item Id |
      | physicalProduct | delivers          | orderItem1    |
      | shipmentProduct | deliverWith       | shipItem1     |
    And the node with id "node1" and order item id "shipItem1" has the fallowing shipping characteristics:
      | Shipping Mode | Shipping Address      | Requested delivery date |
      | Instore       | Orange Canebière Shop | 2025-12-25T11:55:49Z    |
    And  the node with id "node2" has the following related products:
      | Product Type    | Relationship Type | Order Item Id |
      | physicalProduct | delivers          | orderItem2    |
      | shipmentProduct | deliverWith       | shipItem2     |
    And the node with id "node2" and order item id "shipItem2" has the fallowing shipping characteristics:
      | Shipping Mode | Shipping Address      | Requested delivery date |
      | Instore       | Orange Canebière Shop | 2025-12-25T11:55:49Z    |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InDelivery"
    Then the system will not fire bulk delivery start event on topic "disco.delivery-management.deliveryOrder-event"


  Scenario: Start bulk delivery for eligible tangible nodes when parent CFS node is in Held state
    Given the system has orchestration plan with id "plan1", state "InProgress" and order id "order1"
    And the plan with product order "order1" has the following order dates:
      | order start date     |
      | 2025-12-16T10:15:30Z |
    And the plan with id "plan1" has the following nodes:
      | Id    | State        | Order Id |
      | node1 | InDelivery   | order1   |
      | node2 | Held         | order1   |
      | node3 | Acknowledged | order1   |
    And  the node with id "node1" has the following related products:
      | Product Id | Product Type    | Relationship Type | Order Item Id |
      | product1   | physicalProduct | delivers          | orderItem1    |
      | product2   | shipmentProduct | deliverWith       | shipItem1     |
    And the product with id "product1" has the following characteristics:
      | Name   | Value Type | Value  |
      | chara1 | string     | value1 |
      | chara2 | string     | value2 | 
    And the node with id "node1" and order item id "shipItem1" has the fallowing shipping characteristics:
      | Shipping Mode | Shipping Address      | Requested delivery date |
      | Instore       | Orange Canebière Shop | 2025-12-25T11:55:49Z    |
    And the node with id "node2" has the following related products:
      | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | CFS          | delivers          | orderItem2    | add               |
    And the node with id "node3" has the following related products:
      | Product Id | Product Type    | Relationship Type | Order Item Id |
      | product3   | physicalProduct | delivers          | orderItem3    |
      | product4   | shipmentProduct | deliverWith       | shipItem3     |
      | product5   | physicalProduct | reliesOn          | orderItem2    |      
    And the product with id "product3" has the following characteristics:
      | Name   | Value Type | Value  |
      | chara1 | string     | value1 |
      | chara2 | string     | value2 |
      | chara3 | string     | value2 |
    And the node with id "node3" and order item id "shipItem3" has the fallowing shipping characteristics:
      | Shipping Mode | Shipping Address      | Requested delivery date |
      | Instore       | Orange Canebière Shop | 2025-12-25T11:55:49Z    |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node2" and state "Held"
    Then the system will fire bulk delivery start event on topic "disco.delivery-management.deliveryOrder-event" with "ShippingOrderManagement" delivery factory type, "2025-12-16T10:15:30Z" start date and the following node ids:
      | Id    |
      | node1 |
    And the delivery start event will have the following characteristics for node id "node1"
      | type                 | name                    | value                 |
      | StringCharacteristic | Shipping Address        | Orange Canebière Shop |
      | StringCharacteristic | Shipping mode           | Instore               |
      | DateCharacteristic   | Requested delivery date | 2025-12-25T11:55:49Z  |
    And the system marks the following nodes as part of a bulk delivery batch:
      | Id    |
      | node1 |

  Scenario: Start bulk delivery for eligible tangible nodes when parent CFS node is in Failed or Aborted state
    Given the system has orchestration plan with id "plan1", state "InProgress" and order id "order1"
    And the plan with product order "order1" has the following order dates:
      | order start date     |
      | 2025-12-16T10:15:30Z |
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
      | node2 | Failed     | order1   |
      | node3 | Aborted    | order1   |
    And  the node with id "node1" has the following related products:
      | Product Id | Product Type    | Relationship Type | Order Item Id |
      | product1   | physicalProduct | delivers          | orderItem1    |
      | product2   | shipmentProduct | deliverWith       | shipItem1     |
    And the product with id "product1" has the following characteristics:
      | Name   | Value Type | Value  |
      | chara1 | string     | value1 |
      | chara2 | string     | value2 | 
    And the node with id "node1" and order item id "shipItem1" has the fallowing shipping characteristics:
      | Shipping Mode | Shipping Address      | Requested delivery date |
      | Instore       | Orange Canebière Shop | 2025-12-25T11:55:49Z    |
    And the node with id "node2" has the following related products:
      | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | CFS          | delivers          | orderItem2    | add               |
    And the node with id "node3" has the following related products:
      | Product Id | Product Type    | Relationship Type | Order Item Id |
      | product3   | physicalProduct | delivers          | orderItem3    |
      | product4   | shipmentProduct | deliverWith       | shipItem3     |
      | product5   | physicalProduct | reliesOn          | orderItem2    |      
    And the product with id "product3" has the following characteristics:
      | Name   | Value Type | Value  |
      | chara1 | string     | value1 |
      | chara2 | string     | value2 |
      | chara3 | string     | value2 |
    And the node with id "node3" and order item id "shipItem3" has the fallowing shipping characteristics:
      | Shipping Mode | Shipping Address      | Requested delivery date |
      | Instore       | Orange Canebière Shop | 2025-12-25T11:55:49Z    |
    When the system consumes orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node3" and state "Aborted"
    Then the system will fire bulk delivery start event on topic "disco.delivery-management.deliveryOrder-event" with "ShippingOrderManagement" delivery factory type, "2025-12-16T10:15:30Z" start date and the following node ids:
      | Id    |
      | node1 |
    And the delivery start event will have the following characteristics for node id "node1"
      | type                 | name                    | value                 |
      | StringCharacteristic | Shipping Address        | Orange Canebière Shop |
      | StringCharacteristic | Shipping mode           | Instore               |
      | DateCharacteristic   | Requested delivery date | 2025-12-25T11:55:49Z  |
    And the system marks the following nodes as part of a bulk delivery batch:
      | Id    |
      | node1 |
