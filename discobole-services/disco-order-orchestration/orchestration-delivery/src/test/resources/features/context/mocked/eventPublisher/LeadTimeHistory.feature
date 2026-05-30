Feature: [ISCOOD-926] [implmentation] Storing and represnting lead time history

  Scenario: Get the history of lead time for some node/category in the plan.
    Given the system has the following orchestration plans:
      | plan id | order Id | state    | actual order start date | actual order completion date | contract name |
      | plan1   | order1   | Executed | 2025-09-01T11:00:00Z    | 2025-09-01T12:00:00Z         | name1         |
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id | action |
      | node1 | Completed  | order1   | add    |
      | node2 | Completed  | order1   | add    |
      | node3 | Completed  | order1   | add    |
      | node4 | Completed  | order1   | add    |
      | node5 | Completed  | order1   | add    |
      | node6 | InDelivery | order1   | add    |
    And the node with id "node1" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action | specificationId |
      | product1 | physicalProduct | delivers          | orderItem1    | add               | spec1           |
    And the node with id "node2" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action | specificationId |
      | product2 | CFS             | delivers          | orderItem2    | add               | spec2           |
      | product1 | physicalProduct | reliesOn          | orderItem1    | add               | spec1           |
    And the node with id "node3" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action | specificationId |
      | product3 | CFS             | delivers          | orderItem3    | add               | spec3           |
      | product1 | physicalProduct | reliesOn          | orderItem1    | add               | spec1           |
    And the node with id "node4" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action | specificationId |
      | product4 | CFS          | delivers          | orderItem4    | add               | spec4           |
    And the node with id "node5" has the following related products:
      | Id       | Product Type | Relationship Type | Order Item Id | Order Item Action | specificationId |
      | product5 | CFS          | delivers          | orderItem5    | add               | spec4           |
      | product3 | CFS          | reliesOn          | orderItem3    | add               | spec3           |
    And the node with id "node6" has the following related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action | specificationId |
      | product6 | CFS             | delivers          | orderItem6    | add               | spec4           |
      | product3 | physicalProduct | reliesOn          | orderItem3    | add               | spec3           |
    And the plan with product order "order1" has the following product order item "orderItem1"
      | actual order item  start date | actual order item completion date |
      | 2025-09-01T11:00:00Z          | 2025-09-01T11:03:00Z              |
    And the plan with product order "order1" has the following product order item "orderItem2"
      | actual order item  start date | actual order item completion date |
      | 2025-09-01T11:03:00Z          | 2025-09-01T11:05:00Z              |
    And the plan with product order "order1" has the following product order item "orderItem3"
      | actual order item  start date | actual order item completion date |
      | 2025-09-01T11:05:00Z          | 2025-09-01T11:07:00Z              |
    And the plan with product order "order1" has the following product order item "orderItem4"
      | actual order item  start date | actual order item completion date |
      | 2025-09-01T11:07:00Z          | 2025-09-01T11:08:00Z              |
    And the plan with product order "order1" has the following product order item "orderItem5"
      | actual order item  start date | actual order item completion date |
      | 2025-09-01T11:08:00Z          | 2025-09-01T11:10:00Z              |
    And the plan with product order "order1" has the following product order item "orderItem6"
      | actual order item  start date | actual order item completion date |
      | 2025-09-01T11:10:00Z          |                                   |
    And  the node with id "node1" has the following shipping orders:
      | id     | shippingOrder id  | shippingOrderItem  |
      | order1 | shippingOrder id1 | shippingOrderItem1 |
    And  the node with id "node2" has the following related service orders:
      | Id     | orderItemId | somRef                |
      | order1 | orderItem1  | mobile-factory rSHref |
    And  the node with id "node3" has the following related service orders:
      | Id     | orderItemId | somRef                |
      | order1 | orderItem1  | mobile-factory rSHref |
    And  the node with id "node4" has the following related service orders:
      | Id     | orderItemId | somRef                |
      | order1 | orderItem1  | mobile-factory rSHref |
    And  the node with id "node5" has the following related service orders:
      | Id     | orderItemId | somRef                |
      | order1 | orderItem1  | mobile-factory rSHref |
    And  the node with id "node6" has the following related service orders:
      | Id     | orderItemId | somRef                |
      | order1 | orderItem1  | mobile-factory rSHref |
    And  the current system timestamp is "2025-09-01T11:10:00Z"
    And  the cron job of storing nodes running every 10m
    When cron job that collects node statistics is executed
    Then the node lead time history sampled statistics should be stored
                                                                                         # Lead time in minutes
      | specificationId | deliveryFactoryName   | min actual lead time | max actual lead time | average actual lead time | sample size | sample window        |
      | spec4           | mobile-factory rSHref | 1                    | 2                    | 1.5                      | 2           | 2025-09-01T11:10:00Z |
      | spec3           | mobile-factory rSHref | 2                    | 2                    | 2                        | 1           | 2025-09-01T11:10:00Z |
      | spec1           | Shipment              | 3                    | 3                    | 3                        | 1           | 2025-09-01T11:10:00Z |
      | spec2           | mobile-factory rSHref | 2                    | 2                    | 2                        | 1           | 2025-09-01T11:10:00Z |

  Scenario: Get the history of lead time for multi plans
    Given the system has the following orchestration plans:
      | plan id | order Id | state      | actual order start date | actual order completion date | contract name |
      | plan1   | order1   | Executed   | 2025-09-01T11:00:00Z    | 2025-09-01T12:00:00Z         | name1         |
      | plan2   | order2   | Executed   | 2025-09-01T12:00:00Z    | 2025-09-01T12:30:00Z         | name1         |
      | plan3   | order3   | InProgress | 2025-09-01T12:00:00Z    |                              | name1         |
      | plan4   | order4   | Executed   | 2025-09-01T12:00:00Z    | 2025-09-01T12:40:00Z         | name2         |
    And  the current system timestamp is "2025-09-01T12:40:00Z"
    And  the cron job of storing plans running every 100m
    When cron job that collects plan statistics is executed
    Then the following plan lead time history sampled statistics will be stored
      | contract name | min actual lead time | max actual lead time | average actual lead time | sample size | sample window        |
      | name2         | 40                   | 40                   | 40                       | 1           | 2025-09-01T12:40:00Z |
      | name1         | 30                   | 60                   | 45                       | 2           | 2025-09-01T12:40:00Z |
