Feature: [IPCEISCOOD-965] Migrating from old contract to new contract

  Scenario: 1- create orchestration plan when migrating from old contract to new contract and has node action delete
    Given The product catalog has the following installed products:
      | specificationId | name         | relatedSpecificationID |
      | spec1           | SIM card     |                        |
      | spec2           | mobile line  | spec1                  |
      | spec3           | connectivity | spec2                  |
      | spec4           | time bundle  | spec2                  |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action   | specificationId | relationType | relatedOrderItemId | productOfferingType | productOfferingName    |
      # Atomic offer No-Change order item
      | orderItem1  | noChange | spec1           |              |                    |                     |                        |
      # Atomic offer Migrate order items
      | orderItem2  | migrate  | spec2           | migrateTo    | orderItem3         |                     |                        |
      | orderItem3  | migrate  | spec2           | migrateFrom  | orderItem2         |                     |                        |
      # Atomic offer Delete order item
      | orderItem4  | delete   | spec4           | reliesOn     | orderItem2         |                     |                        |
      # Atomic offer add order item
      | orderItem5  | add      | spec3           | reliesOn     | orderItem3         |                     |                        |
      #Contract Migrate order items
      | orderItem6  | migrate  |                 | migrateTo    | orderItem7         | Contract            | Mobile Package Basic   |
      | orderItem7  | migrate  |                 | migrateFrom  | orderItem6         | Contract            | Mobile Package Comfort |
    Then The system will create orchestration plan with order id "order1", state "Acknowledged" and contract name "Mobile Package Comfort"
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action  | state        |
      | orderItem3  | migrate | Acknowledged |
      | orderItem5  | add     | Acknowledged |
      | orderItem4  | delete  | Acknowledged |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged"
