<!--

SPDX-FileCopyrightText: 2025 Orange SA

SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Getting Started

Welcome to the Lead Time Statistics API! This guide will help you get started with using our API to query historical, sampled lead time statistics at node and contract levels.

## Authentication

The API requires authentication and authorization using a token from keycloak. Please refer to your platform’s authentication documentation.

## API Endpoints

### Get Node Lead Time History Statistics

Retrieve sampled lead time statistics at node level. Node lead time represents the time for an individual product/delivery factory.

Constraints:

- Selector: provide exactly one of the following must be used
  - productSpecId
  - deliveryFactoryName
- Time range: exactly one of the following must be used
  - timePeriodStart + timePeriodEnd (together)
  - approximateCount
- Statistics: one or more of the following must be true
  - min
  - max
  - average

Endpoint:
```
GET /leadTimeHistoryStatistics/node
```

Parameters:

| Name               | In    | Type    | Required | Description                                                                 |
|:-------------------|:------|:--------|:---------|:----------------------------------------------------------------------------|
| productSpecId      | query | string  | false    | Product Specification identifier (node level).                              |
| deliveryFactoryName| query | string  | false    | Delivery factory/category (e.g., Mobile, Fixed, Fiber, Shipment).          |
| min                | query | boolean | false    | If true, include minActualLeadTime in response.                             |
| max                | query | boolean | false    | If true, include maxActualLeadTime in response.                             |
| average            | query | boolean | false    | If true, include averageActualLeadTime in response.                         |
| timePeriodStart    | query | string  | false    | Start of the time window (inclusive), format: date-time. Requires timePeriodEnd and must not be used with approximateCount. |
| timePeriodEnd      | query | string  | false    | End of the time window (exclusive), format: date-time. Requires timePeriodStart and must not be used with approximateCount. |
| approximateCount   | query | integer | false    | Approximate number of most recent samples to return. Must not be used with timePeriodStart/End. |
| fields             | query | string  | false    | Comma-separated properties to be provided in response.                      |
| offset             | query | integer | false    | Index of the first statistics sample to return (default: 0).                |
| limit              | query | integer | false    | Number of statistics samples to return (default: 100).                      |
| sort               | query | string  | false    | Sort by sampleWindow; prefix with “-” for descending (e.g., -sampleWindow). |

Responses:

| Code | Description           | Content-Type     | Schema                             | Headers                                        |
|:-----|:----------------------|:-----------------|:------------------------------------|:-----------------------------------------------|
| 200  | OK                    | application/json | NodeLeadTimeHistoryStatistics       | X-Result-Count, X-Total-Count                  |
| 400  | Bad Request           | application/json | Error                               |                                                |
| 401  | Unauthorized          | application/json | Error                               |                                                |
| 404  | Not Found             | application/json | Error                               |                                                |
| 500  | Internal Server Error | application/json | Error                               |                                                |

Example Request (most recent 24 samples, descending by time):
```bash
curl -G "{{baseUrl}}/leadTimeHistoryStatistics/node" \
  --data-urlencode "deliveryFactoryName=Fiber" \
  --data-urlencode "fields=deliveryFactoryName,statistics,productSpecId" \
  --data-urlencode "min=true" \
  --data-urlencode "max=true" \
  --data-urlencode "average=true" \
  --data-urlencode "approximateCount=130" \
  --data-urlencode "sort=-sampleWindow" \
  --data-urlencode "limit=2" \
  -H "Accept: application/json"
```

Example Response:
```json
{
  "deliveryFactoryName": "Fiber",
  "statistics": [
    {
      "sampleWindow": "2025-01-10T10:00:00Z",
      "minActualLeadTime": 12,
      "maxActualLeadTime": 40,
      "averageActualLeadTime": 24.6,
      "sampleSize": 136
    },
    {
      "sampleWindow": "2025-01-10T09:00:00Z",
      "minActualLeadTime": 10,
      "maxActualLeadTime": 38,
      "averageActualLeadTime": 22.9,
      "sampleSize": 121
    }
  ]
}
```

Response headers:

- X-Result-Count: 2
- X-Total-Count: 24

---

### Get Contract Lead Time History Statistics

Retrieve sampled lead time statistics at contract level. Contract lead time represents the total time to complete an entire contract/plan (sum across nodes).

Constraints:
- Selector: contractName is required
- Time range: exactly one of the following must be used
  - timePeriodStart + timePeriodEnd (together)
  - approximateCount
- Statistics: one or more of the following must be true
  - min
  - max
  - average

Endpoint:
```
GET /leadTimeHistoryStatistics/contract
```

Parameters:

| Name             | In    | Type    | Required | Description                                                                 |
|:-----------------|:------|:--------|:---------|:----------------------------------------------------------------------------|
| contractName     | query | string  | true     | Contract (plan) name for contract-level statistics.                         |
| min              | query | boolean | false    | If true, include minActualLeadTime in response.                             |
| max              | query | boolean | false    | If true, include maxActualLeadTime in response.                             |
| average          | query | boolean | false    | If true, include averageActualLeadTime in response.                         |
| timePeriodStart  | query | string  | false    | Start of the time window (inclusive), format: date-time. Requires timePeriodEnd and must not be used with approximateCount. |
| timePeriodEnd    | query | string  | false    | End of the time window (exclusive), format: date-time. Requires timePeriodStart and must not be used with approximateCount. |
| approximateCount | query | integer | false    | Approximate number of most recent hourly samples to return. Must not be used with timePeriodStart/End. |
| fields           | query | string  | false    | Comma-separated properties to be provided in response.                      |
| offset           | query | integer | false    | Index of the first statistics sample to return (default: 0).                |
| limit            | query | integer | false    | Number of statistics samples to return (default: 100).                      |
| sort             | query | string  | false    | Sort by sampleWindow; prefix with “-” for descending (e.g., -sampleWindow). |

Responses:

| Code | Description           | Content-Type     | Schema                               | Headers                                        |
|:-----|:----------------------|:-----------------|:--------------------------------------|:-----------------------------------------------|
| 200  | OK                    | application/json | ContractLeadTimeHistoryStatistics     | X-Result-Count, X-Total-Count                  |
| 400  | Bad Request           | application/json | Error                                 |                                                |
| 401  | Unauthorized          | application/json | Error                                 |                                                |
| 404  | Not Found             | application/json | Error                                 |                                                |
| 500  | Internal Server Error | application/json | Error                                 |                                                |

Example Request (fixed time window):
```bash
curl -G "{{baseUrl}}/leadTimeHistoryStatistics/contract" \
  --data-urlencode "contractName=Home-Fiber-Install" \
  --data-urlencode "fields=contractName,statistics" \
  --data-urlencode "min=true" \
  --data-urlencode "max=true" \
  --data-urlencode "average=true" \
  --data-urlencode "timePeriodStart=2025-01-01T00:00:00Z" \
  --data-urlencode "timePeriodEnd=2025-01-08T00:00:00Z" \
  --data-urlencode "sort=sampleWindow" \
  --data-urlencode "limit=2" \
  -H "Accept: application/json"
```

Example Response:
```json
{
  "contractName": "Home-Fiber-Install",
  "statistics": [
    {
      "sampleWindow": "2025-01-01T00:00:00Z",
      "minActualLeadTime": 36,
      "maxActualLeadTime": 72,
      "averageActualLeadTime": 54.1,
      "sampleSize": 58
    },
    {
      "sampleWindow": "2025-01-02T00:00:00Z",
      "minActualLeadTime": 32,
      "maxActualLeadTime": 68,
      "averageActualLeadTime": 49.7,
      "sampleSize": 64
    }
  ]
}
```

Response headers:

- X-Result-Count: 2
- X-Total-Count: 169

## Schemas

### Error

Used when the API returns an error (HTTP 4xx/5xx).

Properties:

| Name            | Type   | Description                                                                                   |
|:----------------|:-------|:----------------------------------------------------------------------------------------------|
| code            | string | Application relevant detail, defined in the API or a common list.                             |
| reason          | string | Explanation of the reason for the error which can be shown to a client user.                  |
| message         | string | More details and corrective actions related to the error which can be shown to a client user. |
| status          | string | HTTP Error code extension.                                                                    |
| referenceError  | string | URI of documentation describing the error.                                                    |
| @baseType       | string | When sub-classing, this defines the super-class.                                              |
| @schemaLocation | string | A URI to a JSON-Schema file that defines additional attributes and relationships.             |
| @type           | string | When sub-classing, this defines the sub-class entity name.                                    |

---

### NodeLeadTimeHistoryStatistics

Node-level lead time history sampled statistics.

Properties:

| Name                | Type    | Description                                                            |
|:--------------------|:--------|:-----------------------------------------------------------------------|
| productSpecId       | string  | Product Specification identifier (node level).                         |
| deliveryFactoryName | string  | Delivery factory/category (Mobile, Fixed, Fiber, Shipment).            |
| statistics          | array   | Hourly sampled node-level statistics. See LeadTimeHistorySample.       |

---

### ContractLeadTimeHistoryStatistics

Contract-level lead time history sampled statistics.

Properties:

| Name            | Type   | Description                                                      |
|:----------------|:-------|:-----------------------------------------------------------------|
| contractName    | string | Contract (plan) name.                                            |
| statistics      | array  | Hourly sampled contract-level statistics. See LeadTimeHistorySample. |

---

### LeadTimeHistorySample

One hourly sample of lead time statistics.

Properties:

| Name                   | Type    | Description                                                   |
|:-----------------------|:--------|:--------------------------------------------------------------|
| sampleWindow           | string  | Hour at which this data was aggregated (format: date-time).   |
| minActualLeadTime      | integer | Minimum actual lead time in the sample window (hours).        |
| maxActualLeadTime      | integer | Maximum actual lead time in the sample window (hours).        |
| averageActualLeadTime  | number  | Average actual lead time in the sample window (hours).        |
| sampleSize             | integer | Number of items included in this sample window.               |

---

### Response Headers

| Header          | Type    | Description                                                              |
|:----------------|:--------|:-------------------------------------------------------------------------|
| X-Result-Count  | integer | Number of statistics samples returned in this page.                      |
| X-Total-Count   | integer | Total number of statistics samples matching the filter (before pagination). |

Notes:

- Use sort=-sampleWindow with approximateCount to obtain the latest samples first.
- When timePeriodStart/timePeriodEnd are provided, both must be present and approximateCount must be omitted.

