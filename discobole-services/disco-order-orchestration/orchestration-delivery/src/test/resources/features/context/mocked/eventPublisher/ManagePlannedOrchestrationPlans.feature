Feature: [IPCEISCOOD-1036] Manage Orchestration plans with status <Planned>

  Background:
    Given the transition window is configured from "02:00" to "06:00"
    And the recommended batch size is 2
    And the job delay is "PT1M"

  Scenario: 1- change the orchestration plan from status <Planned> to status <Acknowledged> based orchestrationPlanStartDate
    Given the system has orchestration plan with id "plan1", state "Planned" and order id "order1"
    And the plan with order id "order1" has requested delivery date "2025-12-13T10:15:30Z"
    And the plan with product order "order1" has the following order dates:
    | order start date     |
    | 2025-12-12T05:15:30Z |
    And the current system timestamp is "2025-12-13T02:15:30Z"
    When the configured cron job to check plans with "Planned" state is triggered
    Then the system will fire orchestration plan state change events to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged"
    And the plan "plan1" will be updated with "Acknowledged" state

  Scenario: 2- change the orchestration plan from status <Planned> to status <Acknowledged>  based on requested delivery date in case  <orchestrationPlanStartDate>  equal null
    Given the system has orchestration plan with id "plan1", state "Planned" and order id "order1"
    And the plan with order id "order1" has requested delivery date "2025-12-13T04:15:30Z"
    And the plan with product order "order1" has no order start date
    And the current system timestamp is "2025-12-13T05:15:30Z"
    When  the configured cron job to check plans with "Planned" state is triggered
    Then the system will fire orchestration plan state change events to topic "disco.order-orchestration.orchestrationPlanStateChange-event" with order id "order1" and state "Acknowledged"
    And the plan "plan1" will be updated with "Acknowledged" state

  Scenario: 3- the orchestration plan should be still with state  <Planned> in case <requested delivery date> and <order start date> still in the future date
    Given the system has orchestration plan with id "plan1", state "Planned" and order id "order1"
    And the plan with order id "order1" has requested delivery date "2025-12-13T10:15:30Z"
    And  the plan with product order "order1" has the following order dates:
    | order start date     |
    | 2025-12-12T05:15:30Z |
    And the current system timestamp is "2025-12-10T02:15:30Z"
    When  the configured cron job to check plans with "Planned" state is triggered
    Then the plan "plan1" still with "Planned" state
