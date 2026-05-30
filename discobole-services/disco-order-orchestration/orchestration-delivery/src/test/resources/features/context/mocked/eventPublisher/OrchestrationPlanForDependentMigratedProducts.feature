# IPCEISCOOD-1039: [integration][migration] there is no plan created and plan status is initialized when Migration from Comfort → Extra Offer and from relax → extra
Feature: Create orchestration plan for product orders with dependent migrated products

  Scenario: Create an orchestration plan for a product order containing two migrated products, where one related product depends on the other.
    Given The product catalog has the following installed products:
      | specificationId | name         | relatedSpecificationID |
      | spec1           | SIM card     |                        |
      | spec2           | mobile line  | spec1                  |
      | spec3           | connectivity | spec2                  |
    When The topic "disco.order-management.productOrderStateChange-event" receives an event with product order id "order1" and state "accepted" and the following product order items:
      | orderItemId | action   | specificationId | relationType | relatedOrderItemId | productOfferingType | productOfferingName |
      # Atomic offer No-Change order item
      | orderItem1  | noChange | spec1           |              |                    |                     |                     |
      # Atomic offer migrate order items
      | orderItem2  | migrate  | spec2           | migrateTo    | orderItem3         |                     |                     |
      | orderItem3  | migrate  | spec2           | migrateFrom  | orderItem2         |                     |                     |
      # Atomic offer Migrate order items
      | orderItem4  | migrate  | spec3           | migrateTo    | orderItem5         |                     |                     |
      | orderItem4  |          |                 | reliesOn     | orderItem2         |                     |                     |
      | orderItem5  | migrate  | spec3           | migrateFrom  | orderItem4         |                     |                     |
      | orderItem5  |          |                 | reliesOn     | orderItem3         |                     |                     |
    Then The system will create orchestration plan with order id "order1" and state "Acknowledged"
    And The system orchestration plan with order id "order1" will have exactly the following orchestration plan nodes
      | orderItemId | action  | state        |
      | orderItem3  | migrate | Acknowledged |
      | orderItem5  | migrate | Acknowledged |
    And the system will fire orchestration plan state change event to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged"
