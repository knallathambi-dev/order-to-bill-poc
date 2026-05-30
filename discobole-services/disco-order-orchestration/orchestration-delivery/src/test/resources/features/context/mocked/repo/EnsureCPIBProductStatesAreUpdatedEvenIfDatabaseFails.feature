Feature: Ensure CPIB product states are updated even if database fails

  Scenario: Before delivery, set CPIB physicalProduct status to PendingDelivery when action is add
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "Confirmed" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "Confirmed" operational state returns success
    And the system has a mock orchestration plan with id "plan1", state "InProgress", order id "order1" and nodes:
      | Id    | State      | Order Id |
      | node1 | InProgress | order1   |
    And the system mocked node with "node1" has related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product1 | physicalProduct | delivers          | orderItem1    | add               |
    And the system repository will fail on save
    When the system consumes mocked orchestration plan node state change event on topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" with node id "node1" and state "InProgress"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Created" and operational state will be "PendingDelivery"

  Scenario: Update CPIB operational status to Aborted when delivery factory is Failed and node has action add
    Given the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "PendingActive" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "PendingActive" operational state returns success
    And the system has a mock orchestration plan with id "plan1", state "InProgress", order id "order1" and nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the system mocked node with "node1" has related products:
      | Id       | Product Type    | Relationship Type | Order Item Id | Order Item Action |
      | product1 | physicalProduct | delivers          | orderItem1    | add               |
    And the system repository will fail on save
    When the system consumes delivery status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for mocked orchestration plan node with id "node1" and state "Failed"
    Then the CPIB installed product with id "product1" and type "Product" main state will be "Aborted" and operational state will be "Aborted"


