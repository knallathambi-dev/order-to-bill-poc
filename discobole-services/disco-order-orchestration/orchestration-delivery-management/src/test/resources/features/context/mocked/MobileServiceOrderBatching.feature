Feature: [IPCEISCOOD-XXXX] Mobile Service Order Batching

  # Note: Batching configuration is set in test application.yml:
  # maxBatchSize: 3, batchCollectionSeconds: 5

  Scenario: Single mobile service order triggers batch after timeout
    Given service specification "mobile-spec-001" has service ordering url "http://localhost:9997/mobile/serviceOrdering/v1/serviceOrder"
    And service catalog returns service specification "mobile-spec-001" for id "mobile-spec-001"
    And service order batch response returns successfully for url "http://localhost:9997/mobile/serviceOrdering/v1/serviceOrder/batch"
    And mobile delivery order event for order with id "DO-001" and factory order id "FO-001" has the following order item refs:
      | Product Order Item Id | Orchestration Node Id | Service Specification Id | action |
      | OI-001                | node-001              | mobile-spec-001          | add    |
    And mobile delivery order request has the following data:
      | Requested Delivery Date | Start Date           | Product Order Id | Orchestration Plan Id |
      | 2026-03-20T10:00:00Z    | 2026-03-20T10:00:00Z | DO-001           | plan-001              |
    When the mobile batching system consumes delivery order event on topic "disco.delivery-management.deliveryOrder-event" with factory order id "FO-001" and state "InDelivery"
    And wait for 7 seconds
    Then a batched POST request should be made to "/mobile/serviceOrdering/v1/serviceOrder/batch" with 1 service orders

  Scenario: Multiple mobile service orders are batched together before timeout
    Given service specification "mobile-spec-002" has service ordering url "http://localhost:9997/mobile/serviceOrdering/v1/serviceOrder"
    And service catalog returns service specification "mobile-spec-002" for id "mobile-spec-002"
    And service order batch response returns successfully for url "http://localhost:9997/mobile/serviceOrdering/v1/serviceOrder/batch"
    And mobile delivery order event for order with id "DO-002" and factory order id "FO-002" has the following order item refs:
      | Product Order Item Id | Orchestration Node Id | Service Specification Id | action |
      | OI-002                | node-002              | mobile-spec-002          | add    |
    And mobile delivery order request has the following data:
      | Requested Delivery Date | Start Date           | Product Order Id | Orchestration Plan Id |
      | 2026-03-20T10:00:00Z    | 2026-03-20T10:00:00Z | DO-002           | plan-002              |
    When the mobile batching system consumes delivery order event on topic "disco.delivery-management.deliveryOrder-event" with factory order id "FO-002" and state "InDelivery"
    And mobile delivery order event for order with id "DO-003" and factory order id "FO-003" has the following order item refs:
      | Product Order Item Id | Orchestration Node Id | Service Specification Id | action |
      | OI-003                | node-003              | mobile-spec-002          | add    |
    And mobile delivery order request has the following data:
      | Requested Delivery Date | Start Date           | Product Order Id | Orchestration Plan Id |
      | 2026-03-20T10:00:00Z    | 2026-03-20T10:00:00Z | DO-003           | plan-003              |
    When the mobile batching system consumes delivery order event on topic "disco.delivery-management.deliveryOrder-event" with factory order id "FO-003" and state "InDelivery"
    And wait for 7 seconds
    Then a batched POST request should be made to "/mobile/serviceOrdering/v1/serviceOrder/batch" with 2 service orders

  Scenario: Batch sent immediately when max batch size is reached
    Given service specification "mobile-spec-003" has service ordering url "http://localhost:9997/mobile/serviceOrdering/v1/serviceOrder"
    And service catalog returns service specification "mobile-spec-003" for id "mobile-spec-003"
    And service order batch response returns successfully for url "http://localhost:9997/mobile/serviceOrdering/v1/serviceOrder/batch"
    And mobile delivery order event for order with id "DO-004" and factory order id "FO-004" has the following order item refs:
      | Product Order Item Id | Orchestration Node Id | Service Specification Id | action |
      | OI-004                | node-004              | mobile-spec-003          | add    |
    And mobile delivery order request has the following data:
      | Requested Delivery Date | Start Date           | Product Order Id | Orchestration Plan Id |
      | 2026-03-20T10:00:00Z    | 2026-03-20T10:00:00Z | DO-004           | plan-004              |
    When the mobile batching system consumes delivery order event on topic "disco.delivery-management.deliveryOrder-event" with factory order id "FO-004" and state "InDelivery"
    And mobile delivery order event for order with id "DO-005" and factory order id "FO-005" has the following order item refs:
      | Product Order Item Id | Orchestration Node Id | Service Specification Id | action |
      | OI-005                | node-005              | mobile-spec-003          | add    |
    And mobile delivery order request has the following data:
      | Requested Delivery Date | Start Date           | Product Order Id | Orchestration Plan Id |
      | 2026-03-20T10:00:00Z    | 2026-03-20T10:00:00Z | DO-005           | plan-005              |
    When the mobile batching system consumes delivery order event on topic "disco.delivery-management.deliveryOrder-event" with factory order id "FO-005" and state "InDelivery"
    And mobile delivery order event for order with id "DO-006" and factory order id "FO-006" has the following order item refs:
      | Product Order Item Id | Orchestration Node Id | Service Specification Id | action |
      | OI-006                | node-006              | mobile-spec-003          | add    |
    And mobile delivery order request has the following data:
      | Requested Delivery Date | Start Date           | Product Order Id | Orchestration Plan Id |
      | 2026-03-20T10:00:00Z    | 2026-03-20T10:00:00Z | DO-006           | plan-006              |
    When the mobile batching system consumes delivery order event on topic "disco.delivery-management.deliveryOrder-event" with factory order id "FO-006" and state "InDelivery"
    And wait for 2 seconds
    Then a batched POST request should be made to "/mobile/serviceOrdering/v1/serviceOrder/batch" with 3 service orders

  Scenario: Non-mobile service orders bypass batching
    Given service specification "fixed-spec-001" has service ordering url "http://localhost:9997/serviceOrdering/v1/serviceOrder"
    And service catalog returns service specification "fixed-spec-001" for id "fixed-spec-001"
    And service order response returns service order "SO-FIXED-001" with the following order items:
      | Id          | action | quantity |
      | SOI-FIXED-001 | add    | 1        |
    And The delivery order event for order with id "DO-FIXED-001" and the delivery factory type is "ServiceOrderManagement" has the following order item refs:
      | Product Order Item Id | Orchestration Node Id | Service Specification Id | action |
      | OI-FIXED-001          | node-fixed-001        | fixed-spec-001           | add    |
    And the delivery order request has the following data:
      | Requested Delivery Date | Start Date           | Product Order Id | Orchestration Plan Id |
      | 2026-03-20T10:00:00Z    | 2026-03-20T10:00:00Z | DO-FIXED-001     | plan-fixed-001        |
    When the system consumes delivery order event on topic "disco.delivery-management.deliveryOrder-event" with factory order id "FO-FIXED-001" and state "InDelivery"
    And wait for 3 seconds
    Then a non-batched POST request should be made to "/serviceOrdering/v1/serviceOrder"
    And no batched POST request should be made to "/batch"

  Scenario: Batching disabled sends orders individually
    Given batching is disabled
    And service specification "mobile-spec-004" has service ordering url "http://localhost:9997/mobile/serviceOrdering/v1/serviceOrder"
    And service catalog returns service specification "mobile-spec-004" for id "mobile-spec-004"
    And service order response returns service order "SO-007" with the following order items:
      | Id      | action | quantity |
      | SOI-007 | add    | 1        |
    And mobile delivery order event for order with id "DO-007" and factory order id "FO-007" has the following order item refs:
      | Product Order Item Id | Orchestration Node Id | Service Specification Id | action |
      | OI-007                | node-007              | mobile-spec-004          | add    |
    And mobile delivery order request has the following data:
      | Requested Delivery Date | Start Date           | Product Order Id | Orchestration Plan Id |
      | 2026-03-20T10:00:00Z    | 2026-03-20T10:00:00Z | DO-007           | plan-007              |
    When the mobile batching system consumes delivery order event on topic "disco.delivery-management.deliveryOrder-event" with factory order id "FO-007" and state "InDelivery"
    And wait for 3 seconds
    Then a non-batched POST request should be made to "/mobile/serviceOrdering/v1/serviceOrder"
    And no batched POST request should be made to "/batch"
