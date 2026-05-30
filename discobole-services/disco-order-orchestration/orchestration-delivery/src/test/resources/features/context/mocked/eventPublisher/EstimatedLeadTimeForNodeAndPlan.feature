Feature: [IPCEISCOOD-1031] Evolve Create Orchestration plan: Set the initial estimate lead-time for nodes and plan

  Scenario: 1- Set initial estimated lead time when the longest dependency branch determines the plan lead time - A
    Given The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 | spec1                  |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec3                  |
      | spec5           | cfs5 |                        |
    And the system has the following aggregate spec id lead time history statistics
      | specificationId | averageLeadTime |
      | spec1           | 5               |
      | spec2           | 7               |
      | spec3           | 8               |
      | spec4           | 6               |
      | spec5           | 10              |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem2         |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem3         |
      | orderItem5  | add    | spec5           |              |                    |
    # estimated lead time for plan (longest branch) = spec1(5) + spec2(7) + spec3(8) + spec4(6) = 26
    Then The system will create orchestration plan with order id "order1", state "Acknowledged" and estimated lead time 26
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        | estimatedLeadTimeDelivery |
      | orderItem1  | add    | Acknowledged | 5                         |
      | orderItem2  | add    | Acknowledged | 7                         |
      | orderItem3  | add    | Acknowledged | 8                         |
      | orderItem4  | add    | Acknowledged | 6                         |
      | orderItem5  | add    | Acknowledged | 10                        |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged"

  Scenario: 2- Set initial estimated lead time when an independent node has the highest lead time
    Given The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 | spec1                  |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec3                  |
      | spec5           | cfs5 |                        |
    And the system has the following aggregate spec id lead time history statistics
      | specificationId | averageLeadTime |
      | spec1           | 5               |
      | spec2           | 7               |
      | spec3           | 8               |
      | spec4           | 6               |
      | spec5           | 30              |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem2         |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem3         |
      | orderItem5  | add    | spec5           |              |                    |
    # estimated lead time for plan = spec5(30) = 30
    Then The system will create orchestration plan with order id "order1", state "Acknowledged" and estimated lead time 30
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        | estimatedLeadTimeDelivery |
      | orderItem1  | add    | Acknowledged | 5                         |
      | orderItem2  | add    | Acknowledged | 7                         |
      | orderItem3  | add    | Acknowledged | 8                         |
      | orderItem4  | add    | Acknowledged | 6                         |
      | orderItem5  | add    | Acknowledged | 30                        |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged"

  Scenario: 3- Set initial estimated lead time when the longest dependency branch determines the plan lead time - B
    Given The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 |                        |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec2                  |
      | spec5           | cfs5 | spec4                  |
      | spec6           | cfs1 | spec5                  |
      | spec7           | cfs2 | spec5                  |
    And the system has the following aggregate spec id lead time history statistics
      | specificationId | averageLeadTime |
      | spec1           | 5               |
      | spec2           | 5               |
      | spec3           | 15              |
      | spec4           | 3               |
      | spec5           | 1               |
      | spec6           | 4               |
      | spec7           | 7               |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           |              |                    |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem2         |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem2         |
      | orderItem5  | add    | spec5           | reliesOn     | orderItem4         |
      | orderItem6  | add    | spec6           | reliesOn     | orderItem5         |
      | orderItem7  | add    | spec7           | reliesOn     | orderItem5         |
    # estimated lead time for plan (longest branch) = spec2(5) + spec3(15) = 20
    Then The system will create orchestration plan with order id "order1", state "Acknowledged" and estimated lead time 20
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        | estimatedLeadTimeDelivery |
      | orderItem1  | add    | Acknowledged | 5                         |
      | orderItem2  | add    | Acknowledged | 5                         |
      | orderItem3  | add    | Acknowledged | 15                        |
      | orderItem4  | add    | Acknowledged | 3                         |
      | orderItem5  | add    | Acknowledged | 1                         |
      | orderItem6  | add    | Acknowledged | 4                         |
      | orderItem7  | add    | Acknowledged | 7                         |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged"

  Scenario: 4- Set initial estimated lead time when the longest dependency branch determines the plan lead time - C
    Given The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 |                        |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec2                  |
      | spec5           | cfs5 | spec4                  |
      | spec6           | cfs6 | spec5                  |
      | spec7           | cfs7 | spec5                  |
    And the system has the following aggregate spec id lead time history statistics
      | specificationId | averageLeadTime |
      | spec1           | 5               |
      | spec2           | 5               |
      | spec3           | 4               |
      | spec4           | 3               |
      | spec5           | 1               |
      | spec6           | 4               |
      | spec7           | 7               |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           |              |                    |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem2         |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem2         |
      | orderItem5  | add    | spec5           | reliesOn     | orderItem4         |
      | orderItem6  | add    | spec6           | reliesOn     | orderItem5         |
      | orderItem7  | add    | spec7           | reliesOn     | orderItem5         |
    # estimated lead time for plan (longest branch) = spec2(5) + spec4(3) + spec5(1) + spec7(7) = 16
    Then The system will create orchestration plan with order id "order1", state "Acknowledged" and estimated lead time 16
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        | estimatedLeadTimeDelivery |
      | orderItem1  | add    | Acknowledged | 5                         |
      | orderItem2  | add    | Acknowledged | 5                         |
      | orderItem3  | add    | Acknowledged | 4                         |
      | orderItem4  | add    | Acknowledged | 3                         |
      | orderItem5  | add    | Acknowledged | 1                         |
      | orderItem6  | add    | Acknowledged | 4                         |
      | orderItem7  | add    | Acknowledged | 7                         |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged"
