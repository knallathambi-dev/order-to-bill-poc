<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# API Getting Started Guide

Welcome to the **TMF701** API! This guide will help you get started with using our API to query and show the installed.

## Prerequisites

Before you begin using the Catalog TMF701 API, make sure you have the following prerequisites in place:

- **API Base URL**: The API is hosted for integration environment at `https:/{{baseUrl}}`.

- **Security**: Bearer token is used for authentication and authorization. DISCOBOLE uses oauth2 .

## API Description

### Summary of Resources

APIs have different resource based on the entities.

| Resource              | Definitions                                                  |
| :-------------------- | :----------------------------------------------------------- |
| Product Specification | s a detailed description of a tangible or intangible object made available externally in the form of a ProductOffering to customers or other parties playing a party role. |
| ProductOffering       | Represents entities that are orderable from the provider of the catalog, this resource includes pricing information |
| ProductOfferingPrice  | Is based on both the basic cost to develop and produce products and the enterprises policy on revenue targets. This price may be further revised through discounting (productOfferPriceAlteration). The price, applied for a productOffering may also be influenced by the productOfferingTerm, the customer selected, eg: a productOffering can be offered with multiple terms, like commitment periods for the contract. The price may be influenced by this productOfferingTerm. A productOffering may be cheaper with a 24 month commitment than with a 12 month commitment. |
| Category              | The category resource is used to group product offerings, service and resource candidates in logical containers. Categories can contain other categories and/or product offerings, resource or service candidates. |
| Lifecycle             | The lifecycle resource is used to change the lifecycle of the entites. |

### Summary of  Attributes

| Attributes | Description |
|:----------- |:---------- |
| _links	 | This object contains all the navigation and actions that you can perform with required parameters |
|self	    |This attribute contains endpoint of the process which you created while starting a process in the form  http://route/processManagement/v1/process/{processId} |
|nextTaskList|	This attribute contains the endpoint which gives you all the task that a current process have. |
|nextTask	|This attribute contains the endpoint of next user task at which our current execution is waiting with all the required parameters or body needed to execute that task. |

|NextTask's Attribute|	Description |
|:------------------ |:------------ |
|title	|This field identifies a task in camunda BPMN or GUI. This field has to be unique. |
|href	|This field contains URL or endpoint for executing this task. |
|priority|	The attribute specifies the initial priority of an External Task when it is created.  |This field comes to play when there is multiple tasks waiting for execution, and based on priority it will be decided to execute a particular task first. |
|characteristic	|This object contains the field and values which are required to execute a task. |
|method	|This attribute tells which Http method we have to use |
|accepts|	This attribute tells about the content-type. |

### Summary of Methods and URLs

**Entity**  can be : `ps` (productSpecification) , `po` (productOffering) , `pop` (productOfferingPrice) , `category` ,`lifecycle`

| Use case using the method  | Description | Method | URL |
|:---------------------------|:------------|:-------|:----|
| Create an Entity           | This operation creates an entity. | POST    | `/processManagement/{{{version}}/processFLow` |
| Update an Entity    | This operation updates an entity | PATH    | `/processManagement/{{{version}}/processFLow/{{id}}/taskflow/{{task_id}}` |

## Create an Entity

Entity ID can be: `ProductSpecCreation`,`ProductOfferingPriceCreation`, `ProductOfferingCreation` , `category` , `lifecycle`

### Request to create a ProductSpecification

```shell
curl -X POST "http://{{baseURL}}/ps/processManagement/v1/processFLow" \
-H "accept: application/json;charset=utf-8" \
--data-raw '{
    "processFlowSpecification": "ProductSpecCreation"
}'
```

### Response

```json
201 CREATED
Content-Type
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
            "href": "http://{{baseURL}}/ps/processManagement/v1/processFlow/bd77ebcd-1786-400c-b493-90cac1d5a51b"
        },
        "taskFlowList": {
            "href": "http://{{baseURL}}/ps/processManagement/v1/processFlow/bd77ebcd-1786-400c-b493-90cac1d5a51b/taskFlow"
        },
        "nextTaskstoBePerformed": [
            {
                "title": "ProductSpecCreation.selectSupportEntity",
                **"href": "https:/{{baseUrl}}/ps/processManagement/v1/processFlow/bd77ebcd-1786-400c-b493-90cac1d5a51b/taskFlow/60c0ea81-4d48-4a3c-9ae8-36c73c7fdec5",**
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
                "href": "https:/{{baseUrl}}/ps/processManagement/v1/processFlow/bd77ebcd-1786-400c-b493-90cac1d5a51b/taskFlow/5994b06e-928e-46c4-afc8-b7e95157425a",
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

## Update an Entity 

### Request to update the ProductSpecification

Hit the patch request using 'href' present in 'nextTaskstoBePerformed' in the above response.

```shell
curl -X PATCH "http://{{baseURL}}/ps/processManagement/v1/processFLow}/bd77ebcd-1786-400c-b493-90cac1d5a51b/taskFlow/60c0ea81-4d48-4a3c-9ae8-36c73c7fdec5" \
-H "accept: application/json;charset=utf-8" \
--data-raw '{
  "characteristic": [
    {
        "name": "SelectSupportEntity",
        "valueType": "Object",
        "value":{
            "supportEntitySpecification.id":"SIM",
            "supportEntityType":"StockItemType"
        },
        "@baseType": null,
        "@schemaLocation": null,
        "@type": "ObjectCharacteristic"
    }
  ]
}'
```

### Response

```json
200 OK
Content-Type
{ ... }
```

Similary follow step 2 till the last task

For more details, check the [Product Inventory documentation](http://gitlab.ow2.org/discobole/disco-oda-components/disco-product-inventory/doc).

### History of Document

| Version of the document | modification date | description of modifications  |
|:------------------------|:------------------|:------------------------------|
| 1.0                     | 04/10/2023        | initialization                |
| 1.1                     | 04/12/2023        | improving patch documentation |
| 2.0                     | 09/06/2024        | adding task api               |



