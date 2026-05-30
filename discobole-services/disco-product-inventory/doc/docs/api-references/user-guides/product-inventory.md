<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# API Getting Started Guide

Welcome to the **Product Inventory Management** API! This guide will help you get started with using our API to query and show the installed.

#### Summary of Resources
Both APIs have one principal resource **Product** and the other resources used as parts of the main resource.

| Resource           | Definition                                                                                                                                                                                                                          |
|:-------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Product`          | A product offering procured by a customer or other interested party playing a party role. A product is realized as one or more service(s) and / or resource(s)                                                                      |
| `JobSpecification` | Represents a job specification entity, which encapsulates information about a specific job specification. job specifications are job specifications or processes that need to be executed, each with its own unique characteristics |
| `Job`              | Represents the execution details of a specific job specification                                                                                                                                                                    |
| `Report`           | A generic report entity that can represent various types of reports                                                                                                                                                                 |
| `Config`           | The system configuration details                                                                                                                                                                                                    |
| `Status`           | Status of the API and the status of its services or components                                                                                                                                                                      |
| `Version`          | Version of the API Specification provided by its OpenAPI Specification                                                                                                                                                              |

#### Summary of methods and URL of get products api

| Use case using the method                       | Description                                                                                                                                                                        | Method | URL                                                                      |
|:------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-------|:-------------------------------------------------------------------------|
| List or find Product objects                    | This operation list or find Product entities.<br />Attribute selection is enabled for all first level attributes.<br />Filtering is enabled until third level attributes           | GET    | /productInventoryManagement/v1/product                                   |
| Creates a Product                               | This operation creates a product entity.                                                                                                                                           | POST   | /productInventoryManagement/v1/product                                   |
| Updates partially Products                      | This operation allows a caller to send a list of patch operation to be applied to a list of products                                                                               | PATCH  | /productInventoryManagement/v1/product                                   |
| Retrieves a Product by ID                       | This operation retrieves a product entity.<br />Attribute selection is enabled for all first level attributes.<br />Filtering is enabled until third level attributes.             | GET    | /productInventoryManagement/v1/product/{id}                              |
| Updates partially a Product                     | This operation does a json merge patch of a product                                                                                                                                | PATCH  | /productInventoryManagement/v1/product/{id}                              |
| Delete a Product by ID                          | This operation retrieves a product entity.<br />Attribute selection is enabled for all first level attributes.<br />Filtering is enabled until third level attributes.             | DELETE | /productInventoryManagement/v1/product/{id}                              |
| Export list Product objects                     | export the list of product                                                                                                                                                         | GET    | /productInventoryManagement/v1/product/export                            |
| List or find JobSpecification objects           | This operation list or find JobSpecification entities.<br />Attribute selection is enabled for all first level attributes.<br />Filtering is enabled until third level attributes. | GET    | /productInventoryManagement/v1/jobSpecification                          |
| Create a job specification                      | create a job specification based on type, available types: export the list of product                                                                                              | POST   | /productInventoryManagement/v1/jobSpecification                          |
| Retrieves a JobSpecification by ID              | This operation retrieves a JobSpecification entity.<br />Attribute selection is enabled for all first level attributes.<br />Filtering is enabled until third level attributes.    | GET    | /productInventoryManagement/v1/jobSpecification/{id}                     |
| Delete a JobSpecification by ID                 | This operation delete job specification                                                                                                                                            | DELETE | /productInventoryManagement/v1/jobSpecification/{id}                     |
| Retrieves a Job by ID                           | This operation retrieves a Job entity.                                                                                                                                             | GET    | /productInventoryManagement/v1/job/{id}                                  |
| List or find Job objects                        | This operation list or find Job entities.                                                                                                                                          | GET    | /productInventoryManagement/v1/job                                       |
| List or find Job objects by jobSpecificationId  | This operation list or find Job entities by jobSpecificationId.                                                                                                                    | GET    | /productInventoryManagement/v1/jobSpecification/{jobSpecificationId}/Job |
| Retrieves export File Information by job ID     | This operation retrieves the export File Information of a job.                                                                                                                     | GET    | /productInventoryManagement/v1/job/{id}/exportFileInformation            |
| List system configurations                      | This operation list different system configurations                                                                                                                                | GET    | /configuration                                                           |
| Return the status of the service (health check) | This operation returns the status of the service.                                                                                                                                  | GET    | /status                                                                  |
| Return the version of the service               | This operation returns the version of the service.                                                                                                                                 | GET    | /version                                                                 |
| List or find Report objects                     | This operation lists or finds Report entities. Attribute selection is enabled for all first-level attributes.                                                                      | GET    | /productInventoryManagement/v1/reports                                   |

### Paging

Paging is supported by providing `offset` and `limit` as query parameters to the following apis:
- GET /productInventoryManagement/v1/product
- GET /productInventoryManagement/v1/jobSpecification
- GET /productInventoryManagement/v1/jobSpecification/{jobSpecificationId}/Job
- GET /productInventoryManagement/v1/job

### Authentication

In case the security is enabled, bearer token is used for authentication and authorization. Our API is a resource server based on OAuth2.

For more details, please consult [User Role & Permission management component](https://discobole.ow2.io/disco-oda-components/disco-security/doc/api-references/specifications/user-role-and-permissions/).

### List or Find Product Objects

#### Parameters

| Name                               | Description                       | Type                                | Mandatory | In    |
|------------------------------------|-----------------------------------|-------------------------------------|-----------|-------|
| `relatedParty.partyOrPartyRole.id` | ID of related party               | Array[string]                       | No        | query |
| `productCharacteristic.value`      | Value of product Characteristic   | string                              | No        | query |
| `productCharacteristic.name`       | Name of product Characteristic    | string                              | No        | query |
| `productCharacteristic.@type`      | Type of product Characteristic    | string                              | No        | query |
| `productOrderItem.productOrderId`  | Product order ID                  | Array[string]                       | No        | query |
| `id`                               | Identifier of the product         | Array[string]                       | No        | query |
| `status`                           | Status of product                 | Array[ProductStatusType]            | No        | query |
| `operationalStatus`                | Operational status of product     | Array[ProductOperationalStatusType] | No        | query |
| `startDate`                        | Start date of product             | Array[date-time]                    | No        | query |
| `startDate.gte`                    | Start date greater than or equal  | date-time                           | No        | query |
| `startDate.lte`                    | Start date less than or equal     | date-time                           | No        | query |
| `productSpecification.id`          | Product specification ID          | Array[string]                       | No        | query |
| `productOffering.id`               | Product offering ID               | Array[string]                       | No        | query |
| `@type`                            | Type of product                   | Array[string]                       | No        | query |
| `name`                             | Name of product                   | Array[string]                       | No        | query |
| `isRoot`                           | Is root product                   | boolean                             | No        | query |
| `fields`                           | Properties to include in response | string                              | No        | query |
| `offset`                           | Start index for resources         | integer                             | No        | query |
| `limit`                            | Number of resources to return     | integer                             | No        | query |
| `sort`                             | Fields to sort on                 | string                              | No        | query |

#### Request
```shell
curl -X GET "{{baseUrl}}/productInventoryManagement/v1/product?limit=2&offset=1000" -H "accept: application/json;charset=utf-8"
```

#### Response

```json
206 PARTIAL
Content-Type: application/json
X-Total-Count: 10000
X-Result-Count: 2

[
  {
    "@type": "Product",
    "id": "660e99932615fa6115a6b6ee",
    "href": "{{baseUrl}}/productInventoryManagement/v1/product/660e99932615fa6115a6b6ee",
    "isBundle": false,
    "name": "Mobileline",
    "agreement": [],
    "place": [],
    "productCharacteristic": [],
    "productOrderItem": [
      {
        "orderItemAction": "add",
        "orderItemId": "07",
        "productOrderId": "post-for-attach-056306a3-1931-4481-8e53-93cdf18d60d3"
      }
    ],
    "productPrice": [],
    "productRelationship": [],
    "productSpecification": {
      "id": "9a2fb547-92ae-4fd0-afa7-6418e27d51cb",
      "name": "Mobileline",
      "@type": "ProductSpecificationRef"
    },
    "productTerm": [],
    "realizingResource": [],
    "realizingService": [],
    "relatedParty": [
      {
        "@type": "RelatedPartyRefOrPartyRoleRef",
        "role": "Customer",
        "partyOrPartyRole": {
          "@type": "PartyRef",
          "id": "45hj-8888",
          "name": "Jean",
          "@referredType": "Individual"
        }
      }
    ],
    "status": "Created",
    "operationalStatus": "Confirmed",
    "creationDate": "2024-04-04T12:14:11Z",
    "statusChange": [
      {
        "changeDate": "2024-04-04T12:14:11Z",
        "status": "Created"
      }
    ],
    "operationalStatusChange": [
      {
        "changeDate": "2024-04-04T12:14:11Z",
        "status": "Confirmed"
      }
    ]
  },
  {
    "@type": "Product",
    "id": "660e99932615fa6115a6b6ef",
    "href": "{{baseUrl}}/productInventoryManagement/v1/product/660e99932615fa6115a6b6ef",
    "name": "Mobileline",
    "agreement": [],
    "place": [],
    "productCharacteristic": [],
    "productOffering": {
      "id": "39d17453-6b6c-4902-9743-a50ec9ad5e67",
      "name": "Mobileline",
      "@type": "AtomicProductOffering"
    },
    "productOrderItem": [
      {
        "orderItemAction": "add",
        "orderItemId": "03",
        "productOrderId": "post-for-attach-056306a3-1931-4481-8e53-93cdf18d60d3"
      }
    ],
    "productPrice": [
      {
        "description": "POP_5Euro",
        "name": "POP_5Euro",
        "priceType": "NRC",
        "price": {
          "taxRate": 0.0,
          "dutyFreeAmount": {
            "unit": "EUR",
            "value": 5.0
          },
          "taxIncludedAmount": {
            "unit": "EUR",
            "value": 5.0
          }
        },
        "productPriceAlteration": []
      }
    ],
    "productRelationship": [
      {
        "relationshipType": "sells",
        "product": {
          "@type": "ProductRef",
          "id": "660e99932615fa6115a6b6ee",
          "href": "{{baseUrl}}/productInventoryManagement/v1/product/660e99932615fa6115a6b6ee"
        }
      }
    ],
    "productTerm": [],
    "realizingResource": [],
    "realizingService": [],
    "relatedParty": [
      {
        "@type": "RelatedPartyRefOrPartyRoleRef",
        "role": "Customer",
        "partyOrPartyRole": {
          "@type": "PartyRef",
          "id": "45hj-8888",
          "name": "Jean",
          "@referredType": "Individual"
        }
      }
    ],
    "status": "Created",
    "operationalStatus": "Confirmed",
    "creationDate": "2024-04-04T12:14:11Z",
    "statusChange": [
      {
        "changeDate": "2024-04-04T12:14:11Z",
        "status": "Created"
      }
    ],
    "operationalStatusChange": [
      {
        "changeDate": "2024-04-04T12:14:11Z",
        "status": "Confirmed"
      }
    ]
  }
]
```

#### Fields Description
Check [Product](#schemaproduct) model in [Models](#models)

### Creates a Product

#### Request
```shell
curl -X POST "{{baseUrl}}/productInventoryManagement/v1/product" \
-H "accept: application/json;charset=utf-8" \
--data-raw '{
  "isBundle": "true",
  "status": "Created",
  "operationalStatus": "Confirmed",
  "relatedParty": [
    {
      "id": "45hj-8888",
      "name": "Jean",
      "role": "Customer",
      "@referredType": "Individual"
    }
  ],
  "productOffering": {
    "id": "772056cd-485b-4877-8453-1648a07c43f7",
    "name": "Mobile Package Basic",
    "@type": "Contract"
  },
  "productOrderItem": [
    {
      "productOrderId": "7000",
      "orderItemId": "01",
      "orderItemAction": "add"
    }
  ],
  "productRelationship": [
    {
      "relationshipType": "bundles",
      "product": {
        "status": "Created",
        "operationalStatus": "Confirmed",
        "relatedParty": [
          {
            "id": "45hj-8888",
            "name": "Jean",
            "role": "Customer",
            "@referredType": "Individual"
          }
        ],
        "productOffering": {
          "id": "414e3b5e-7fca-4b7e-bf73-51fa1e66b87f",
          "name": "Mobile Package Basic",
          "@type": "BundleProductOffering"
        },
        "productOrderItem": [
          {
            "productOrderId": "7000",
            "orderItemId": "02",
            "orderItemAction": "add"
          }
        ],
        "productRelationship": [
          {
            "relationshipType": "bundles",
            "product": {
              "status": "Created",
              "operationalStatus": "Confirmed",
              "relatedParty": [
                {
                  "id": "45hj-8888",
                  "name": "Jean",
                  "role": "Customer",
                  "@referredType": "Individual"
                }
              ],
              "productPrice": [
                {
                  "description": "POP_5Euro",
                  "name": "POP_5Euro",
                  "priceType": "NRC",
                  "price": {
                    "taxRate": 0,
                    "dutyFreeAmount": {
                      "unit": "EUR",
                      "value": 5
                    },
                    "taxIncludedAmount": {
                      "unit": "EUR",
                      "value": 5
                    }
                  },
                  "productOfferingPrice": {
                    "id": "d45ba2c5-2863-41c6-8069-052b909aea2e"
                  },
                  "productPriceAlteration": [
                    {
                      "price": {
                        "percentage": 1
                      },
                      "description": "productPriceAlteration description",
                      "name": "productPriceAlteration name",
                      "priceType": "productPriceAlteration priceType",
                      "recurringChargePeriod": {
                        "amount": 1,
                        "units": "EUR"
                      }
                    }
                  ]
                },
                {
                  "description": "POP_10Euro",
                  "name": "POP_10Euro",
                  "priceType": "NRC2",
                  "price": {
                    "taxRate": 0,
                    "dutyFreeAmount": {
                      "unit": "EUR",
                      "value": 10
                    },
                    "taxIncludedAmount": {
                      "unit": "EUR",
                      "value": 10
                    }
                  },
                  "productOfferingPrice": {
                    "id": "d45ba2c5-2863-41c6-8069-052b909aea2e"
                  },
                  "productPriceAlteration": [
                    {
                      "price": {
                        "percentage": 1
                      },
                      "description": "productPriceAlteration description 2",
                      "name": "productPriceAlteration name 2",
                      "priceType": "productPriceAlteration priceType 3",
                      "recurringChargePeriod": {
                        "amount": 1,
                        "units": "EUR"
                      }
                    }
                  ]
                }
              ],
              "productOffering": {
                "id": "39d17453-6b6c-4902-9743-a50ec9ad5e67",
                "name": "MobileLine",
                "@type": "AtomicProductOffering"
              },
              "productSpecification": {
                "id": "9a2fb547-92ae-4fd0-afa7-6418e27d51cb",
                "name": "MobileLine",
                "@type": "ProductSpecificationRef"
              },
              "productOrderItem": [
                {
                  "productOrderId": "7000",
                  "orderItemId": "03",
                  "orderItemAction": "add"
                }
              ],
              "@type": "Product"
            }
          },
          {
            "relationshipType": "bundles",
            "product": {
              "status": "Created",
              "operationalStatus": "Confirmed",
              "relatedParty": [
                {
                  "id": "45hj-8888",
                  "name": "Jean",
                  "role": "Customer",
                  "@referredType": "Individual"
                }
              ],
              "productOffering": {
                "id": "73effd22-3bb1-4a35-8dbd-323d8cc46772",
                "name": "Sim Card",
                "@type": "AtomicProductOffering"
              },
              "productSpecification": {
                "id": "191424cb-6002-46aa-a5a9-0602cfba83c7",
                "name": "SIM CARD",
                "@type": "ProductSpecificationRef"
              },
              "productOrderItem": [
                {
                  "productOrderId": "7000",
                  "orderItemId": "06",
                  "orderItemAction": "add"
                }
              ],
              "@type": "Product"
            }
          }
        ],
        "@type": "Product"
      }
    }
  ],
  "@type": "Product"
}'
```

#### Response

```json
201 CREATED
Content-Type: application/json

{
  "@type": "Product",
  "id": "678e48612eb76e280b4c46f2",
  "href": "{{baseUrl}}/productInventoryManagement/v1/product/678e48612eb76e280b4c46f2",
  "isBundle": true,
  "name": "Mobile Package Basic",
  "productOffering": {
    "id": "772056cd-485b-4877-8453-1648a07c43f7",
    "name": "Mobile Package Basic",
    "@type": "Contract"
  },
  "productOrderItem": [
    {
      "orderItemAction": "add",
      "orderItemId": "01",
      "productOrderId": "7000"
    }
  ],
  "productRelationship": [
    {
      "relationshipType": "bundles",
      "product": {
        "@type": "Product",
        "id": "678e48612eb76e280b4c46f3",
        "href": "{{baseUrl}}/productInventoryManagement/v1/product/678e48612eb76e280b4c46f3",
        "name": "Mobile Package Basic",
        "productOffering": {
          "id": "414e3b5e-7fca-4b7e-bf73-51fa1e66b87f",
          "name": "Mobile Package Basic",
          "@type": "BundleProductOffering"
        },
        "productOrderItem": [
          {
            "orderItemAction": "add",
            "orderItemId": "02",
            "productOrderId": "7000"
          }
        ],
        "productRelationship": [
          {
            "relationshipType": "bundles",
            "product": {
              "@type": "Product",
              "id": "678e48612eb76e280b4c46f4",
              "href": "{{baseUrl}}/productInventoryManagement/v1/product/678e48612eb76e280b4c46f4",
              "name": "MobileLine",
              "productOffering": {
                "id": "39d17453-6b6c-4902-9743-a50ec9ad5e67",
                "name": "MobileLine",
                "@type": "AtomicProductOffering"
              },
              "productOrderItem": [
                {
                  "orderItemAction": "add",
                  "orderItemId": "03",
                  "productOrderId": "7000"
                }
              ],
              "productPrice": [
                {
                  "description": "POP_5Euro",
                  "name": "POP_5Euro",
                  "priceType": "NRC",
                  "productOfferingPrice": {
                    "id": "d45ba2c5-2863-41c6-8069-052b909aea2e"
                  },
                  "price": {
                    "taxRate": 0.0,
                    "dutyFreeAmount": {
                      "unit": "EUR",
                      "value": 5.0
                    },
                    "taxIncludedAmount": {
                      "unit": "EUR",
                      "value": 5.0
                    }
                  },
                  "productPriceAlteration": [
                    {
                      "description": "productPriceAlteration description",
                      "name": "productPriceAlteration name",
                      "priceType": "productPriceAlteration priceType",
                      "recurringChargePeriod": {
                        "amount": 1.0,
                        "units": "EUR"
                      },
                      "price": {
                        "percentage": 1.0
                      }
                    }
                  ]
                },
                {
                  "description": "POP_10Euro",
                  "name": "POP_10Euro",
                  "priceType": "NRC2",
                  "productOfferingPrice": {
                    "id": "d45ba2c5-2863-41c6-8069-052b909aea2e"
                  },
                  "price": {
                    "taxRate": 0.0,
                    "dutyFreeAmount": {
                      "unit": "EUR",
                      "value": 10.0
                    },
                    "taxIncludedAmount": {
                      "unit": "EUR",
                      "value": 10.0
                    }
                  },
                  "productPriceAlteration": [
                    {
                      "description": "productPriceAlteration description 2",
                      "name": "productPriceAlteration name 2",
                      "priceType": "productPriceAlteration priceType 3",
                      "recurringChargePeriod": {
                        "amount": 1.0,
                        "units": "EUR"
                      },
                      "price": {
                        "percentage": 1.0
                      }
                    }
                  ]
                }
              ],
              "productRelationship": [
                {
                  "relationshipType": "rootProduct",
                  "product": {
                    "@type": "ProductRef",
                    "id": "678e48612eb76e280b4c46f2",
                    "href": "{{baseUrl}}/productInventoryManagement/v1/product/678e48612eb76e280b4c46f2"
                  }
                }
              ],
              "productSpecification": {
                "id": "9a2fb547-92ae-4fd0-afa7-6418e27d51cb",
                "name": "MobileLine",
                "@type": "ProductSpecificationRef"
              },
              "relatedParty": [
                {
                  "@type": "RelatedPartyRefOrPartyRoleRef",
                  "role": "Customer"
                }
              ],
              "status": "Created",
              "operationalStatus": "Confirmed",
              "creationDate": "2025-01-20T12:58:09Z",
              "lastUpdateDate": "2025-01-20T12:58:09Z",
              "statusChange": [
                {
                  "changeDate": "2025-01-20T12:58:09Z",
                  "status": "Created"
                }
              ],
              "operationalStatusChange": [
                {
                  "changeDate": "2025-01-20T12:58:09Z",
                  "status": "Confirmed"
                }
              ]
            }
          },
          {
            "relationshipType": "bundles",
            "product": {
              "@type": "Product",
              "id": "678e48612eb76e280b4c46f5",
              "href": "{{baseUrl}}/productInventoryManagement/v1/product/678e48612eb76e280b4c46f5",
              "name": "Sim Card",
              "productOffering": {
                "id": "73effd22-3bb1-4a35-8dbd-323d8cc46772",
                "name": "Sim Card",
                "@type": "AtomicProductOffering"
              },
              "productOrderItem": [
                {
                  "orderItemAction": "add",
                  "orderItemId": "06",
                  "productOrderId": "7000"
                }
              ],
              "productRelationship": [
                {
                  "relationshipType": "rootProduct",
                  "product": {
                    "@type": "ProductRef",
                    "id": "678e48612eb76e280b4c46f2",
                    "href": "{{baseUrl}}/productInventoryManagement/v1/product/678e48612eb76e280b4c46f2"
                  }
                }
              ],
              "productSpecification": {
                "id": "191424cb-6002-46aa-a5a9-0602cfba83c7",
                "name": "SIM CARD",
                "@type": "ProductSpecificationRef"
              },
              "relatedParty": [
                {
                  "@type": "RelatedPartyRefOrPartyRoleRef",
                  "role": "Customer"
                }
              ],
              "status": "Created",
              "operationalStatus": "Confirmed",
              "creationDate": "2025-01-20T12:58:09Z",
              "lastUpdateDate": "2025-01-20T12:58:09Z",
              "statusChange": [
                {
                  "changeDate": "2025-01-20T12:58:09Z",
                  "status": "Created"
                }
              ],
              "operationalStatusChange": [
                {
                  "changeDate": "2025-01-20T12:58:09Z",
                  "status": "Confirmed"
                }
              ]
            }
          },
          {
            "relationshipType": "rootProduct",
            "product": {
              "@type": "ProductRef",
              "id": "678e48612eb76e280b4c46f2",
              "href": "{{baseUrl}}/productInventoryManagement/v1/product/678e48612eb76e280b4c46f2"
            }
          }
        ],
        "relatedParty": [
          {
            "@type": "RelatedPartyRefOrPartyRoleRef",
            "role": "Customer"
          }
        ],
        "status": "Created",
        "operationalStatus": "Confirmed",
        "creationDate": "2025-01-20T12:58:09Z",
        "lastUpdateDate": "2025-01-20T12:58:09Z",
        "statusChange": [
          {
            "changeDate": "2025-01-20T12:58:09Z",
            "status": "Created"
          }
        ],
        "operationalStatusChange": [
          {
            "changeDate": "2025-01-20T12:58:09Z",
            "status": "Confirmed"
          }
        ]
      }
    }
  ],
  "relatedParty": [
    {
      "@type": "RelatedPartyRefOrPartyRoleRef",
      "role": "Customer"
    }
  ],
  "status": "Created",
  "operationalStatus": "Confirmed",
  "creationDate": "2025-01-20T12:58:09Z",
  "lastUpdateDate": "2025-01-20T12:58:09Z",
  "statusChange": [
    {
      "changeDate": "2025-01-20T12:58:09Z",
      "status": "Created"
    }
  ],
  "operationalStatusChange": [
    {
      "changeDate": "2025-01-20T12:58:09Z",
      "status": "Confirmed"
    }
  ]
}
```

#### Fields Description
Check [Product](#schemaproduct) model in [Models](#models)

### Updates Partially Products

#### Request
```shell
curl -X PATCH '{{baseUrl}}/productInventoryManagement/v1/product' \
-H 'Content-Type: application/json-patch+json' \
--data-raw '[
  {
    "op": "replace",
    "path": "/productInventoryManagement/v1/product/678f83133257425f66df660b/operationalStatus",
    "value": "Confirmed"
  },
  {
    "op": "replace",
    "path": "/productInventoryManagement/v1/product/678f83133257425f66df660c/operationalStatus",
    "value": "Confirmed"
  },
  {
    "op": "replace",
    "path": "/productInventoryManagement/v1/product/678f83133257425f66df660d/operationalStatus",
    "value": "Confirmed"
  }
]
'
```

#### Response

```json
200 OK
Content-Type: application/json

[
  {
    "@type": "Product",
    "id": "678f83133257425f66df660d",
    "href": "{{baseUrl}}/productInventoryManagement/v1/product/678f83133257425f66df660d",
    "name": "MobileLine",
    "productOffering": {
      "id": "39d17453-6b6c-4902-9743-a50ec9ad5e67",
      "name": "MobileLine",
      "@type": "AtomicProductOffering"
    },
    "productOrderItem": [
      {
        "orderItemAction": "add",
        "orderItemId": "03",
        "productOrderId": "7000"
      }
    ],
    "productPrice": [
      {
        "description": "POP_5Euro",
        "name": "POP_5Euro",
        "priceType": "NRC",
        "productOfferingPrice": {
          "id": "d45ba2c5-2863-41c6-8069-052b909aea2e"
        },
        "price": {
          "taxRate": 0.0,
          "dutyFreeAmount": {
            "unit": "EUR",
            "value": 5.0
          },
          "taxIncludedAmount": {
            "unit": "EUR",
            "value": 5.0
          }
        },
        "productPriceAlteration": [
          {
            "description": "productPriceAlteration description",
            "name": "productPriceAlteration name",
            "priceType": "productPriceAlteration priceType",
            "recurringChargePeriod": {
              "amount": 1.0,
              "units": "EUR"
            },
            "price": {
              "percentage": 1.0
            }
          }
        ]
      },
      {
        "description": "POP_10Euro",
        "name": "POP_10Euro",
        "priceType": "NRC2",
        "productOfferingPrice": {
          "id": "d45ba2c5-2863-41c6-8069-052b909aea2e"
        },
        "price": {
          "taxRate": 0.0,
          "dutyFreeAmount": {
            "unit": "EUR",
            "value": 10.0
          },
          "taxIncludedAmount": {
            "unit": "EUR",
            "value": 10.0
          }
        },
        "productPriceAlteration": [
          {
            "description": "productPriceAlteration description 2",
            "name": "productPriceAlteration name 2",
            "priceType": "productPriceAlteration priceType 3",
            "recurringChargePeriod": {
              "amount": 1.0,
              "units": "EUR"
            },
            "price": {
              "percentage": 1.0
            }
          }
        ]
      }
    ],
    "productRelationship": [
      {
        "relationshipType": "rootProduct",
        "product": {
          "@type": "ProductRef",
          "id": "678f83133257425f66df660b",
          "href": "{{baseUrl}}/productInventoryManagement/v1/product/678f83133257425f66df660b"
        }
      }
    ],
    "productSpecification": {
      "id": "9a2fb547-92ae-4fd0-afa7-6418e27d51cb",
      "name": "MobileLine",
      "@type": "ProductSpecificationRef"
    },
    "relatedParty": [
      {
        "@type": "RelatedPartyRefOrPartyRoleRef",
        "role": "Customer"
      }
    ],
    "status": "Created",
    "operationalStatus": "Confirmed",
    "creationDate": "2025-01-21T11:20:51Z",
    "lastUpdateDate": "2025-01-21T11:30:23Z",
    "statusChange": [
      {
        "changeDate": "2025-01-21T11:20:51Z",
        "status": "Created"
      }
    ],
    "operationalStatusChange": [
      {
        "changeDate": "2025-01-21T11:20:51Z",
        "status": "Confirmed"
      }
    ]
  },
  {
    "@type": "Product",
    "id": "678f83133257425f66df660b",
    "href": "{{baseUrl}}/productInventoryManagement/v1/product/678f83133257425f66df660b",
    "isBundle": true,
    "name": "Mobile Package Basic",
    "productOffering": {
      "id": "772056cd-485b-4877-8453-1648a07c43f7",
      "name": "Mobile Package Basic",
      "@type": "Contract"
    },
    "productOrderItem": [
      {
        "orderItemAction": "add",
        "orderItemId": "01",
        "productOrderId": "7000"
      }
    ],
    "productRelationship": [
      {
        "relationshipType": "bundles",
        "product": {
          "@type": "ProductRef",
          "id": "678f83133257425f66df660c",
          "href": "{{baseUrl}}/productInventoryManagement/v1/product/678f83133257425f66df660c"
        }
      }
    ],
    "relatedParty": [
      {
        "@type": "RelatedPartyRefOrPartyRoleRef",
        "role": "Customer"
      }
    ],
    "status": "Created",
    "operationalStatus": "Confirmed",
    "creationDate": "2025-01-21T11:20:51Z",
    "lastUpdateDate": "2025-01-21T11:30:23Z",
    "statusChange": [
      {
        "changeDate": "2025-01-21T11:20:51Z",
        "status": "Created"
      }
    ],
    "operationalStatusChange": [
      {
        "changeDate": "2025-01-21T11:20:51Z",
        "status": "Confirmed"
      }
    ]
  },
  {
    "@type": "Product",
    "id": "678f83133257425f66df660c",
    "href": "{{baseUrl}}/productInventoryManagement/v1/product/678f83133257425f66df660c",
    "name": "Mobile Package Basic",
    "productOffering": {
      "id": "414e3b5e-7fca-4b7e-bf73-51fa1e66b87f",
      "name": "Mobile Package Basic",
      "@type": "BundleProductOffering"
    },
    "productOrderItem": [
      {
        "orderItemAction": "add",
        "orderItemId": "02",
        "productOrderId": "7000"
      }
    ],
    "productRelationship": [
      {
        "relationshipType": "bundles",
        "product": {
          "@type": "ProductRef",
          "id": "678f83133257425f66df660d",
          "href": "{{baseUrl}}/productInventoryManagement/v1/product/678f83133257425f66df660d"
        }
      },
      {
        "relationshipType": "bundles",
        "product": {
          "@type": "ProductRef",
          "id": "678f83133257425f66df660e",
          "href": "{{baseUrl}}/productInventoryManagement/v1/product/678f83133257425f66df660e"
        }
      },
      {
        "relationshipType": "rootProduct",
        "product": {
          "@type": "ProductRef",
          "id": "678f83133257425f66df660b",
          "href": "{{baseUrl}}/productInventoryManagement/v1/product/678f83133257425f66df660b"
        }
      }
    ],
    "relatedParty": [
      {
        "@type": "RelatedPartyRefOrPartyRoleRef",
        "role": "Customer"
      }
    ],
    "status": "Created",
    "operationalStatus": "Confirmed",
    "creationDate": "2025-01-21T11:20:51Z",
    "lastUpdateDate": "2025-01-21T11:30:23Z",
    "statusChange": [
      {
        "changeDate": "2025-01-21T11:20:51Z",
        "status": "Created"
      }
    ],
    "operationalStatusChange": [
      {
        "changeDate": "2025-01-21T11:20:51Z",
        "status": "Confirmed"
      }
    ]
  }
]
```

#### Fields Description
Check [ProductPatch](#schemaproductpatch) and [product](#schemaproduct) models in [Models](#models)

### Retrieves a Product by ID

#### Parameters

| Name     | Description                                           | Type   | Mandatory | In    |
|----------|-------------------------------------------------------|--------|-----------|-------|
| `id`     | Identifier of product                                 | string | Yes       | path  |
| `fields` | Comma-separated properties to be provided in response | string | No        | query |

#### Request
```shell
curl -X GET '{{baseUrl}}/productInventoryManagement/v1/product/{id}?fields=status,operationalStatus,name,isBundle,creationDate,lastUpdateDate'
```

#### Response

```json
200 OK
Content-Type: application/json

{
  "@type": "Product",
  "id": "678f83133257425f66df660b",
  "href": "{{baseUrl}}/productInventoryManagement/v1/product/678f83133257425f66df660b",
  "isBundle": true,
  "name": "Mobile Package Basic",
  "status": "Created",
  "operationalStatus": "Confirmed",
  "creationDate": "2025-01-21T11:20:51Z",
  "lastUpdateDate": "2025-01-21T11:30:23Z"
}
```

#### Fields Description
Check [Product](#schemaproduct) model in [Models](#models)

### Updates Partially a Product

#### Parameters

| Name     | Description                                           | Type   | Mandatory | In    |
|----------|-------------------------------------------------------|--------|-----------|-------|
| `id`     | Identifier of product                                 | string | Yes       | path  |

#### Request
```shell
curl -X PATCH '{{baseUrl}}/productInventoryManagement/v1/product/{id}' \
-H 'Content-Type: application/json' \
--data-raw '{
    "@type": "Product",
    "operationalStatus": "Confirmed"
}'
```

#### Response

```json
200 OK
Content-Type: application/json

{
  "@type": "Product",
  "id": "678f83133257425f66df660b",
  "href": "{{baseUrl}}/productInventoryManagement/v1/product/678f83133257425f66df660b",
  "isBundle": true,
  "name": "Mobile Package Basic",
  "productOffering": {
    "id": "772056cd-485b-4877-8453-1648a07c43f7",
    "name": "Mobile Package Basic",
    "@type": "Contract"
  },
  "productOrderItem": [
    {
      "orderItemAction": "add",
      "orderItemId": "01",
      "productOrderId": "7000"
    }
  ],
  "productRelationship": [
    {
      "relationshipType": "bundles",
      "product": {
        "@type": "ProductRef",
        "id": "678f83133257425f66df660c",
        "href": "{{baseUrl}}/productInventoryManagement/v1/product/678f83133257425f66df660c"
      }
    }
  ],
  "relatedParty": [
    {
      "@type": "RelatedPartyRefOrPartyRoleRef",
      "role": "Customer"
    }
  ],
  "status": "Created",
  "operationalStatus": "Confirmed",
  "creationDate": "2025-01-21T11:20:51Z",
  "lastUpdateDate": "2025-01-21T11:55:11Z",
  "statusChange": [
    {
      "changeDate": "2025-01-21T11:20:51Z",
      "status": "Created"
    },
    {
      "changeDate": "2025-01-21T11:55:11Z"
    }
  ],
  "operationalStatusChange": [
    {
      "changeDate": "2025-01-21T11:20:51Z",
      "status": "Confirmed"
    }
  ]
}
```

> Note: `@type` field must be provided to do the PATCH operation

#### Fields Description
Check [Product](#schemaproduct) model in [Models](#models)

### Delete a Product by ID

#### Parameters

| Name     | Description                                           | Type   | Mandatory | In    |
|----------|-------------------------------------------------------|--------|-----------|-------|
| `id`     | Identifier of product                                 | string | Yes       | path  |

#### Request
```shell
curl -X DELETE '{{baseUrl}}/productInventoryManagement/v1/product/{id}'
```

#### Response

```json
204 NO CONTENT
```

### Export List Product Objects

#### Parameters

| Name                  | Description                                                                              | Type                                                                                    | Mandatory | In    |
|-----------------------|------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|-----------|-------|
| `relatedParty.id`     | ID of related party                                                                      | string                                                                                  | No        | query |
| `status`              | Status of product has to be one of ProductStatusType                                     | array[string] (Available values: Aborted, Active, Cancelled, Created, Terminated, Sold) | No        | query |
| `startDate.gte`       | StartDate.gte greater than or equal value provided which could be date or date time      | string                                                                                  | No        | query |
| `startDate.lte`       | StartDate.lte greater than or equal value provided which could be date or date time      | string                                                                                  | No        | query |
| `creationDate.gte`    | CreationDate.gte greater than or equal value provided which could be date or date time   | string                                                                                  | No        | query |
| `creationDate.lte`    | CreationDate.lte greater than or equal value provided which could be date or date time   | string                                                                                  | No        | query |
| `lastUpdateDate.gte`  | LastUpdateDate.gte greater than or equal value provided which could be date or date time | string                                                                                  | No        | query |
| `lastUpdateDate.lte`  | LastUpdateDate.lte greater than or equal value provided which could be date or date time | string                                                                                  | No        | query |
| `contentType`         | The content type of the exported file                                                    | string (json, csv)                                                                      | Yes       | query |

#### Request
```shell
curl -X GET '{{baseUrl}}/productInventoryManagement/v1/product/export?contentType=json'
```

#### Response

```json
200 OK
Content-Type: application/octet-stream

// steam of products to be downloaded as file
```

#### Fields Description
Check [Product](#schemaproduct) model in [Models](#models)


### List or Find JobSpecification Objects

#### Parameters

| Name                             | Description                                                                         | Type          | Mandatory | In    |
|----------------------------------|-------------------------------------------------------------------------------------|---------------|-----------|-------|
| `id`                             | Identifier of the JobSpecification                                                  | array[string] | No        | query |
| `name`                           | Name of the JobSpecification                                                        | array[string] | No        | query |
| `lifecycleStatus`                | LifecycleStatus of job specification has to be one of JobSpecificationStatusType    | array[string] | No        | query |
| `@type`                          | Type of job specification has to be one of JobSpecificationType                     | array[string] | No        | query |
| `activePeriod.startDateTime`     | StartDate of job specification and has to follow a date time format                 | array[string] | No        | query |
| `activePeriod.startDateTime.gte` | StartDate.gte greater than or equal value provided which could be date or date time | string        | No        | query |
| `activePeriod.startDateTime.lte` | StartDate.Lte greater than or equal value provided which could be date or date time | string        | No        | query |
| `activePeriod.endDateTime`       | EndDate of job specification and has to follow a date time format                   | array[string] | No        | query |
| `activePeriod.endDateTime.gte`   | EndDate greater than or equal value provided which could be date or date time       | string        | No        | query |
| `activePeriod.endDateTime.lte`   | EndDate less than or equal value provided which could be date or date time          | string        | No        | query |
| `creationDate`                   | CreationDate of job specification and has to follow a date time format              | array[string] | No        | query |
| `creationDate.gte`               | CreationDate greater than or equal value provided which could be date or date time  | string        | No        | query |
| `creationDate.lte`               | CreationDate less than or equal value provided which could be date or date time     | string        | No        | query |
| `fields`                         | Comma-separated properties to be provided in response                               | string        | No        | query |
| `offset`                         | Requested index for start of resources to be provided in response                   | integer       | No        | query |
| `limit`                          | Requested number of resources to be provided in response                            | integer       | No        | query |
| `sort`                           | Comma-separated list of fields to sort on                                           | array[string] | No        | query |

#### Request
```shell
curl -X GET '{{baseUrl}}/productInventoryManagement/v1/jobSpecification'
```

#### Response

```json
200 OK
Content-Type: application/json
X-Total-Count: 1
X-Result-Count: 1

[
  {
    "@type": "TerminationJobSpecification",
    "id": "678e5b99dc8e480de6953d54",
    "name": "Termination Job",
    "href": "{{baseUrl}}/productInventoryManagement/v1/jobSpecification/678e5b99dc8e480de6953d54",
    "lifecycleStatus": "Active",
    "lifeCycleStatusChange": [
      {
        "changeDate": "2025-01-20T14:20:09Z",
        "lifecycleStatus": "Active"
      }
    ],
    "activePeriod": {
      "endDateTime": "2075-01-20T14:20:09Z",
      "startDateTime": "2025-01-20T14:20:09Z"
    },
    "creationDate": "2025-01-20T14:20:09Z",
    "schedule": {
      "@type": "RecurringJobScheduler",
      "frequency": {
        "amount": 1,
        "timePeriod": "Hour"
      },
      "scheduledPeriod": {
        "endDateTime": "2075-01-20",
        "startDateTime": "2025-01-20"
      },
      "executionTime": "16:20"
    }
  },
  {
    "@type": "ExportJobSpecification",
    "id": "6790fbda2e2c1a063bd77fa0",
    "href": "{{baseUrl}}/productInventoryManagement/v1/jobSpecification/6790fbda2e2c1a063bd77fa0",
    "lifecycleStatus": "Created",
    "lifeCycleStatusChange": [
      {
        "changeDate": "2025-01-22T14:08:26Z",
        "lifecycleStatus": "Created"
      }
    ],
    "creationDate": "2025-01-22T14:08:26Z",
    "schedule": {
      "@type": "OneTimeJobScheduler",
      "plannedDate": "2025-04-01T00:00:00Z"
    },
    "contentType": "csv",
    "query": "creationDate.gte=2025-01-01T00:00:00Z",
    "fields": []
  }
]
```

#### Fields Description
Check [JobSpecification](#schemajobspecification) model in [Models](#models)

### Create a Job Specification

#### Request
```shell
curl -X POST '{{baseUrl}}/productInventoryManagement/v1/jobSpecification' \
-H 'Content-Type: application/json' \
--data-raw '{
  "@type": "ExportJobSpecification",
  "query": "creationDate.gte=2025-01-01T00:00:00Z",
  "schedule": {
    "plannedDate": "2025-04-01T00:00:00Z",
    "@type": "OneTimeJobScheduler"
  },
  "contentType": "csv"
}'
```

#### Response

```json
201 CREATED
Content-Type: application/json

{
  "@type": "ExportJobSpecification",
  "id": "6790fbda2e2c1a063bd77fa0",
  "href": "{{baseUrl}}/productInventoryManagement/v1/jobSpecification/6790fbda2e2c1a063bd77fa0",
  "lifecycleStatus": "Created",
  "lifeCycleStatusChange": [
    {
      "changeDate": "2025-01-22T14:08:26Z",
      "lifecycleStatus": "Created"
    }
  ],
  "creationDate": "2025-01-22T14:08:26Z",
  "schedule": {
    "@type": "OneTimeJobScheduler",
    "plannedDate": "2025-04-01T00:00:00Z"
  },
  "contentType": "csv",
  "query": "creationDate.gte=2025-01-01T00:00:00Z",
  "fields": []
}
```

#### Fields Description
Check [JobSpecification](#schemajobspecification) model in [Models](#models)

### Retrieves a JobSpecification by ID

#### Parameters

| Name     | Description                                           | Type   | Mandatory | In    |
|----------|-------------------------------------------------------|--------|-----------|-------|
| `id`     | Identifier of Job Specification                       | string | Yes       | path  |
| `fields` | Comma-separated properties to be provided in response | string | No        | query |

#### Request
```shell
curl -X GET '{{baseUrl}}/productInventoryManagement/v1/jobSpecification/{id}'
```

#### Response

```json
200 OK
Content-Type: application/json

{
  "@type": "ExportJobSpecification",
  "id": "6790fbda2e2c1a063bd77fa0",
  "href": "{{baseUrl}}/productInventoryManagement/v1/jobSpecification/6790fbda2e2c1a063bd77fa0",
  "lifecycleStatus": "Created",
  "lifeCycleStatusChange": [
    {
      "changeDate": "2025-01-22T14:08:26Z",
      "lifecycleStatus": "Created"
    }
  ],
  "creationDate": "2025-01-22T14:08:26Z",
  "schedule": {
    "@type": "OneTimeJobScheduler",
    "plannedDate": "2025-04-01T00:00:00Z"
  },
  "contentType": "csv",
  "query": "creationDate.gte=2025-01-01T00:00:00Z",
  "fields": []
}
```

#### Fields Description
Check [JobSpecification](#schemajobspecification) model in [Models](#models)

### Delete a JobSpecification by ID

#### Parameters

| Name     | Description                     | Type   | Mandatory | In    |
|----------|---------------------------------|--------|-----------|-------|
| `id`     | Identifier of Job Specification | string | Yes       | path  |

#### Request
```shell
curl -X DELETE '{{baseUrl}}/productInventoryManagement/v1/jobSpecification/{id}'
```

#### Response

```json
204 NO CONTENT
```

### Retrieves a Job by JobSpecification ID

#### Parameters

| Name                                | Description                                                                         | Type          | Mandatory | In    |
|-------------------------------------|-------------------------------------------------------------------------------------|---------------|-----------|-------|
| `jobSpecificationId`                | Identifier of Job parent JobSpecification                                           | string        | Yes       | path  |
| `id`                                | Identifier of the Parent Job                                                        | array[string] | No        | query |
| `status`                            | Status of job specification has to be one of JobStatusType                          | array[string] | No        | query |
| `executionPeriod.startDateTime`     | StartDate of Job and has to follow a date time format                               | array[string] | No        | query |
| `executionPeriod.startDateTime.gte` | StartDate.gte greater than or equal value provided which could be date or date time | string        | No        | query |
| `executionPeriod.startDateTime.lte` | StartDate.Lte greater than or equal value provided which could be date or date time | string        | No        | query |
| `executionPeriod.endDateTime`       | EndDate of Job and has to follow a date time format                                 | array[string] | No        | query |
| `executionPeriod.endDateTime.gte`   | EndDate greater than or equal value provided which could be date or date time       | string        | No        | query |
| `executionPeriod.endDateTime.lte`   | EndDate less than or equal value provided which could be date or date time          | string        | No        | query |
| `plannedDate`                       | PlannedDate of Job and has to follow a date time format                             | array[string] | No        | query |
| `plannedDate.gte`                   | PlannedDate greater than or equal value provided which could be date or date time   | string        | No        | query |
| `plannedDate.lte`                   | PlannedDate less than or equal value provided which could be date or date time      | string        | No        | query |
| `fields`                            | Comma-separated properties to be provided in response                               | string        | No        | query |
| `offset`                            | Requested index for start of resources to be provided in response                   | integer       | No        | query |
| `limit`                             | Requested number of resources to be provided in response                            | integer       | No        | query |
| `sort`                              | Comma-separated list of fields to sort on                                           | array[string] | No        | query |

#### Request
```shell
curl -X GET '{{baseUrl}}/productInventoryManagement/v1/jobSpecification/678e5b99dc8e480de6953d54/Job?fields=id,status,plannedDate,@type&limit=3'
```

#### Response

```json
200 OK
Content-Type: application/json
X-Total-Count: 11
X-Result-Count: 11

[
  {
    "@type": "TerminationJob",
    "id": "678e5b99dc8e480de6953d55",
    "jobSpecification": {
      "id": "678e5b99dc8e480de6953d54"
    },
    "plannedDate": "2025-01-20T16:20:00Z",
    "status": "Succeeded",
    "href": "{{baseUrl}}/productInventoryManagement/v1/job/678e5b99dc8e480de6953d55"
  },
  {
    "@type": "TerminationJob",
    "id": "678f82683257425f66df6608",
    "jobSpecification": {
      "id": "678e5b99dc8e480de6953d54"
    },
    "plannedDate": "2025-01-21T11:20:00Z",
    "status": "Succeeded",
    "href": "{{baseUrl}}/productInventoryManagement/v1/job/678f82683257425f66df6608"
  },
  {
    "@type": "TerminationJob",
    "id": "678f82e03257425f66df660a",
    "jobSpecification": {
      "id": "678e5b99dc8e480de6953d54"
    },
    "plannedDate": "2025-01-21T12:20:00Z",
    "status": "Succeeded",
    "href": "{{baseUrl}}/productInventoryManagement/v1/job/678f82e03257425f66df660a"
  }
]
```

#### Fields Description
Check [Job](#schemajob) model in [Models](#models)


### Retrieves Job by ID

#### Parameters

| Name     | Description                                           | Type   | Mandatory | In    |
|----------|-------------------------------------------------------|--------|-----------|-------|
| `id`     | Identifier of Job                                     | string | Yes       | path  |
| `fields` | Comma-separated properties to be provided in response | string | No        | query |

#### Request
```shell
curl -X GET '{{baseUrl}}/productInventoryManagement/v1/job/{id}?fields=plannedDate'
```

#### Response

```json
200 OK
Content-Type: application/json

{
  "@type": "TerminationJob",
  "id": "67960147d9745a00e95427a6",
  "jobSpecification": {
    "id": "67960147d9745a00e95427a5"
  },
  "plannedDate": "2025-01-26T11:32:00Z",
  "href": "{{baseUrl}}/productInventoryManagement/v1/job/67960147d9745a00e95427a6"
}
```

#### Fields Description
Check [Job](#schemajob) model in [Models](#models)

### List or Find Job Objects

#### Parameters

| Name                                | Description                                                                         | Type          | Mandatory | In    |
|-------------------------------------|-------------------------------------------------------------------------------------|---------------|-----------|-------|
| `id`                                | Identifier of the Parent Job                                                        | array[string] | No        | query |
| `jobSpecification.id`               | Identifier of the Parent JobSpecification                                           | array[string] | No        | query |
| `@type`                             | Type of job specification has to be one of JobSpecificationType                     | array[string] | No        | query |
| `status`                            | Status of job specification has to be one of JobStatusType                          | array[string] | No        | query |
| `executionPeriod.startDateTime`     | StartDate of Job and has to follow a date time format                               | array[string] | No        | query |
| `executionPeriod.startDateTime.gte` | StartDate.gte greater than or equal value provided which could be date or date time | string        | No        | query |
| `executionPeriod.startDateTime.lte` | StartDate.Lte greater than or equal value provided which could be date or date time | string        | No        | query |
| `executionPeriod.endDateTime`       | EndDate of Job and has to follow a date time format                                 | array[string] | No        | query |
| `executionPeriod.endDateTime.gte`   | EndDate greater than or equal value provided which could be date or date time       | string        | No        | query |
| `executionPeriod.endDateTime.lte`   | EndDate less than or equal value provided which could be date or date time          | string        | No        | query |
| `plannedDate`                       | PlannedDate of Job and has to follow a date time format                             | array[string] | No        | query |
| `plannedDate.gte`                   | PlannedDate greater than or equal value provided which could be date or date time   | string        | No        | query |
| `plannedDate.lte`                   | PlannedDate less than or equal value provided which could be date or date time      | string        | No        | query |
| `fields`                            | Comma-separated properties to be provided in response                               | string        | No        | query |
| `offset`                            | Requested index for start of resources to be provided in response                   | integer       | No        | query |
| `limit`                             | Requested number of resources to be provided in response                            | integer       | No        | query |
| `sort`                              | Comma-separated list of fields to sort on                                           | array[string] | No        | query |

#### Request
```shell
curl -X GET '{{baseUrl}}/productInventoryManagement/v1/job?fields=jobSpecification.id'
```

#### Response

```json
200 OK
Content-Type: application/json
X-Total-Count: 1
X-Result-Count: 1

[
  {
    "@type": "TerminationJob",
    "id": "67960147d9745a00e95427a6",
    "jobSpecification": {
      "id": "67960147d9745a00e95427a5"
    },
    "href": "{{baseUrl}}/productInventoryManagement/v1/job/67960147d9745a00e95427a6"
  }
]
```

#### Fields Description
Check [Job](#schemajob) model in [Models](#models)

### Retrieves Export File Information by Job ID

#### Parameters

| Name     | Description                                           | Type   | Mandatory | In    |
|----------|-------------------------------------------------------|--------|-----------|-------|
| `id`     | Identifier of Job                                     | string | Yes       | path  |

#### Request
```shell
curl -X GET '{{baseUrl}}/productInventoryManagement/v1/job/6790cecfe69e9c437a71ad83/exportFileInformation'
```

#### Response

```json
200 OK
Content-Type: application/json

{
  "url": "{{s3Url}}/s3-bucket/products-export-JobSpecification%20generated%20by%20automation%20%E3%83%84-2d022c64-1737543375970.csv?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250126T123522Z&X-Amz-SignedHeaders=host&X-Amz-Credential=amazoncredential%2F20250126%2Fs-east-1%2Fs3%2Faws4_request&X-Amz-Expires=1800&X-Amz-Signature=amazonsignature",
  "completionDate": "2025-01-22T10:57:00Z",
  "contentType": "csv",
  "validFor": {
    "endDateTime": "2025-01-26T13:05:22Z",
    "startDateTime": "2025-01-26T12:35:24Z"
  }
}
```

#### Fields Description
Check [Job](#schemajob) model in [Models](#models)


### List or Find Report Objects

#### Parameters

| Name              | Description                                                                  | Type   | Mandatory | In    |
|-------------------|------------------------------------------------------------------------------|--------|-----------|-------|
| `@type`           | Defines the sub-class entity name when sub-classing.                         | string | Yes       | query |
| `collectDate`     | Filter to search by exact date.                                              | string | No        | query |
| `collectDate.lte` | The start date for the report period.                                        | string | No        | query |
| `collectDate.gte` | The end date for the report period.                                          | string | No        | query |
| `granularity`     | The granularity of the report data (day, week, month, year). defaults to Day | string | No        | query |

#### Request
```shell
curl -X GET '{{baseUrl}}/productInventoryManagement/v1/reports?@type=ReportProductByStatus&collectDate=2025-01-22'
```

#### Response

```json
200 OK
Content-Type: application/json
```

```json
[
  {
    "@type": "ReportProductByStatus",
    "id": "67905b00ca6610238afeaad4",
    "collectDate": "2025-01-22",
    "details": [
      {
        "count": 0,
        "characteristic": [
          {
            "name": "status",
            "value": "Aborted"
          }
        ]
      },
      {
        "count": 134791,
        "characteristic": [
          {
            "name": "status",
            "value": "Active"
          }
        ]
      },
      {
        "count": 1,
        "characteristic": [
          {
            "name": "status",
            "value": "Cancelled"
          }
        ]
      },
      {
        "count": 298088,
        "characteristic": [
          {
            "name": "status",
            "value": "Created"
          }
        ]
      },
      {
        "count": 1027,
        "characteristic": [
          {
            "name": "status",
            "value": "Terminated"
          }
        ]
      },
      {
        "count": 50477,
        "characteristic": [
          {
            "name": "status",
            "value": "Sold"
          }
        ]
      }
    ]
  }
]
```

#### Fields Description
Check [Report](#schemareport) model in [Models](#models)

### Models

<h3 id="tocS_ExportJobSpecification">ExportJobSpecification</h3>
<!-- backwards compatibility -->
<a id="schemaexportjobspecification"></a>
<a id="schema_ExportJobSpecification"></a>
<a id="tocSexportjobspecification"></a>
<a id="tocsexportjobspecification"></a>

**Polymorphism**

Parent: JobSpecification

Discriminator: @type

#### Properties

all of [JobSpecification](#schemajobspecification)

and

| Name        | Type                                      | Required | Restrictions | Description                                     |
|-------------|-------------------------------------------|----------|--------------|-------------------------------------------------|
| @type       | string                                    | false    | none         | none                                            |
| contentType | [ContentTypeEnum](#schemacontenttypeenum) | false    | none         | Represents the content type of the export file. |
| query       | string                                    | false    | none         | none                                            |
| fields      | [string]                                  | false    | none         | none                                            |

<h3 id="tocS_TerminationJobSpecification">TerminationJobSpecification</h3>
<!-- backwards compatibility -->
<a id="schematerminationjobspecification"></a>
<a id="schema_TerminationJobSpecification"></a>
<a id="tocSterminationjobspecification"></a>
<a id="tocsterminationjobspecification"></a>

**Polymorphism**

Parent: JobSpecification

Discriminator: @type

#### Properties

all of [JobSpecification](#schemajobspecification)

and

| Name  | Type   | Required | Restrictions | Description |
|-------|--------|----------|--------------|-------------|
| @type | string | false    | none         | none        |

<h3 id="tocS_PurgeJobSpecification">PurgeJobSpecification</h3>
<!-- backwards compatibility -->
<a id="schemapurgejobspecification"></a>
<a id="schema_PurgeJobSpecification"></a>
<a id="tocSpurgejobspecification"></a>
<a id="tocspurgejobspecification"></a>

**Polymorphism**

Parent: JobSpecification

Discriminator: @type

#### Properties

all of [JobSpecification](#schemajobspecification)

and

| Name      | Type                                  | Required | Restrictions | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
|-----------|---------------------------------------|----------|--------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| @type     | string                                | false    | none         | none                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| purgeType | [PurgeTypeEnum](#schemapurgetypeenum) | false    | none         | Represents the type of what to purge. Depending on the `purgeType` value, different parameters and values of `query` attribute are required:<br> - For `PurgeProduct`: At least one of the query parameters (`creationDate.lte`, `creationDate.gte`,`terminationDate.lte`, `terminationDate.gte`, `startDate.lte`, `startDate.gte`, `status`) must be provided, and the status values must be one of `Cancelled`, `Aborted`, `Terminated`.<br> - For `PurgeJob`: At least one of the query parameters (`creationDate.lte`, `creationDate.gte`, `status`) must be provided, and the status values must be one of `Done`, `TerminatedWithError`. |
| query     | string                                | false    | none         | none                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |

<h3 id="tocS_JobSpecificationRef">JobSpecificationRef</h3>
<!-- backwards compatibility -->
<a id="schemajobspecificationref"></a>
<a id="schema_JobSpecificationRef"></a>
<a id="tocSjobspecificationref"></a>
<a id="tocsjobspecificationref"></a>

reference to a jobSpecification

#### Properties

| Name  | Type   | Required | Restrictions | Description                                               |
|-------|--------|----------|--------------|-----------------------------------------------------------|
| id    | string | true     | none         | Unique identifier of jobSpecification                     |
| href  | string | false    | none         | Reference of the jobSpecification                         |
| @type | string | true     | none         | When sub-classing, this defines the sub-class entity name |

<h3 id="tocS_JobSpecification">JobSpecification</h3>
<!-- backwards compatibility -->
<a id="schemajobspecification"></a>
<a id="schema_JobSpecification"></a>
<a id="tocSjobspecification"></a>
<a id="tocsjobspecification"></a>

Represents a job specification entity, which encapsulates information about a specific job specification. job specifications are job specifications or processes that need to be executed, each with its own unique characteristics. This DTO includes details such as the job specification's unique identifier, name, type, status, start date, end date, and a list of processed Job Specifications associated with the job specification.

#### Properties

| Name                  | Type                                                                  | Required | Restrictions | Description                                                                             |
|-----------------------|-----------------------------------------------------------------------|----------|--------------|-----------------------------------------------------------------------------------------|
| id                    | string                                                                | false    | none         | The unique identifier for the job specification.                                        |
| name                  | string                                                                | false    | none         | The name of the job specification.                                                      |
| @type                 | string                                                                | false    | none         | The type of the job specification.                                                      |
| @baseType             | string                                                                | false    | none         | The base type of the job specification.                                                 |
| href                  | string                                                                | false    | none         | The reference link to the job specification.                                            |
| lifecycleStatus       | [JobSpecificationStatusType](#schemajobspecificationstatustype)       | false    | none         | Type JobSpecification Status Enum.                                                      |
| lifeCycleStatusChange | [[JobSpecificationStatusChange](#schemajobspecificationstatuschange)] | false    | none         | [represents a status change at some time for JobSpecification]                          |
| activePeriod          | [TimePeriod](#schematimeperiod)                                       | false    | none         | A period of time, either as a deadline (endDateTime only) a startDateTime only, or both |
| creationDate          | string(date-time)                                                     | false    | none         | The creation date and time of the job specification."                                   |
| schedule              | [JobScheduler](#schemajobscheduler)                                   | false    | none         | Represents a JobScheduler entity                                                        |

<h3 id="tocS_ProductCount">ProductCount</h3>
<!-- backwards compatibility -->
<a id="schemaproductcount"></a>
<a id="schema_ProductCount"></a>
<a id="tocSproductcount"></a>
<a id="tocsproductcount"></a>

Represents the product count

#### Properties

| Name   | Type    | Required | Restrictions | Description             |
|--------|---------|----------|--------------|-------------------------|
| status | string  | false    | none         | status of Product Count |
| count  | integer | false    | none         | count of Product Count  |

<h3 id="tocS_JobScheduler">JobScheduler</h3>
<!-- backwards compatibility -->
<a id="schemajobscheduler"></a>
<a id="schema_JobScheduler"></a>
<a id="tocSjobscheduler"></a>
<a id="tocsjobscheduler"></a>

Represents a JobScheduler entity

#### Properties

| Name  | Type   | Required | Restrictions | Description          |
|-------|--------|----------|--------------|----------------------|
| @type | string | true     | none         | type of JobScheduler |

<h3 id="tocS_OneTimeJobScheduler">OneTimeJobScheduler</h3>
<!-- backwards compatibility -->
<a id="schemaonetimejobscheduler"></a>
<a id="schema_OneTimeJobScheduler"></a>
<a id="tocSonetimejobscheduler"></a>
<a id="tocsonetimejobscheduler"></a>

subType of job specification schedule, for a job specification that occurs only once in the future

#### Properties

all of [JobScheduler](#schemajobscheduler)

and

| Name        | Type              | Required | Restrictions | Description                                                                                          |
|-------------|-------------------|----------|--------------|------------------------------------------------------------------------------------------------------|
| plannedDate | string(date-time) | true     | none         | Exact date and time when the job specification should be executed (for one-time job specifications). |
| @type       | string            | false    | none         | none                                                                                                 |

<h3 id="tocS_ImmediateJobScheduler">ImmediateJobScheduler</h3>
<!-- backwards compatibility -->
<a id="schemaimmediatejobscheduler"></a>
<a id="schema_ImmediateJobScheduler"></a>
<a id="tocSimmediatejobscheduler"></a>
<a id="tocsimmediatejobscheduler"></a>

subType of job specification schedule, for a job specification that occurs only once and Immediately

#### Properties

all of [JobScheduler](#schemajobscheduler)

and

| Name  | Type   | Required | Restrictions | Description |
|-------|--------|----------|--------------|-------------|
| @type | string | false    | none         | none        |

<h3 id="tocS_RecurringJobScheduler">RecurringJobScheduler</h3>
<!-- backwards compatibility -->
<a id="schemarecurringjobscheduler"></a>
<a id="schema_RecurringJobScheduler"></a>
<a id="tocSrecurringjobscheduler"></a>
<a id="tocsrecurringjobscheduler"></a>

subType of job specification schedule, for a job specification that is recurring

#### Properties

all of [JobScheduler](#schemajobscheduler)

and

| Name            | Type                                | Required | Restrictions | Description                                                                             |
|-----------------|-------------------------------------|----------|--------------|-----------------------------------------------------------------------------------------|
| frequency       | [JobFrequency](#schemajobfrequency) | true     | none         | job Frequency                                                                           |
| scheduledPeriod | [DatePeriod](#schemadateperiod)     | true     | none         | A period of time, either as a deadline (endDateTime only) a startDateTime only, or both |
| executionTime   | string(time)                        | true     | none         | Time of day when the job specification should be executed (in UTC).                     |
| @type           | string                              | false    | none         | none                                                                                    |

<h3 id="tocS_JobFrequency">JobFrequency</h3>
<!-- backwards compatibility -->
<a id="schemajobfrequency"></a>
<a id="schema_JobFrequency"></a>
<a id="tocSjobfrequency"></a>
<a id="tocsjobfrequency"></a>

job Frequency

#### Properties

| Name       | Type                                    | Required | Restrictions | Description                                                          |
|------------|-----------------------------------------|----------|--------------|----------------------------------------------------------------------|
| amount     | integer                                 | true     | none         | Number of times the job specification should repeat within a period. |
| timePeriod | [TimePeriodType](#schematimeperiodtype) | false    | none         | Unit of time for the frequency.                                      |

<h3 id="tocS_JobSpecificationStatusType">JobSpecificationStatusType</h3>
<!-- backwards compatibility -->
<a id="schemajobspecificationstatustype"></a>
<a id="schema_JobSpecificationStatusType"></a>
<a id="tocSjobspecificationstatustype"></a>
<a id="tocsjobspecificationstatustype"></a>

Type JobSpecification Status Enum.

##### Enumerated Values

| Value      |
|------------|
| Created    |
| Active     |
| Suspend    |
| Terminated |

<h3 id="tocS_JobSchedulerType">JobSchedulerType</h3>
<!-- backwards compatibility -->
<a id="schemajobschedulertype"></a>
<a id="schema_JobSchedulerType"></a>
<a id="tocSjobschedulertype"></a>
<a id="tocsjobschedulertype"></a>

Type discriminator for the job specification schedule.

##### Enumerated Values

| Value                 |
|-----------------------|
| OneTimeJobScheduler   |
| RecurringJobScheduler |
| ImmediateJobScheduler |

<h3 id="tocS_TimePeriodType">TimePeriodType</h3>
<!-- backwards compatibility -->
<a id="schematimeperiodtype"></a>
<a id="schema_TimePeriodType"></a>
<a id="tocStimeperiodtype"></a>
<a id="tocstimeperiodtype"></a>

Unit of time for the frequency.

##### Enumerated Values

| Value |
|-------|
| Hour  |
| Day   |
| Week  |
| Month |
| Year  |

<h3 id="tocS_ContentTypeEnum">ContentTypeEnum</h3>
<!-- backwards compatibility -->
<a id="schemacontenttypeenum"></a>
<a id="schema_ContentTypeEnum"></a>
<a id="tocScontenttypeenum"></a>
<a id="tocscontenttypeenum"></a>

Represents the content type of the export file.

##### Enumerated Values

| Value |
|-------|
| json  |
| csv   |

<h3 id="tocS_JobSpecificationType">JobSpecificationType</h3>
<!-- backwards compatibility -->
<a id="schemajobspecificationtype"></a>
<a id="schema_JobSpecificationType"></a>
<a id="tocSjobspecificationtype"></a>
<a id="tocsjobspecificationtype"></a>

Represents the type of a job specification. Possible values include: - `TerminationJobSpecification`: The job specification is a batch job specification. - `ExportJobSpecification`: The job specification is an export job. - `PurgeJobSpecification`: The job specification is a purge job.

##### Enumerated Values

| Value                       |
|-----------------------------|
| TerminationJobSpecification |
| ExportJobSpecification      |
| PurgeJobSpecification       |

<h3 id="tocS_ProductRelationshipType">ProductRelationshipType</h3>
<!-- backwards compatibility -->
<a id="schemaproductrelationshiptype"></a>
<a id="schema_ProductRelationshipType"></a>
<a id="tocSproductrelationshiptype"></a>
<a id="tocsproductrelationshiptype"></a>

Represents the type of relationship between products, such as [bundled] if the product is a bundle and you want to describe the bundled products inside this bundle; [reliesOn] if the product needs another already owned product to rely on (e.g. an option on an already owned mobile access product) [targets] or [isTargeted] (depending on the way of expressing the link) for any other kind of links that may be useful

##### Enumerated Values

| Value           |
|-----------------|
| bundles         |
| sells           |
| rootProduct     |
| isChild         |
| reliesOn        |
| hasParent       |
| reliesFrom      |
| bundlesMigrate  |
| reliesOnMigrate |

<h3 id="tocS_Job">Job</h3>
<!-- backwards compatibility -->
<a id="schemajob"></a>
<a id="schema_Job"></a>
<a id="tocSjob"></a>
<a id="tocsjob"></a>

Represents the execution details of a specific job specification.

#### Properties

| Name             | Type                                              | Required | Restrictions | Description                                                                                                                                                                                                                                                            |
|------------------|---------------------------------------------------|----------|--------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| id               | string                                            | false    | none         | The unique identifier of the job.                                                                                                                                                                                                                                      |
| jobSpecification | [JobSpecificationRef](#schemajobspecificationref) | false    | none         | reference to a jobSpecification                                                                                                                                                                                                                                        |
| executionPeriod  | [TimePeriod](#schematimeperiod)                   | false    | none         | A period of time, either as a deadline (endDateTime only) a startDateTime only, or both                                                                                                                                                                                |
| plannedDate      | string(date-time)                                 | false    | none         | The planned start date and time for the job.                                                                                                                                                                                                                           |
| status           | [JobStatusType](#schemajobstatustype)             | false    | none         | Represents the possible statuses of a job. Possible values include: - `NotStarted`: The job has not started yet. - `Running`: The job is currently in progress. - `Succeeded`: The job has successfully completed. - `Failed`: The job was terminated due to an error. |
| href             | string                                            | false    | none         | Reference of the related entity.                                                                                                                                                                                                                                       |
| errorLog         | string                                            | false    | none         | none                                                                                                                                                                                                                                                                   |
| @type            | string                                            | false    | none         | The type of the job that reflect the same type as its parent job specification.                                                                                                                                                                                        |
| @baseType        | string                                            | false    | none         | When sub-classing, this defines the super-class.                                                                                                                                                                                                                       |

<h3 id="tocS_ExportJob">ExportJob</h3>
<!-- backwards compatibility -->
<a id="schemaexportjob"></a>
<a id="schema_ExportJob"></a>
<a id="tocSexportjob"></a>
<a id="tocsexportjob"></a>

**Polymorphism**

Parent: Job

Discriminator: @type

#### Properties

all of [Job](#schemajob)

and

| Name     | Type   | Required | Restrictions | Description |
|----------|--------|----------|--------------|-------------|
| @type    | string | false    | none         | none        |
| fileName | string | false    | none         | none        |

<h3 id="tocS_PurgeJob">PurgeJob</h3>
<!-- backwards compatibility -->
<a id="schemapurgejob"></a>
<a id="schema_PurgeJob"></a>
<a id="tocSpurgejob"></a>
<a id="tocspurgejob"></a>

**Polymorphism**

Parent: Job

Discriminator: @type

#### Properties

all of [Job](#schemajob)

and

| Name  | Type   | Required | Restrictions | Description |
|-------|--------|----------|--------------|-------------|
| @type | string | false    | none         | none        |

<h3 id="tocS_TerminationJob">TerminationJob</h3>
<!-- backwards compatibility -->
<a id="schematerminationjob"></a>
<a id="schema_TerminationJob"></a>
<a id="tocSterminationjob"></a>
<a id="tocsterminationjob"></a>

**Polymorphism**

Parent: Job

Discriminator: @type

#### Properties

all of [Job](#schemajob)

and

| Name          | Type                                                | Required | Restrictions | Description                                                         |
|---------------|-----------------------------------------------------|----------|--------------|---------------------------------------------------------------------|
| @type         | string                                              | false    | none         | none                                                                |
| failureReport | [TerminationJobReport](#schematerminationjobreport) | false    | none         | **Polymorphism**<br><br>  Parent: Job<br><br>  Discriminator: @type |
| successReport | [TerminationJobReport](#schematerminationjobreport) | false    | none         | **Polymorphism**<br><br>  Parent: Job<br><br>  Discriminator: @type |

<h3 id="tocS_TerminationJobReport">TerminationJobReport</h3>
<!-- backwards compatibility -->
<a id="schematerminationjobreport"></a>
<a id="schema_TerminationJobReport"></a>
<a id="tocSterminationjobreport"></a>
<a id="tocsterminationjobreport"></a>

**Polymorphism**

Parent: Job

Discriminator: @type

#### Properties

| Name     | Type                                                  | Required | Restrictions | Description                                                                                                                                                            |
|----------|-------------------------------------------------------|----------|--------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| status   | [TerminationStatusType](#schematerminationstatustype) | false    | none         | Represents the status of a processed product. Possible values include: - `Failed`: The processed product is failed. - `Completed`: the processed product is completed. |
| products | [[ProductRef](#schemaproductref)]                     | false    | none         | [reference to a product]                                                                                                                                               |

<h3 id="tocS_JobStatusType">JobStatusType</h3>
<!-- backwards compatibility -->
<a id="schemajobstatustype"></a>
<a id="schema_JobStatusType"></a>
<a id="tocSjobstatustype"></a>
<a id="tocsjobstatustype"></a>

Represents the possible statuses of a job. Possible values include: - `NotStarted`: The job has not started yet. - `Running`: The job is currently in progress. - `Succeeded`: The job has successfully completed. - `Failed`: The job was terminated due to an error.

##### Enumerated Values

| Value      |
|------------|
| NotStarted |
| Running    |
| Succeeded  |
| Failed     |

<h3 id="tocS_TerminationStatusType">TerminationStatusType</h3>
<!-- backwards compatibility -->
<a id="schematerminationstatustype"></a>
<a id="schema_TerminationStatusType"></a>
<a id="tocSterminationstatustype"></a>
<a id="tocsterminationstatustype"></a>

Represents the status of a processed product. Possible values include: - `Failed`: The processed product is failed. - `Completed`: the processed product is completed.

##### Enumerated Values

| Value     |
|-----------|
| Failed    |
| succeeded |

<h3 id="tocS_JobTypeEnum">JobTypeEnum</h3>
<!-- backwards compatibility -->
<a id="schemajobtypeenum"></a>
<a id="schema_JobTypeEnum"></a>
<a id="tocSjobtypeenum"></a>
<a id="tocsjobtypeenum"></a>

Represents the type of a job.

##### Enumerated Values

| Value          |
|----------------|
| TerminationJob |
| ExportJob      |
| PurgeJob       |

<h3 id="tocS_AgreementItemRef">AgreementItemRef</h3>
<!-- backwards compatibility -->
<a id="schemaagreementitemref"></a>
<a id="schema_AgreementItemRef"></a>
<a id="tocSagreementitemref"></a>
<a id="tocsagreementitemref"></a>

Agreement reference. An agreement represents a contract or arrangement, either written or verbal and sometimes enforceable by law, such as a service level agreement or a customer price agreement. An agreement involves a number of other business entities, such as products, services, and resources and/or their specifications.

#### Properties

| Name            | Type   | Required | Restrictions | Description                                                            |
|-----------------|--------|----------|--------------|------------------------------------------------------------------------|
| id              | string | true     | none         | Unique identifier of a related entity.                                 |
| href            | string | false    | none         | Reference of the related entity.                                       |
| agreementItemId | string | false    | none         | Identifier of the agreement                                            |
| name            | string | false    | none         | Name of the related entity.                                            |
| @type           | string | false    | none         | When sub-classing, this defines the sub-class entity name              |
| @referredType   | string | false    | none         | The actual type of the target instance when needed for disambiguation. |

<h3 id="tocS_BillingAccountRef">BillingAccountRef</h3>
<!-- backwards compatibility -->
<a id="schemabillingaccountref"></a>
<a id="schema_BillingAccountRef"></a>
<a id="tocSbillingaccountref"></a>
<a id="tocsbillingaccountref"></a>

BillingAccount reference. A BillingAccount is a detailed description of a bill structure.

#### Properties

| Name          | Type   | Required | Restrictions | Description                                                            |
|---------------|--------|----------|--------------|------------------------------------------------------------------------|
| id            | string | true     | none         | Unique identifier of the billing account                               |
| href          | string | false    | none         | Reference of the billing account                                       |
| name          | string | false    | none         | Name of the billing account                                            |
| @referredType | string | false    | none         | The actual type of the target instance when needed for disambiguation. |
| ratingType    | string | false    | none         | type of rating                                                         |

<h3 id="tocS_BooleanArrayCharacteristic">BooleanArrayCharacteristic</h3>
<!-- backwards compatibility -->
<a id="schemabooleanarraycharacteristic"></a>
<a id="schema_BooleanArrayCharacteristic"></a>
<a id="tocSbooleanarraycharacteristic"></a>
<a id="tocsbooleanarraycharacteristic"></a>

**Polymorphism**

Parent: Characteristic

Discriminator: @type

#### Properties

all of [Characteristic](#schemacharacteristic)

and

| Name  | Type      | Required | Restrictions | Description |
|-------|-----------|----------|--------------|-------------|
| value | [boolean] | false    | none         | none        |

<h3 id="tocS_BooleanCharacteristic">BooleanCharacteristic</h3>
<!-- backwards compatibility -->
<a id="schemabooleancharacteristic"></a>
<a id="schema_BooleanCharacteristic"></a>
<a id="tocSbooleancharacteristic"></a>
<a id="tocsbooleancharacteristic"></a>

**Polymorphism**

Parent: Characteristic

Discriminator: @type

#### Properties

all of [Characteristic](#schemacharacteristic)

and

| Name  | Type    | Required | Restrictions | Description |
|-------|---------|----------|--------------|-------------|
| value | boolean | false    | none         | none        |

<h3 id="tocS_Characteristic">Characteristic</h3>
<!-- backwards compatibility -->
<a id="schemacharacteristic"></a>
<a id="schema_Characteristic"></a>
<a id="tocScharacteristic"></a>
<a id="tocscharacteristic"></a>

Describes a given characteristic of an object or entity through a name/value pair.

**Polymorphism**

Discriminator: @type

One of:
- BooleanCharacteristic
- IntegerCharacteristic
- ObjectCharacteristic
- ObjectArrayCharacteristic
- StringCharacteristic
- StringArrayCharacteristic
- NumberCharacteristic
- NumberArrayCharacteristic
- IntegerArrayCharacteristic
- BooleanArrayCharacteristic      - ValidityCharacteristic

#### Properties

| Name                               | Type                                                              | Required | Restrictions | Description                                               |
|------------------------------------|-------------------------------------------------------------------|----------|--------------|-----------------------------------------------------------|
| id                                 | string                                                            | false    | none         | identifier of characteristic                              |
| name                               | string                                                            | false    | none         | Name of the characteristic                                |
| valueType                          | string                                                            | false    | none         | Data type of the value of the characteristic              |
| productCharacteristicRelationships | [[CharacteristicRelationship](#schemacharacteristicrelationship)] | false    | none         | array of characteristics in relation                      |
| @type                              | string                                                            | true     | none         | When sub-classing, this defines the sub-class entity name |

<h3 id="tocS_CharacteristicRelationship">CharacteristicRelationship</h3>
<!-- backwards compatibility -->
<a id="schemacharacteristicrelationship"></a>
<a id="schema_CharacteristicRelationship"></a>
<a id="tocScharacteristicrelationship"></a>
<a id="tocscharacteristicrelationship"></a>

represents a relation ship between characteristics

#### Properties

| Name             | Type   | Required | Restrictions | Description                  |
|------------------|--------|----------|--------------|------------------------------|
| id               | string | true     | none         | Identifier of characteristic |
| relationshipType | string | false    | none         | type of relationship         |

<h3 id="tocS_Error">Error</h3>
<!-- backwards compatibility -->
<a id="schemaerror"></a>
<a id="schema_Error"></a>
<a id="tocSerror"></a>
<a id="tocserror"></a>

Used when an API throws an Error, typically with a HTTP error response-code (3xx, 4xx, 5xx)

#### Properties

| Name            | Type        | Required | Restrictions | Description                                                                                   |
|-----------------|-------------|----------|--------------|-----------------------------------------------------------------------------------------------|
| code            | string      | true     | none         | Application relevant detail, defined in the API or a common list.                             |
| reason          | string      | true     | none         | Explanation of the reason for the error which can be shown to a client user.                  |
| message         | string      | false    | none         | More details and corrective actions related to the error which can be shown to a client user. |
| status          | string      | false    | none         | HTTP Error code extension                                                                     |
| referenceError  | string(uri) | false    | none         | URI of documentation describing the error.                                                    |
| @baseType       | string      | false    | none         | When sub-classing, this defines the super-class.                                              |
| @schemaLocation | string(uri) | false    | none         | A URI to a JSON-Schema file that defines additional attributes and relationships              |
| @type           | string      | false    | none         | When sub-classing, this defines the sub-class entity name.                                    |

<h3 id="tocS_Config">Config</h3>
<!-- backwards compatibility -->
<a id="schemaconfig"></a>
<a id="schema_Config"></a>
<a id="tocSconfig"></a>
<a id="tocsconfig"></a>

Response object containing the system configuration details.

#### Properties

| Name                               | Type           | Required | Restrictions | Description                                                                                                                                                  |
|------------------------------------|----------------|----------|--------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|
| paginationLimit                    | integer(int32) | false    | none         | The maximum number of products that can be fetched or counted in the product listing API. Requests for offsets exceeding this limit will result in an error. |
| productCatalogUrl                  | string         | false    | none         | The URL of the product catalog service.                                                                                                                      |
| resourceInventoryManagementUrl     | string         | false    | none         | The URL of the resource inventory management service.                                                                                                        |
| productCatalogEnabled              | boolean        | false    | none         | Indicates whether the product catalog service is enabled. Possible values are 'true' or 'false'.                                                             |
| resourceInventoryManagementEnabled | boolean        | false    | none         | Indicates whether the resource inventory management service is enabled. Possible values are 'true' or 'false'.                                               |

<h3 id="tocS_ExternalIdentifier">ExternalIdentifier</h3>
<!-- backwards compatibility -->
<a id="schemaexternalidentifier"></a>
<a id="schema_ExternalIdentifier"></a>
<a id="tocSexternalidentifier"></a>
<a id="tocsexternalidentifier"></a>

external identifier

#### Properties

| Name                   | Type   | Required | Restrictions | Description                         |
|------------------------|--------|----------|--------------|-------------------------------------|
| id                     | string | true     | none         | identifier of an external reference |
| owner                  | string | false    | none         | owner of an external reference      |
| externalIdentifierType | string | false    | none         | type of an external reference       |

<h3 id="tocS_IntegerArrayCharacteristic">IntegerArrayCharacteristic</h3>
<!-- backwards compatibility -->
<a id="schemaintegerarraycharacteristic"></a>
<a id="schema_IntegerArrayCharacteristic"></a>
<a id="tocSintegerarraycharacteristic"></a>
<a id="tocsintegerarraycharacteristic"></a>

**Polymorphism**

Parent: Characteristic

Discriminator: @type

#### Properties

all of [Characteristic](#schemacharacteristic)

and

| Name  | Type      | Required | Restrictions | Description |
|-------|-----------|----------|--------------|-------------|
| value | [integer] | false    | none         | none        |

<h3 id="tocS_IntegerCharacteristic">IntegerCharacteristic</h3>
<!-- backwards compatibility -->
<a id="schemaintegercharacteristic"></a>
<a id="schema_IntegerCharacteristic"></a>
<a id="tocSintegercharacteristic"></a>
<a id="tocsintegercharacteristic"></a>

**Polymorphism**

Parent: Characteristic

Discriminator: @type

#### Properties

all of [Characteristic](#schemacharacteristic)

and

| Name  | Type           | Required | Restrictions | Description |
|-------|----------------|----------|--------------|-------------|
| value | integer(int32) | true     | none         | none        |

<h3 id="tocS_ValidityCharacteristic">ValidityCharacteristic</h3>
<!-- backwards compatibility -->
<a id="schemavaliditycharacteristic"></a>
<a id="schema_ValidityCharacteristic"></a>
<a id="tocSvaliditycharacteristic"></a>
<a id="tocsvaliditycharacteristic"></a>

**Polymorphism**

Parent: Characteristic

Discriminator: @type

#### Properties

all of [Characteristic](#schemacharacteristic)

and

| Name  | Type                  | Required | Restrictions | Description                         |
|-------|-----------------------|----------|--------------|-------------------------------------|
| value | [Value](#schemavalue) | false    | none         | the value of ValidityCharacteristic |

<h3 id="tocS_Value">Value</h3>
<!-- backwards compatibility -->
<a id="schemavalue"></a>
<a id="schema_Value"></a>
<a id="tocSvalue"></a>
<a id="tocsvalue"></a>

the value of ValidityCharacteristic

#### Properties

| Name          | Type              | Required | Restrictions | Description                              |
|---------------|-------------------|----------|--------------|------------------------------------------|
| value         | integer           | false    | none         | The value of the validity of product     |
| unitOfMeasure | string            | false    | none         | The unite of the validity of product     |
| validTo       | string(date-time) | false    | none         | The validTo of the validity of product   |
| validFrom     | string(date-time) | false    | none         | The validFrom of the validity of product |

<h3 id="tocS_Money">Money</h3>
<!-- backwards compatibility -->
<a id="schemamoney"></a>
<a id="schema_Money"></a>
<a id="tocSmoney"></a>
<a id="tocsmoney"></a>

A base / value business entity used to represent money

#### Properties

| Name  | Type          | Required | Restrictions | Description                                                   |
|-------|---------------|----------|--------------|---------------------------------------------------------------|
| unit  | string        | false    | none         | Currency (ISO4217 norm uses 3 letters to define the currency) |
| value | number(float) | false    | none         | A positive floating point number                              |

<h3 id="tocS_NumberArrayCharacteristic">NumberArrayCharacteristic</h3>
<!-- backwards compatibility -->
<a id="schemanumberarraycharacteristic"></a>
<a id="schema_NumberArrayCharacteristic"></a>
<a id="tocSnumberarraycharacteristic"></a>
<a id="tocsnumberarraycharacteristic"></a>

**Polymorphism**

Parent: Characteristic

Discriminator: @type

#### Properties

all of [Characteristic](#schemacharacteristic)

and

| Name  | Type     | Required | Restrictions | Description |
|-------|----------|----------|--------------|-------------|
| value | [number] | false    | none         | none        |

<h3 id="tocS_NumberCharacteristic">NumberCharacteristic</h3>
<!-- backwards compatibility -->
<a id="schemanumbercharacteristic"></a>
<a id="schema_NumberCharacteristic"></a>
<a id="tocSnumbercharacteristic"></a>
<a id="tocsnumbercharacteristic"></a>

**Polymorphism**

Parent: Characteristic

Discriminator: @type

#### Properties

all of [Characteristic](#schemacharacteristic)

and

| Name  | Type          | Required | Restrictions | Description                 |
|-------|---------------|----------|--------------|-----------------------------|
| value | number(float) | false    | none         | Value of the characteristic |

<h3 id="tocS_ObjectArrayCharacteristic">ObjectArrayCharacteristic</h3>
<!-- backwards compatibility -->
<a id="schemaobjectarraycharacteristic"></a>
<a id="schema_ObjectArrayCharacteristic"></a>
<a id="tocSobjectarraycharacteristic"></a>
<a id="tocsobjectarraycharacteristic"></a>

**Polymorphism**

Parent: Characteristic

Discriminator: @type

#### Properties

all of [Characteristic](#schemacharacteristic)

and

| Name  | Type     | Required | Restrictions | Description |
|-------|----------|----------|--------------|-------------|
| value | [object] | false    | none         | none        |

<h3 id="tocS_ObjectCharacteristic">ObjectCharacteristic</h3>
<!-- backwards compatibility -->
<a id="schemaobjectcharacteristic"></a>
<a id="schema_ObjectCharacteristic"></a>
<a id="tocSobjectcharacteristic"></a>
<a id="tocsobjectcharacteristic"></a>

**Polymorphism**

Parent: Characteristic

Discriminator: @type

#### Properties

all of [Characteristic](#schemacharacteristic)

and

| Name  | Type   | Required | Restrictions | Description |
|-------|--------|----------|--------------|-------------|
| value | object | true     | none         | none        |

<h3 id="tocS_OperationalStatusChange">OperationalStatusChange</h3>
<!-- backwards compatibility -->
<a id="schemaoperationalstatuschange"></a>
<a id="schema_OperationalStatusChange"></a>
<a id="tocSoperationalstatuschange"></a>
<a id="tocsoperationalstatuschange"></a>

represents an operational status change at some time

#### Properties

| Name          | Type                                                                | Required | Restrictions | Description                                   |
|---------------|---------------------------------------------------------------------|----------|--------------|-----------------------------------------------|
| changeDate    | string(date-time)                                                   | false    | none         | date that the change happened                 |
| changeReason  | string                                                              | false    | none         | reason that the change happened               |
| status        | [ProductOperationalStatusType](#schemaproductoperationalstatustype) | false    | none         | Possible values for the status of the product |
| @type         | string                                                              | false    | none         | type of OperationalStatusChange               |
| @referredType | string                                                              | false    | none         | referredType type of OperationalStatusChange  |

<h3 id="tocS_PatchOperationType">PatchOperationType</h3>
<!-- backwards compatibility -->
<a id="schemapatchoperationtype"></a>
<a id="schema_PatchOperationType"></a>
<a id="tocSpatchoperationtype"></a>
<a id="tocspatchoperationtype"></a>

Patch Operation to be executed

##### Enumerated Values

| Value   |
|---------|
| add     |
| copy    |
| move    |
| remove  |
| replace |
| test    |

<h3 id="tocS_Place">Place</h3>
<!-- backwards compatibility -->
<a id="schemaplace"></a>
<a id="schema_Place"></a>
<a id="tocSplace"></a>
<a id="tocsplace"></a>

Place reference. Place defines the places where the products are sold or delivered.

#### Properties

| Name            | Type   | Required | Restrictions | Description                                                                            |
|-----------------|--------|----------|--------------|----------------------------------------------------------------------------------------|
| id              | string | false    | none         | Unique identifier of the place                                                         |
| href            | string | false    | none         | Unique reference of the place                                                          |
| name            | string | false    | none         | A user-friendly name for the place, such as [Paris Store], [London Store], [Main Home] |
| @baseType       | string | false    | none         | When sub-classing, this defines the super-class                                        |
| @schemaLocation | string | false    | none         | A URI to a JSON-Schema file that defines additional attributes and relationships       |
| @type           | string | false    | none         | When sub-classing, this defines the sub-class entity name                              |

<h3 id="tocS_PlaceRef">PlaceRef</h3>
<!-- backwards compatibility -->
<a id="schemaplaceref"></a>
<a id="schema_PlaceRef"></a>
<a id="tocSplaceref"></a>
<a id="tocsplaceref"></a>

Place reference. PlaceRef defines the placeRefs where the products are sold or delivered.

#### Properties

| Name            | Type   | Required | Restrictions | Description                                                                      |
|-----------------|--------|----------|--------------|----------------------------------------------------------------------------------|
| id              | string | true     | none         | Unique identifier of a related entity.                                           |
| href            | string | false    | none         | Reference of the related entity.                                                 |
| name            | string | false    | none         | Name of the related entity.                                                      |
| @baseType       | string | false    | none         | When sub-classing, this defines the super-class                                  |
| @schemaLocation | string | false    | none         | A URI to a JSON-Schema file that defines additional attributes and relationships |
| @type           | string | false    | none         | When sub-classing, this defines the sub-class entity name                        |
| @referredType   | string | false    | none         | The actual type of the target instance when needed for disambiguation.           |

<h3 id="tocS_Price">Price</h3>
<!-- backwards compatibility -->
<a id="schemaprice"></a>
<a id="schema_Price"></a>
<a id="tocSprice"></a>
<a id="tocsprice"></a>

Provides all amounts (tax included, duty free, tax rate), used currency and percentage to apply for Price Alteration.

#### Properties

| Name              | Type                  | Required | Restrictions | Description                                            |
|-------------------|-----------------------|----------|--------------|--------------------------------------------------------|
| percentage        | number(float)         | false    | none         | Percentage to apply for ProdOfferPriceAlteration       |
| taxRate           | number(float)         | false    | none         | Tax rate                                               |
| dutyFreeAmount    | [Money](#schemamoney) | false    | none         | A base / value business entity used to represent money |
| taxIncludedAmount | [Money](#schemamoney) | false    | none         | A base / value business entity used to represent money |

<h3 id="tocS_PriceAlteration">PriceAlteration</h3>
<!-- backwards compatibility -->
<a id="schemapricealteration"></a>
<a id="schema_PriceAlteration"></a>
<a id="tocSpricealteration"></a>
<a id="tocspricealteration"></a>

Is an amount, usually of money, that modifies the price charged for an order item.

#### Properties

| Name                  | Type                                                      | Required | Restrictions | Description                                                                                                                                   |
|-----------------------|-----------------------------------------------------------|----------|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| applicationDuration   | integer(int32)                                            | false    | none         | Duration during which the alteration applies on the order item price (for instance 2 months free of charge for the recurring charge)          |
| description           | string                                                    | false    | none         | A narrative that explains in detail the semantics of this order item price alteration                                                         |
| name                  | string                                                    | false    | none         | Name of the order item price alteration                                                                                                       |
| priceType             | string                                                    | true     | none         | A category that describes the price such as recurring, one time and usage.                                                                    |
| priority              | integer(int32)                                            | false    | none         | Priority level for applying this alteration among all the defined alterations on the order item price                                         |
| recurringChargePeriod | [RecurringChargePeriod](#schemarecurringchargeperiod)     | false    | none         | Could be month, week...                                                                                                                       |
| unitOfMeasure         | string                                                    | false    | none         | Could be minutes, GB...                                                                                                                       |
| productOfferingPrice  | [ProductOfferingPriceRef](#schemaproductofferingpriceref) | false    | none         | ProductPriceOffering reference. An amount, usually of money, that is asked for or allowed when a ProductOffering is bought, rented, or leased |
| price                 | [Price](#schemaprice)                                     | true     | none         | Provides all amounts (tax included, duty free, tax rate), used currency and percentage to apply for Price Alteration.                         |

<h3 id="tocS_Product">Product</h3>
<!-- backwards compatibility -->
<a id="schemaproduct"></a>
<a id="schema_Product"></a>
<a id="tocSproduct"></a>
<a id="tocsproduct"></a>

A product offering procured by a customer or other interested party playing a party role. A product is realized as one or more service(s) and / or resource(s).

#### Properties

| Name                    | Type                                                                | Required | Restrictions | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
|-------------------------|---------------------------------------------------------------------|----------|--------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| id                      | string                                                              | false    | none         | Unique identifier of the product                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| href                    | string                                                              | false    | none         | Reference of the product                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| description             | string                                                              | false    | none         | Is the description of the product. It could be copied from the description of the Product Offering.                                                                                                                                                                                                                                                                                                                                                                                                                            |
| isBundle                | boolean                                                             | false    | none         | If true, the product is a ProductBundle which is an instantiation of a BundledProductOffering. If false, the product is a ProductComponent which is an instantiation of a SimpleProductOffering.                                                                                                                                                                                                                                                                                                                               |
| isCustomerVisible       | boolean                                                             | false    | none         | If true, the product is visible by the customer.                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| name                    | string                                                              | false    | none         | Name of the product. It could be the same as the name of the product offering                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| orderDate               | string(date-time)                                                   | false    | none         | Is the date when the product was ordered                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| productSerialNumber     | string                                                              | false    | none         | Is the serial number for the product. This is typically applicable to tangible products e.g. Broadband Router.                                                                                                                                                                                                                                                                                                                                                                                                                 |
| startDate               | string(date-time)                                                   | false    | none         | Is the date from which the product starts                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| billingAccount          | [BillingAccountRef](#schemabillingaccountref)                       | false    | none         | BillingAccount reference. A BillingAccount is a detailed description of a bill structure.                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| agreement               | [[AgreementItemRef](#schemaagreementitemref)]                       | false    | none         | [Agreement reference. An agreement represents a contract or arrangement, either written or verbal and sometimes enforceable by law, such as a service level agreement or a customer price agreement. An agreement involves a number of other business entities, such as products, services, and resources and/or their specifications.]                                                                                                                                                                                        |
| terminationDate         | string(date-time)                                                   | false    | none         | Is the date when the product was terminated                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| place                   | [[RelatedPlaceRefOrValue](#schemarelatedplacereforvalue)]           | false    | none         | [Related Entity reference. A related place defines a place described by reference or by value linked to a specific entity. The polymorphic attributes @type, @schemaLocation & @referredType are related to the place entity and not the RelatedPlaceRefOrValue class itself<br><br><br><br><br><br>**Inheritance**<br><br>  Children:<br>  - GeographicAddress]                                                                                                                                                               |
| productCharacteristic   | [[Characteristic](#schemacharacteristic)]                           | false    | none         | [Describes a given characteristic of an object or entity through a name/value pair.<br><br><br><br><br><br>**Polymorphism**<br><br>  Discriminator: @type<br><br>  One of:<br>  - BooleanCharacteristic<br>  - IntegerCharacteristic<br>  - ObjectCharacteristic<br>  - ObjectArrayCharacteristic<br>  - StringCharacteristic<br>  - StringArrayCharacteristic<br>  - NumberCharacteristic<br>  - NumberArrayCharacteristic<br>  - IntegerArrayCharacteristic<br>  - BooleanArrayCharacteristic      - ValidityCharacteristic] |
| productOffering         | [ProductOfferingRef](#schemaproductofferingref)                     | false    | none         | ProductOffering reference. A product offering represents entities that are orderable from the provider of the catalog, this resource includes pricing information.                                                                                                                                                                                                                                                                                                                                                             |
| productOrderItem        | [[RelatedProductOrderItem](#schemarelatedproductorderitem)]         | true     | none         | [RelatedProductOrderItem (ProductOrder item) .The product order item which triggered product creation/change/termination.]                                                                                                                                                                                                                                                                                                                                                                                                     |
| productPrice            | [[ProductPrice](#schemaproductprice)]                               | false    | none         | [An amount, usually of money, that represents the actual price paid by a Customer for a purchase, a rent or a lease of a Product. The price is valid for a defined period of time.]                                                                                                                                                                                                                                                                                                                                            |
| productRelationship     | [[ProductRelationship](#schemaproductrelationship)]                 | false    | none         | [Linked products to the one instantiate, such as [bundled] if the product is a bundle and you want to describe the bundled products inside this bundle; [reliesOn] if the product needs another already owned product to rely on (e.g. an option on an already owned mobile access product) [targets] or [isTargeted] (depending on the way of expressing the link) for any other kind of links that may be useful]                                                                                                            |
| productSpecification    | [ProductSpecificationRef](#schemaproductspecificationref)           | false    | none         | Product specification reference: A ProductSpecification is a detailed description of a tangible or intangible object made available externally in the form of a ProductOffering to customers or other parties playing a party role.                                                                                                                                                                                                                                                                                            |
| productTerm             | [[ProductTerm](#schemaproductterm)]                                 | false    | none         | [Description of a productTerm linked to this product. This represent a commitment with a duration]                                                                                                                                                                                                                                                                                                                                                                                                                             |
| realizingResource       | [[ResourceRef](#schemaresourceref)]                                 | false    | none         | [Reference to a resource]                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| realizingService        | [[ServiceRef](#schemaserviceref)]                                   | false    | none         | [Service reference, for when Service is used by other entities]                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| relatedParty            | [[RelatedPartyOrPartyRole](#schemarelatedpartyorpartyrole)]         | false    | none         | [RelatedParty reference. A related party defines party or party role or its reference,linked to a specific entity]                                                                                                                                                                                                                                                                                                                                                                                                             |
| status                  | [ProductStatusType](#schemaproductstatustype)                       | true     | none         | Possible values for the status of the product                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| operationalStatus       | [ProductOperationalStatusType](#schemaproductoperationalstatustype) | false    | none         | Possible values for the status of the product                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| creationDate            | string(date-time)                                                   | false    | none         | date when the product was created                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| lastUpdateDate          | string(date-time)                                                   | false    | none         | date when the product was last updated                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| @type                   | string                                                              | false    | none         | When sub-classing, this defines the sub-class entity name                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| statusChange            | [[StatusChange](#schemastatuschange)]                               | false    | none         | [represents a status change at some time]                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| operationalStatusChange | [[OperationalStatusChange](#schemaoperationalstatuschange)]         | false    | none         | [represents an operational status change at some time]                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| externalIdentifier      | [[ExternalIdentifier](#schemaexternalidentifier)]                   | false    | none         | [external identifier]                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |

<h3 id="tocS_ProductAttributeValueChangeEventPayload">ProductAttributeValueChangeEventPayload</h3>
<!-- backwards compatibility -->
<a id="schemaproductattributevaluechangeeventpayload"></a>
<a id="schema_ProductAttributeValueChangeEventPayload"></a>
<a id="tocSproductattributevaluechangeeventpayload"></a>
<a id="tocsproductattributevaluechangeeventpayload"></a>

The event data structure

#### Properties

| Name            | Type                      | Required | Restrictions | Description                                                                                                                                                     |
|-----------------|---------------------------|----------|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| product         | [Product](#schemaproduct) | false    | none         | A product offering procured by a customer or other interested party playing a party role. A product is realized as one or more service(s) and / or resource(s). |
| @baseType       | string                    | false    | none         | When sub-classing, this defines the super-class                                                                                                                 |
| @schemaLocation | string                    | false    | none         | A URI to a JSON-Schema file that defines additional attributes and relationships                                                                                |
| @type           | string                    | false    | none         | When sub-classing, this defines the sub-class entity name                                                                                                       |

<h3 id="tocS_ProductOfferingPriceRef">ProductOfferingPriceRef</h3>
<!-- backwards compatibility -->
<a id="schemaproductofferingpriceref"></a>
<a id="schema_ProductOfferingPriceRef"></a>
<a id="tocSproductofferingpriceref"></a>
<a id="tocsproductofferingpriceref"></a>

ProductPriceOffering reference. An amount, usually of money, that is asked for or allowed when a ProductOffering is bought, rented, or leased

#### Properties

| Name            | Type   | Required | Restrictions | Description                                                                      |
|-----------------|--------|----------|--------------|----------------------------------------------------------------------------------|
| id              | string | true     | none         | Unique identifier of a related entity.                                           |
| href            | string | false    | none         | Reference of the related entity.                                                 |
| name            | string | false    | none         | Name of the related entity.                                                      |
| @type           | string | false    | none         | When sub-classing, this defines the sub-class entity name                        |
| @referredType   | string | false    | none         | The actual type of the target instance when needed for disambiguation.           |
| @baseType       | string | false    | none         | When sub-classing, this defines the super-class                                  |
| @schemaLocation | string | false    | none         | A URI to a JSON-Schema file that defines additional attributes and relationships |

<h3 id="tocS_ProductOfferingRef">ProductOfferingRef</h3>
<!-- backwards compatibility -->
<a id="schemaproductofferingref"></a>
<a id="schema_ProductOfferingRef"></a>
<a id="tocSproductofferingref"></a>
<a id="tocsproductofferingref"></a>

ProductOffering reference. A product offering represents entities that are orderable from the provider of the catalog, this resource includes pricing information.

#### Properties

| Name            | Type   | Required | Restrictions | Description                                                                      |
|-----------------|--------|----------|--------------|----------------------------------------------------------------------------------|
| id              | string | true     | none         | Unique identifier of a related entity.                                           |
| href            | string | false    | none         | Reference of the related entity.                                                 |
| name            | string | false    | none         | Name of the related entity.                                                      |
| version         | string | false    | none         | version of the product offering.                                                 |
| @type           | string | true     | none         | When sub-classing, this defines the sub-class entity name                        |
| @referredType   | string | false    | none         | The actual type of the target instance when needed for disambiguation.           |
| @baseType       | string | false    | none         | When sub-classing, this defines the super-class.                                 |
| @schemaLocation | string | false    | none         | A URI to a JSON-Schema file that defines additional attributes and relationships |

<h3 id="tocS_ProductOperationalStatusType">ProductOperationalStatusType</h3>
<!-- backwards compatibility -->
<a id="schemaproductoperationalstatustype"></a>
<a id="schema_ProductOperationalStatusType"></a>
<a id="tocSproductoperationalstatustype"></a>
<a id="tocsproductoperationalstatustype"></a>

Possible values for the status of the product

##### Enumerated Values

| Value               |
|---------------------|
| Aborted             |
| Active              |
| Cancelled           |
| Confirmed           |
| Created             |
| InDisturbance       |
| Locked              |
| LockedActive        |
| PendingActive       |
| PendingCancel       |
| PendingDelivery     |
| PendingModification |
| PendingTerminate    |
| PendingMigrate      |
| Terminated          |
| Sold                |

<h3 id="tocS_SortEnum">SortEnum</h3>
<!-- backwards compatibility -->
<a id="schemasortenum"></a>
<a id="schema_SortEnum"></a>
<a id="tocSsortenum"></a>
<a id="tocssortenum"></a>

Possible values for the sort of the product

##### Enumerated Values

| Value                                 |
|---------------------------------------|
| startDate                             |
| -startDate                            |
| productRelationship.relationshipType  |
| -productRelationship.relationshipType |
| creationDate                          |
| -creationDate                         |

<h3 id="tocS_SortJobSpecificationEnum">SortJobSpecificationEnum</h3>
<!-- backwards compatibility -->
<a id="schemasortjobspecificationenum"></a>
<a id="schema_SortJobSpecificationEnum"></a>
<a id="tocSsortjobspecificationenum"></a>
<a id="tocssortjobspecificationenum"></a>

Possible values for the sort of the product

##### Enumerated Values

| Value                       |
|-----------------------------|
| activePeriod.startDateTime  |
| -activePeriod.startDateTime |
| activePeriod.endDateTime    |
| -activePeriod.endDateTime   |
| status                      |
| -status                     |

<h3 id="tocS_SortJobEnum">SortJobEnum</h3>
<!-- backwards compatibility -->
<a id="schemasortjobenum"></a>
<a id="schema_SortJobEnum"></a>
<a id="tocSsortjobenum"></a>
<a id="tocssortjobenum"></a>

Possible values for the sort of the product

##### Enumerated Values

| Value                          |
|--------------------------------|
| executionPeriod.startDateTime  |
| -executionPeriod.startDateTime |
| executionPeriod.endDateTime    |
| -executionPeriod.endDateTime   |
| status                         |
| -status                        |
| plannedDate                    |
| -plannedDate                   |

<h3 id="tocS_ProductPatch">ProductPatch</h3>
<!-- backwards compatibility -->
<a id="schemaproductpatch"></a>
<a id="schema_ProductPatch"></a>
<a id="tocSproductpatch"></a>
<a id="tocsproductpatch"></a>

this is product patch object that will be applied on the specified product

#### Properties

| Name  | Type                                            | Required | Restrictions | Description                                                           |
|-------|-------------------------------------------------|----------|--------------|-----------------------------------------------------------------------|
| op    | [PatchOperationType](#schemapatchoperationtype) | true     | none         | Patch Operation to be executed                                        |
| path  | string                                          | true     | none         | Path of the product/field to be updated                               |
| value | object                                          | true     | none         | value of to be updated or added, it can be text or can be json object |

<h3 id="tocS_ProductPrice">ProductPrice</h3>
<!-- backwards compatibility -->
<a id="schemaproductprice"></a>
<a id="schema_ProductPrice"></a>
<a id="tocSproductprice"></a>
<a id="tocsproductprice"></a>

An amount, usually of money, that represents the actual price paid by a Customer for a purchase, a rent or a lease of a Product. The price is valid for a defined period of time.

#### Properties

| Name                   | Type                                                      | Required | Restrictions | Description                                                                                                                                   |
|------------------------|-----------------------------------------------------------|----------|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| description            | string                                                    | false    | none         | A narrative that explains in detail the semantics of this product price.                                                                      |
| name                   | string                                                    | false    | none         | A short descriptive name such as "Subscription price".                                                                                        |
| priceType              | string                                                    | true     | none         | A category that describes the price, such as recurring, discount, allowance, penalty, and so forth.                                           |
| recurringChargePeriod  | [RecurringChargePeriod](#schemarecurringchargeperiod)     | false    | none         | Could be month, week...                                                                                                                       |
| unitOfMeasure          | string                                                    | false    | none         | Could be minutes, GB...                                                                                                                       |
| billingAccount         | [BillingAccountRef](#schemabillingaccountref)             | false    | none         | BillingAccount reference. A BillingAccount is a detailed description of a bill structure.                                                     |
| productOfferingPrice   | [ProductOfferingPriceRef](#schemaproductofferingpriceref) | false    | none         | ProductPriceOffering reference. An amount, usually of money, that is asked for or allowed when a ProductOffering is bought, rented, or leased |
| price                  | [Price](#schemaprice)                                     | true     | none         | Provides all amounts (tax included, duty free, tax rate), used currency and percentage to apply for Price Alteration.                         |
| productPriceAlteration | [[PriceAlteration](#schemapricealteration)]               | false    | none         | [Is an amount, usually of money, that modifies the price charged for an order item.]                                                          |
| validFor               | [TimePeriod](#schematimeperiod)                           | false    | none         | A period of time, either as a deadline (endDateTime only) a startDateTime only, or both                                                       |

<h3 id="tocS_ProductRef">ProductRef</h3>
<!-- backwards compatibility -->
<a id="schemaproductref"></a>
<a id="schema_ProductRef"></a>
<a id="tocSproductref"></a>
<a id="tocsproductref"></a>

reference to a product

#### Properties

| Name  | Type   | Required | Restrictions | Description                                               |
|-------|--------|----------|--------------|-----------------------------------------------------------|
| id    | string | true     | none         | Unique identifier of product                              |
| name  | string | false    | none         | The name of the product                                   |
| href  | string | false    | none         | Reference of the product                                  |
| @type | string | true     | none         | When sub-classing, this defines the sub-class entity name |

<h3 id="tocS_PhysicalProduct">PhysicalProduct</h3>
<!-- backwards compatibility -->
<a id="schemaphysicalproduct"></a>
<a id="schema_PhysicalProduct"></a>
<a id="tocSphysicalproduct"></a>
<a id="tocsphysicalproduct"></a>

A child of product representing a tangible product

#### Properties

all of [Product](#schemaproduct)

and

| Name  | Type   | Required | Restrictions | Description |
|-------|--------|----------|--------------|-------------|
| @type | string | false    | none         | none        |

<h3 id="tocS_ShipmentProduct">ShipmentProduct</h3>
<!-- backwards compatibility -->
<a id="schemashipmentproduct"></a>
<a id="schema_ShipmentProduct"></a>
<a id="tocSshipmentproduct"></a>
<a id="tocsshipmentproduct"></a>

A child of product representing a shipment offer that is related to a PhysicalProduct

#### Properties

all of [Product](#schemaproduct)

and

| Name  | Type   | Required | Restrictions | Description |
|-------|--------|----------|--------------|-------------|
| @type | string | false    | none         | none        |

<h3 id="tocS_Offer">Offer</h3>
<!-- backwards compatibility -->
<a id="schemaoffer"></a>
<a id="schema_Offer"></a>
<a id="tocSoffer"></a>
<a id="tocsoffer"></a>

A child of product representing an offer.

#### Properties

all of [Product](#schemaproduct)

and

| Name  | Type   | Required | Restrictions | Description |
|-------|--------|----------|--------------|-------------|
| @type | string | false    | none         | none        |

<h3 id="tocS_SimCard">SimCard</h3>
<!-- backwards compatibility -->
<a id="schemasimcard"></a>
<a id="schema_SimCard"></a>
<a id="tocSsimcard"></a>
<a id="tocssimcard"></a>

A child of product representing a SimCard.

#### Properties

all of [Product](#schemaproduct)

and

| Name  | Type   | Required | Restrictions | Description |
|-------|--------|----------|--------------|-------------|
| @type | string | false    | none         | none        |

<h3 id="tocS_MobileLine">MobileLine</h3>
<!-- backwards compatibility -->
<a id="schemamobileline"></a>
<a id="schema_MobileLine"></a>
<a id="tocSmobileline"></a>
<a id="tocsmobileline"></a>

A child of product representing a MobileLine.

#### Properties

all of [Product](#schemaproduct)

and

| Name  | Type   | Required | Restrictions | Description |
|-------|--------|----------|--------------|-------------|
| @type | string | false    | none         | none        |

<h3 id="tocS_Service">Service</h3>
<!-- backwards compatibility -->
<a id="schemaservice"></a>
<a id="schema_Service"></a>
<a id="tocSservice"></a>
<a id="tocsservice"></a>

A child of product representing a Service

#### Properties

all of [Product](#schemaproduct)

and

| Name  | Type   | Required | Restrictions | Description |
|-------|--------|----------|--------------|-------------|
| @type | string | false    | none         | none        |

<h3 id="tocS_ProductRefOrValue">ProductRefOrValue</h3>
<!-- backwards compatibility -->
<a id="schemaproductreforvalue"></a>
<a id="schema_ProductRefOrValue"></a>
<a id="tocSproductreforvalue"></a>
<a id="tocsproductreforvalue"></a>

A product to be created defined by value or existing defined by reference. The polymorphic attributes @type, @schemaLocation & @referredType are related to the product entity and not the RelatedProductRefOrValue class itself

#### Properties

oneOf - discriminator: Product.@type

| Name        | Type                      | Required | Restrictions | Description                                                                                                                                                     |
|-------------|---------------------------|----------|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| *anonymous* | [Product](#schemaproduct) | false    | none         | A product offering procured by a customer or other interested party playing a party role. A product is realized as one or more service(s) and / or resource(s). |

xor - discriminator: ProductRef.@type

| Name        | Type                            | Required | Restrictions | Description            |
|-------------|---------------------------------|----------|--------------|------------------------|
| *anonymous* | [ProductRef](#schemaproductref) | false    | none         | reference to a product |

<h3 id="tocS_ProductRelationship">ProductRelationship</h3>
<!-- backwards compatibility -->
<a id="schemaproductrelationship"></a>
<a id="schema_ProductRelationship"></a>
<a id="tocSproductrelationship"></a>
<a id="tocsproductrelationship"></a>

Linked products to the one instantiate, such as [bundled] if the product is a bundle and you want to describe the bundled products inside this bundle; [reliesOn] if the product needs another already owned product to rely on (e.g. an option on an already owned mobile access product) [targets] or [isTargeted] (depending on the way of expressing the link) for any other kind of links that may be useful

#### Properties

| Name             | Type                                          | Required | Restrictions | Description                                                                                                                                                                                                                      |
|------------------|-----------------------------------------------|----------|--------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| relationshipType | string                                        | true     | none         | Type of relation ship                                                                                                                                                                                                            |
| product          | [ProductRefOrValue](#schemaproductreforvalue) | true     | none         | A product to be created defined by value or existing defined by reference. The polymorphic attributes @type, @schemaLocation & @referredType are related to the product entity and not the RelatedProductRefOrValue class itself |

<h3 id="tocS_ProductSpecificationRef">ProductSpecificationRef</h3>
<!-- backwards compatibility -->
<a id="schemaproductspecificationref"></a>
<a id="schema_ProductSpecificationRef"></a>
<a id="tocSproductspecificationref"></a>
<a id="tocsproductspecificationref"></a>

Product specification reference: A ProductSpecification is a detailed description of a tangible or intangible object made available externally in the form of a ProductOffering to customers or other parties playing a party role.

#### Properties

| Name          | Type   | Required | Restrictions | Description                                                            |
|---------------|--------|----------|--------------|------------------------------------------------------------------------|
| id            | string | true     | none         | Unique identifier of a related entity.                                 |
| href          | string | false    | none         | Reference of the related entity.                                       |
| name          | string | false    | none         | Name of the related entity.                                            |
| version       | string | false    | none         | Version of the product specification                                   |
| @type         | string | true     | none         | When sub-classing, this defines the sub-class entity name              |
| @referredType | string | false    | none         | The actual type of the target instance when needed for disambiguation. |

<h3 id="tocS_ProductStatusType">ProductStatusType</h3>
<!-- backwards compatibility -->
<a id="schemaproductstatustype"></a>
<a id="schema_ProductStatusType"></a>
<a id="tocSproductstatustype"></a>
<a id="tocsproductstatustype"></a>

Possible values for the status of the product

##### Enumerated Values

| Value      |
|------------|
| Aborted    |
| Active     |
| Cancelled  |
| Created    |
| Terminated |
| Sold       |

<h3 id="tocS_ProductTerm">ProductTerm</h3>
<!-- backwards compatibility -->
<a id="schemaproductterm"></a>
<a id="schema_ProductTerm"></a>
<a id="tocSproductterm"></a>
<a id="tocsproductterm"></a>

Description of a productTerm linked to this product. This represent a commitment with a duration

#### Properties

| Name        | Type                            | Required | Restrictions | Description                                                                             |
|-------------|---------------------------------|----------|--------------|-----------------------------------------------------------------------------------------|
| description | string                          | false    | none         | Description of the productTerm                                                          |
| name        | string                          | false    | none         | Name of the productTerm                                                                 |
| duration    | [Quantity](#schemaquantity)     | false    | none         | An amount in a given unit                                                               |
| validFor    | [TimePeriod](#schematimeperiod) | false    | none         | A period of time, either as a deadline (endDateTime only) a startDateTime only, or both |

<h3 id="tocS_Quantity">Quantity</h3>
<!-- backwards compatibility -->
<a id="schemaquantity"></a>
<a id="schema_Quantity"></a>
<a id="tocSquantity"></a>
<a id="tocsquantity"></a>

An amount in a given unit

#### Properties

| Name   | Type          | Required | Restrictions | Description                   |
|--------|---------------|----------|--------------|-------------------------------|
| amount | number(float) | false    | none         | Numeric value in a given unit |
| units  | string        | false    | none         | Unit                          |

<h3 id="tocS_RelatedPlaceRefOrValue">RelatedPlaceRefOrValue</h3>
<!-- backwards compatibility -->
<a id="schemarelatedplacereforvalue"></a>
<a id="schema_RelatedPlaceRefOrValue"></a>
<a id="tocSrelatedplacereforvalue"></a>
<a id="tocsrelatedplacereforvalue"></a>

Related Entity reference. A related place defines a place described by reference or by value linked to a specific entity. The polymorphic attributes @type, @schemaLocation & @referredType are related to the place entity and not the RelatedPlaceRefOrValue class itself

**Inheritance**

Children:
- GeographicAddress

#### Properties

| Name          | Type   | Required | Restrictions | Description                                                                            |
|---------------|--------|----------|--------------|----------------------------------------------------------------------------------------|
| id            | string | false    | none         | Unique identifier of the place                                                         |
| href          | string | false    | none         | Unique reference of the place                                                          |
| name          | string | false    | none         | A user-friendly name for the place, such as [Paris Store], [London Store], [Main Home] |
| role          | string | true     | none         | role of the place                                                                      |
| @type         | string | false    | none         | When sub-classing, this defines the sub-class entity name                              |
| @referredType | string | false    | none         | The actual type of the target instance when needed for disambiguation.                 |

<h3 id="tocS_RelatedProductOrderItem">RelatedProductOrderItem</h3>
<!-- backwards compatibility -->
<a id="schemarelatedproductorderitem"></a>
<a id="schema_RelatedProductOrderItem"></a>
<a id="tocSrelatedproductorderitem"></a>
<a id="tocsrelatedproductorderitem"></a>

RelatedProductOrderItem (ProductOrder item) .The product order item which triggered product creation/change/termination.

#### Properties

| Name             | Type   | Required | Restrictions | Description                                                            |
|------------------|--------|----------|--------------|------------------------------------------------------------------------|
| orderItemAction  | string | false    | none         | Action of the order item for this product                              |
| orderItemId      | string | true     | none         | Identifier of the order item where the product was managed             |
| productOrderHref | string | false    | none         | Reference of the related entity.                                       |
| productOrderId   | string | true     | none         | Unique identifier of a related entity.                                 |
| role             | string | false    | none         | role of the product order item for this product                        |
| @referredType    | string | false    | none         | The actual type of the target instance when needed for disambiguation. |

<h3 id="tocS_ResourceRef">ResourceRef</h3>
<!-- backwards compatibility -->
<a id="schemaresourceref"></a>
<a id="schema_ResourceRef"></a>
<a id="tocSresourceref"></a>
<a id="tocsresourceref"></a>

Reference to a resource

#### Properties

| Name          | Type   | Required | Restrictions | Description                                                            |
|---------------|--------|----------|--------------|------------------------------------------------------------------------|
| id            | string | true     | none         | Unique identifier of a related entity.                                 |
| href          | string | false    | none         | Reference of the related entity.                                       |
| name          | string | false    | none         | Name of the resource                                                   |
| @type         | string | false    | none         | When sub-classing, this defines the sub-class entity name              |
| @referredType | string | false    | none         | The actual type of the target instance when needed for disambiguation. |

<h3 id="tocS_ServiceRef">ServiceRef</h3>
<!-- backwards compatibility -->
<a id="schemaserviceref"></a>
<a id="schema_ServiceRef"></a>
<a id="tocSserviceref"></a>
<a id="tocsserviceref"></a>

Service reference, for when Service is used by other entities

#### Properties

| Name          | Type   | Required | Restrictions | Description                                                            |
|---------------|--------|----------|--------------|------------------------------------------------------------------------|
| id            | string | true     | none         | Unique identifier of a related entity.                                 |
| href          | string | false    | none         | Reference of the related entity.                                       |
| name          | string | false    | none         | Name of the related entity.                                            |
| @type         | string | false    | none         | When sub-classing, this defines the sub-class entity name              |
| @referredType | string | false    | none         | The actual type of the target instance when needed for disambiguation. |

<h3 id="tocS_StatusChange">StatusChange</h3>
<!-- backwards compatibility -->
<a id="schemastatuschange"></a>
<a id="schema_StatusChange"></a>
<a id="tocSstatuschange"></a>
<a id="tocsstatuschange"></a>

represents a status change at some time

#### Properties

| Name          | Type                                          | Required | Restrictions | Description                                   |
|---------------|-----------------------------------------------|----------|--------------|-----------------------------------------------|
| changeDate    | string(date-time)                             | false    | none         | date that the change happened                 |
| changeReason  | string                                        | false    | none         | reason that the change happened               |
| status        | [ProductStatusType](#schemaproductstatustype) | false    | none         | Possible values for the status of the product |
| @type         | string                                        | false    | none         | type of StatusChange                          |
| @referredType | string                                        | false    | none         | referredType type of StatusChange             |

<h3 id="tocS_JobSpecificationStatusChange">JobSpecificationStatusChange</h3>
<!-- backwards compatibility -->
<a id="schemajobspecificationstatuschange"></a>
<a id="schema_JobSpecificationStatusChange"></a>
<a id="tocSjobspecificationstatuschange"></a>
<a id="tocsjobspecificationstatuschange"></a>

represents a status change at some time for JobSpecification

#### Properties

| Name            | Type                                                            | Required | Restrictions | Description                        |
|-----------------|-----------------------------------------------------------------|----------|--------------|------------------------------------|
| changeDate      | string(date-time)                                               | false    | none         | date that the change happened      |
| lifecycleStatus | [JobSpecificationStatusType](#schemajobspecificationstatustype) | false    | none         | Type JobSpecification Status Enum. |
| @type           | string                                                          | false    | none         | type of StatusChange               |
| @referredType   | string                                                          | false    | none         | referredType type of StatusChange  |

<h3 id="tocS_StringArrayCharacteristic">StringArrayCharacteristic</h3>
<!-- backwards compatibility -->
<a id="schemastringarraycharacteristic"></a>
<a id="schema_StringArrayCharacteristic"></a>
<a id="tocSstringarraycharacteristic"></a>
<a id="tocsstringarraycharacteristic"></a>

**Polymorphism**

Parent: Characteristic

Discriminator: @type

#### Properties

all of [Characteristic](#schemacharacteristic)

and

| Name  | Type     | Required | Restrictions | Description |
|-------|----------|----------|--------------|-------------|
| value | [string] | false    | none         | none        |

<h3 id="tocS_StringCharacteristic">StringCharacteristic</h3>
<!-- backwards compatibility -->
<a id="schemastringcharacteristic"></a>
<a id="schema_StringCharacteristic"></a>
<a id="tocSstringcharacteristic"></a>
<a id="tocsstringcharacteristic"></a>

**Polymorphism**

Parent: Characteristic

Discriminator: @type

#### Properties

all of [Characteristic](#schemacharacteristic)

and

| Name  | Type   | Required | Restrictions | Description |
|-------|--------|----------|--------------|-------------|
| value | string | false    | none         | none        |

<h3 id="tocS_TimePeriod">TimePeriod</h3>
<!-- backwards compatibility -->
<a id="schematimeperiod"></a>
<a id="schema_TimePeriod"></a>
<a id="tocStimeperiod"></a>
<a id="tocstimeperiod"></a>

A period of time, either as a deadline (endDateTime only) a startDateTime only, or both

#### Properties

| Name          | Type              | Required | Restrictions | Description                                                                                              |
|---------------|-------------------|----------|--------------|----------------------------------------------------------------------------------------------------------|
| endDateTime   | string(date-time) | false    | none         | End of the time period, using IETF-RFC-3339 format                                                       |
| startDateTime | string(date-time) | false    | none         | Start of the time period, using IETF-RFC-3339 format. If you define a start, you must also define an end |

<h3 id="tocS_DatePeriod">DatePeriod</h3>
<!-- backwards compatibility -->
<a id="schemadateperiod"></a>
<a id="schema_DatePeriod"></a>
<a id="tocSdateperiod"></a>
<a id="tocsdateperiod"></a>

A period of time, either as a deadline (endDateTime only) a startDateTime only, or both

#### Properties

| Name          | Type         | Required | Restrictions | Description              |
|---------------|--------------|----------|--------------|--------------------------|
| endDateTime   | string(date) | false    | none         | End of the time period   |
| startDateTime | string(date) | false    | none         | Start of the time period |

<h3 id="tocS_ServiceStatus">ServiceStatus</h3>
<!-- backwards compatibility -->
<a id="schemaservicestatus"></a>
<a id="schema_ServiceStatus"></a>
<a id="tocSservicestatus"></a>
<a id="tocsservicestatus"></a>

An object representing the response status of the service

#### Properties

| Name        | Type   | Required | Restrictions | Description                 |
|-------------|--------|----------|--------------|-----------------------------|
| serviceName | string | false    | none         | the Service Name            |
| version     | string | false    | none         | the Service version         |
| message     | string | false    | none         | the status response message |

<h3 id="tocS_ServiceVersion">ServiceVersion</h3>
<!-- backwards compatibility -->
<a id="schemaserviceversion"></a>
<a id="schema_ServiceVersion"></a>
<a id="tocSserviceversion"></a>
<a id="tocsserviceversion"></a>

An object containing the version of the  service

#### Properties

| Name    | Type   | Required | Restrictions | Description         |
|---------|--------|----------|--------------|---------------------|
| version | string | false    | none         | the Service version |

<h3 id="tocS_ExportFileInformation">ExportFileInformation</h3>
<!-- backwards compatibility -->
<a id="schemaexportfileinformation"></a>
<a id="schema_ExportFileInformation"></a>
<a id="tocSexportfileinformation"></a>
<a id="tocsexportfileinformation"></a>

An object containing the download link of an export file

#### Properties

| Name           | Type                                      | Required | Restrictions | Description                                                                             |
|----------------|-------------------------------------------|----------|--------------|-----------------------------------------------------------------------------------------|
| url            | string                                    | false    | none         | The download url                                                                        |
| completionDate | string(date-time)                         | false    | none         | The CompletionDate of the export                                                        |
| contentType    | [ContentTypeEnum](#schemacontenttypeenum) | false    | none         | Represents the content type of the export file.                                         |
| validFor       | [TimePeriod](#schematimeperiod)           | false    | none         | A period of time, either as a deadline (endDateTime only) a startDateTime only, or both |

<h3 id="tocS_RecurringChargePeriod">RecurringChargePeriod</h3>
<!-- backwards compatibility -->
<a id="schemarecurringchargeperiod"></a>
<a id="schema_RecurringChargePeriod"></a>
<a id="tocSrecurringchargeperiod"></a>
<a id="tocsrecurringchargeperiod"></a>

Could be month, week...

#### Properties

| Name   | Type          | Required | Restrictions | Description                   |
|--------|---------------|----------|--------------|-------------------------------|
| amount | number(float) | false    | none         | Numeric value in a given unit |
| units  | string        | false    | none         | Unit                          |

<h3 id="tocS_PurgeTypeEnum">PurgeTypeEnum</h3>
<!-- backwards compatibility -->
<a id="schemapurgetypeenum"></a>
<a id="schema_PurgeTypeEnum"></a>
<a id="tocSpurgetypeenum"></a>
<a id="tocspurgetypeenum"></a>

Represents the type of what to purge. Depending on the `purgeType` value, different parameters and values of `query` attribute are required:
- For `PurgeProduct`: At least one of the query parameters (`creationDate.lte`, `creationDate.gte`,`terminationDate.lte`, `terminationDate.gte`, `startDate.lte`, `startDate.gte`, `status`) must be provided, and the status values must be one of `Cancelled`, `Aborted`, `Terminated`.
- For `PurgeJob`: At least one of the query parameters (`creationDate.lte`, `creationDate.gte`, `status`) must be provided, and the status values must be one of `Done`, `TerminatedWithError`.

##### Enumerated Values

| Value        |
|--------------|
| PurgeProduct |
| PurgeJob     |

<h3 id="tocS_RelatedPartyOrPartyRole">RelatedPartyOrPartyRole</h3>
<!-- backwards compatibility -->
<a id="schemarelatedpartyorpartyrole"></a>
<a id="schema_RelatedPartyOrPartyRole"></a>
<a id="tocSrelatedpartyorpartyrole"></a>
<a id="tocsrelatedpartyorpartyrole"></a>

RelatedParty reference. A related party defines party or party role or its reference,linked to a specific entity

#### Properties

| Name             | Type                                        | Required | Restrictions | Description                                                                                                                                                  |
|------------------|---------------------------------------------|----------|--------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|
| @type            | string                                      | false    | none         | none                                                                                                                                                         |
| role             | string(string)                              | false    | none         | Role played by the related party or party role in the context of the specific entity it is linked to. Such as 'initiator', 'customer',  'salesAgent', 'user' |
| partyOrPartyRole | [PartyOrPartyRole](#schemapartyorpartyrole) | false    | none         | none                                                                                                                                                         |

<h3 id="tocS_PartyOrPartyRole">PartyOrPartyRole</h3>
<!-- backwards compatibility -->
<a id="schemapartyorpartyrole"></a>
<a id="schema_PartyOrPartyRole"></a>
<a id="tocSpartyorpartyrole"></a>
<a id="tocspartyorpartyrole"></a>

#### Properties

oneOf - discriminator: PartyRef.@type

| Name        | Type                        | Required | Restrictions | Description       |
|-------------|-----------------------------|----------|--------------|-------------------|
| *anonymous* | [PartyRef](#schemapartyref) | false    | none         | A Party reference |

xor - discriminator: PartyRoleRef.@type

| Name        | Type                                | Required | Restrictions | Description                                                                                  |
|-------------|-------------------------------------|----------|--------------|----------------------------------------------------------------------------------------------|
| *anonymous* | [PartyRoleRef](#schemapartyroleref) | false    | none         | Party role reference. A party role represents the part played by a party in a given context. |

<h3 id="tocS_PartyRef">PartyRef</h3>
<!-- backwards compatibility -->
<a id="schemapartyref"></a>
<a id="schema_PartyRef"></a>
<a id="tocSpartyref"></a>
<a id="tocspartyref"></a>

A Party reference

#### Properties

| Name          | Type   | Required | Restrictions | Description                                                            |
|---------------|--------|----------|--------------|------------------------------------------------------------------------|
| @type         | string | false    | none         | none                                                                   |
| id            | string | false    | none         | The identifier of the PartyRef.                                        |
| href          | string | false    | none         | The URI of the PartyRef.                                               |
| name          | string | false    | none         | Name of the PartyRef.                                                  |
| @referredType | string | false    | none         | The actual type of the target instance when needed for disambiguation. |

<h3 id="tocS_PartyRoleRef">PartyRoleRef</h3>
<!-- backwards compatibility -->
<a id="schemapartyroleref"></a>
<a id="schema_PartyRoleRef"></a>
<a id="tocSpartyroleref"></a>
<a id="tocspartyroleref"></a>

Party role reference. A party role represents the part played by a party in a given context.

#### Properties

| Name          | Type   | Required | Restrictions | Description                                                                 |
|---------------|--------|----------|--------------|-----------------------------------------------------------------------------|
| @type         | string | false    | none         | none                                                                        |
| id            | string | false    | none         | The identifier of the PartyRef.                                             |
| href          | string | false    | none         | The URI of the PartyRef.                                                    |
| name          | string | false    | none         | Name of the PartyRef.                                                       |
| @referredType | string | false    | none         | The actual type of the target instance when needed for disambiguation.      |
| partyId       | string | false    | none         | The identifier of the engaged party that is linked to the PartyRole object. |
| partyName     | string | false    | none         | The name of the engaged party that is linked to the PartyRole object.       |

<h3 id="tocS_Report">Report</h3>
<!-- backwards compatibility -->
<a id="schemareport"></a>
<a id="schema_Report"></a>
<a id="tocSreport"></a>
<a id="tocsreport"></a>

A generic report entity that can represent various types of reports.

#### Properties

| Name        | Type                                    | Required | Restrictions | Description                                          |
|-------------|-----------------------------------------|----------|--------------|------------------------------------------------------|
| id          | string                                  | false    | none         | Unique identifier of the report.                     |
| href        | string                                  | false    | none         | Reference URL of the report.                         |
| @type       | string                                  | false    | none         | Defines the sub-class entity name when sub-classing. |
| collectDate | string(date)                            | false    | none         | The date for which the report is generated.          |
| details     | [[ReportDetails](#schemareportdetails)] | false    | none         | [the report details.]                                |

<h3 id="tocS_ReportDetails">ReportDetails</h3>
<!-- backwards compatibility -->
<a id="schemareportdetails"></a>
<a id="schema_ReportDetails"></a>
<a id="tocSreportdetails"></a>
<a id="tocsreportdetails"></a>

the report details.

#### Properties

| Name           | Type                                                  | Required | Restrictions | Description                               |
|----------------|-------------------------------------------------------|----------|--------------|-------------------------------------------|
| count          | integer(int64)                                        | false    | none         | The count of the specific characteristic. |
| characteristic | [[ReportCharacteristic](#schemareportcharacteristic)] | false    | none         | [A name value report characteristic.]     |

<h3 id="tocS_ReportCharacteristic">ReportCharacteristic</h3>
<!-- backwards compatibility -->
<a id="schemareportcharacteristic"></a>
<a id="schema_ReportCharacteristic"></a>
<a id="tocSreportcharacteristic"></a>
<a id="tocsreportcharacteristic"></a>

A name value report characteristic.

#### Properties

| Name  | Type   | Required | Restrictions | Description |
|-------|--------|----------|--------------|-------------|
| name  | string | false    | none         | none        |
| value | string | false    | none         | none        |

<h3 id="tocS_ReportProductByStatus">ReportProductByStatus</h3>
<!-- backwards compatibility -->
<a id="schemareportproductbystatus"></a>
<a id="schema_ReportProductByStatus"></a>
<a id="tocSreportproductbystatus"></a>
<a id="tocsreportproductbystatus"></a>

the status report type.

#### Properties

| Name  | Type   | Required | Restrictions | Description                                          |
|-------|--------|----------|--------------|------------------------------------------------------|
| @type | string | false    | none         | Defines the sub-class entity name when sub-classing. |

<h3 id="tocS_ReportGranularity">ReportGranularity</h3>
<!-- backwards compatibility -->
<a id="schemareportgranularity"></a>
<a id="schema_ReportGranularity"></a>
<a id="tocSreportgranularity"></a>
<a id="tocsreportgranularity"></a>

The granularity of the report data (day, week, month, year)

##### Enumerated Values

| Value |
|-------|
| Day   |
| Week  |
| Month |
| Year  |

<h3 id="tocS_ReportType">ReportType</h3>
<!-- backwards compatibility -->
<a id="schemareporttype"></a>
<a id="schema_ReportType"></a>
<a id="tocSreporttype"></a>
<a id="tocsreporttype"></a>

The type of the report data (StatusReport,...)

##### Enumerated Values

| Value                 |
|-----------------------|
| ReportProductByStatus |
| ReportProductByOffer  |

### History of Document
| Version of the document | modification date | description of modifications  |
|:------------------------|:------------------|:------------------------------|
| 1.0                     | 04/10/2023        | initialization                |
| 1.1                     | 04/12/2023        | improving patch documentation |
| 2.0                     | 09/06/2024        | adding task api               |
| 3.0                     | 30/01/2025        | reflect changes from swagger  |
