Feature: [IPCEISCOOD-898] Rejected Plans due to lack of specs in ODACAT or lack or relations in Received order

  Scenario: 1- plan state updated to be "Held" due to missing specification ids from catalog side
    Given There is no orchestration plan created
    And The product catalog has the following installed products:
      | specificationId | relationType | relatedSpecificationID |
      | spec1           |              |                        |
      | spec2           | reliesOn     | spec1                  |
      | spec3           | reliesOn     | spec1                  |
    And the following specifications are missed from product catalog
      | specificationId | relationType | relatedSpecificationID |
      | spec4           |              |                        |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action   | specificationId |
      | orderItem1  | add      | spec1           |
      | orderItem2  | add      | spec2           |
      | orderItem3  | noChange | spec3           |
      | orderItem4  | add      | spec4           |
    Then the system will create orchestration plan without nodes with order id "order1" and state "Held"
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Held":

  Scenario: 2- plan state updated to be "Rejected" in case of unresolve fallout incident due to missing specification ids from catalog side
    Given the system has orchestration plan with id "plan1" and state "Held"
    And the plan with id "plan1" has no orchestration plan nodes
    And the following specifications are missed from product catalog
      | specificationId | relationType | relatedSpecificationID |
      | spec1           |              |                        |
      | spec2           | reliesOn     | spec1                  |
      | spec3           | reliesOn     | spec1                  |
      | spec4           | reliesOn     | spec3                  |
    And the fallout service has the following fallout incidents:
      | Related Entity Id | Process Id | Task Id |
      | plan1             | process1   | task1   |
    When the system consumes fallout incident state change event from topic "disco.order-orchestration-fallout.falloutIncidentStateChange-event" with id "fId", state "Completed", resolution state "unresolved" and initiator plan id "plan1"
    Then The orchestration plan "plan1" is going to be updated with status "Rejected"
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Rejected"


  Scenario: 3- plan state updated to be "Held" due to missing relationships
    Given  There is no orchestration plan created
    And The product catalog has the following installed products:
      | specificationId | relationType | relatedSpecificationID |
      | spec1           |              |                        |
      | spec2           | reliesOn     | spec1                  |
      | spec3           | reliesOn     | spec1                  |
      | spec4           | reliesOn     | spec3                  |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem1         |
      | orderItem4  | add    | spec4           |              |                    |
      | orderItem5  | add    | spec1           |              |                    |
      | orderItem6  | add    | spec2           | reliesOn     | orderItem5         |
      | orderItem7  | add    | spec3           | reliesOn     | orderItem5         |
      | orderItem8  | add    | spec4           |              |                    |
    And  product order item with id "orderItem1" has no prerequisites
    And  product order item with id "orderItem5" has no prerequisites
    And  product order item with id "orderItem4" has no relationType "reliesOn" with product order item with id "orderItem3"
    And  product order item with id "orderItem8" has no relationType "reliesOn" with product order item with id "orderItem7"
    Then the system will create orchestration plan without nodes with order id "order1" and state "Held"
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Held":

  Scenario: 4- plan state updated to be "Rejected" in case of unresolving fallout due to missing relationships
    Given  the system has orchestration plan with id "plan1" and state "Held"
    And the fallout service has the following fallout incidents:
      | Related Entity Id | Process Id | Task Id |
      | plan1             | process1   | task1   |
    When the system consumes fallout incident state change event from topic "disco.order-orchestration-fallout.falloutIncidentStateChange-event" with id "fId", state "Completed", resolution state "unresolved" and initiator plan id "plan1"
    Then The orchestration plan "plan1" is going to be updated with status "Rejected"
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Rejected"


  Scenario: 5- plan state updated to be "Held" due to wrong relationships
    Given  There is no orchestration plan created
    And    The product catalog has the following installed products:
      | specificationId | relationType | relatedSpecificationID |
      | spec1           |              |                        |
      | spec2           | reliesOn     | spec1                  |
      | spec3           | reliesOn     | spec1                  |
      | spec4           | reliesOn     | spec3                  |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action | specificationId | relationType | relatedOrderItemId |
      | orderItem1  | add    | spec1           |              |                    |
      | orderItem2  | add    | spec2           | reliesOn     | orderItem1         |
      | orderItem3  | add    | spec3           | reliesOn     | orderItem1         |
      | orderItem4  | add    | spec4           | reliesOn     | orderItem3         |
      | orderItem5  | add    | spec1           |              |                    |
      | orderItem6  | add    | spec2           | reliesOn     | orderItem3         |
      | orderItem7  | add    | spec3           | reliesOn     | orderItem5         |
      | orderItem8  | add    | spec4           | reliesOn     | orderItem7         |
    And  product order item with id "orderItem1" has no prerequisites
    And  product order item with id "orderItem5" has no prerequisites
    # product order item with id "orderItem6" has wrong relationType "reliesOn" with product order item with id "orderItem3"
    Then the system will create orchestration plan without nodes with order id "order1" and state "Held"
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Held":

  