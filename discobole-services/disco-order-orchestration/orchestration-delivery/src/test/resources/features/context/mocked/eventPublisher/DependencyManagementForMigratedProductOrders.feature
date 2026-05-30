Feature: [IPCEISCOOD-556] Manage Dependencies and Relationships for Nodes with Action Migrate

  Scenario: 1 Add A Deliver After Relationship for Add or Modify Actions
    Given The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 | spec1                  |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec3                  |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | modify | spec3           | reliesOn     | orderItem1         |
      | orderItem4  | modify | spec4           | reliesOn     | orderItem3         |
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        |
      | orderItem1  | add    | Acknowledged |
      | orderItem2  | add    | Acknowledged |
      | orderItem3  | modify | Acknowledged |
      | orderItem4  | modify | Acknowledged |
    And The system is going to have node with order id "orderItem2" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem1  | DeliverAfter |
    And The system is going to have node with order id "orderItem3" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem1  | DeliverAfter |
    And The system is going to have node with order id "orderItem4" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged":

  Scenario: 2 Add A Deliver After Relationship with Migrate Action
    Given The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 | spec1                  |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec3                  |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action  | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add     | spec1           |              |                    |
      | orderItem2  | add     | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | modify  | spec3           | reliesOn     | orderItem1         |
      | orderItem4  | migrate | spec3           | reliesOn     | orderItem3         |
      | orderItem5  | migrate | spec3           | migrateTo    | orderItem4         |
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action  | state        |
      | orderItem1  | add     | Acknowledged |
      | orderItem2  | add     | Acknowledged |
      | orderItem3  | modify  | Acknowledged |
      | orderItem4  | migrate | Acknowledged |
    And The system is going to have node with order id "orderItem2" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem1  | DeliverAfter |
    And The system is going to have node with order id "orderItem3" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem1  | DeliverAfter |
    And The system is going to have node with order id "orderItem4" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged":

  Scenario: 3 Add A Deliver After Relationship with Delete Action only
    Given The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 | spec1                  |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec3                  |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | delete | spec1           |              |                    |
      | orderItem2  | delete | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | delete | spec3           | reliesOn     | orderItem1         |
      | orderItem4  | delete | spec3           | reliesOn     | orderItem3         |
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        |
      | orderItem1  | delete | Acknowledged |
      | orderItem2  | delete | Acknowledged |
      | orderItem3  | delete | Acknowledged |
      | orderItem4  | delete | Acknowledged |
    And The system is going to have node with order id "orderItem2" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem1  | DeliverAfter |
    And The system is going to have node with order id "orderItem3" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem1  | DeliverAfter |
    And The system is going to have node with order id "orderItem4" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged":

  Scenario: 4 Add A Deliver After Relationship for Modify and Delete Actions
    Given The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 | spec1                  |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec3                  |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | modify | spec1           |              |                    |
      | orderItem2  | modify | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | delete | spec3           | reliesOn     | orderItem1         |
      | orderItem4  | delete | spec3           | reliesOn     | orderItem3         |
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        |
      | orderItem1  | modify | Acknowledged |
      | orderItem2  | modify | Acknowledged |
      | orderItem3  | delete | Acknowledged |
      | orderItem4  | delete | Acknowledged |
    And The system is going to have node with order id "orderItem2" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem1  | DeliverAfter |
    And The system is going to have node with order id "orderItem3" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem1  | DeliverAfter |
    And The system is going to have node with order id "orderItem4" and the following related nodes relations
      | orderItemId | RelationType |
      | orderItem3  | DeliverAfter |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged":

