Feature: [IPCEISCOOD-555] Create a New Related product with Relationship Migrated From

  Scenario: 1- Add two related products (delivers - migratedFrom) to one node for order items with migrate actions
    Given The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 | spec3                  |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec1                  |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action  | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add     | spec1           |              |                    |
      | orderItem2  | migrate | spec2           | migrateTo    | orderItem3         |
      | orderItem3  | migrate | spec3           |              |                    |
      | orderItem4  | add     | spec4           | reliesOn     | orderItem1         |
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And The system is going to have node with order id "orderItem1" and the following related products
      | RelationType |
      | delivers     |
      | reliesOn     |
    And The system is going to have node with order id "orderItem3" and the following related products
      | RelationType |
      | delivers     |
      | migratedFrom |
      | reliesOn     |
    And The system is going to have node with order id "orderItem4" and the following related products
      | RelationType |
      | delivers     |
      | reliesOn     |
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action  | state        |
      | orderItem1  | add     | Acknowledged |
      | orderItem3  | migrate | Acknowledged |
      | orderItem4  | add     | Acknowledged |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged":

  Scenario: 2- Add two related products (delivers - migratedFrom) to two nodes for order items with migrate actions
    Given The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 | spec3                  |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec1                  |
      | spec5           | cfs4 | spec6                  |
      | spec6           | cfs4 | spec3                  |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action  | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add     | spec1           |              |                    |
      | orderItem2  | migrate | spec2           | migrateTo    | orderItem3         |
      | orderItem3  | migrate | spec3           |              |                    |
      | orderItem4  | add     | spec4           | reliesOn     | orderItem1         |
      | orderItem5  | migrate | spec5           | migrateTo    | orderItem6         |
      | orderItem6  | migrate | spec6           |              |                    |
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And The system is going to have node with order id "orderItem1" and the following related products
      | RelationType |
      | delivers     |
      | reliesOn     |
    And The system is going to have node with order id "orderItem3" and the following related products
      | RelationType |
      | delivers     |
      | migratedFrom |
      | reliesOn     |
    And The system is going to have node with order id "orderItem4" and the following related products
      | RelationType |
      | delivers     |
      | reliesOn     |
    And The system is going to have node with order id "orderItem6" and the following related products
      | RelationType |
      | delivers     |
      | migratedFrom |
      | reliesOn     |
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action  | state        |
      | orderItem1  | add     | Acknowledged |
      | orderItem3  | migrate | Acknowledged |
      | orderItem4  | add     | Acknowledged |
      | orderItem6  | migrate | Acknowledged |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged":

  Scenario: 3- Add two related products (delivers - migratedFrom) to two node for order items with migrate actions and neglect noChange action
    Given The product catalog has the following installed products:
      | specificationId | name | relatedSpecificationID |
      | spec1           | cfs1 |                        |
      | spec2           | cfs2 | spec3                  |
      | spec3           | cfs3 | spec2                  |
      | spec4           | cfs4 | spec7                  |
      | spec5           | cfs4 | spec6                  |
      | spec6           | cfs4 | spec3                  |
      | spec7           | cfs4 |                        |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action   | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | noChange | spec1           |              |                    |
      | orderItem2  | migrate  | spec2           | migrateTo    | orderItem3         |
      | orderItem3  | migrate  | spec3           |              |                    |
      | orderItem4  | add      | spec4           | reliesOn     | orderItem7         |
      | orderItem5  | migrate  | spec5           | migrateTo    | orderItem6         |
      | orderItem6  | migrate  | spec6           |              |                    |
      | orderItem7  | add      | spec7           |              |                    |
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And The system is going to have node with order id "orderItem3" and the following related products
      | RelationType |
      | delivers     |
      | migratedFrom |
      | reliesOn     |
    And The system is going to have node with order id "orderItem4" and the following related products
      | RelationType |
      | delivers     |
      | reliesOn     |
    And The system is going to have node with order id "orderItem6" and the following related products
      | RelationType |
      | delivers     |
      | migratedFrom |
      | reliesOn     |
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action  | state        |
      | orderItem3  | migrate | Acknowledged |
      | orderItem4  | add     | Acknowledged |
      | orderItem6  | migrate | Acknowledged |
      | orderItem7  | add     | Acknowledged |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged":
