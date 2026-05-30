Feature: [IPCEISCOOD-926] Lead time history statistics API

  Scenario: Request contract lead time history with all query parameters by time period
    Given the system has the following contract lead time history statistics
      | contract name | sample window        | min actual lead time | max actual lead time | average actual lead time | sample size |
      | Contract-A    | 2025-09-10T10:00:00Z | 7000                 | 21000                | 14050.5                  | 20          |
      | Contract-A    | 2025-09-10T11:00:00Z | 7200                 | 21600                | 14500.0                  | 22          |
      | Contract-A    | 2025-09-10T12:00:00Z | 7500                 | 22500                | 15000.3                  | 24          |
      | Contract-A    | 2025-09-10T13:00:00Z | 7300                 | 22000                | 14750.2                  | 21          |
      | Contract-A    | 2025-09-10T14:00:00Z | 7600                 | 23000                | 15200.7                  | 23          |
      | Contract-A    | 2025-09-10T15:00:00Z | 7800                 | 23500                | 15400.9                  | 25          |
      | Contract-A    | 2025-09-10T16:00:00Z | 8000                 | 24000                | 16000.0                  | 18          |
      | Contract-A    | 2025-09-10T17:00:00Z | 8200                 | 25000                | 16500.4                  | 20          |
      | Contract-B    | 2025-09-10T12:00:00Z | 6800                 | 21000                | 13950.0                  | 19          |
      | Contract-C    | 2025-09-10T14:00:00Z | 7000                 | 22000                | 14500.5                  | 17          |

    When the user requests contract lead time history statistics with query params "?contractName=Contract-A&min=true&max=true&average=true&fields=contractName,statistics&offset=0&limit=3&sort=sampleWindow&timePeriodStart=2025-09-10T11:00:00Z&timePeriodEnd=2025-09-10T15:00:00Z"

    Then the system responds with status code 200 contract name "Contract-A"
    And the following lead time history statistics
      | sample window        | min actual lead time | max actual lead time | average actual lead time | sample size |
      | 2025-09-10T11:00:00Z | 7200                 | 21600                | 14500.0                  | 22          |
      | 2025-09-10T12:00:00Z | 7500                 | 22500                | 15000.3                  | 24          |
      | 2025-09-10T13:00:00Z | 7300                 | 22000                | 14750.2                  | 21          |
    And the statistics are sorted by sample window "ascending"
    And the response headers contain "X-Result-Count" = "3"
    And the response headers contain "X-Total-Count" = "4"

  Scenario: Request contract lead time history with all query parameters by approximate count
    Given the system has the following contract lead time history statistics
      | contract name | sample window        | min actual lead time | max actual lead time | average actual lead time | sample size |
      | Contract-A    | 2025-09-10T10:00:00Z | 7000                 | 21000                | 14050.5                  | 20          |
      | Contract-A    | 2025-09-10T11:00:00Z | 7200                 | 21600                | 14500.0                  | 22          |
      | Contract-A    | 2025-09-10T12:00:00Z | 7500                 | 22500                | 15000.3                  | 24          |
      | Contract-A    | 2025-09-10T13:00:00Z | 7300                 | 22000                | 14750.2                  | 21          |
      | Contract-A    | 2025-09-10T14:00:00Z | 7600                 | 23000                | 15200.7                  | 23          |
      | Contract-A    | 2025-09-10T15:00:00Z | 7800                 | 23500                | 15400.9                  | 25          |
      | Contract-A    | 2025-09-10T16:00:00Z | 8000                 | 24000                | 16000.0                  | 18          |
      | Contract-A    | 2025-09-10T17:00:00Z | 8200                 | 25000                | 16500.4                  | 20          |
      | Contract-B    | 2025-09-10T12:00:00Z | 6800                 | 21000                | 13950.0                  | 19          |
      | Contract-C    | 2025-09-10T14:00:00Z | 7000                 | 22000                | 14500.5                  | 17          |

    When the user requests contract lead time history statistics with query params "?contractName=Contract-A&min=true&max=true&average=true&fields=contractName,statistics&offset=1&limit=3&sort=-sampleWindow&approximateCount=40"

    Then the system responds with status code 200 contract name "Contract-A"
    And the following lead time history statistics
      | sample window        | min actual lead time | max actual lead time | average actual lead time | sample size |
      | 2025-09-10T16:00:00Z | 8000                 | 24000                | 16000.0                  | 18          |
      | 2025-09-10T15:00:00Z | 7800                 | 23500                | 15400.9                  | 25          |
    And the statistics are sorted by sample window "descending"
    And the response headers contain "X-Result-Count" = "2"
    And the response headers contain "X-Total-Count" = "3"

  Scenario: Request node lead time history with all query parameters by time period
    Given the system has the following node lead time history statistics
      | product spec id | delivery factory name | sample window        | min actual lead time | max actual lead time | average actual lead time | sample size |
      | PS-6001         | Mobile                | 2025-09-05T09:00:00Z | 6800                 | 20000                | 13450.0                  | 18          |
      | PS-6001         | Mobile                | 2025-09-05T10:00:00Z | 7000                 | 21000                | 14000.5                  | 20          |
      | PS-6001         | Mobile                | 2025-09-05T11:00:00Z | 7200                 | 21600                | 14500.0                  | 22          |
      | PS-6001         | Mobile                | 2025-09-05T12:00:00Z | 7500                 | 22500                | 15000.3                  | 24          |
      | PS-6001         | Mobile                | 2025-09-05T13:00:00Z | 7600                 | 23000                | 15200.7                  | 23          |
      | PS-6002         | Mobile                | 2025-09-05T09:00:00Z | 6600                 | 19800                | 13200.0                  | 16          |
      | PS-6002         | Mobile                | 2025-09-05T10:00:00Z | 6700                 | 20500                | 13550.8                  | 15          |
      | PS-7001         | Fixed                 | 2025-09-05T10:00:00Z | 8200                 | 26000                | 17100.4                  | 19          |

    When the user requests node lead time history statistics with query params "?productSpecId=PS-6001&min=true&max=true&average=true&fields=productSpecId,deliveryFactoryName,statistics&offset=0&limit=3&sort=sampleWindow&timePeriodStart=2025-09-05T09:00:00Z&timePeriodEnd=2025-09-05T13:00:00Z"

    Then the system responds with status code 200 product spec id "PS-6001"
    And the following lead time history statistics
      | sample window        | min actual lead time | max actual lead time | average actual lead time | sample size |
      | 2025-09-05T09:00:00Z | 6800                 | 20000                | 13450.0                  | 18          |
      | 2025-09-05T10:00:00Z | 7000                 | 21000                | 14000.5                  | 20          |
      | 2025-09-05T11:00:00Z | 7200                 | 21600                | 14500.0                  | 22          |
    And the statistics are sorted by sample window "ascending"
    And the response headers contain "X-Result-Count" = "3"
    And the response headers contain "X-Total-Count" = "4"


  Scenario: Request node lead time history with all query parameters group by delivery factory using time period
    Given the system has the following node lead time history statistics
      | product spec id | delivery factory name | sample window        | min actual lead time | max actual lead time | average actual lead time | sample size |
      | PS-6001         | Mobile                | 2025-09-05T09:00:00Z | 6800                 | 20000                | 13450.0                  | 18          |
      | PS-6001         | Mobile                | 2025-09-05T10:00:00Z | 7000                 | 21000                | 14000.5                  | 20          |
      | PS-6001         | Mobile                | 2025-09-05T11:00:00Z | 7200                 | 21600                | 14500.0                  | 22          |
      | PS-6001         | Mobile                | 2025-09-05T12:00:00Z | 7500                 | 22500                | 15000.3                  | 24          |
      | PS-6001         | Mobile                | 2025-09-05T13:00:00Z | 7600                 | 23000                | 15200.7                  | 23          |
      | PS-6002         | Mobile                | 2025-09-05T09:00:00Z | 6600                 | 19800                | 13200.0                  | 16          |
      | PS-6002         | Mobile                | 2025-09-05T11:00:00Z | 6700                 | 20500                | 13550.8                  | 15          |
      | PS-7001         | Fixed                 | 2025-09-05T10:00:00Z | 8200                 | 26000                | 17100.4                  | 19          |

    When the user requests node lead time history statistics with query params "?deliveryFactoryName=Mobile&min=true&max=true&average=true&fields=productSpecId,deliveryFactoryName,statistics&offset=2&limit=3&sort=-sampleWindow&timePeriodStart=2022-09-05T09:00:00Z&timePeriodEnd=2026-09-05T13:00:00Z"

    Then the system responds with status code 200 delivery factory name "Mobile"
    And the following lead time history statistics
      | sample window        | min actual lead time | max actual lead time | average actual lead time | sample size |
      | 2025-09-05T11:00:00Z | 6700                 | 20500                | 13550.8                  | 15          |
      | 2025-09-05T11:00:00Z | 7200                 | 21600                | 14500.0                  | 22          |
      | 2025-09-05T10:00:00Z | 7000                 | 21000                | 14000.5                  | 20          |
    And the statistics are sorted by sample window "descending"
    And the response headers contain "X-Result-Count" = "3"
    And the response headers contain "X-Total-Count" = "7"
