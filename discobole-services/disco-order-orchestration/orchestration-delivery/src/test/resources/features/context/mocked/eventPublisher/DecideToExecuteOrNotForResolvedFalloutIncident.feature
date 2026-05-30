Feature: [IPCEISCOOD-525] Decide to re-execute or not for resolved fallout incident

  Scenario: Orchestration plan node completed after being held
    Given the system has orchestration plan with id "plan1" and state "Held"
    And the plan with id "plan1" has the following nodes:
      | Id    | State | Order Id |
      | node1 | Held  | order1   |
    And the fallout service has the following fallout incidents:
      | Related Entity Id | Process Id | Task Id |
      | node1             | process1   | task1   |
    Then the system consumes orchestration plan node state change event to topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" and fallout source topic name "disco.service-order-management.serviceOrderStateChange-event" with node id "node1" and state "Completed"
    And  the fallout service will be called will the following data:
      | Process Id | Task Id | Resolution State |
      | process1   | task1   | skipped          |

  Scenario: Orchestration plan node failed after being held
    Given the system has orchestration plan with id "plan1" and state "Held"
    And the plan with id "plan1" has the following nodes:
      | Id    | State | Order Id |
      | node1 | Held  | order1   |
    And the fallout service has the following fallout incidents:
      | Related Entity Id | Process Id | Task Id |
      | node1             | process1   | task1   |
    Then the system consumes orchestration plan node state change event to topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" and fallout source topic name "disco.service-order-management.serviceOrderStateChange-event" with node id "node1" and state "Failed"
    And  the fallout service will be called will the following data:
      | Process Id | Task Id | Resolution State |
      | process1   | task1   | unresolved       |

  Scenario: Orchestration plan node held after being held
    Given the system has orchestration plan with id "plan1" and state "Held"
    And the plan with id "plan1" has the following nodes:
      | Id    | State | Order Id |
      | node1 | Held  | order1   |
    And the fallout service has the following fallout incidents:
      | Related Entity Id | Process Id | Task Id |
      | node1             | process1   | task1   |
      | node1             | process2   | task2   |
      | node1             | process3   | task3   |
    Then the system consumes orchestration plan node state change event to topic "disco.order-orchestration.orchestrationPlanNodeStateChange-event" and fallout source topic name "disco.service-order-management.serviceOrderStateChange-event" with node id "node1" and state "Held"
    And  the fallout service will be called will the following data:
      | Process Id | Task Id | Resolution State |
      | process1   | task1   | unresolved       |
      | process2   | task2   | skipped          |
      | process3   | task3   | skipped          |