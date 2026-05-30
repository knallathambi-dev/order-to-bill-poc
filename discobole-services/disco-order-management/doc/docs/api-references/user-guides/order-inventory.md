<!--
SPDX-FileCopyrightText: 2025 - 2026 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Getting Started

## Introduction

The Product Ordering API provides a standardized mechanism for placing a product order with all the
necessary order parameters. The API consists of a simple set of operations that interact with CRM/Order
Negotiation systems in a consistent manner. A product order is created based on a product offer that is defined
in a catalog. The product offer identifies the product or set of products that are available to a customer, and
includes characteristics such as pricing, product options and market.

## API Description

### Summary of Resources

This API has one resource **ProductOrder**.

| Resource                                    | Definitions                                                                                                                                                                                                                             |
|:--------------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ProductOrder                                | A ProductOrder refers to a formal structure that defines the details of a product order, including the products or services requested, their configuration, pricing, and all other necessary elements required to fulfill the order.    |

### Summary of methods and URL

| Use case using the method                                                      | Method | URL                                                                                                                                                          |
|:-------------------------------------------------------------------------------|:-------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| I would like to **get all product order**                                      | GET    | [http://{{baseURL}}/productOrderingManagement/{{version}}/productOrder](http://{{baseURL}}/productOrderingManagement/{{version}}/productOrder)               |
| I would like to **get all information of a product order from its identifier** | GET    | [http://{{baseURL}}/productOrderingManagement/{{version}}/productOrder/{{id}}](http://{{baseURL}}/productOrderingManagement/{{version}}/productOrder/{{id}}) |
| I would like to **create product order**	                                      | POST   | [http://{{baseURL}}/productOrderingManagement/{{version}}/productOrder](http://{{baseURL}}/productOrderingManagement/{{version}}/productOrder)               |

### Authentication

In case the security is enabled, bearer token is used for authentication and authorization. Our API is a resource server based on OAuth2.

For more details, please consult [User Role & Permission management component](https://discobole.ow2.io/disco-oda-components/disco-security/doc/api-references/specifications/user-role-and-permissions/).

### Get product order information

#### Parameters

| Name                | Description                                                                     | Type          | Mandatory |
|---------------------|---------------------------------------------------------------------------------|---------------|-----------|
| offset              | Requested index for start of resources to be provided in response               | Integer       | No        |
| limit               | Requested number of resources to be provided in response                        | Integer       | No        |
| creationDate        | Date when the order was created                                                 | Date          | No        |
| id                  | Identifier of the product order                                                 | Array[string] | No        |
| notificationContact | Contact attached to the order to send back information regarding this order     | String        | No        |
| category            | Used to categorize the order from a business perspective                        | String        | No        |
| relatedParty.id     | Unique identifier of a related entity                                           | String        | No        |
| relatedParty.name   | Name of a related entity                                                        | String        | No        |
| relatedParty.role   | Role of a related entity                                                        | String        | No        |
| channel.name        | Channel identifier                                                              | String        | No        |
| state               | Status of the product order                                                     | String        | No        |
| cancellationDate    | Date when the order is cancelled                                                | Date          | No        |
| creationDate.gt     | OrderDate greater than value provided which could be date or date time          | Date          | No        |
| creationDate.gte    | OrderDate greater than or equal value provided which could be date or date time | Date          | No        |
| creationDate.lt     | OrderDate less than value provided which could be date or date time             | Date          | No        |
| creationDate.lte    | OrderDate less than or equal value provided which could be date or date time    | Date          | No        |

#### Request (get the first element)

```shell
curl -X GET "{{baseURL}}/productOrderingManagement/{{version}}/productOrder&offset=0&limit=1" -H "accept: application/json;charset=utf-8"
```

#### Response

```json
206 PARTIAL
Content-Type: application/json
X-Total-Count: 1
X-Result-Count: 1

[
  {
    "id": "66d45a16cc34205e0f04aacc",
    "href": "{{baseURL}}/productOrderingManagement/v1/productOrder/66d45a16cc34205e0f04aacc",
    "orderDate": "2024-09-01T12:12:06.106Z",
    "orderTotalPrice": [
      {
        "priceType": "NRC",
        "price": {
          "dutyFreeAmount": {
            "unit": "EUR",
            "value": 5
          },
          "taxIncludedAmount": {
            "value": 0
          }
        }
      }
    ],
    "productOrderItem": [
      {
        "id": "24fa0d5c-e014-4624-b2dc-dec0fe081d4b",
        "quantity": 1,
        "action": "add",
        "itemPrice": [
          {
            "description": "MobileLine",
            "name": "NRC 5 Euro",
            "priceType": "NRC",
            "recurringChargePeriod": {
              "amount": 0
            },
            "unitOfMeasure": "units",
            "price": {
              "dutyFreeAmount": {
                "unit": "EUR",
                "value": 5
              }
            },
            "productOfferingPrice": {
              "id": "b1a14825-ffa7-4a27-841f-16f798650de3",
              "name": "NRC 5 Euro",
              "@type": "productOfferingPrice",
              "@referredType": "ProductOfferingPriceCharge"
            }
          }
        ],
        "payment": [
          {
            "id": "1294af-483a-4071-1a04-b6fcfe4ee455",
            "@referredType": "Payment"
          }
        ],
        "product": {
          "id": "66d45a2f1a08816ae91b7a1f",
          "href": "{{baseURL}}/v1/product/66d45a2f1a08816ae91b7a1f",
          "productSpecification": {
            "id": "9a2fb547-92ae-4fd0-afa7-6418e27d51cb",
            "name": "Mobile Line",
            "@type": "ProductSpecification"
          },
          "@type": "ProductRef"
        },
        "productOffering": {
          "id": "39d17453-6b6c-4902-9743-a50ec9ad5e67",
          "name": "Mobile Line",
          "@type": "AtomicProductOffering",
          "@referredType": "ProductOfferingRef"
        },
        "productOrderItemRelationship": [
          {
            "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
            "relationshipType": "isChild"
          },
          {
            "id": "ee1aef26-a601-442b-9852-1966f6f0a545",
            "relationshipType": "reliesOn"
          }
        ],
        "state": "completed",
        "@type": "ProductOrderItem",
        "isInstallable": true
      },
      {
        "id": "3d70cc60-f417-4811-9991-d6ed7534e63d",
        "quantity": 1,
        "action": "add",
        "product": {
          "id": "66d45a2f1a08816ae91b7a1c",
          "href": "{{baseURL}}/v1/product/66d45a2f1a08816ae91b7a1c",
          "@type": "ProductRef"
        },
        "productOffering": {
          "id": "030520ea-6953-4e3d-90db-87b3887f87c0",
          "name": "Mobile Package Comfort",
          "@type": "Contract",
          "@referredType": "ProductOfferingRef"
        },
        "productOrderItemRelationship": [
          {
            "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
            "relationshipType": "bundles"
          }
        ],
        "state": "completed",
        "@type": "ProductOrderItem",
        "isInstallable": true
      },
      {
        "id": "ee1aef26-a601-442b-9852-1966f6f0a545",
        "quantity": 1,
        "action": "add",
        "product": {
          "id": "66d45a2f1a08816ae91b7a23",
          "href": "{{baseURL}}/v1/product/66d45a2f1a08816ae91b7a23",
          "productCharacteristic": [
            {
              "name": "IMSI",
              "value": "IMSI#123",
              "id": "1"
            }
          ],
          "productSpecification": {
            "id": "191424cb-6002-46aa-a5a9-0602cfba83c7",
            "name": "SIM card",
            "@type": "ProductSpecification"
          },
          "@type": "ProductRef"
        },
        "productOffering": {
          "id": "73effd22-3bb1-4a35-8dbd-323d8cc46772",
          "name": "SIM Card",
          "@type": "AtomicProductOffering",
          "@referredType": "ProductOfferingRef"
        },
        "productOrderItemRelationship": [
          {
            "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
            "relationshipType": "isChild"
          }
        ],
        "state": "completed",
        "@type": "ProductOrderItem",
        "isInstallable": true
      },
      {
        "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
        "quantity": 1,
        "action": "add",
        "product": {
          "id": "66d45a2f1a08816ae91b7a1d",
          "href": "{{baseURL}}/v1/product/66d45a2f1a08816ae91b7a1d",
          "@type": "ProductRef"
        },
        "productOffering": {
          "id": "aafbd483-1fde-4297-aea5-f24b88e1a905",
          "name": "Mobile Package Comfort Bundle",
          "@type": "BundleProductOffering",
          "@referredType": "ProductOfferingRef"
        },
        "productOrderItemRelationship": [
          {
            "id": "24fa0d5c-e014-4624-b2dc-dec0fe081d4b",
            "relationshipType": "bundles"
          },
          {
            "id": "ee1aef26-a601-442b-9852-1966f6f0a545",
            "relationshipType": "bundles"
          },
          {
            "id": "3d70cc60-f417-4811-9991-d6ed7534e63d",
            "relationshipType": "isChild"
          }
        ],
        "state": "completed",
        "@type": "ProductOrderItem",
        "isInstallable": true
      }
    ],
    "relatedParty": [
      {
        "id": "231-mf20",
        "name": "Homer",
        "role": "customer",
        "@referredType": "referredType"
      }
    ],
    "state": "completed",
    "@type": "ProductOrder"
  }
]

```

#### Fields description

ProductOrder Model

| Field name       | Type                                                                       | Description                                                                                                                          |
|------------------|----------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| id               | String                                                                     | Identifier of a product order                                                                                                        |
| href             | String                                                                     | Hyperlink to access the order                                                                                                        |
| category         | String                                                                     | Used to categorize the order from a business perspective that can be usefulfor the OM system (e.g. "enterprise", "residential", ...) |
| channel          | RelatedChannel [*]                                                         | Related channel to another entity                                                                                                    |
| orderDate        | DateTime                                                                   | Date when the order was created                                                                                                      |
| orderTotalPrice  | OrderPrice [*]                                                             | An amount, usually of money, that represents the actual price paid by the Customer for this item or this order                       |
| productOrderItem | ProductOrderItem [1..*]                                                    | An identified part of the order. A product order is decomposed into one or more order items                                          |
| relatedParty     | RelatedParty [*]                                                           | A related party defines party or party role linked to a specific entity                                                              |
| state            | Enum[accepted,acknowledged,draft,completed,failed,held,inProgress,partial] | Tracks the lifecycle status of the product order                                                                                     |


RelatedChannel model

| Field name    | Type    | Description                                                           |
|---------------|---------|-----------------------------------------------------------------------|
| id            | String  | Identifier of a related channel                                       |
| name          | String  | Name of the channel                                                   |
| role          | String  | Role playing by the channel                                           |
| @referredType | String  | The actual type of the target instance when needed for disambiguation |

OrderPrice model

| Field name              | Type                    | Description                                                                                                                                                    |
|-------------------------|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| billingAccount          | BillingAccountRef       | A reference to a billing account used for paid the order price charge                                                                                          | 
| name                    | String                  | A short descriptive name such as "Subscription price"                                                                                                          |
| price                   | Price                   | A structure used to define price amount                                                                                                                        |
| priceAlteration         | PriceAlteration [*]     | A structure used to describe a price alteration                                                                                                                |
| priceType               | String                  | A category that describes the price, such as recurring, discount, allowance, penalty, and so forth.                                                            |
| productOfferingPrice    | ProductOfferingPriceRef | An amount, usually of money, that is asked for or allowed when a ProductOffering is bought, rented, or leased. The price is valid for a defined period of time |
| recurringChargePeriod   | String                  | Could be month, week...                                                                                                                                        |
| unitOfMeasure           | String                  | Could be minutes, GB...                                                                                                                                        |

PriceAlteration model

| Field name              | Type                    | Description                                                                                                                                                    |
|-------------------------|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| name                    | String                  | Name of the order item price alteration                                                                                                                        |
| price                   | Price                   | Provides all amounts (tax included, duty free, tax rate), used currency and percentage to apply for Price Alteration                                           |
| priceType               | String                  | A category that describes the price such as recurring, one time and usage                                                                                      |
| priority                | Integer                 | Priority level for applying this alteration among all the defined alterations on the order item price                                                          |
| productOfferingPrice    | ProductOfferingPriceRef | An amount, usually of money, that is asked for or allowed when a ProductOffering is bought, rented, or leased. The price is valid for a defined period of time |
| recurringChargePeriod   | String                  | Could be month, week...                                                                                                                                        |
| unitOfMeasure           | String                  | Could be minutes, GB...                                                                                                                                        |


ProductOfferingPriceRef model

| Field name    | Type     | Description                                                           |
|---------------|----------|-----------------------------------------------------------------------|
| id            | String   | Unique identifier of a related entity                                 |
| name          | String   | Name of the related entity                                            |
| href          | String   | Reference of the related entity                                       |
| @referredType | String   | The actual type of the target instance when needed for disambiguation |

ProductOrderItem model

| Field name                   | Type                                                                       | Description                                                                                                                                          |
|------------------------------|----------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| id                           | String                                                                     | Identifier of the item                                                                                                                               |
| action                       | Enum[ add, modify, delete]                                                 | The action to be carried out on the Product                                                                                                          |
| billingAccount               | BillingAccountRef                                                          | A BillingAccount is a detailed description of a bill structure                                                                                       | 
| itemPrice                    | OrderPrice [*]                                                             | An amount, usually of money, that represents the actual price paid by the Customer for this item or this order                                       |
| payment                      | PaymentRef [*]                                                             | If an immediate payment has been done at the product order submission, the payment information are captured and stored (as a reference) in the order |
| product                      | ProductRefOrValue                                                          | A product to be created defined by value or existing defined by reference                                                                            |
| productOffering              | ProductOfferingRef                                                         | A product offering represents entities that are orderable from the provider of the catalog                                                           |
| productOrderItemRelationship | OrderItemRelationship [*]                                                  | A list of order item relationships                                                                                                                   |
| quantity                     | Integer                                                                    | Quantity ordered                                                                                                                                     |
| state                        | Enum[accepted,acknowledged,draft,completed,failed,held,inProgress,partial] | Tracks the lifecycle status of the product order                                                                                                     |

BillingAccountRef model

| Field name    | Type     | Description                                                           |
|---------------|----------|-----------------------------------------------------------------------|
| id            | String   | Unique identifier of the billing account                              |
| name          | String   | Name of the billing account                                           |
| href          | String   | Reference of the billing account                                      |
| @referredType | String   | The actual type of the target instance when needed for disambiguation |

PaymentRef model

| Field name    | Type     | Description                                                           |
|---------------|----------|-----------------------------------------------------------------------|
| id            | String   | Unique identifier of a related entity                                 |
| name          | String   | A name for the payment                                                |
| href          | String   | Reference of the related entity                                       |
| @referredType | String   | The actual type of the target instance when needed for disambiguation |

ProductRefOrValue model

| Field name            | Type                    | Description                                                                                                                                                                                     |
|-----------------------|-------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| id                    | String                  | Unique identifier of the product                                                                                                                                                                |
| name                  | String                  | Name of the product                                                                                                                                                                             |
| href                  | String                  | Reference of the product                                                                                                                                                                        |
| isBundle              | Boolean                 | If true, the product is a ProductBundle which is an instantiation of a BundledProductOffering. If false, the product is a ProductComponent which is an instantiation of a SimpleProductOffering |
| productSerialNumber   | String                  | The serial number for the product                                                                                                                                                               |
| productCharacteristic | Characteristic [*]      | Describes a given characteristic of anobject or entity through a name/value pair                                                                                                                |
| productOffering       | ProductOfferingRef      | A product offering represents entities that are orderable from the provider of the catalog                                                                                                      |
| productRelationship   | ProductRelationship [*] | Linked products to the one instantiate                                                                                                                                                          |
| productSpecification  | ProductSpecificationRef | A ProductSpecification is a detailed description of a tangible or intangible object                                                                                                             |
| realizingResource     | ResourceRef [*]         | A list of resource references                                                                                                                                                                   |
| realizingService      | ServiceRef [*]          | A list of service references                                                                                                                                                                    |

Characteristic model

| Field name | Type   | Description                                   |
|------------|--------|-----------------------------------------------|
| id         | String | Unique identifier of the characteristic       |
| name       | String | Name of the characteristic                    |
| value      | Object | The value of the characteristic               |
| valueType  | String | Data type of the value of the characteristic  |

ProductOfferingRef model

| Field name    | Type     | Description                                                           |
|---------------|----------|-----------------------------------------------------------------------|
| id            | String   | Unique identifier of a related entity                                 |
| name          | String   | Name of the related entity                                            |
| href          | String   | Reference of the related entity                                       |
| @referredType | String   | The actual type of the target instance when needed for disambiguation |

ProductRelationship model

| Field name         | Type               | Description                                                               |
|--------------------|--------------------|---------------------------------------------------------------------------|
| product            | ProductRefOrValue  | A product to be created defined by value or existing defined by reference |
| relationshipType   | String             | Type of the product relationship                                          |

ProductSpecificationRef model

| Field name    | Type     | Description                                                           |
|---------------|----------|-----------------------------------------------------------------------|
| id            | String   | Unique identifier of a related entity                                 |
| name          | String   | Name of the related entity                                            |
| href          | String   | Reference of the related entity                                       |
| @referredType | String   | The actual type of the target instance when needed for disambiguation |

ResourceRef model

| Field name    | Type     | Description                                                                   |
|---------------|----------|-------------------------------------------------------------------------------|
| id            | String   | Unique identifier of a related entity                                         |
| name          | String   | Name of the resource                                                          |
| value         | String   | The resource value that can be used to identify a resource with a public key  |
| href          | String   | Reference of the related entity                                               |
| @referredType | String   | The actual type of the target instance when needed for disambiguation         |

ServiceRef model

| Field name    | Type     | Description                                                           |
|---------------|----------|-----------------------------------------------------------------------|
| id            | String   | Unique identifier of a related entity                                 |
| name          | String   | Name of the related entity                                            |
| href          | String   | Reference of the related entity                                       |
| @referredType | String   | The actual type of the target instance when needed for disambiguation |

OrderItemRelationship model

| Field name        | Type     | Description                           |
|-------------------|----------|---------------------------------------|
| id                | String   | Unique identifier of a related entity |
| relationshipType  | String   | The type of order item relationship   |

RelatedParty model

| Field name    | Type     | Description                                                           |
|---------------|----------|-----------------------------------------------------------------------|
| id            | String   | Unique identifier of a related entity                                 |
| name          | String   | Name of the related entity                                            |
| role          | String   | Role playing by the related party                                     |
| @referredType | String   | The actual type of the target instance when needed for disambiguation |



### Get product order information by id

#### Parameters

| Name | Description                        | Type   | Mandatory |
|------|------------------------------------|--------|-----------|
| id   | Identifier of the product order    | String | Yes       |

#### Request

```shell
curl -X GET "{{baseURL}}/productOrderingManagement/{{version}}/productOrder/66d45a16cc34205e0f04aacc" -H "accept: application/json;charset=utf-8"
```

#### Response

```json
200 OK
Content-Type: application/json
  
{
  "id": "66d45a16cc34205e0f04aacc",
  "href": "{{baseURL}}/productOrderingManagement/v1/productOrder/66d45a16cc34205e0f04aacc",
  "orderDate": "2024-09-01T12:12:06.106Z",
  "orderTotalPrice": [
    {
      "priceType": "NRC",
      "price": {
        "dutyFreeAmount": {
          "unit": "EUR",
          "value": 5
        },
        "taxIncludedAmount": {
          "value": 0
        }
      }
    }
  ],
  "productOrderItem": [
    {
      "id": "24fa0d5c-e014-4624-b2dc-dec0fe081d4b",
      "quantity": 1,
      "action": "add",
      "itemPrice": [
        {
          "description": "MobileLine",
          "name": "NRC 5 Euro",
          "priceType": "NRC",
          "recurringChargePeriod": {
            "amount": 0
          },
          "unitOfMeasure": "units",
          "price": {
            "dutyFreeAmount": {
              "unit": "EUR",
              "value": 5
            }
          },
          "productOfferingPrice": {
            "id": "b1a14825-ffa7-4a27-841f-16f798650de3",
            "name": "NRC 5 Euro",
            "@type": "productOfferingPrice",
            "@referredType": "ProductOfferingPriceCharge"
          }
        }
      ],
      "payment": [
        {
          "id": "1294af-483a-4071-1a04-b6fcfe4ee455",
          "@referredType": "Payment"
        }
      ],
      "product": {
        "id": "66d45a2f1a08816ae91b7a1f",
        "href": "{{baseURL}}/productManagement/v1/product/66d45a2f1a08816ae91b7a1f",
        "productSpecification": {
          "id": "9a2fb547-92ae-4fd0-afa7-6418e27d51cb",
          "name": "Mobile Line",
          "@type": "ProductSpecification"
        },
        "@type": "ProductRef"
      },
      "productOffering": {
        "id": "39d17453-6b6c-4902-9743-a50ec9ad5e67",
        "name": "Mobile Line",
        "@type": "AtomicProductOffering",
        "@referredType": "ProductOfferingRef"
      },
      "productOrderItemRelationship": [
        {
          "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
          "relationshipType": "isChild"
        },
        {
          "id": "ee1aef26-a601-442b-9852-1966f6f0a545",
          "relationshipType": "reliesOn"
        }
      ],
      "state": "completed",
      "@type": "ProductOrderItem",
      "isInstallable": true
    },
    {
      "id": "3d70cc60-f417-4811-9991-d6ed7534e63d",
      "quantity": 1,
      "action": "add",
      "product": {
        "id": "66d45a2f1a08816ae91b7a1c",
        "href": "{{baseURL}}/productManagement/v1/product/66d45a2f1a08816ae91b7a1c",
        "@type": "ProductRef"
      },
      "productOffering": {
        "id": "030520ea-6953-4e3d-90db-87b3887f87c0",
        "name": "Mobile Package Comfort",
        "@type": "Contract",
        "@referredType": "ProductOfferingRef"
      },
      "productOrderItemRelationship": [
        {
          "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
          "relationshipType": "bundles"
        }
      ],
      "state": "completed",
      "@type": "ProductOrderItem",
      "isInstallable": true
    },
    {
      "id": "ee1aef26-a601-442b-9852-1966f6f0a545",
      "quantity": 1,
      "action": "add",
      "product": {
        "id": "66d45a2f1a08816ae91b7a23",
        "href": "{{baseURL}}/productManagement/v1/product/66d45a2f1a08816ae91b7a23",
        "productCharacteristic": [
          {
            "name": "IMSI",
            "value": "IMSI#123",
            "id": "1"
          }
        ],
        "productSpecification": {
          "id": "191424cb-6002-46aa-a5a9-0602cfba83c7",
          "name": "SIM card",
          "@type": "ProductSpecification"
        },
        "@type": "ProductRef"
      },
      "productOffering": {
        "id": "73effd22-3bb1-4a35-8dbd-323d8cc46772",
        "name": "SIM Card",
        "@type": "AtomicProductOffering",
        "@referredType": "ProductOfferingRef"
      },
      "productOrderItemRelationship": [
        {
          "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
          "relationshipType": "isChild"
        }
      ],
      "state": "completed",
      "@type": "ProductOrderItem",
      "isInstallable": true
    },
    {
      "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
      "quantity": 1,
      "action": "add",
      "product": {
        "id": "66d45a2f1a08816ae91b7a1d",
        "href": "{{baseURL}}/productManagement/v1/product/66d45a2f1a08816ae91b7a1d",
        "@type": "ProductRef"
      },
      "productOffering": {
        "id": "aafbd483-1fde-4297-aea5-f24b88e1a905",
        "name": "Mobile Package Comfort Bundle",
        "@type": "BundleProductOffering",
        "@referredType": "ProductOfferingRef"
      },
      "productOrderItemRelationship": [
        {
          "id": "24fa0d5c-e014-4624-b2dc-dec0fe081d4b",
          "relationshipType": "bundles"
        },
        {
          "id": "ee1aef26-a601-442b-9852-1966f6f0a545",
          "relationshipType": "bundles"
        },
        {
          "id": "3d70cc60-f417-4811-9991-d6ed7534e63d",
          "relationshipType": "isChild"
        }
      ],
      "state": "completed",
      "@type": "ProductOrderItem",
      "isInstallable": true
    }
  ],
  "relatedParty": [
    {
      "id": "231-mf20",
      "name": "Homer",
      "role": "customer",
      "@referredType": "referredType"
    }
  ],
  "state": "completed",
  "@type": "ProductOrder"
}

```

#### Fields description
Same ProductOrder model described before.

### Create product order

#### Parameters(body)
Same ProductOrder model described before without id field and orderDate.

#### Request

```shell
curl -X POST "{{baseURL}}/productOrderingManagement/{{version}}/productOrder" -H "accept: application/json;charset=utf-8"
```
#### Response

```json
201 OK
Content-Type: application/json
  
{
  "id": "66d45a16cc34205e0f04aacc",
  "href": "{{baseURL}}/productOrderingManagement/v1/productOrder/66d45a16cc34205e0f04aacc",
  "orderDate": "2024-09-01T12:12:06.106Z",
  "orderTotalPrice": [
    {
      "priceType": "NRC",
      "price": {
        "dutyFreeAmount": {
          "unit": "EUR",
          "value": 5
        },
        "taxIncludedAmount": {
          "value": 0
        }
      }
    }
  ],
  "productOrderItem": [
    {
      "id": "24fa0d5c-e014-4624-b2dc-dec0fe081d4b",
      "quantity": 1,
      "action": "add",
      "itemPrice": [
        {
          "description": "MobileLine",
          "name": "NRC 5 Euro",
          "priceType": "NRC",
          "recurringChargePeriod": {
            "amount": 0
          },
          "unitOfMeasure": "units",
          "price": {
            "dutyFreeAmount": {
              "unit": "EUR",
              "value": 5
            }
          },
          "productOfferingPrice": {
            "id": "b1a14825-ffa7-4a27-841f-16f798650de3",
            "name": "NRC 5 Euro",
            "@type": "productOfferingPrice",
            "@referredType": "ProductOfferingPriceCharge"
          }
        }
      ],
      "payment": [
        {
          "id": "1294af-483a-4071-1a04-b6fcfe4ee455",
          "@referredType": "Payment"
        }
      ],
      "product": {
        "id": "66d45a2f1a08816ae91b7a1f",
        "href": "{{baseURL}}/productManagement/v1/product/66d45a2f1a08816ae91b7a1f",
        "productSpecification": {
          "id": "9a2fb547-92ae-4fd0-afa7-6418e27d51cb",
          "name": "Mobile Line",
          "@type": "ProductSpecification"
        },
        "@type": "ProductRef"
      },
      "productOffering": {
        "id": "39d17453-6b6c-4902-9743-a50ec9ad5e67",
        "name": "Mobile Line",
        "@type": "AtomicProductOffering",
        "@referredType": "ProductOfferingRef"
      },
      "productOrderItemRelationship": [
        {
          "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
          "relationshipType": "isChild"
        },
        {
          "id": "ee1aef26-a601-442b-9852-1966f6f0a545",
          "relationshipType": "reliesOn"
        }
      ],
      "state": "completed",
      "@type": "ProductOrderItem",
      "isInstallable": true
    },
    {
      "id": "3d70cc60-f417-4811-9991-d6ed7534e63d",
      "quantity": 1,
      "action": "add",
      "product": {
        "id": "66d45a2f1a08816ae91b7a1c",
        "href": "{{baseURL}}/productManagement/v1/product/66d45a2f1a08816ae91b7a1c",
        "@type": "ProductRef"
      },
      "productOffering": {
        "id": "030520ea-6953-4e3d-90db-87b3887f87c0",
        "name": "Mobile Package Comfort",
        "@type": "Contract",
        "@referredType": "ProductOfferingRef"
      },
      "productOrderItemRelationship": [
        {
          "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
          "relationshipType": "bundles"
        }
      ],
      "state": "completed",
      "@type": "ProductOrderItem",
      "isInstallable": true
    },
    {
      "id": "ee1aef26-a601-442b-9852-1966f6f0a545",
      "quantity": 1,
      "action": "add",
      "product": {
        "id": "66d45a2f1a08816ae91b7a23",
        "href": "{{baseURL}}/productManagement/v1/product/66d45a2f1a08816ae91b7a23",
        "productCharacteristic": [
          {
            "name": "IMSI",
            "value": "IMSI#123",
            "id": "1"
          }
        ],
        "productSpecification": {
          "id": "191424cb-6002-46aa-a5a9-0602cfba83c7",
          "name": "SIM card",
          "@type": "ProductSpecification"
        },
        "@type": "ProductRef"
      },
      "productOffering": {
        "id": "73effd22-3bb1-4a35-8dbd-323d8cc46772",
        "name": "SIM Card",
        "@type": "AtomicProductOffering",
        "@referredType": "ProductOfferingRef"
      },
      "productOrderItemRelationship": [
        {
          "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
          "relationshipType": "isChild"
        }
      ],
      "state": "completed",
      "@type": "ProductOrderItem",
      "isInstallable": true
    },
    {
      "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
      "quantity": 1,
      "action": "add",
      "product": {
        "id": "66d45a2f1a08816ae91b7a1d",
        "href": "{{baseURL}}/productManagement/v1/product/66d45a2f1a08816ae91b7a1d",
        "@type": "ProductRef"
      },
      "productOffering": {
        "id": "aafbd483-1fde-4297-aea5-f24b88e1a905",
        "name": "Mobile Package Comfort Bundle",
        "@type": "BundleProductOffering",
        "@referredType": "ProductOfferingRef"
      },
      "productOrderItemRelationship": [
        {
          "id": "24fa0d5c-e014-4624-b2dc-dec0fe081d4b",
          "relationshipType": "bundles"
        },
        {
          "id": "ee1aef26-a601-442b-9852-1966f6f0a545",
          "relationshipType": "bundles"
        },
        {
          "id": "3d70cc60-f417-4811-9991-d6ed7534e63d",
          "relationshipType": "isChild"
        }
      ],
      "state": "completed",
      "@type": "ProductOrderItem",
      "isInstallable": true
    }
  ],
  "relatedParty": [
    {
      "id": "231-mf20",
      "name": "Homer",
      "role": "customer",
      "@referredType": "referredType"
    }
  ],
  "state": "completed",
  "@type": "ProductOrder"
}

```


#### Fields description

Same ProductOrder model described before.

## Query partial Resource representation

According to the [TMF630 guidelines, Part 1, chapter 4](https://www.tmforum.org/resources/standard/tmf630-rest-api-design-guidelines-4-2-0/#), the configuration APIs allow retrieving partial representation of Resource by selecting attributes.

An attribute selector directive called `fields` MUST be used to specify the attributes to be returned as part of a partial representation of a resource:

```
GET {apiRoot}/{resourceName}?fields={attributeName*}
GET {apiRoot}/{resourceName}/{resourceID}?fields={attributeName*}
```

If the `no` attribute selector directive is provided, then the complete resource representation (with all the attributes) must be returned.

```
GET {apiRoot}/{resourceName}
GET {apiRoot}/{resourceName}/{resourceID}
```

If the `none` attribute selector directive is provided, then the resource properties should be returned

 ```
GET {apiRoot}/{resourceName}?fields=none
GET {apiRoot}/{resourceName}/{resourceID}?fields=none
 ```

`ID` and `HREF` MUST be returned in the resource body representation when `fields=none` is used.

The filtering can be applied on any attribute,

- at level 1
- at level 2 when the inner class is an array (if it not the case, a bad request error will be returned)

### Example with no attribute selector directive

#### Request

```shell
curl -X GET "{{baseURL}}/productOrderingManagement/{{version}}/productOrder/66d45a16cc34205e0f04aacc" -H "accept: application/json;charset=utf-8"

```

#### Response

```json
200 OK
Content-Type: application/json
  
{
  "id": "66d45a16cc34205e0f04aacc",
  "href": "{{baseURL}}/productOrderingManagement/v1/productOrder/66d45a16cc34205e0f04aacc",
  "orderDate": "2024-09-01T12:12:06.106Z",
  "orderTotalPrice": [
    {
      "priceType": "NRC",
      "price": {
        "dutyFreeAmount": {
          "unit": "EUR",
          "value": 5
        },
        "taxIncludedAmount": {
          "value": 0
        }
      }
    }
  ],
  "productOrderItem": [
    {
      "id": "24fa0d5c-e014-4624-b2dc-dec0fe081d4b",
      "quantity": 1,
      "action": "add",
      "itemPrice": [
        {
          "description": "MobileLine",
          "name": "NRC 5 Euro",
          "priceType": "NRC",
          "recurringChargePeriod": {
            "amount": 0
          },
          "unitOfMeasure": "units",
          "price": {
            "dutyFreeAmount": {
              "unit": "EUR",
              "value": 5
            }
          },
          "productOfferingPrice": {
            "id": "b1a14825-ffa7-4a27-841f-16f798650de3",
            "name": "NRC 5 Euro",
            "@type": "productOfferingPrice",
            "@referredType": "ProductOfferingPriceCharge"
          }
        }
      ],
      "payment": [
        {
          "id": "1294af-483a-4071-1a04-b6fcfe4ee455",
          "@referredType": "Payment"
        }
      ],
      "product": {
        "id": "66d45a2f1a08816ae91b7a1f",
        "href": "{{baseURL}}/productManagement/v1/product/66d45a2f1a08816ae91b7a1f",
        "productSpecification": {
          "id": "9a2fb547-92ae-4fd0-afa7-6418e27d51cb",
          "name": "Mobile Line",
          "@type": "ProductSpecification"
        },
        "@type": "ProductRef"
      },
      "productOffering": {
        "id": "39d17453-6b6c-4902-9743-a50ec9ad5e67",
        "name": "Mobile Line",
        "@type": "AtomicProductOffering",
        "@referredType": "ProductOfferingRef"
      },
      "productOrderItemRelationship": [
        {
          "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
          "relationshipType": "isChild"
        },
        {
          "id": "ee1aef26-a601-442b-9852-1966f6f0a545",
          "relationshipType": "reliesOn"
        }
      ],
      "state": "completed",
      "@type": "ProductOrderItem",
      "isInstallable": true
    },
    {
      "id": "3d70cc60-f417-4811-9991-d6ed7534e63d",
      "quantity": 1,
      "action": "add",
      "product": {
        "id": "66d45a2f1a08816ae91b7a1c",
        "href": "{{baseURL}}/productManagement/v1/product/66d45a2f1a08816ae91b7a1c",
        "@type": "ProductRef"
      },
      "productOffering": {
        "id": "030520ea-6953-4e3d-90db-87b3887f87c0",
        "name": "Mobile Package Comfort",
        "@type": "Contract",
        "@referredType": "ProductOfferingRef"
      },
      "productOrderItemRelationship": [
        {
          "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
          "relationshipType": "bundles"
        }
      ],
      "state": "completed",
      "@type": "ProductOrderItem",
      "isInstallable": true
    },
    {
      "id": "ee1aef26-a601-442b-9852-1966f6f0a545",
      "quantity": 1,
      "action": "add",
      "product": {
        "id": "66d45a2f1a08816ae91b7a23",
        "href": "{{baseURL}}/productManagement/v1/product/66d45a2f1a08816ae91b7a23",
        "productCharacteristic": [
          {
            "name": "IMSI",
            "value": "IMSI#123",
            "id": "1"
          }
        ],
        "productSpecification": {
          "id": "191424cb-6002-46aa-a5a9-0602cfba83c7",
          "name": "SIM card",
          "@type": "ProductSpecification"
        },
        "@type": "ProductRef"
      },
      "productOffering": {
        "id": "73effd22-3bb1-4a35-8dbd-323d8cc46772",
        "name": "SIM Card",
        "@type": "AtomicProductOffering",
        "@referredType": "ProductOfferingRef"
      },
      "productOrderItemRelationship": [
        {
          "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
          "relationshipType": "isChild"
        }
      ],
      "state": "completed",
      "@type": "ProductOrderItem",
      "isInstallable": true
    },
    {
      "id": "6d4d829b-e459-46eb-8f4e-0483e996226e",
      "quantity": 1,
      "action": "add",
      "product": {
        "id": "66d45a2f1a08816ae91b7a1d",
        "href": "{{baseURL}}/productManagement/v1/product/66d45a2f1a08816ae91b7a1d",
        "@type": "ProductRef"
      },
      "productOffering": {
        "id": "aafbd483-1fde-4297-aea5-f24b88e1a905",
        "name": "Mobile Package Comfort Bundle",
        "@type": "BundleProductOffering",
        "@referredType": "ProductOfferingRef"
      },
      "productOrderItemRelationship": [
        {
          "id": "24fa0d5c-e014-4624-b2dc-dec0fe081d4b",
          "relationshipType": "bundles"
        },
        {
          "id": "ee1aef26-a601-442b-9852-1966f6f0a545",
          "relationshipType": "bundles"
        },
        {
          "id": "3d70cc60-f417-4811-9991-d6ed7534e63d",
          "relationshipType": "isChild"
        }
      ],
      "state": "completed",
      "@type": "ProductOrderItem",
      "isInstallable": true
    }
  ],
  "relatedParty": [
    {
      "id": "231-mf20",
      "name": "Homer",
      "role": "customer",
      "@referredType": "referredType"
    }
  ],
  "state": "completed",
  "@type": "ProductOrder"
}

```

### Example with `fields` attribute selector directive

#### Request

```shell
curl -X GET "{{baseURL}}/productOrderingManagement/{{version}}/productOrder/66d45a16cc34205e0f04aacc?fields=orderDate,state" -H "accept: application/json;charset=utf-8"

```

#### Response

```json
{
  "id": "66d45a16cc34205e0f04aacc",
  "href": "{{baseURL}}/productOrderingManagement/v1/productOrder/66d45a16cc34205e0f04aacc",
  "orderDate": "2024-09-01T12:12:06.106Z",
  "state": "completed"
}      
```

### Example with `fields` attribute selector directive on inner classes


#### Request

```shell
curl -X GET "{{baseURL}}/productOrderingManagement/{{version}}/productOrder/66d45a16cc34205e0f04aacc?fields=orderDate,state,relatedParty.id" -H "accept: application/json;charset=utf-8"

```

#### Response

```json
{
  "id": "66d45a16cc34205e0f04aacc",
  "href": "{{baseURL}}/productOrderingManagement/v1/productOrder/66d45a16cc34205e0f04aacc",
  "orderDate": "2024-09-01T12:12:06.106Z",
    "relatedParty": [
    {
      "id": "231-mf20"
    }
  ],
  "state": "completed"
}
```

### Example with `fields` attribute selector directive with `none` attribute

#### Request

```shell
curl -X GET "{{baseURL}}/productOrderingManagement/{{version}}/productOrder/66d45a16cc34205e0f04aacc?fields=none" -H "accept: application/json;charset=utf-8"

```

#### Response

```json
{
  "id": "66d45a16cc34205e0f04aacc",
  "href": "{{baseURL}}/productOrderingManagement/v1/productOrder/66d45a16cc34205e0f04aacc"
}
```
### History of Document
| Version of the document | modification date | description of modifications  |
|:------------------------|:------------------|:------------------------------|
| 1.0                     | 23/10/2023        | initialization                |


