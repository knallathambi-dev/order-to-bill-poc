Feature: [IPCEISCOOD-542][IPCEISCOOD-1012] Planned Orchestration Plan Scheduler with Adaptive Processing
  As a COOD system
  I need to transition orchestration plans from PLANNED to ACKNOWLEDGED state
  So that plans are processed efficiently using an adaptive batch algorithm
  And all eligible plans are processed within the configured time window

  Background:
    Given the transition window is configured from "01:00" to "01:06"
    And the recommended batch size is 2
    And the job delay is "PT1M"
    And the current system timestamp is "2024-01-01T01:00:01Z"

  Scenario: Single eligible plan transitions from PLANNED to ACKNOWLEDGED
    Given the system has the following plans
      | plan id | state   | Requested Delivery Date |
      | planId  | Planned | 2024-01-01T00:00:00Z    |
    When the configured cron job to check plans with "Planned" state is triggered
    Then the plan "planId" will be updated with "Acknowledged" state
    And The system is going to send "orchestrationPlanStateChange" event for plan "planId" to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with status "Acknowledged"

  Scenario: Plans with non-PLANNED states are not processed
    Given the system has the following plans
      | plan id | state        | Requested Delivery Date |
      | planId1 | InProgress   | 2024-01-01T00:00:00Z    |
      | planId2 | Acknowledged | 2024-01-01T00:00:00Z    |
      | planId3 | Executed     | 2024-01-01T00:00:00Z    |
      | planId4 | Held         | 2024-01-01T00:00:00Z    |
    When the configured cron job to check plans with "Planned" state is triggered
    Then the following plans state will not be changed
      | plan id | state        |
      | planId1 | InProgress   |
      | planId2 | Acknowledged |
      | planId3 | Executed     |
      | planId4 | Held         |
    And The orchestration plan is not going to fire any event to topic "disco.order-orchestration.orchestrationPlanStateChange-event"

  Scenario: Gradual processing respects recommended batch size with multiple eligible plans
    Given the system has the following plans
      | plan id  | state   | Requested Delivery Date |
      | planId1  | Planned | 2024-01-01T00:00:00Z    |
      | planId2  | Planned | 2024-01-01T00:00:00Z    |
      | planId3  | Planned | 2024-01-01T00:00:00Z    |
      | planId4  | Planned | 2024-01-01T00:00:00Z    |
      | planId5  | Planned | 2024-01-01T00:00:00Z    |
      | planId6  | Planned | 2024-01-01T00:00:00Z    |
      | planId7  | Planned | 2024-01-01T00:00:00Z    |
      | planId8  | Planned | 2024-01-01T00:00:00Z    |
      | planId9  | Planned | 2024-01-01T00:00:00Z    |
      | planId10 | Planned | 2024-01-01T00:00:00Z    |
    When the configured cron job to check plans with "Planned" state is triggered
    Then the plan "planId1" will be updated with "Acknowledged" state
    And the plan "planId2" will be updated with "Acknowledged" state
    And the plan "planId3" still with "Planned" state
    And the plan "planId4" still with "Planned" state
    And the plan "planId5" still with "Planned" state
    And the plan "planId6" still with "Planned" state
    And the plan "planId7" still with "Planned" state
    And the plan "planId8" still with "Planned" state
    And the plan "planId9" still with "Planned" state
    And the plan "planId10" still with "Planned" state
    And The system is going to send "orchestrationPlanStateChange" event for plan "planId1" to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with status "Acknowledged"
    And The system is going to send "orchestrationPlanStateChange" event for plan "planId2" to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with status "Acknowledged"

  Scenario: Scheduler processes only eligible plans based on delivery date
    Given the system has the following plans
      | plan id | state   | Requested Delivery Date |
      | planId1 | Planned | 2024-01-01T00:00:00Z    |
      | planId2 | Planned | 2024-01-01T00:00:00Z    |
      | planId3 | Planned | 2024-01-01T00:00:00Z    |
      | planId4 | Planned | 2024-06-01T00:00:00Z    |
      | planId5 | Planned | 2024-07-01T00:00:00Z    |
    When the configured cron job to check plans with "Planned" state is triggered
    Then the plan "planId1" will be updated with "Acknowledged" state
    And the plan "planId2" will be updated with "Acknowledged" state
    And the plan "planId3" still with "Planned" state
    And the plan "planId4" still with "Planned" state
    And the plan "planId5" still with "Planned" state

  Scenario: Adaptive algorithm uses recommended rate early in time window
    Given the system has the following plans
      | plan id  | state   | Requested Delivery Date |
      | planId1  | Planned | 2024-01-01T00:00:00Z    |
      | planId2  | Planned | 2024-01-01T00:00:00Z    |
      | planId3  | Planned | 2024-01-01T00:00:00Z    |
      | planId4  | Planned | 2024-01-01T00:00:00Z    |
      | planId5  | Planned | 2024-01-01T00:00:00Z    |
      | planId6  | Planned | 2024-01-01T00:00:00Z    |
      | planId7  | Planned | 2024-01-01T00:00:00Z    |
      | planId8  | Planned | 2024-01-01T00:00:00Z    |
    When the configured cron job to check plans with "Planned" state is triggered
    And the plan "planId1" will be updated with "Acknowledged" state
    And the plan "planId2" will be updated with "Acknowledged" state
    And the plan "planId3" still with "Planned" state
    And the plan "planId4" still with "Planned" state
    And the plan "planId5" still with "Planned" state
    And the plan "planId6" still with "Planned" state
    And the plan "planId7" still with "Planned" state
    And the plan "planId8" still with "Planned" state
    And The system is going to send "orchestrationPlanStateChange" event for plan "planId1" to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with status "Acknowledged"
    And The system is going to send "orchestrationPlanStateChange" event for plan "planId2" to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with status "Acknowledged"

  Scenario: Adaptive algorithm increases batch size when approaching window end
    Given the system has the following plans
      | plan id  | state   | Requested Delivery Date |
      | planId1  | Planned | 2024-01-01T00:00:00Z    |
      | planId2  | Planned | 2024-01-01T00:00:00Z    |
      | planId3  | Planned | 2024-01-01T00:00:00Z    |
      | planId4  | Planned | 2024-01-01T00:00:00Z    |
      | planId5  | Planned | 2024-01-01T00:00:00Z    |
      | planId6  | Planned | 2024-01-01T00:00:00Z    |
      | planId7  | Planned | 2024-01-01T00:00:00Z    |
      | planId8  | Planned | 2024-01-01T00:00:00Z    |
      | planId9  | Planned | 2024-01-01T00:00:00Z    |
      | planId10 | Planned | 2024-01-01T00:00:00Z    |
      | planId11| Planned | 2024-01-01T00:00:00Z    |
    And the current system timestamp is "2024-01-01T01:04:00Z"
    When the configured cron job to check plans with "Planned" state is triggered
    Then the plan "planId1" will be updated with "Acknowledged" state
    And the plan "planId2" will be updated with "Acknowledged" state
    And the plan "planId3" will be updated with "Acknowledged" state
    And the plan "planId4" will be updated with "Acknowledged" state
    And the plan "planId5" will be updated with "Acknowledged" state
    And the plan "planId6" will be updated with "Acknowledged" state
    And the plan "planId7" will be updated with "Planned" state
    And the plan "planId8" will be updated with "Planned" state
    And the plan "planId9" will be updated with "Planned" state
    And the plan "planId10" will be updated with "Planned" state
    And the plan "planId11" will be updated with "Planned" state
    And The system is going to send "orchestrationPlanStateChange" event for plan "planId1" to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with status "Acknowledged"
    And The system is going to send "orchestrationPlanStateChange" event for plan "planId2" to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with status "Acknowledged"
    And The system is going to send "orchestrationPlanStateChange" event for plan "planId3" to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with status "Acknowledged"
    And The system is going to send "orchestrationPlanStateChange" event for plan "planId4" to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with status "Acknowledged"
    And The system is going to send "orchestrationPlanStateChange" event for plan "planId5" to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with status "Acknowledged"
    And The system is going to send "orchestrationPlanStateChange" event for plan "planId6" to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with status "Acknowledged"
