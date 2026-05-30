Feature: Purge Events Scheduler

  Scenario: Events purge job only deletes events older than specified threshold
    Given the events collection has the following events
      | Aggregate Id | Event Timestamp          |
      | 1            | 2025-05-01T09:00:00.000Z |
      | 3            | 2025-08-01T09:00:00.000Z |
    And the current system time is "2025-09-04T05:00:00Z"
    And events purge job deletes events older than "P90D"
                                                  # P90D = 90 days = three months
    When the delete events job is executed
    Then events with the following ids no longer exist
      | Aggregate Id |
      | 1            |
    And events with the following ids still exist
      | Aggregate Id |
      | 3            |

  Scenario: Events purge job doesn't delete any event when all of them are above threshold
    Given the events collection has the following events
      | Aggregate Id | Event Timestamp          |
      | 1            | 2025-07-01T09:00:00.000Z |
      | 2            | 2025-08-01T09:00:00.000Z |
      | 3            | 2025-08-21T09:00:00.000Z |
    And the current system time is "2025-09-04T05:00:00Z"
    And events purge job deletes events older than "P90D"
                                                  # P90D = 90 days = three months
    When the delete events job is executed
    Then events with the following ids still exist
      | Aggregate Id |
      | 1            |
      | 2            |
      | 3            |

  Scenario: Events purge job deletes all events when all of them are bellow threshold
    Given the events collection has the following events
      | Aggregate Id | Event Timestamp          |
      | 1            | 2025-07-01T09:00:00.000Z |
      | 2            | 2025-08-01T09:00:00.000Z |
      | 3            | 2025-08-21T09:00:00.000Z |
    And the current system time is "2025-09-04T05:00:00Z"
    And events purge job deletes events older than "P1D"
                                                  # P1D = 1 day
    When the delete events job is executed
    Then events with the following ids no longer exist
      | Aggregate Id |
      | 1            |
      | 2            |
      | 3            |
