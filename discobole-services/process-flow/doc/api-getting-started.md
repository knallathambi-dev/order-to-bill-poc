<!--
Software Name: process-flow
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n
-->

# Getting Started

## Introduction

Welcome to the **TMF701** API! This guide will help you get started with using our API to query and show the installed.

## Advanced Description

This API handles both **process** **management** and **task** **management**. The main purpose of this library is to dynamically derive GUI via API with the help of [HATEOAS](https://spring.io/understanding/HATEOAS) (which includes hypermedia links with the responses).

So, whenever we start a process in micro-service which is using this library, it will return a response as mentioned below. It contains all the necessary information about the process which we created and also contains the [HATEOAS](https://spring.io/understanding/HATEOAS) part which is depicted using " **_links**" attribute in the below response. This attribute gives details about all the navigation scope and next action that you can perform after executing a process or task. Description of "**_links**" attribute is defined inside the table below.

Typically, this library is used to implement: 

- the catalog management (creation/modification/deletion of catalog entities) through its catalog GUI, and 
- the order capture journey (offer acquisition, modification and termination) called from a selfcare GUI. 

## API Description

Process Flow API is the host API used to create entities with 5 different resources listed below.

### Summary of resources

This API creates different entities based on these resources.

| Resource                | Definitions                                                  |
| ----------------------- | ------------------------------------------------------------ |
| `Product Specification` | It is a detailed description of a tangible or intangible object made available externally in the form of a `ProductOffering` to customers or other parties playing a party role. |
| `ProductOffering`       | Represents entities that are orderable from the provider of the catalog. This resource includes pricing information. |
| `ProductOfferingPrice`  | Is based on both the basic cost to develop and produce products and the enterprise's policy on revenue targets. This price may be further revised through discounting (`ProductOfferPriceAlteration`). The price applied for a `ProductOffering` may also be influenced by the `ProductOfferingTerm`. For example, a `ProductOffering` can be offered with multiple terms, like commitment periods for the contract. A ProductOffering may be cheaper with a 24-month commitment than with a 12-month commitment. |
| `Category`              | The `Category` resource is used to group `ProductOffering` entities, service, and resource candidates in logical containers. Categories can contain other categories and/or product offerings, resource, or service candidates. |
| `Lifecycle`             | The `Lifecycle` resource is used to change the lifecycle of the entities. |

#### Summary of methods and URL

|Use case using the method|Method|URL|
|--- |--- |--- |
|List or find ProcessFlow objects|GET|/processManagement/{{version}}/processFlow|
|Create different kind of entities (`ProductSpecCreation`, `ProductOfferingPriceCreation`, `ProductOfferingCreation` , `Category` , `Lifecycle`) |POST|{{entity}}/processManagement/{{version}}/processFlow|
|Retrieve a ProcessFlow by ID |GET|/processManagement/{{version}}/processFlow/{{id}}|
|Retrieve the TaskFlow of a given ProcessFlow|GET|/processManagement/{{version}}/processFlow/{{id}}/taskFlow|
|Retrieve a TaskFlow by ID of a given ProcessFlow||/processManagement/{{version}}/processFlow/{{id}}/taskFlow/{{taskFlowId}}|


### Authentication

In case the security is enabled, bearer token is used for authentication and authorization. Our API is a resource server based on OAuth2.

For more details, please consult [User Role & Permission management component](https://discobole.ow2.io/disco-oda-components/disco-security/doc/api-references/specifications/user-role-and-permissions/).

 ### Create a Resource

Entities are created at `http://{{baseURL}}/{entity}/processManagement/{{version}}/processFlow`.

#### Parameters 

| Name                       | Description              | Type   | Mandatory | Value                                                        | In   |
| -------------------------- | ------------------------ | ------ | --------- | ------------------------------------------------------------ | ---- |
| `entity`                   | Type of entity to create | string | Yes       | `ps`, `po`, `pop`, `category`, `lc`                          | path |
| `processFlowSpecification` | Type of entity to create | string | Yes       | `ProductSpecCreation`, `ProductOfferingPriceCreation`, `ProductOfferingCreation`, `Category` , `Lifecycle` | body |

#### Step 1: start a process flow to create an entity

Let's see how to create a `ProductSpecification` entity.

##### Request

```bash
curl -X POST http://{{baseURL}}/ps/processManagement/{{version}}/processFlow \
-H 'Content-Type: application/json-patch+json' \
--data-raw '{
  "processFlowSpecification": "ProductSpecCreation"
}'
```

 ##### Response

```json
201 CREATED
Content-Type: application/json

{
  "id": "bd77ebcd-1786-400c-b493-90cac1d5a51b",
  "href": "http://{{baseUrl}}/ps/processManagement/v1/processFlow/bd77ebcd-1786-400c-b493-90cac1d5a51b",
  "correlationId": null,
  "processFlowDate": "2024-07-18T07:44:57.124864781Z",
  "processFlowSpecification": "ProductSpecCreation",
  "description": null,
  "channel": [],
  "characteristic": [],
  "relatedEntity": [],
  "relatedParty": [],
  "state": "active",
  "taskFlow": null,
  "@baseType": null,
  "@schemaLocation": null,
  "@type": "ProcessFlow",
  "_links": {
    "self": {
      "href": "http://{{baseUrl}}/ps/processManagement/v1/processFlow/bd77ebcd-1786-400c-b493-90cac1d5a51b"
    },
    "taskFlowList": {
      "href": "http://{{baseUrl}}/ps/processManagement/v1/processFlow/bd77ebcd-1786-400c-b493-90cac1d5a51b/taskFlow"
    },
    "nextTaskstoBePerformed": [
      {
        "title": "ProductSpecCreation.selectSupportEntity",
        "href": "https://{{baseUrl}}/ps/processManagement/v1/processFlow/bd77ebcd-1786-400c-b493-90cac1d5a51b/taskFlow/60c0ea81-4d48-4a3c-9ae8-36c73c7fdec5",
        "state": "active",
        "taskFlowSpecificationId": "60c0ea81-4d48-4a3c-9ae8-36c73c7fdec5",
        "taskFlowSpecificationCharacteristic": [
          {
            "name": "SelectSupportEntity",
            "id": "60c0ea81-4d48-4a3c-9ae8-36c73c7fdec5-SelectSupportEntity-0",
            "valueType": "Object",
            "minCardinality": 1,
            "maxCardinality": 1,
            "characteristicSpecificationRelationship": null,
            "characteristicValueSpecification": [
              {
                "@type": "ObjectCharacteristicValueSpecification",
                "isDefault": null,
                "value": {
                  "SelectSupportEntity": {
                    "type": "object",
                    "required": [
                      "supportEntitySpecification.id",
                      "supportEntityType"
                    ],
                    "properties": {
                      "supportEntitySpecification.id": {
                        "readOnly": true,
                        "type": "string"
                      },
                      "supportEntityType": {
                        "type": "string",
                        "enum": [
                          "CFSSpec",
                          "StockItemType",
                          "SupplierProduct"
                        ]
                      }
                    }
                  }
                }
              }
            ]
          }
        ],
        "method": "PATCH",
        "accepts": "merge-json-patch"
      },
      {
        "title": "ProductSpecCreation.cancel",
        "href": "https://{{baseUrl}}/ps/processManagement/v1/processFlow/bd77ebcd-1786-400c-b493-90cac1d5a51b/taskFlow/5994b06e-928e-46c4-afc8-b7e95157425a",
        "state": "active",
        "taskFlowSpecificationId": "5994b06e-928e-46c4-afc8-b7e95157425a",
        "taskFlowSpecificationCharacteristic": [
          {
            "name": "CancelEntityOperation",
            "id": "5994b06e-928e-46c4-afc8-b7e95157425a-CancelEntityOperation-0",
            "valueType": "Object",
            "minCardinality": 1,
            "maxCardinality": 1,
            "characteristicSpecificationRelationship": null,
            "characteristicValueSpecification": [
              {
                "@type": "ObjectCharacteristicValueSpecification",
                "isDefault": null,
                "value": {
                  "CancelEntityOperation": {
                    "required": [
                      "isCancelled"
                    ],
                    "type": "object",
                    "properties": {
                      "isCancelled": {
                        "type": "boolean",
                        "default": true
                      }
                    }
                  }
                }
              }
            ]
          }
        ],
        "method": "PATCH",
        "accepts": "merge-json-patch"
      }
    ],
    "existingTaskEditable": null
  }
}
```
**Fields Description**

Check [ProcessFlow](#processflow) in [Model](#model).

#### Step 2: perform the next tasks to end the creation

Hit the patch request indicated by the `nextTaskstoBePerformed[0].href` attribute in the above response. 

##### Request

```bash
curl -X PATCH 'https://{{baseUrl}}/ps/processManagement/v1/processFlow/bd77ebcd-1786-400c-b493-90cac1d5a51b/taskFlow/60c0ea81-4d48-4a3c-9ae8-36c73c7fdec5 \ 
-H 'Content-Type: application/json-patch+json'
```

##### Response  

  ```json
200 OK
Content-Type: application/json

{
  "characteristic": [
    {
      "name": "SelectSupportEntity",
      "valueType": "Object",
      "value": {
        "supportEntitySpecification.id": "{{SIM_STK}}",
        "supportEntityType": "StockItemType"
      },
      "@baseType": null,
      "@schemaLocation": null,
      "@type": "ObjectCharacteristic"
    }
  ]
}
  ```

Similarly follow step 2 till the last task.

### Model 

#### ProcessFlow

| Attributes     | Type   | Description                                                  |
| -------------- | ------ | ------------------------------------------------------------ |
| `_links`       | object | This object contains all the navigation and actions that you can perform with required parameters. |
| `self`         | string | This attribute contains the endpoint of the process you created while starting a process in the form `http://route/processManagement/v1/process/{processId}`. |
| `nextTaskList` | array  | This attribute contains the endpoint which gives you the `TaskFlow` (or all next tasks) that a current process has. |
| `nextTask`     | object | This attribute contains the endpoint of the next `NextTask` at which our current execution is waiting, with all the required parameters or body needed to execute that task. |

#### TaskFlow

| Attribute        | Type                                                         | Description                                                  |
| ---------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `title`          | string                                                       | This field identifies a task in Camunda BPMN or GUI. This field has to be unique. |
| `href`           | string                                                       | This field contains the URL or endpoint for executing this task. |
| `priority`       | integer                                                      | This attribute specifies the initial priority of an External Task when it is created. This helps decide which task is executed first if multiple tasks are waiting for execution. |
| `characteristic` | one of BooleanCharacterisic, IntegerCharacteristic, ObjectCharacteristic, StringCharacteristic | This object contains the fields and values required to execute a task. |
| `method`         | string                                                       | This attribute specifies the HTTP method to use.             |
| `accepts`        | string                                                       | This attribute specifies the content-type.                   |

### History of Document

| Version of the document | modification date | description of modifications  |
|:------------------------|:------------------|:------------------------------|
| 1.0                     | 04/10/2023        | initialization                |
| 1.1                     | 04/12/2023        | improving patch documentation |
| 2.0                     | 09/06/2024        | adding task api               |

