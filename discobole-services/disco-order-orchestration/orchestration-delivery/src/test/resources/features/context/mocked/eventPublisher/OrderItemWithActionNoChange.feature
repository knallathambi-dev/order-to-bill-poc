Feature: OrderItem with action == No Change

  Scenario: Exclude Order Items with NoChange Action from Node Creation
    Given The product catalog has the following installed products:
      | specificationId | name |
      | spec1           | cfs1 |
      | spec2           | cfs2 |
      | spec3           | cfs3 |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action   | specificationId |
      | orderItem1  | add      | spec1           |
      | orderItem2  | modify   | spec2           |
      | orderItem3  | noChange | spec3           |
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        |
      | orderItem1  | add    | Acknowledged |
      | orderItem2  | modify | Acknowledged |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged"


  Scenario: Node Creation for Order Items with Actions Add, Del, and Modify
    Given The product catalog has the following installed products:
      | specificationId | name |
      | spec1           | cfs1 |
      | spec2           | cfs2 |
      | spec3           | cfs3 |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action | specificationId |
      | orderItem1  | add    | spec1           |
      | orderItem2  | modify | spec2           |
      | orderItem3  | delete | spec3           |
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action | state        |
      | orderItem1  | add    | Acknowledged |
      | orderItem2  | modify | Acknowledged |
      | orderItem3  | delete | Acknowledged |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged"

