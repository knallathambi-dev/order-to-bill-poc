<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Getting Started

## Introduction
Welcome to the **DISCOBOLE Customer Order Orchestration & Distribution (COOD) API!** This guide will help you get started with using our API to query and manage the delivery of Product Orders. The API allows you to fetch an Orchestration Plan, including its orchestration plan nodes, by a specific ID or query them using all available model fields.

## API Description

#### Summary of Resources

Both APIs are centered around a primary resource, Orchestration Plan, with secondary resources acting as components of this main resource. All details related to an Orchestration Plan are retrieved using its unique Orchestration Plan ID. Similarly, information about OrchestrationPlans and their classifications is accessed through their respective object IDs.
An OrchestrationPlan transitions through various states during its lifecycle. Initially, it is set to Initialized upon creation. When it's ready to begin execution, the state changes to Acknowledged. As the execution or delivery of nodes starts, the plan moves to the In Progress state. Once all nodes are in the Held state, the plan transitions to Held. If any nodes are Rejected, Failed, or Completed, the plan advances to the Executed state.

| Resource                      | Definitions                                                                                                                          |
|:------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------|
| OrchestrationPlan             | this represent the full representation of the orchestration plan with its nodes that is related to each other with relationship type |
| OrchestrationPlanNode         | Items that requiring delivery during the execution of the orchestration plan                                                         |
| RelatedOrchestrationPlanNode  | Represents delivery prerequisites for this node with relationship “DeliverAfter”  or “DeliverAfter”                                  |               |
| RelatedParty                  | The related party as received in the Product Order                                                                                   |
| RelatedProductOrderItem       | Represents the Product order item that triggered the delivery of the orchestration node                                              |
| RelatedServiceOrder           | Used to link the Orchestration Plan to the Product Order it is trying to fulfil                                                      |
| RelatedSupplyChainOrderItem   | Represents the item sent to supply chain for tangible products delivery.                                                             |
| ServiceSpecification          | Represents the service specification retrieved from product catalog for non-tangible products                                        |
| RelatedProduct                | Represents the product that was delivered/modified during the delivery of this orchestration node.                                   |
| ProductSpecification          | associated product specification for each order item                                                                                 |
| OrchestrationNodeErrorMessage | in case of failure or held of any node, the reason of the action is saved in the field                                               |

#### Summary of methods and URL

| Use case using the method                                                      | Method | URL                                                                                                          |
|:-------------------------------------------------------------------------------|:-------|:-------------------------------------------------------------------------------------------------------------|
| I would like to **get all orchesteration plans**                               | GET    | {{baseUrl}}/orchestrationManagement/{{version}}/orchestrationPlan/{{id}}]                            |
| I would like to **get orchestration plans filtered by state**                  | GET    | {{baseUrl}}/orchestrationManagement/{{version}}/orchestrationPlan?state=held]                        |
| I would like to **get orchestration plans filtered by relatedparty.id**        | GET    | {{baseUrl}}/orchestrationManagement/{{version}}/orchestrationPlan?relatedParty.id=231-mf4]           |
| I would like to **get orchestration plans filtered by node state**             | GET    | {{baseUrl}}/orchestrationManagement/{{version}}/orchestrationPlan?orchestrationPlanNodes.state=held] |
| I would like to **get orchestration plans relatedproduct order id field only** | GET    | {{baseUrl}}/orchestrationManagement/{{version}}/orchestrationPlan?fields=relatedProductOrder.id]     |
| I would like to **get orchestration plans filtered by node state**             | GET    | {{baseUrl}}/orchestrationManagement/{{version}}/orchestrationPlan?orchestrationPlanNodes.state=held] |
| I would like to **get orchestration plans with pagination**                    | GET    | {{baseUrl}}/orchestrationManagement/{{version}}/orchestrationPlan?limit=5&offset=0]                  |
| I would like to **get orchestration plans with sort with receivedDate desc**   | GET    | {{baseUrl}}/orchestrationManagement/{{version}}/orchestrationPlan?sort=-receivedDate]                |
| I would like to **get orchestration plans with sort with receivedDate asc**    | GET    | {{baseUrl}}/orchestrationManagement/{{version}}/orchestrationPlan?sort=receivedDate]                 |

### Authentication

In case the security is enabled, bearer token is used for authentication and authorization. Our API is a resource server based on OAuth2.

For more details, please consult [User Role & Permission management component](https://discobole.ow2.io/disco-oda-components/disco-security/doc/api-references/specifications/user-role-and-permissions/).

### Get all entities of Orchestration Plans

- **Path** : GET /orchestrationManagement/v1/orchestrationPlan
- **Description** : Get OrchestrationPlans by filter(parameters) with pagination.
- **Summary** : Get Orchestration Plans
- **Description** : Retrieve orchestration plans based on specified parameters.
- **Parameters**:

| Parameter                                         | Description                                                         | Optional |
|---------------------------------------------------|---------------------------------------------------------------------|----------|
| id                                                | Orchestration plan's ID.                                            | true     |
| state                                             | Orchestration plan's state.                                         | true     |
| receivedDate                                      | Orchestration plan's received date.                                 | true     |
| relatedParty.id                                   | Related party's ID.                                                 | true     |
| relatedParty.role                                 | Related party's role.                                               | true     |
| relatedParty.href                                 | Related party's href.                                               | true     |
| relatedParty.name                                 | Related party's name.                                               | true     |
| relatedProductOrder.id                            | Related Product Order's ID.                                         | true     |
| orchestrationPlanNodes.state                      | Orchestration Plan Node's state.                                    | true     |
| fields                                            | Comma-separated properties to be provided in response.              | true     |
| orchestrationPlanNodes.relatedServiceOrder.id     | Related Service Order's ID.                                         | true     |
| orchestrationPlanNodes.relatedProductOrderItem.id | Related Product Order Item's ID.                                    | true     |
| orchestrationPlanNodes.relatedProduct.id          | Related Product's ID.                                               | true     |
| offset                                            | Requested index for the start of resources in the response.         | true     |
| limit                                             | Requested number of resources in the response.                      | true     |
| receivedDate.gte                                  | Fetch orchestration plans starting with this received date.         | true     |
| receivedDate.lte                                  | Fetch orchestration plans ending with this received date.           | true     |
| requestedDeliveryDate.gte                         | Fetch orchestration plans starting with this request delivery date. | true     |
| requestedDeliveryDate.lte                         | Fetch orchestration plans ending with this request delivery date.   | true     |
| sort                                              | Sort parameters (array of strings).                                 | true     |
| archived                                          | value indicating if this plan is archived pending deletion          | true     |

- **Responses** :
  - 200 OK 
  - Content-Type: application/json

    | Status Code               | Description                                              | Content                                      | Header                                                                                                                     |
    |---------------------------|----------------------------------------------------------|----------------------------------------------|----------------------------------------------------------------------------------------------------------------------------|
    | 200 OK                    | Successful response with list of the orchestration plan. | Array of orchestration plans                 | Content-Type: Application/JSON; charset=utf-8, X-Result-Count: Result count (integer), Total-Count: Total count (integer). |
    | 400 Bad Request           | When request parameter value is invalid format           | Bad request with an error message            | Content-Type: Application/JSON; charset=utf-8                                                                              |
    | 401 Unauthorized          | When token is absent                                     | Unauthorized request with an error message   | Content-Type: Application/JSON; charset=utf-8                                                                              |
    | 403 Forbidden             | If token doesn't have role                               | Unauthorized request with an error message   | Content-Type: Application/JSON; charset=utf-8                                                                              |
    | 204 Not Found             | when requested resource is not found                     | Resource not found with an error message.    | Content-Type: Application/JSON; charset=utf-8                                                                              |
    | 404 Not Found             | When the url didn't match any api in COOD                | Resource not found with an error message.    | Content-Type: Application/JSON; charset=utf-8                                                                              |
    | 500 Internal Server Error | when technical error while hit the api                   | Internal server error with an error message. | Content-Type: Application/JSON; charset=utf-8                                                                              |

#### Fields Description

Orchestration Plan Fields

| Field Name                                            | Type               | Description                                                                                                                            |
|-------------------------------------------------------|--------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| id                                                    | String             | Unique identifier of the entity                                                                                                        |
| href                                                  | String             | Hyperlink to access the object                                                                                                         |
| relatedProductOrder                                   | Object             | Reference to the related product order                                                                                                 |
| relatedParty                                          | Array              | List of related parties                                                                                                                |
| state                                                 | Enum               | Status of the orchestration plan   (Initialized, Acknowledged, Planned, InProgress, Executed, Held, Rejected, Aborted)                 |
| receivedDate                                          | String (date-time) | Date when the orchestration plan was received                                                                                          |
| requestedDeliveryDate                                 | String (date-time) | Requested delivery date for the orchestration plan                                                                                     |
| orchestrationPlanNodes                                | Array              | List of orchestration plan nodes                                                                                                       |
| relatedContractName                                   | String             | Name of the contract level offer delivered by this plan                                                                                |
| errorMessage                                          | Array              | List of error messages during orchestration plan execution                                                                             |
| archived                                              | Boolean            | Flag indicating if the plan is ready for deletion                                                                                      |
| @baseType                                             | String             | Defines the super-class when sub-classing                                                                                              |
| @schemaLocation                                       | String             | URI to a JSON-Schema file defining additional attributes and relationships                                                             |
| @type                                                 | String             | Defines the sub-class entity name when sub-classing                                                                                    |
| errorMessage[].href                                   | String             | Hyperlink to access the error message object                                                                                           |
| errorMessage[].code                                   | String             | Error code                                                                                                                             |
| errorMessage[].message                                | String             | Error message                                                                                                                          |
| errorMessage[].reason                                 | String             | Error reason                                                                                                                           |
| errorMessage[].timeStamp                              | String (date-time) | Time of error occurrence                                                                                                               |
| errorMessage[].@baseType                              | String             | Defines the super-class when sub-classing                                                                                              |
| errorMessage[].@schemaLocation                        | String             | URI to a JSON-Schema file defining additional attributes and relationships                                                             |
| errorMessage[].@type                                  | String             | Defines the sub-class entity name when sub-classing                                                                                    |
| orchestrationPlanNodes[].href                         | String             | Hyperlink to access the orchestration plan node                                                                                        |
| orchestrationPlanNodes[].id                           | String             | Unique identifier of the orchestration plan node                                                                                       |
| orchestrationPlanNodes[].state                        | Enum               | Status of the orchestration plan node  (Initialized, Acknowledged, InProgress, InDelivery, Held, Aborted, Rejected, Failed, Completed) |
| orchestrationPlanNodes[].relatedServiceOrder          | Object             | Reference to the related service order                                                                                                 |
| orchestrationPlanNodes[].relatedProductOrderItem      | Array              | List of related product order items                                                                                                    |
| orchestrationPlanNodes[].relatedSupplyChainOrderItem  | Object             | Reference to the related supply chain order item                                                                                       |
| orchestrationPlanNodes[].relatedProduct               | Array              | List of related products                                                                                                               |
| orchestrationPlanNodes[].relatedOrchestrationPlanNode | Array              | List of related orchestration plan nodes                                                                                               |
| orchestrationPlanNodes[].errorMessage                 | Array              | List of error messages for the node                                                                                                    |
| orchestrationPlanNodes[].@type                        | String             | Defines the sub-class entity name when sub-classing                                                                                    |
| orchestrationPlanNodes[].@baseType                    | String             | Defines the super-class when sub-classing                                                                                              |
| orchestrationPlanNodes[].@schemaLocation              | String             | URI to a JSON-Schema file defining additional attributes and relationships                                                             |

## Get Orchestration Plan by ID

- **Path**: GET /orchestrationManagement/v1/orchestrationPlan/{id}
- **Description** : Retrieve details of an orchestration plan based on its ID.
- **Parameters** :
    - id (required): Orchestration plan's ID. 
    - fields (optional): Comma-separated properties to be provided in response.
- **Responses** :
  - 200 OK 
  - Content-Type: application/json
    
    | Status Code               | Description                                                 | Content                                      | Header                                        |
    |---------------------------|-------------------------------------------------------------|----------------------------------------------|-----------------------------------------------|
    | 200 OK                    | Successful response with details of the orchestration plan. | Single object of orchestration plan          | Content-Type: Application/JSON; charset=utf-8 |
    | 400 Bad Request           | When request parameter value is invalid format              | Bad request with an error message            | Content-Type: Application/JSON; charset=utf-8 |
    | 401 Unauthorized          | When token is absent                                        | Unauthorized request with an error message   | Content-Type: Application/JSON; charset=utf-8 |
    | 403 Forbidden             | If token doesn't have role                                  | Unauthorized request with an error message   | Content-Type: Application/JSON; charset=utf-8 |
    | 204 Not Found             | when requested resource is not found                        | Resource not found with an error message.    | Content-Type: Application/JSON; charset=utf-8 |
    | 404 Not Found             | When the url didn't match any api in COOD                   | Resource not found with an error message.    | Content-Type: Application/JSON; charset=utf-8 |
    | 500 Internal Server Error | when technical error while hit the api                      | Internal server error with an error message. | Content-Type: Application/JSON; charset=utf-8 |

###### Example value full orchestration plan object without define the extracted fields

```json
{
  "id": "664f3d2a3d66de2f04b9ca87",
  "relatedProductOrder": {
    "id": "1109"
  },
  "relatedParty": [
    {
      "id": "227-mf30",
      "role": "customer",
      "name": "Lisa"
    }
  ],
  "state": "InProgress",
  "receivedDate": "2024-05-23T12:57:14.782Z",
  "orchestrationPlanNodes": [
    {
      "id": "4271da82-c678-4604-bf9e-95bcf7ae7815",
      "state": "Acknowledged",
      "relatedServiceOrder": {
        "orderItemId": "09",
        "SOMRef": "{{baseUrl}}}/tmf-api/serviceOrdering/v1/serviceOrder"
      },
      "relatedProductOrder": {
        "id": "1109"
      },
      "relatedProductOrderItem": [
        {
          "id": "09",
          "action": "add",
          "quantity": 1
        }
      ],
      "relatedProduct": [
        {
          "relationshipType": "reliesOn",
          "productSpecification": {
            "id": "c458d968-3718-4754-9408-7bd81526e7b2"
          },
          "isInstallable": true
        },
        {
          "relationshipType": "delivers",
          "productSpecification": {
            "id": "82f3bab6-63a1-4008-96a3-8d411d0c5b38",
            "name": "Time Bundle",
            "serviceSpecification": [
              {
                "href": "{{baseUrl}}}/serviceCatalogManagement/v1/serviceSpecification",
                "id": "Time bundle",
                "name": "Time bundle"
              }
            ]
          },
          "isInstallable": true,
          "productCharacteristic": [
            {
              "name": "volume",
              "valueType": "string",
              "value": "1h"
            }
          ],
          "@type": "CFS",
          "productOrderItemId": "09"
        }
      ],
      "relatedOrchestrationPlanNode": [
        {
          "relatedNodeId": "eaa06685-53e6-4b46-bcb5-f652f33a678a",
          "relationshipType": "DeliverAfter"
        }
      ]
    },
    {
      "id": "cb4767fa-04d5-4981-9032-d96bb4d179e2",
      "state": "InDelivery",
      "relatedProductOrder": {
        "id": "1109"
      },
      "relatedProductOrderItem": [
        {
          "id": "f60fbfac-ce01-43d2-80f5-263ae55f1188",
          "action": "add",
          "quantity": 1
        },
        {
          "id": "10",
          "action": "add",
          "quantity": 1
        }
      ],
      "relatedProduct": [
        {
          "relationshipType": "delivers",
          "isInstallable": false,
          "productCharacteristic": [
            {
              "name": "Shipping mode",
              "valueType": "string",
              "value": "In Shop"
            },
            {
              "name": "Shipping address",
              "valueType": "string",
              "value": "Lac Biwa"
            },
            {
              "name": "requested delivery date",
              "valueType": "Date",
              "value": "2024-02-14"
            }
          ],
          "@type": "shipmentProduct",
          "productOrderItemId": "f60fbfac-ce01-43d2-80f5-263ae55f1188"
        },
        {
          "id": "649aaf9f2b4b3225cb5737ed",
          "realisingService": [
            {
              "id": "1108_10"
            }
          ],
          "relationshipType": "delivers",
          "productSpecification": {
            "id": "df32402e-ceb9-4467-aafd-fec0bbff3124",
            "name": "SIM CARD"
          },
          "isInstallable": true,
          "productCharacteristic": [
            {
              "name": "IMSI",
              "valueType": "string",
              "value": "310478356879098"
            }
          ],
          "@type": "physicalProduct",
          "productOrderItemId": "10"
        }
      ]
    },
    {
      "id": "eaa06685-53e6-4b46-bcb5-f652f33a678a",
      "state": "Acknowledged",
      "relatedServiceOrder": {
        "orderItemId": "07",
        "SOMRef": "{{baseUrl}}}/tmf-api/serviceOrdering/v1/serviceOrder"
      },
      "relatedProductOrder": {
        "id": "1109"
      },
      "relatedProductOrderItem": [
        {
          "id": "07",
          "action": "add",
          "quantity": 1
        }
      ],
      "relatedProduct": [
        {
          "relationshipType": "reliesOn",
          "productSpecification": {
            "id": "df32402e-ceb9-4467-aafd-fec0bbff3124"
          },
          "isInstallable": true
        },
        {
          "relationshipType": "delivers",
          "productSpecification": {
            "id": "c458d968-3718-4754-9408-7bd81526e7b2",
            "name": "Mobile Line",
            "serviceSpecification": [
              {
                "href": "{{baseUrl}}}/tmf-api/serviceCatalogManagement/v1/serviceSpecification",
                "id": "MobileLine",
                "name": "MobileLine"
              }
            ]
          },
          "isInstallable": true,
          "productCharacteristic": [
            {
              "name": "MSISDN",
              "valueType": "string",
              "value": "4152797439"
            }
          ],
          "@type": "CFS",
          "productOrderItemId": "07"
        }
      ],
      "relatedOrchestrationPlanNode": [
        {
          "relatedNodeId": "cb4767fa-04d5-4981-9032-d96bb4d179e2",
          "relationshipType": "DeliverAfter"
        }
      ]
    },
    {
      "id": "6de8b76a-14e8-4f5f-ac58-2741720ca4aa",
      "state": "Acknowledged",
      "relatedServiceOrder": {
        "orderItemId": "08",
        "SOMRef": "{{baseUrl}}}/tmf-api/serviceOrdering/v1/serviceOrder"
      },
      "relatedProductOrder": {
        "id": "1109"
      },
      "relatedProductOrderItem": [
        {
          "id": "08",
          "action": "add",
          "quantity": 1
        }
      ],
      "relatedProduct": [
        {
          "relationshipType": "reliesOn",
          "productSpecification": {
            "id": "df32402e-ceb9-4467-aafd-fec0bbff3124"
          },
          "isInstallable": true
        },
        {
          "relationshipType": "delivers",
          "productSpecification": {
            "id": "5e18402d-c964-4d52-b362-22ef79c27c01",
            "name": "Connectivity",
            "serviceSpecification": [
              {
                "href": "{{baseUrl}}}/serviceCatalogManagement/v1/serviceSpecification",
                "id": "Connectivity",
                "name": "Connectivity"
              }
            ]
          },
          "isInstallable": true,
          "productCharacteristic": [
            {
              "name": "ICCID",
              "valueType": "string",
              "value": "891004234814455936"
            }
          ],
          "@type": "CFS",
          "productOrderItemId": "08"
        }
      ],
      "relatedOrchestrationPlanNode": [
        {
          "relatedNodeId": "cb4767fa-04d5-4981-9032-d96bb4d179e2",
          "relationshipType": "DeliverAfter"
        }
      ]
    }
  ],
  "href": "https://<domainurl>/orchestrationManagement/v1/orchestrationPlan/664f3d2a3d66de2f04b9ca87",
  "archived": false
}
```

###### Example value of orchestration plan with extraction of 2 fields  relatedParty.id and state

```json
{
  "id": "651543838bc9ad6c502a10eb",
  "relatedParty": [
    {
      "id": "231-mf4"
    }
  ],
  "state": "Held"
}
```

The apis must follow the TMF630 rest guidelines.

### History of Document

| Version of the document | modification date | description of modifications                 |
|:------------------------|:------------------|:---------------------------------------------|
| 1.0                     | 04/10/2023        | initialization                               |
| 1.1                     | 28/11/2023        | update params                                |
| 1.2                     | 26/2/2024         | update orchestration plan details            |
| 1.3                     | 26/5/2024         | fix format of tables                         |
| 1.4                     | 26/5/2024         | remove confluence links                      |
| 1.5                     | 2/2/2025          | adjust the document based on review comments |
| 1.6                     | 4/5/2025          | replace urls                                 |

