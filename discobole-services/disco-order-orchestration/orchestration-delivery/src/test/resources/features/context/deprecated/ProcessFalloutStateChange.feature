Feature: Process Fallout State Change
  @BeforeProcessFalloutStateChange
  Scenario: 1- Closing held node with dependency
    Given an orchestration node has an associated fallout with json "OrchestrationPlanWithFalloutIncident.json"
    And the orchestration node state = "HELD"
    And other orchestration nodes have a "deliver after" relationship with this node
    And the fallout has been completed with resolution = "UNRESOLVED"
    When COOD is notified of the completion of the fallout
    Then it sets the orchestration node state = held
    And it updates the state of the delivered product in CPIB to "Aborted"
    And all the nodes relying on this node will be updated to have state = "Aborted"

  @BeforeProcessFalloutStateChange
  Scenario: 2- Closing last held node
    Given an orchestration node has an associated fallout with json "OrchestrationPlanWithOneNodeWithFalloutIncident.json"
    And the orchestration node state = "HELD"
    And all other orchestration nodes in the orchestration plan have state = "failed" or "completed"
    And the fallout has been completed with resolution = "UNRESOLVED"
    When COOD is notified of the completion of the fallout
    Then it sets the orchestration node state = held
    And it updates the state of the delivered product in CPIB to "Aborted"
    And orchestration plan state will be updated to "EXECUTED"

  @BeforeProcessFalloutStateChange
  Scenario: 3- Closing failed node
    Given an orchestration node has an associated fallout with json "OrchestrationPlanWithOneNodeWithFalloutIncident.json"
    And the orchestration node state = "FAILED"
    And the fallout has been completed with resolution = "UNRESOLVED"
    When COOD is notified of the completion of the fallout
    Then it ignores the fallout with node state remains "FAILED"

  @BeforeProcessFalloutStateChange
  Scenario: 4- Closing inProgress node
    Given an orchestration node has an associated fallout with json "OrchestrationPlanWithOneNodeWithFalloutIncident.json"
    And the orchestration node state = "IN_PROGRESS"
    And the fallout has been completed with resolution = "UNRESOLVED"
    When COOD is notified of the completion of the fallout
    Then it ignores the fallout with node state remains "IN_PROGRESS"

