Feature: [IPCEISCOOD-756] Support multi purchase and Allow duplicated product specification ID in same order

  Scenario: 1 support Multi-purchase in same order has action add for mobile offer
    Given The product catalog has the following installed products:
      | specificationId | relationType | relatedSpecificationID |
      | spec1           |              |                        |
      | spec2           | reliesOn     | spec1                  |
      | spec3           | reliesOn     | spec1                  |
      | spec4           | reliesOn     | spec3                  |
    When The topic "disco.order-management.productOrderStateChange-event" receives event "productOrderStateChange-event" with product order id "order1" and state "accepted"
    And  product order "order1" has the following product order items
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem1         |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem3         |
      | orderItem5  | add    | spec1           |              |                    |
      | orderItem6  | add    | spec2           | reliesOn     | orderItem5         |
      | orderItem7  | add    | spec3           | reliesOn     | orderItem5         |
      | orderItem8  | add    | spec4           | reliesOn     | orderItem7         |
    And order item "orderItem1" with spec id "spec1" has the following characteristics
      | name | value    |
      | IMSI | IMSI#123 |
    And order item "orderItem2" with spec id "spec2" has the following characteristics
      | name  | value |
      | ICCID | ICCID |
    And order item "orderItem3" with spec id "spec3" has the following characteristics
      | name     | value  |
      | Validity | 30days |
    And order item "orderItem4" with spec id "spec4" has the following characteristics
      | name      | value |
      | Quantity  | 10GB  |
      | Frequency | Month |
      | Recurring | yes   |
    And order item "orderItem5" with spec id "spec1" has the following characteristics
      | name | value    |
      | IMSI | IMSI#123 |
    And order item "orderItem6" with spec id "spec2" has the following characteristics
      | name  | value |
      | ICCID | ICCID |
    And order item "orderItem7" with spec id "spec3" has the following characteristics
      | name     | value  |
      | Validity | 30days |
    And order item "orderItem8" with spec id "spec4" has the following characteristics
      | name      | value |
      | Quantity  | 10GB  |
      | Frequency | Month |
      | Recurring | yes   |
    And the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And  The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        |
      | orderItem1  | add    | Acknowledged |
      | orderItem2  | add    | Acknowledged |
      | orderItem3  | add    | Acknowledged |
      | orderItem4  | add    | Acknowledged |
      | orderItem5  | add    | Acknowledged |
      | orderItem6  | add    | Acknowledged |
      | orderItem7  | add    | Acknowledged |
      | orderItem8  | add    | Acknowledged |
    And the plan with product order "order1" has the following characteristics for order item "orderItem1" with spec id "spec1"
      | name | value    |
      | IMSI | IMSI#123 |
    And The system is going to have node with order id "orderItem2" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem1  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem2" with spec id "spec2"
      | name  | value |
      | ICCID | ICCID |
    And The system is going to have node with order id "orderItem3" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem1  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem3" with spec id "spec3"
      | name     | value  |
      | Validity | 30days |
    And The system is going to have node with order id "orderItem4" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem4" with spec id "spec4"
      | name      | value |
      | Quantity  | 10GB  |
      | Frequency | Month |
      | Recurring | yes   |
    And the plan with product order "order1" has the following characteristics for order item "orderItem5" with spec id "spec1"
      | name | value    |
      | IMSI | IMSI#123 |
    And The system is going to have node with order id "orderItem6" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem5  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem6" with spec id "spec2"
      | name  | value |
      | ICCID | ICCID |
    And The system is going to have node with order id "orderItem7" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem5  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem7" with spec id "spec3"
      | name     | value  |
      | Validity | 30days |
    And The system is going to have node with order id "orderItem8" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem7  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem8" with spec id "spec4"
      | name      | value |
      | Quantity  | 10GB  |
      | Frequency | Month |
      | Recurring | yes   |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged"

  Scenario: 2 support Multi-purchase in same order has action delete for mobile offer
    Given The product catalog has the following installed products:
      | specificationId | relationType | relatedSpecificationID |
      | spec1           |              |                        |
      | spec2           |              |                        |
      | spec3           |              |                        |
      | spec4           | reliesOn     | spec3                  |
      | spec5           |              |                        |
      | spec6           |              |                        |
      | spec7           |              |                        |
      | spec8           | reliesOn     | spec7                  |
    When The topic "disco.order-management.productOrderStateChange-event" receives event "productOrderStateChange-event" with product order id "order1" and state "accepted"
    And  product order "order1" has the following product order items
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem2  | delete | spec2           |              |                    |
      | orderItem3  | delete | spec3           |              |                    |
      | orderItem4  | delete | spec4           | reliesOn     | orderItem3         |
      | orderItem6  | delete | spec2           |              |                    |
      | orderItem7  | delete | spec3           |              |                    |
      | orderItem8  | delete | spec4           | reliesOn     | orderItem7         |
    And order item "orderItem2" with spec id "spec2" has the following characteristics
      | name  | value |
      | ICCID | ICCID |
    And order item "orderItem3" with spec id "spec3" has the following characteristics
      | name     | value  |
      | Validity | 30days |
    And order item "orderItem4" with spec id "spec4" has the following characteristics
      | name      | value |
      | Quantity  | 10GB  |
      | Frequency | Month |
      | Recurring | yes   |
    And order item "orderItem6" with spec id "spec2" has the following characteristics
      | name  | value |
      | ICCID | ICCID |
    And order item "orderItem7" with spec id "spec3" has the following characteristics
      | name     | value  |
      | Validity | 30days |
    And order item "orderItem8" with spec id "spec4" has the following characteristics
      | name      | value |
      | Quantity  | 10GB  |
      | Frequency | Month |
      | Recurring | yes   |
    And the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And  The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        |
      | orderItem2  | delete | Acknowledged |
      | orderItem3  | delete | Acknowledged |
      | orderItem4  | delete | Acknowledged |
      | orderItem6  | delete | Acknowledged |
      | orderItem7  | delete | Acknowledged |
      | orderItem8  | delete | Acknowledged |
    And the plan with product order "order1" has the following characteristics for order item "orderItem2" with spec id "spec2"
      | name  | value |
      | ICCID | ICCID |
    And the plan with product order "order1" has the following characteristics for order item "orderItem3" with spec id "spec3"
      | name     | value  |
      | Validity | 30days |
    And The system is going to have node with order id "orderItem4" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem4" with spec id "spec4"
      | name      | value |
      | Quantity  | 10GB  |
      | Frequency | Month |
      | Recurring | yes   |
    And the plan with product order "order1" has the following characteristics for order item "orderItem6" with spec id "spec2"
      | name  | value |
      | ICCID | ICCID |
    And the plan with product order "order1" has the following characteristics for order item "orderItem7" with spec id "spec3"
      | name     | value  |
      | Validity | 30days |
    And The system is going to have node with order id "orderItem8" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem7  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem8" with spec id "spec4"
      | name      | value |
      | Quantity  | 10GB  |
      | Frequency | Month |
      | Recurring | yes   |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged"

  Scenario:  3 support Multi-purchase in same order has action add for smart offer
    Given The product catalog has the following installed products:
      | specificationId | relationType | relatedSpecificationID |
      | spec1           |              |                        |
      | spec2           | reliesOn     | spec1                  |
      | spec3           | reliesOn     | spec1                  |
      | spec4           | reliesOn     | spec3                  |
      | spec5           | reliesOn     | spec3                  |
      | spec6           | reliesOn     | spec3                  |
      | spec7           | reliesOn     | spec3                  |
      | spec8           | reliesOn     | spec3                  |
      | spec9           | reliesOn     |                        |
      | spec10          | reliesOn     |                        |
    When The topic "disco.order-management.productOrderStateChange-event" receives event "productOrderStateChange-event" with product order id "order1" and state "accepted"
    And  product order "order1" has the following product order items
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem1         |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem3         |
      | orderItem5  | add    | spec5           | reliesOn     | orderItem3         |
      | orderItem6  | add    | spec6           | reliesOn     | orderItem3         |
      | orderItem7  | add    | spec7           | reliesOn     | orderItem3         |
      | orderItem8  | add    | spec8           | reliesOn     | orderItem3         |
      | orderItem9  | add    | spec9           |              |                    |
      | orderItem10 | add    | spec10          |              |                    |
      | orderItem11 | add    | spec10          |              |                    |
    And order item "orderItem1" with spec id "spec1" has the following characteristics
      | name | value     |
      | IMSI | IMSI#1234 |
    And order item "orderItem2" with spec id "spec2" has the following characteristics
      | name  | value |
      | ICCID | ICCID |
    And order item "orderItem4" with spec id "spec4" has the following characteristics
      | name      | value      |
      | Quantity  | 999999Hour |
      | Frequency | Month      |
      | Recurring | yes        |
      | Rollover  | no         |
      | Blocked   | no         |
    And order item "orderItem5" with spec id "spec5" has the following characteristics
      | name      | value    |
      | Quantity  | 999999GB |
      | Frequency | Month    |
      | Recurring | yes      |
      | Rollover  | no       |
    And order item "orderItem6" with spec id "spec6" has the following characteristics
      | name      | value |
      | Frequency | Month |
      | Recurring | yes   |
      | Rollover  | no    |
    And order item "orderItem7" with spec id "spec7" has the following characteristics
      | name      | value   |
      | Quantity  | 99999GB |
      | Frequency | Month   |
      | Recurring | yes     |
      | Rollover  | no      |
      | Blocked   | no      |
    And order item "orderItem8" with spec id "spec8" has the following characteristics
      | name      | value   |
      | Quantity  | 99999GB |
      | Frequency | Month   |
      | Recurring | yes     |
      | Rollover  | no      |
      | Blocked   | no      |
    And order item "orderItem9" with spec id "spec9" has the following characteristics
      | name   | value |
      | memory | 512GB |
      | Colour | Lilac |
    And order item "orderItem10" with spec id "spec10" has the following characteristics
      | name  | value |
      | level | basic |
    And order item "orderItem11" with spec id "spec10" has the following characteristics
      | name  | value |
      | level | basic |
    And the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And  The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        |
      | orderItem1  | add    | Acknowledged |
      | orderItem2  | add    | Acknowledged |
      | orderItem3  | add    | Acknowledged |
      | orderItem4  | add    | Acknowledged |
      | orderItem5  | add    | Acknowledged |
      | orderItem6  | add    | Acknowledged |
      | orderItem7  | add    | Acknowledged |
      | orderItem8  | add    | Acknowledged |
      | orderItem9  | add    | Acknowledged |
      | orderItem10 | add    | Acknowledged |
      | orderItem11 | add    | Acknowledged |
    And the plan with product order "order1" has the following characteristics for order item "orderItem1" with spec id "spec1"
      | name | value     |
      | IMSI | IMSI#1234 |
    And The system is going to have node with order id "orderItem2" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem1  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem2" with spec id "spec2"
      | name  | value |
      | ICCID | ICCID |
    And The system is going to have node with order id "orderItem3" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem1  | DeliverAfter |
    And The system is going to have node with order id "orderItem4" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem4" with spec id "spec4"
      | name      | value      |
      | Quantity  | 999999Hour |
      | Frequency | Month      |
      | Recurring | yes        |
      | Rollover  | no         |
      | Blocked   | no         |
    And The system is going to have node with order id "orderItem5" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem5" with spec id "spec5"
      | name      | value    |
      | Quantity  | 999999GB |
      | Frequency | Month    |
      | Recurring | yes      |
      | Rollover  | no       |
    And The system is going to have node with order id "orderItem6" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem6" with spec id "spec6"
      | name      | value |
      | Frequency | Month |
      | Recurring | yes   |
      | Rollover  | no    |
    And The system is going to have node with order id "orderItem7" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem7" with spec id "spec7"
      | name      | value   |
      | Quantity  | 99999GB |
      | Frequency | Month   |
      | Recurring | yes     |
      | Rollover  | no      |
      | Blocked   | no      |
    And The system is going to have node with order id "orderItem8" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem8" with spec id "spec8"
      | name      | value   |
      | Quantity  | 99999GB |
      | Frequency | Month   |
      | Recurring | yes     |
      | Rollover  | no      |
      | Blocked   | no      |
    And the plan with product order "order1" has the following characteristics for order item "orderItem9" with spec id "spec9"
      | name   | value |
      | memory | 512GB |
      | Colour | Lilac |
    And the plan with product order "order1" has the following characteristics for order item "orderItem10" with spec id "spec10"
      | name  | value |
      | level | basic |
    And the plan with product order "order1" has the following characteristics for order item "orderItem11" with spec id "spec10"
      | name  | value |
      | level | basic |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged"

  Scenario:  4 support Multi-purchase in same order has action delete for smart offer
    Given The product catalog has the following installed products:
      | specificationId | relationType | relatedSpecificationID |
      | spec1           |              |                        |
      | spec2           | reliesOn     | spec1                  |
      | spec3           | reliesOn     | spec1                  |
      | spec4           | reliesOn     | spec3                  |
      | spec5           | reliesOn     | spec3                  |
      | spec6           | reliesOn     | spec3                  |
      | spec7           | reliesOn     | spec3                  |
      | spec8           | reliesOn     | spec3                  |
    When The topic "disco.order-management.productOrderStateChange-event" receives event "productOrderStateChange-event" with product order id "order1" and state "accepted"
    And  product order "order1" has the following product order items
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | delete | spec1           |              |                    |
      | orderItem2  | delete | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | delete | spec3           | reliesOn     | orderItem1         |
      | orderItem4  | delete | spec4           | reliesOn     | orderItem3         |
      | orderItem5  | delete | spec5           | reliesOn     | orderItem3         |
      | orderItem6  | delete | spec6           | reliesOn     | orderItem3         |
      | orderItem7  | delete | spec7           | reliesOn     | orderItem3         |
      | orderItem8  | delete | spec8           | reliesOn     | orderItem3         |
    And order item "orderItem2" with spec id "spec2" has the following characteristics
      | name  | value |
      | ICCID | ICCID |
    And order item "orderItem4" with spec id "spec4" has the following characteristics
      | name      | value      |
      | Quantity  | 999999Hour |
      | Frequency | Month      |
      | Recurring | yes        |
      | Rollover  | no         |
      | Blocked   | no         |
    And order item "orderItem5" with spec id "spec5" has the following characteristics
      | name      | value    |
      | Quantity  | 999999GB |
      | Frequency | Month    |
      | Recurring | yes      |
      | Rollover  | no       |
    And order item "orderItem6" with spec id "spec6" has the following characteristics
      | name      | value |
      | Frequency | Month |
      | Recurring | yes   |
      | Rollover  | no    |
    And order item "orderItem7" with spec id "spec7" has the following characteristics
      | name      | value   |
      | Quantity  | 99999GB |
      | Frequency | Month   |
      | Recurring | yes     |
      | Rollover  | no      |
      | Blocked   | no      |
    And order item "orderItem8" with spec id "spec8" has the following characteristics
      | name      | value   |
      | Quantity  | 99999GB |
      | Frequency | Month   |
      | Recurring | yes     |
      | Rollover  | no      |
      | Blocked   | no      |
    And the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And  The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        |
      | orderItem1  | delete | Acknowledged |
      | orderItem2  | delete | Acknowledged |
      | orderItem3  | delete | Acknowledged |
      | orderItem4  | delete | Acknowledged |
      | orderItem5  | delete | Acknowledged |
      | orderItem6  | delete | Acknowledged |
      | orderItem7  | delete | Acknowledged |
      | orderItem8  | delete | Acknowledged |
    And The system is going to have node with order id "orderItem2" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem1  | DeliverAfter |
    And The system is going to have node with order id "orderItem3" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem1  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem2" with spec id "spec2"
      | name  | value |
      | ICCID | ICCID |
    And The system is going to have node with order id "orderItem4" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem4" with spec id "spec4"
      | name      | value      |
      | Quantity  | 999999Hour |
      | Frequency | Month      |
      | Recurring | yes        |
      | Rollover  | no         |
      | Blocked   | no         |
    And The system is going to have node with order id "orderItem5" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem5" with spec id "spec5"
      | name      | value    |
      | Quantity  | 999999GB |
      | Frequency | Month    |
      | Recurring | yes      |
      | Rollover  | no       |
    And The system is going to have node with order id "orderItem6" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem6" with spec id "spec6"
      | name      | value |
      | Frequency | Month |
      | Recurring | yes   |
      | Rollover  | no    |
    And The system is going to have node with order id "orderItem7" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem7" with spec id "spec7"
      | name      | value   |
      | Quantity  | 99999GB |
      | Frequency | Month   |
      | Recurring | yes     |
      | Rollover  | no      |
      | Blocked   | no      |
    And The system is going to have node with order id "orderItem8" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the plan with product order "order1" has the following characteristics for order item "orderItem8" with spec id "spec8"
      | name      | value   |
      | Quantity  | 99999GB |
      | Frequency | Month   |
      | Recurring | yes     |
      | Rollover  | no      |
      | Blocked   | no      |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged"