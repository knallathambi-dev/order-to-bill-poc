Feature: Purge Orchestration Plan Scheduler

  Scenario: Delete Executed plans
    Given the system has the following plans with 2 nodes with "Completed" state
      | plan id | state    | Last Modified date   |
      | 1       | Executed | 2024-01-04T00:00:00Z |
      | 3       | Executed | 2024-01-01T00:00:00Z |
    And the current system time is "2024-01-04T05:00:00Z"
    And the system has deletion orchestration plan threshold configured to be deleted after 1 day
    When the cron job running every day at 5:00 am
    Then the following plans will deleted
      | plan id |
      | 3       |
    And the follow plans will be still exists
      | plan id |
      | 1       |

  Scenario: Archive Executed plans with Failed nodes
    Given the system has the following plans with 2 nodes with "Failed" state
      | plan id | state    | Last Modified date   |
      | 1       | Executed | 2024-01-04T00:00:00Z |
      | 3       | Executed | 2024-01-01T00:00:00Z |
    And the current system time is "2024-01-04T05:00:00Z"
    And the system has archiving orchestration plan threshold configured to be deleted after 2 day
    When the cron job running every day at 5:00 am
    Then the following plans will archived
      | plan id |
      | 3       |
    And the follow plans will be still not archived
      | plan id |
      | 1       |

  Scenario: Archive Executed plans with Aborted nodes
    Given the system has the following plans with 2 nodes with "Aborted" state
      | plan id | state    | Last Modified date   |
      | 1       | Executed | 2024-01-04T00:00:00Z |
      | 3       | Executed | 2024-01-01T00:00:00Z |
    And the current system time is "2024-01-04T05:00:00Z"
    And the system has archiving orchestration plan threshold configured to be deleted after 2 day
    When the cron job running every day at 5:00 am
    Then the following plans will archived
      | plan id |
      | 3       |
    And the follow plans will be still not archived
      | plan id |
      | 1       |

  Scenario: Archive Aborted plans
    Given the system has the following plans with 2 nodes with "Aborted" state
      | plan id | state   | Last Modified date   |
      | 1       | Aborted | 2024-01-04T00:00:00Z |
      | 3       | Aborted | 2024-01-01T00:00:00Z |
    And the current system time is "2024-01-04T05:00:00Z"
    And the system has archiving orchestration plan threshold configured to be deleted after 2 day
    When the cron job running every day at 5:00 am
    Then the following plans will archived
      | plan id |
      | 3       |
    And the follow plans will be still not archived
      | plan id |
      | 1       |

  Scenario: Archive Rejected plans
    Given the system has the following plans with 2 nodes with "Failed" state
      | plan id | state    | Last Modified date   |
      | 1       | Rejected | 2024-01-04T00:00:00Z |
      | 3       | Rejected | 2024-01-01T00:00:00Z |
    And the current system time is "2024-01-04T05:00:00Z"
    And the system has archiving orchestration plan threshold configured to be deleted after 2 day
    When the cron job running every day at 5:00 am
    Then the following plans will archived
      | plan id |
      | 3       |
    And the follow plans will be still not archived
      | plan id |
      | 1       |

  Scenario: Deleted archived Executed plans with Failed nodes
    Given the system has the following plans with 2 nodes with "Failed" state
      | plan id | state    | Last Modified date   | archived |
      | 1       | Executed | 2024-01-04T00:00:00Z | true     |
      | 3       | Executed | 2024-01-01T00:00:00Z | true     |
    And the current system time is "2024-01-04T05:00:00Z"
    And the system has delete archived orchestration plan threshold configured to be deleted after 2 day
    When the cron job running every day at 5:00 am
    Then the following plans will deleted
      | plan id |
      | 3       |
    And the follow plans will be still exists
      | plan id |
      | 1       |


  Scenario: Archive Aborted plans
    Given the system has the following plans with 2 nodes with "Any" state
      | plan id | state   | Last Modified date   |
      | 1       | Aborted | 2024-01-04T00:00:00Z |
      | 3       | Aborted | 2024-01-01T00:00:00Z |
    And the current system time is "2024-01-04T05:00:00Z"
    And the system has archiving orchestration plan threshold configured to be deleted after 2 day
    When the cron job running every day at 5:00 am
    Then the following plans will archived
      | plan id |
      | 3       |
    And the follow plans will be still not archived
      | plan id |
      | 1       |


  Scenario: Archive Aborted plans
    Given the system has the following plans with 0 nodes with "No" state
      | plan id | state   | Last Modified date   |
      | 1       | Aborted | 2024-01-04T00:00:00Z |
      | 3       | Aborted | 2024-01-01T00:00:00Z |
    And the current system time is "2024-01-04T05:00:00Z"
    And the system has archiving orchestration plan threshold configured to be deleted after 2 day
    When the cron job running every day at 5:00 am
    Then the following plans will archived
      | plan id |
      | 3       |
    And the follow plans will be still not archived
      | plan id |
      | 1       |
