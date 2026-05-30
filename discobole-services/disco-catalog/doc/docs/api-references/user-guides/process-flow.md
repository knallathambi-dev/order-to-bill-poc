---
title: Process Flow Readme
summary:
author:
  - Sumit
---

# Getting Started

## Introduction

Welcome to the **TMF701** API! This guide will help you get started with using our API to query and show the installed.

## API Description

Process Flow API is the host API used to create entities with 5 different resources.

### Summary of resources

APIs have different resource based on the entities.

| Resource                 | Definitions                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
|:-------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Product Specification    | It is a detailed description of a tangible or intangible object made available externally in the form of a ProductOffering to customers or other parties playing a party role.                                                                                                                                                                                                                                                                                                          |
| ProductOffering          | Represents entities that are orderable from the provider of the catalog. This resource includes pricing information.                                                                                                                                                                                                                                                                                                                                                                    |
| ProductOfferingPrice     | Is based on both the basic cost to develop and produce products and the enterprise's policy on revenue targets. This price may be further revised through discounting (productOfferPriceAlteration). The price applied for a ProductOffering may also be influenced by the ProductOfferingTerm. For example, a ProductOffering can be offered with multiple terms, like commitment periods for the contract. A ProductOffering may be cheaper with a 24-month commitment than with a 12-month commitment. |
| Category                 | The category resource is used to group ProductOfferings, service, and resource candidates in logical containers. Categories can contain other categories and/or ProductOfferings, resource, or service candidates.                                                                                                                                                                                                                             |
| Lifecycle                | The lifecycle resource is used to change the lifecycle of the entities.                                                                                                                                                                                                                                                                                                                                                                                                                |

#### Summary of methods and URL

| Attributes          | Description                                                                                                                                               |
|: --------------------|: ----------------------------------------------------------------------------------------------------------------------------------------------------------|
| _links             | This object contains all the navigation and actions that you can perform with required parameters.                                                        |
| self               | This attribute contains the endpoint of the process you created while starting a process in the form `http://{{baseURL}}/processManagement/v1/process/{processId}`. |
| nextTaskList       | This attribute contains the endpoint which gives you all the tasks that a current process has.                                                            |
| nextTask           | This attribute contains the endpoint of the next user task at which our current execution is waiting, with all the required parameters or body needed to execute that task. |

### NextTask's Attributes

| Attribute       | Description                                                                                                             |
|: ----------------|: ------------------------------------------------------------------------------------------------------------------------|
| title           | This field identifies a task in Camunda BPMN or GUI. This field has to be unique.                                       |
| href            | This field contains the URL or endpoint for executing this task.                                                       |
| priority        | This attribute specifies the initial priority of an External Task when it is created. This helps decide which task is executed first if multiple tasks are waiting for execution. |
| characteristic  | This object contains the fields and values required to execute a task.                                                 |
| method          | This attribute specifies the HTTP method to use.                                                                       |
| accepts         | This attribute specifies the content-type.                                                                             |

### Authentication

In case the security is enabled, bearer token is used for authentication and authorization. Our API is a resource server based on OAuth2.

For more details, please consult [User Role & Permission management component](https://discobole.ow2.org/disco-oda-components/disco-security/doc/api-references/overall-apis/.).

#### Example to create Product Specification

 Entity_Id: `ProductSpecCreation`,`ProductOfferingPriceCreation`, `ProductOfferingCreation` , `category` , `lifecycle`

##### Step 1: Hit Post Request [http://{{baseURL}}/{{Entity}}/processManagement/{{version}}/processFlow]

**Request**

```bash
curl -X POST "http://{{baseURL}}/ps/processManagement/v1/processFlow" \
-H "accept: application/json;charset=utf-8" \
--data-raw '{
  "processFlowSpecification": "{{Entity_Id}}"
}'
```

**Response**

```json
{
    "id": "bd77ebcd-1786-400c-b493-90cac1d5a51b",
    "href": "http://{{baseURL}}/ps/processManagement/v1/processFlow/bd77ebcd-1786-400c-b493-90cac1d5a51b",
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
            "href": "http:/{{baseURL}}/ps/processManagement/v1/processFlow/bd77ebcd-1786-400c-b493-90cac1d5a51b"
        },
        "taskFlowList": {
            "href": "http:/{{baseURL}}/ps/processManagement/v1/processFlow/bd77ebcd-1786-400c-b493-90cac1d5a51b/taskFlow"
        },
        "nextTaskstoBePerformed": [
            {
                "title": "ProductSpecCreation.selectSupportEntity",
                **"href": "https://{{baseUrl}}/ps/processManagement/v1/processFlow/bd77ebcd-1786-400c-b493-90cac1d5a51b/taskFlow/60c0ea81-4d48-4a3c-9ae8-36c73c7fdec5",**
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

##### Step 2: hit the patch request using 'href' present in 'nextTaskstoBePerformed' in the above response

**Request**  

```bash
curl -X PATCH "https://{{baseUrl}}/ps/processManagement/v1/processFlow/bd77ebcd-1786-400c-b493-90cac1d5a51b/taskFlow/60c0ea81-4d48-4a3c-9ae8-36c73c7fdec5" -H "accept: application/json;charset=utf-8" \
--data-raw '{
  "characteristic": [
    {
        "name": "SelectSupportEntity",
        "valueType": "Object",
        "value":{
            "supportEntitySpecification.id":"{{SIM_STK}}",
            "supportEntityType":"StockItemType"
        },
        "@baseType": null,
        "@schemaLocation": null,
        "@type": "ObjectCharacteristic"
    }
   ]
}'
```

Similary follow step 2 till the last task.

The apis must follow the TMF630 rest guidelines with its disco extension.

### History of Document

| Version of the document | modification date | description of modifications  |
|:------------------------|:------------------|:------------------------------|
| 1.0                     | 04/10/2023        | initialization                |
| 1.1                     | 04/12/2023        | improving patch documentation |
| 2.0                     | 09/06/2024        | adding task api               |
