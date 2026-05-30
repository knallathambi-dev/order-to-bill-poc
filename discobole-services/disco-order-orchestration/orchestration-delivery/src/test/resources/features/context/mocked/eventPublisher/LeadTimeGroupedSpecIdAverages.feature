Feature: [IPCEISCOOD-1050] Store grouped spec id lead time average

  Scenario: Get the history of lead time for some node/category in the plan.
    Given the system has the following node lead time statistics
      | specificationId | average actual lead time | sample size | sample window        |
      | spec1           | 3                        | 1           | 2025-09-03T11:10:00Z |
      | spec1           | 10.2                     | 4           | 2025-09-01T11:10:00Z |
      | spec2           | 2.31                     | 2           | 2025-09-03T11:10:00Z |
      | spec2           | 10                       | 15          | 2025-09-03T11:10:00Z |
    And the system has the following grouped spec id lead time statistics
      | specificationId | average lead time | sample size | last updated         |
      | spec1           | 10                | 3           | 2025-09-01T11:10:00Z |
      | spec2           | 2                 | 1           | 2025-09-01T11:10:00Z |
      | spec3           | 2                 | 1           | 2025-09-01T11:10:00Z |
    And  the current system timestamp is "2025-09-04T11:10:00Z"
    And  the cron job of grouping spec id average lead times has scan window of 2 day
    When cron job that groups average leadtime stats for spec ids is executed
    Then the system will have the following grouped weighted average spec id lead times
      | specificationId | average lead time | last updated         |
      | spec1           | 3                 | 2025-09-04T11:10:00Z |
      | spec2           | 9.0952            | 2025-09-04T11:10:00Z |
      | spec3           | 2                 | 2025-09-01T11:10:00Z |
