Feature: List Orchestration plan
  @BeforeListOrchestrationPlan
  Scenario: List Orchestration plan
    Given an orchestration plan has been created
    And the logged in user has the appropriate role
    When the COOD administrator clicks to view the list of plans
    Then they should be able to view any datetime field with zone