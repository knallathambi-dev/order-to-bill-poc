Feature: [IPCEISCOOD-392] Pagination - Next button is not working after the 2nd page

  @BeforePagination
  Scenario: Navigate to next page correctly if we are on the first page
    Given the system has the following orchestration plans:
      | planId | receivedDate         |
      | plan1  | 2025-01-26T00:00:00Z |
      | plan2  | 2025-01-25T00:00:00Z |
      | plan3  | 2025-01-24T00:00:00Z |
      | plan4  | 2025-01-23T00:00:00Z |
      | plan5  | 2025-01-22T00:00:00Z |
      | plan6  | 2025-01-21T00:00:00Z |
      | plan7  | 2025-01-20T00:00:00Z |
      | plan8  | 2025-01-19T00:00:00Z |
      | plan9  | 2025-01-18T00:00:00Z |
      | plan10 | 2025-01-17T00:00:00Z |
    When the user requests plans for page 1 with limit 3
    Then the system responds with the following orchestration plans
      | planId | receivedDate         |
      | plan1  | 2025-01-26T00:00:00Z |
      | plan2  | 2025-01-25T00:00:00Z |
      | plan3  | 2025-01-24T00:00:00Z |
    And the system responds with the following pagination details in the headers:
      """
      <http://localhost/orchestrationPlan?offset=0&limit=3>;rel="self",<http://localhost/orchestrationPlan?offset=0&limit=3>;rel="first",<http://localhost/orchestrationPlan?offset=3&limit=3>;rel="next",<http://localhost/orchestrationPlan?offset=9&limit=3>;rel="last"
      """

  @BeforePagination
  Scenario: Navigate to next page correctly if we are on the before last page
    Given the system has the following orchestration plans:
      | planId | receivedDate         |
      | plan1  | 2025-01-26T00:00:00Z |
      | plan2  | 2025-01-25T00:00:00Z |
      | plan3  | 2025-01-24T00:00:00Z |
      | plan4  | 2025-01-23T00:00:00Z |
      | plan5  | 2025-01-22T00:00:00Z |
      | plan6  | 2025-01-21T00:00:00Z |
      | plan7  | 2025-01-20T00:00:00Z |
      | plan8  | 2025-01-19T00:00:00Z |
      | plan9  | 2025-01-18T00:00:00Z |
      | plan10 | 2025-01-17T00:00:00Z |
    When the user requests plans for page 3 with limit 3
    Then the system responds with the following orchestration plans
      | planId | receivedDate         |
      | plan7  | 2025-01-20T00:00:00Z |
      | plan8  | 2025-01-19T00:00:00Z |
      | plan9  | 2025-01-18T00:00:00Z |
    And the system responds with the following pagination details in the headers:
      """
      <http://localhost/orchestrationPlan?offset=6&limit=3>;rel="self",<http://localhost/orchestrationPlan?offset=0&limit=3>;rel="first",<http://localhost/orchestrationPlan?offset=3&limit=3>;rel="prev",<http://localhost/orchestrationPlan?offset=9&limit=3>;rel="next",<http://localhost/orchestrationPlan?offset=9&limit=3>;rel="last"
      """

  @BeforePagination
  Scenario: No next page if we are on the last page
    Given the system has the following orchestration plans:
      | planId | receivedDate         |
      | plan1  | 2025-01-26T00:00:00Z |
      | plan2  | 2025-01-25T00:00:00Z |
      | plan3  | 2025-01-24T00:00:00Z |
      | plan4  | 2025-01-23T00:00:00Z |
      | plan5  | 2025-01-22T00:00:00Z |
      | plan6  | 2025-01-21T00:00:00Z |
      | plan7  | 2025-01-20T00:00:00Z |
      | plan8  | 2025-01-19T00:00:00Z |
      | plan9  | 2025-01-18T00:00:00Z |
      | plan10 | 2025-01-17T00:00:00Z |
    When the user requests plans for page 4 with limit 3
    Then the system responds with the following orchestration plan
      | planId | receivedDate         |
      | plan10 | 2025-01-17T00:00:00Z |
    And the system responds with the following pagination details in the headers:
      """
      <http://localhost/orchestrationPlan?offset=9&limit=3>;rel="self",<http://localhost/orchestrationPlan?offset=0&limit=3>;rel="first",<http://localhost/orchestrationPlan?offset=6&limit=3>;rel="prev",<http://localhost/orchestrationPlan?offset=9&limit=3>;rel="last"
      """
