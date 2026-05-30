Feature: Purge Completed and Canceled Fallout Incidents

  Scenario: Delete Completed fallout incidents and related task flow and process flow
    Given the system has the following fallout incidents:
      | fallout id | state     | modification date    |
      | fallout1   | Completed | 2025-08-09T00:00:00Z |
      | fallout2   | Completed | 2025-09-09T00:00:00Z |
      | fallout3   | Held      | 2025-09-09T00:00:00Z |
    And the system has the following process flows:
      | process flow id | state  |
      | fallout1        | Active |
      | fallout2        | Active |
      | fallout3        | Active |
    And the system has the following task flows:
      | task flow id |
      | fallout1     |
      | fallout2     |
      | fallout3     |
    And the current system time is "2025-10-09T00:00:00Z"
    And the system has delete fallout threshold configured to be "P20D"
                                                                  # P20D = 20 days
    When the cron job is executed
    Then the following fallout incidents are deleted:
      | fallout id | state     | modification date    |
      | fallout1   | Completed | 2025-08-09T00:00:00Z |
      | fallout2   | Completed | 2025-09-09T00:00:00Z |
    And the following process flows are deleted:
      | process flow id | state  |
      | fallout1        | Active |
      | fallout2        | Active |
    And the following task flows are deleted:
      | task flow id |
      | fallout1     |
      | fallout2     |
    And the following fallout incidents still exist:
      | fallout id | state | modification date    |
      | fallout3   | Held  | 2025-09-09T00:00:00Z |
    And the following process flows still exist:
      | process flow id | state  |
      | fallout3        | Active |
    And the following task flows still exist:
      | task flow id |
      | fallout3     |

  Scenario: Delete Canceled fallout incidents and related task flow and process flow
    Given the system has the following fallout incidents:
      | fallout id | state    | modification date    |
      | fallout1   | Canceled | 2025-08-09T00:00:00Z |
      | fallout2   | Canceled | 2025-09-09T00:00:00Z |
      | fallout3   | Held     | 2025-09-09T00:00:00Z |
    And the system has the following process flows:
      | process flow id | state  |
      | fallout1        | Active |
      | fallout2        | Active |
      | fallout3        | Active |
    And the system has the following task flows:
      | task flow id |
      | fallout1     |
      | fallout2     |
      | fallout3     |
    And the current system time is "2025-10-09T00:00:00Z"
    And the system has delete fallout threshold configured to be "P20D"
                                                                  # P20D = 20 days
    When the cron job is executed
    Then the following fallout incidents are deleted:
      | fallout id | state    | modification date    |
      | fallout1   | Canceled | 2025-08-09T00:00:00Z |
      | fallout2   | Canceled | 2025-09-09T00:00:00Z |
    And the following process flows are deleted:
      | process flow id | state  |
      | fallout1        | Active |
      | fallout2        | Active |
    And the following task flows are deleted:
      | task flow id |
      | fallout1     |
      | fallout2     |
    And the following fallout incidents still exist:
      | fallout id | state | modification date    |
      | fallout3   | Held  | 2025-09-09T00:00:00Z |
    And the following process flows still exist:
      | process flow id | state  |
      | fallout3        | Active |
    And the following task flows still exist:
      | task flow id |
      | fallout3     |

  Scenario: Don't delete Completed or Canceled fallout incidents and their related task flows and process flows if they are above threshold
    Given the system has the following fallout incidents:
      | fallout id | state     | modification date    |
      | fallout1   | Completed | 2025-09-07T00:00:00Z |
      | fallout2   | Completed | 2025-10-07T00:00:00Z |
      | fallout3   | Canceled  | 2025-10-07T00:00:00Z |
    And the system has the following process flows:
      | process flow id | state  |
      | fallout1        | Active |
      | fallout2        | Active |
      | fallout3        | Active |
    And the system has the following task flows:
      | task flow id |
      | fallout1     |
      | fallout2     |
      | fallout3     |
    And the current system time is "2025-10-09T00:00:00Z"
    And the system has delete fallout threshold configured to be "P20D"
                                                                  # P20D = 20 days
    When the cron job is executed
    Then the following fallout incidents are deleted:
      | fallout id | state     | modification date    |
      | fallout1   | Completed | 2025-09-07T00:00:00Z |
    And the following process flows are deleted:
      | process flow id | state  |
      | fallout1        | Active |
    And the following task flows are deleted:
      | task flow id |
      | fallout1     |
    And the following fallout incidents still exist:
      | fallout id | state     | modification date    |
      | fallout2   | Completed | 2025-10-07T00:00:00Z |
      | fallout3   | Canceled  | 2025-10-07T00:00:00Z |
    And the following process flows still exist:
      | process flow id | state  |
      | fallout2        | Active |
      | fallout3        | Active |
    And the following task flows still exist:
      | task flow id |
      | fallout2     |
      | fallout3     |


