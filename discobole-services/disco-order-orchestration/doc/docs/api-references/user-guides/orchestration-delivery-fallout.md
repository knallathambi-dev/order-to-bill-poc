<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Orchestration Fallout Management API - Getting Started Guide

Welcome to the Fallout Management API! This guide will help you get started with using our API to manage fallout processes using the TMF 701 process flow library. The Fallout Management API allows you to create, close, retrieve, and filter fallout processes.

## Authentication

The API requires authentication via API keys or OAuth tokens. Please refer to the specific authentication documentation provided by your API service.

## API Endpoints

### Get Fallout Processes

Retrieve a list of fallout processes based on various filter criteria.

**Endpoint:**

```bash
GET /falloutManagement/v1/fallout
```

**Parameters:**

| Name                      | In    | Type    | Required | Description                                                                    |
|---------------------------|-------|---------|----------|--------------------------------------------------------------------------------|
| id                        | query | string  | false    | Fallout process ID                                                             |
| state                     | query | string  | false    | Orchestration plan's state                                                     |
| relatedParty.id           | query | string  | false    | Related party's ID                                                             |
| relatedParty.role         | query | string  | false    | Related party's role                                                           |
| relatedParty.name         | query | string  | false    | Related party's name                                                           |
| relatedProductOrder.id    | query | string  | false    | Related Product Order's ID                                                     |
| relatedEntity.state       | query | string  | false    | Related entity state                                                           |
| parentRelatedEntity.state | query | string  | false    | Parent related entity state                                                    |
| fields                    | query | string  | false    | Comma-separated properties to be provided in response                          |
| offset                    | query | integer | false    | Requested index for start of resources to be provided in response (default: 0) |
| limit                     | query | integer | false    | Requested number of resources to be provided in response (default: 100)        |
| sort                      | query | array   | false    | Sort parameters                                                                |
| relatedEntity.id          | query | string  | false    | Related entity ID                                                              |
| parentRelatedEntity.id    | query | string  | false    | Parent related entity ID                                                       |
| lastModifiedDate.gte      | query | string  | false    | Start date for last modification date range (format: date-time)                |
| lastModifiedDate.lte      | query | string  | false    | End date for last modification date range (format: date-time)                  |
| creationDate.gte          | query | string  | false    | Start date for creation date range (format: date-time)                         |
| creationDate.lte          | query | string  | false    | End date for creation date range (format: date-time)                           |

**Responses:**

| Code | Description           | Content-Type                   | Schema           |
|------|-----------------------|--------------------------------|------------------|
| 200  | OK                    | application/json;charset=utf-8 | Array of Fallout |
| 400  | Bad Request           | application/json;charset=utf-8 | Error            |
| 404  | Not Found             | application/json;charset=utf-8 | Error            |
| 405  | Method Not Allowed    | application/json;charset=utf-8 | Error            |
| 500  | Internal Server Error | application/json;charset=utf-8 | Error            |
| 503  | Service Unavailable   | application/json;charset=utf-8 | Error            |

**Example Request:**

```bash
curl -X GET "{{baseUrl}}/falloutManagement/v1/fallout?id=123&state=active&limit=10 -H "accept: application/json;charset=utf-8"
```

**Example response:**

```json

[
  {
    "id": "68b69c6e-3767-436d-9c1f-657bd0305d6e",
    "state": "Completed",
    "creationDate": "2024-10-13T07:03:36.039Z",
    "modificationDate": "2024-10-13T08:33:17.72Z",
    "relatedEntity": [
      {
        "id": "a1a01184-e070-42c0-af4a-03cead29d54d",
        "@referredType": "OrchestrationPlanNode",
        "role": "initiator"
      },
      {
        "id": "e3e0aeff-23ed-4f2f-9d3e-4548d5af0d7c",
        "@referredType": "OrchestrationPlan",
        "role": "relatedOrchestrationPlan",
        "href": "{{baseUrl}}/falloutManagement/v1/orchestrationPlan/e3e0aeff-23ed-4f2f-9d3e-4548d5af0d7c"
      },
      {
        "id": "1109112233",
        "@referredType": "ProductOrder",
        "role": "relatedProductOrder"
      }
    ],
    "relatedParty": {
      "id": "227-mf30",
      "role": "customer",
      "name": "Lisa"
    },
    "errorMessage": {
      "code": "SERVICE_SPEC_CHARACTERISTICS_NOT_FOUND",
      "reason": "Service specification characteristics are needed for the service order request creation",
      "message": "Service specification characteristics {[msisdn12]} were not found in the service catalog",
      "timestamp": "2024-10-13T07:03:36.026Z"
    },
    "href": "{{baseUrl}}/falloutManagement/v1/falloutIncident/68b69c6e-3767-436d-9c1f-657bd0305d6e",
    "resolution": {
      "status": "unresolved",
      "comment": "ss"
    },
    "@type": "FalloutIncident"
  }
]

```

## Get Fallout Process By ID

Retrieve the details of a specific fallout process by its ID.

### Endpoint:

```bash
curl -X GET "{{baseUrl}}/falloutManagement/v1/fallout/{id}" -H "accept: application/json;charset=utf-8"
```

**Parameters:**

| Name   | In    | Type   | Required | Description                                           |
|--------|-------|--------|----------|-------------------------------------------------------|
| id     | path  | string | true     | Fallout's ID to fetch its details                     |
| fields | query | string | false    | Comma-separated properties to be provided in response |

**Responses:**

| Code | Description           | Content-Type                          | Schema          |
|------|-----------------------|---------------------------------------|-----------------|
| 200  | OK                    | application/json;charset=utf-8        | Fallout         |
| 400  | Bad Request           | application/json;charset=utf-8        | Error           |
| 404  | Not Found             | application/json;charset=utf-8        | Error           |
| 405  | Method Not Allowed    | application/json;charset=utf-8        | Error           |
| 500  | Internal Server Error | application/json;charset=utf-8        | Error           |
| 503  | Service Unavailable   | application/json;charset=utf-8        | Error           |

**Example Request:**

```bash
curl -X GET "{{baseUrl}}/falloutManagement/v1/fallout/1qi39y48237478328 -H "accept: application/json;charset=utf-8"
```

**Example Response:**

```json
{
  "id": "68b69c6e-3767-436d-9c1f-657bd0305d6e",
  "state": "Completed",
  "creationDate": "2024-10-13T07:03:36.039Z",
  "modificationDate": "2024-10-13T08:33:17.72Z",
  "relatedEntity": [
    {
      "id": "a1a01184-e070-42c0-af4a-03cead29d54d",
      "@referredType": "OrchestrationPlanNode",
      "role": "initiator"
    },
    {
      "id": "e3e0aeff-23ed-4f2f-9d3e-4548d5af0d7c",
      "@referredType": "OrchestrationPlan",
      "role": "relatedOrchestrationPlan",
      "href": "{{baseUrl}}/falloutManagement/v1/orchestrationPlan/e3e0aeff-23ed-4f2f-9d3e-4548d5af0d7c"
    },
    {
      "id": "1109112233",
      "@referredType": "ProductOrder",
      "role": "relatedProductOrder"
    }
  ],
  "relatedParty": {
    "id": "227-mf30",
    "role": "customer",
    "name": "Lisa"
  },
  "errorMessage": {
    "code": "SERVICE_SPEC_CHARACTERISTICS_NOT_FOUND",
    "reason": "Service specification characteristics are needed for the service order request creation",
    "message": "Service specification characteristics {[msisdn12]} were not found in the service catalog",
    "timestamp": "2024-10-13T07:03:36.026Z"
  },
  "href": "{{baseUrl}}/falloutManagement/v1/falloutIncident/68b69c6e-3767-436d-9c1f-657bd0305d6e",
  "resolution": {
    "status": "unresolved",
    "comment": "ss"
  },
  "@type": "FalloutIncident"
}

```

## Schemas

### Error

Used when an API throws an Error, typically with an HTTP error response code (3xx, 4xx, 5xx).

**Properties:**

| Name            | Type   | Description                                                                                   |
|-----------------|--------|-----------------------------------------------------------------------------------------------|
| code            | string | Application relevant detail, defined in the API or a common list.                             |
| reason          | string | Explanation of the reason for the error which can be shown to a client user.                  |
| message         | string | More details and corrective actions related to the error which can be shown to a client user. |
| status          | string | HTTP Error code extension                                                                     |
| referenceError  | string | URI of documentation describing the error.                                                    |
| @baseType       | string | When sub-classing, this defines the super-class.                                              |
| @schemaLocation | string | A URI to a JSON-Schema file that defines additional attributes and relationships              |
| @type           | string | When sub-classing, this defines the sub-class entity name.                                    |

### Fallout

| Name             | Type   | Description                              |
|------------------|--------|------------------------------------------|
| id               | string |                                          |
| state            | string |                                          |
| creationDate     | string | (format: date-time)                      |
| modificationDate | string | (format: date-time)                      |
| relatedEntity    | array  | See [RelatedEntity](#relatedentity)      |
| relatedParty     | object | See [RelatedParty](#relatedparty)        |
| reason           | string | reason that added when close the fallout |
| resolution       | object | See [ResolutionState](#resolutionstate)  |
| errorMessage     | Object | See [ErrorMessage](#errormessage)        |

### RelatedEntity

| Name          | Type   | Description                  |
|---------------|--------|------------------------------|
| id            | string | related entity id            |
| state         | string | related entity state         |
| @referredtype | string | related entity referred type |
| @role         | string | related entity role          |
| @href         | string | related entity href          |

### RelatedParty

| Property | Type   | Description             |
|----------|--------|-------------------------|
| id       | string | Related party's ID      |
| role     | string | Related party's role    |
| name     | string | Related party's name    |

### Resolution

| Property | Type   | Description                                                 |
|----------|--------|-------------------------------------------------------------|
| status   | string | resolution state can be resolved or unresolved              |
| comment  | string | comment added by the admin who resolve the fallout incident |
| id       | string | unique id to resolution                                     |
| @href    | string | resolution entity href                                      |

### ErrorMessage

| Property  | Type   | Description     |
|-----------|--------|-----------------|
| code      | string | Error  code     |
| reason    | string | Error  reason   |
| message   | string | Error message   |
| timestamp | string | Error timestamp |
