Feature: [IPCEISCOOD-794] [Enhancement] The error messages are not accumulated

  Scenario: Orchestration plan node has two error messages after creating the second incident
    Given the system has orchestration plan with id "plan1" and state "InProgress"
    And the plan with id "plan1" has the following nodes:
      | Id    | State      | Order Id |
      | node1 | InDelivery | order1   |
    And the node with id "node1" has the following related products:
      | Product Id | Product Type | Relationship Type | Order Item Id | Order Item Action |
      | product1   | CFS          | delivers          | orderItem1    | add               |
    And the node with id "node1" has the following error messages:
      | Code      | Message      | Reason      | Timestamp                     |
      | codeDesc1 | messageDesc1 | reasonDesc1 | 2025-06-02T17:56:09.085+00:00 |
    And the CPIB has installed product with id "product1" and type "Product" associated to order id "order1" and order item id "orderItem1" in "Created" main state and "PendingActive" operational state
    And the PATCH request for the CPIB installed product with id "product1" and type "Product" and "Created" main state and "Locked" operational state returns success
    When the system consumes delivery order item status event from topic "disco.delivery-management.deliveryOrderItemStatus-event" for orchestration plan node with id "node1" and state "Held"
    Then Fallout incident will be created with error code "Orchestration plan node with id[node1] held"
    And the node with id "node1" should have the following error messages:
      | Code                                        | Message                                     | Reason                                      | Timestamp                     |
      | codeDesc1                                   | messageDesc1                                | reasonDesc1                                 | 2025-06-02T17:56:09.085+00:00 |
      | Orchestration plan node with id[node1] held | Orchestration plan node with id[node1] held | Orchestration plan node with id[node1] held | 2000-01-01T00:00:00Z          |
