Feature: [IPCEISCOOD-933] Evolve Create Orchestration Plan - Set start time for Future Dated orders

  Scenario: 1-  Set start time for planned orders for roots node and plan  to requested delivery date in case Orchestration Plan Scheduling is OFF
    Given the current system timestamp is "2025-12-04T10:15:30Z"
    And The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 | spec1                  |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec3                  |
      | spec5           | cfs5 |                        |
    And the system has the following aggregate spec id lead time history statistics
      | specificationId | averageLeadTime |
      | spec1           | 5               |
      | spec2           | 5               |
      | spec3           | 10              |
      | spec4           | 10              |
      | spec5           | 5               |
    And Adaptive Orchestration Plan Scheduling is "OFF"
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and requested delivery date is "2026-01-02T10:15:30Z" and the following product order items:
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem2         |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem3         |
      | orderItem5  | add    | spec5           |              |                    |
      # estimated lead time for plan (longest branch) = spec1(5) + spec2(5) + spec3(10) + spec4(10) = 30h
      ## Lead time in hours
    Then The system will create orchestration plan with order id "order1", state "Planned" and estimated lead time 30
      # order start date and order item start date  for all root nods =  requested delivery date
    And the plan with product order "order1" will have the following data
      | order start date     |
      | 2026-01-02T10:15:30Z |
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        | estimatedLeadTimeDelivery | order item start date |
      | orderItem1  | add    | Acknowledged | 5                         | 2026-01-02T10:15:30Z  |
      | orderItem2  | add    | Acknowledged | 5                         |                       |
      | orderItem3  | add    | Acknowledged | 10                        |                       |
      | orderItem4  | add    | Acknowledged | 10                        |                       |
      | orderItem5  | add    | Acknowledged | 5                         | 2026-01-02T10:15:30Z  |

  Scenario: 2-  Set start time for planned orders for roots node and plan  in case Orchestration Plan Scheduling is ON  and estimated plan leadtime is less that one day
    Given the current system timestamp is "2025-12-04T10:15:30Z"
    And The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 | spec1                  |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec3                  |
      | spec5           | cfs5 |                        |
    And the system has the following aggregate spec id lead time history statistics
      | specificationId | averageLeadTime |
      | spec1           | 1               |
      | spec2           | 2               |
      | spec3           | 1               |
      | spec4           | 2               |
      | spec5           | 1               |
    And  Adaptive Orchestration Plan Scheduling is "ON"
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and requested delivery date is "2026-01-01T10:15:30Z" and the following product order items:
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem2         |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem3         |
      | orderItem5  | add    | spec5           |              |                    |
        # estimated lead time for plan (longest branch) = spec1(1) + spec2(2) + spec3(1) + spec4(2) = 6
        ## Lead time in hours
    Then The system will create orchestration plan with order id "order1", state "Planned" and estimated lead time 6
      # order start date and order item start date  for all root nods =  requested delivery date
    And the plan with product order "order1" will have the following data
      | order start date     |
      | 2026-01-01T10:15:30Z |
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        | estimatedLeadTimeDelivery | order item start date |
      | orderItem1  | add    | Acknowledged | 1                         | 2026-01-01T10:15:30Z  |
      | orderItem2  | add    | Acknowledged | 2                         |                       |
      | orderItem3  | add    | Acknowledged | 1                         |                       |
      | orderItem4  | add    | Acknowledged | 2                         |                       |
      | orderItem5  | add    | Acknowledged | 1                         | 2026-01-01T10:15:30Z  |

  Scenario: 3-  Set start time for planned orders for roots node and plan  in case Orchestration Plan Scheduling is ON  and estimated plan leadtime is more than 1 day
    Given the current system timestamp is "2025-12-04T10:15:30Z"
    And The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 | spec1                  |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec3                  |
      | spec5           | cfs5 |                        |
    And the system has the following aggregate spec id lead time history statistics
      | specificationId | averageLeadTime |
      | spec1           | 5               |
      | spec2           | 5               |
      | spec3           | 10              |
      | spec4           | 10              |
      | spec5           | 5               |
    And  Adaptive Orchestration Plan Scheduling is "ON"
    And  product order "order1" has the following product order items
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem2         |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem3         |
      | orderItem5  | add    | spec5           |              |                    |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and requested delivery date is "2026-01-02T10:15:30Z" and the following product order items:
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem2         |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem3         |
      | orderItem5  | add    | spec5           |              |                    |
        # estimated lead time for plan (longest branch) = spec1(5) + spec2(5) + spec3(10) + spec4(10) = 30h
        ## Lead time in hours
    Then The system will create orchestration plan with order id "order1", state "Planned" and estimated lead time 30
      # order start date and order item start date  for all root nods =  requested delivery date -1
    And the plan with product order "order1" will have the following data
      | order start date     |
      | 2026-01-01T04:15:30Z |
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        | estimatedLeadTimeDelivery | order item start date |
      | orderItem1  | add    | Acknowledged | 5                         | 2026-01-01T04:15:30Z  |
      | orderItem2  | add    | Acknowledged | 5                         |                       |
      | orderItem3  | add    | Acknowledged | 10                        |                       |
      | orderItem4  | add    | Acknowledged | 10                        |                       |
      | orderItem5  | add    | Acknowledged | 5                         | 2026-01-02T10:15:30Z  |

  Scenario: 4-  Set start time for planned orders for roots node and plan  in case Orchestration Plan Scheduling is ON  and smallest branch has the highest lead with  more than 1 day
    Given the current system timestamp is "2025-12-04T10:15:30Z"
    And The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 | spec1                  |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec3                  |
      | spec5           | cfs5 |                        |
    And the system has the following aggregate spec id lead time history statistics
      | specificationId | averageLeadTime |
      | spec1           | 5               |
      | spec2           | 5               |
      | spec3           | 10              |
      | spec4           | 10              |
      | spec5           | 48              |
    And  Adaptive Orchestration Plan Scheduling is "ON"
    And  product order "order1" has the following product order items
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem2         |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem3         |
      | orderItem5  | add    | spec5           |              |                    |         
    # estimated lead time for plan  =  spec5(48) = 48h
    ## Lead time in hours
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and requested delivery date is "2026-01-03T10:15:30Z" and the following product order items:
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem2         |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem3         |
      | orderItem5  | add    | spec5           |              |                    |
    # order start date and order item start date  for all root nods =  requested delivery date -2
    Then The system will create orchestration plan with order id "order1", state "Planned" and estimated lead time 48
    And the plan with product order "order1" will have the following data
      | order start date     |
      | 2026-01-01T10:15:30Z |
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        | estimatedLeadTimeDelivery | order item start date |
      | orderItem1  | add    | Acknowledged | 5                         | 2026-01-02T04:15:30Z  |
      | orderItem2  | add    | Acknowledged | 5                         |                       |
      | orderItem3  | add    | Acknowledged | 10                        |                       |
      | orderItem4  | add    | Acknowledged | 10                        |                       |
      | orderItem5  | add    | Acknowledged | 48                        | 2026-01-01T10:15:30Z  |


  Scenario: 5-  Set start time for planned orders for roots node and plan  to today if requestedDeliveryDate - EstimatedLeadTime is before today
    Given the current system timestamp is "2025-12-31T10:15:30Z"
    And The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 | spec1                  |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec3                  |
      | spec5           | cfs5 |                        |
    And the system has the following aggregate spec id lead time history statistics
      | specificationId | averageLeadTime |
      | spec1           | 10              |
      | spec2           | 10              |
      | spec3           | 10              |
      | spec4           | 20              |
      | spec5           | 5               |
    And  Adaptive Orchestration Plan Scheduling is "ON"
    And  product order "order1" has the following product order items
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem2         |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem3         |
      | orderItem5  | add    | spec5           |              |                    |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and requested delivery date is "2026-01-01T10:15:30Z" and the following product order items:
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem2         |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem3         |
      | orderItem5  | add    | spec5           |              |                    |
      # estimated lead time for plan (longest branch) = spec1(10) + spec2(10) + spec3(10) + spec4(20) = 50h
      ## Lead time in hours
    Then The system will create orchestration plan with order id "order1", state "Planned" and estimated lead time 50
      # order start date and order item start date  for all root nods =  requested delivery date -2 =2025-12-30T10:15:30Z
      # when start date = date in the past so system will set order start date = the current system timestamp is "2025-12-31T10:15:30Z"
    And the plan with product order "order1" will have the following data
      | order start date     |
      | 2025-12-31T10:15:30Z |
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        | estimatedLeadTimeDelivery | order item start date |
      | orderItem1  | add    | Acknowledged | 10                        | 2025-12-31T10:15:30Z  |
      | orderItem2  | add    | Acknowledged | 10                        |                       |
      | orderItem3  | add    | Acknowledged | 10                        |                       |
      | orderItem4  | add    | Acknowledged | 20                        |                       |
      | orderItem5  | add    | Acknowledged | 5                         | 2026-01-01T10:15:30Z  |

  Scenario: 6-  Set start time for planned orders for roots node and plan in case Orchestration Plan Scheduling is ON and estimated plan leadtime is more than 5 days
    Given the current system timestamp is "2025-12-04T10:15:30Z"
    And The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 |                        |
      | spec3           | cfs3 |                        |
      | spec4           | cfs4 | spec3                  |
    And the system has the following aggregate spec id lead time history statistics
      | specificationId | averageLeadTime |
      | spec1           | 5               |
      | spec2           | 30              |
      | spec3           | 60              |
      | spec4           | 70              |
    And  Adaptive Orchestration Plan Scheduling is "ON"
    And  product order "order1" has the following product order items
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           |              |                    |
      | orderItem3  | add    | spec3           |              |                    |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem3         |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and requested delivery date is "2026-01-02T10:15:30Z" and the following product order items:
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           |              |                    |
      | orderItem3  | add    | spec3           |              |                    |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem3         |
        # estimated lead time for plan (longest branch) = spec3(60) + spec4(70) = 130h
        # Lead time in hours
    Then The system will create orchestration plan with order id "order1", state "Planned" and estimated lead time 130
      # order start date and order item start date  for all root nods =  requested delivery date -5
    And the plan with product order "order1" will have the following data
      | order start date     |
      | 2025-12-28T00:15:30Z |
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        | estimatedLeadTimeDelivery | order item start date |
      | orderItem1  | add    | Acknowledged | 5                         | 2026-01-02T10:15:30Z  |
      | orderItem2  | add    | Acknowledged | 30                        | 2026-01-01T04:15:30Z  |
      | orderItem3  | add    | Acknowledged | 60                        | 2025-12-28T00:15:30Z  |
      | orderItem4  | add    | Acknowledged | 70                        |                       |
 
