// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

    const schema = {
  "asyncapi": "2.6.0",
  "info": {
    "title": "ProductOrder Inventory Kafka API",
    "version": "1.0.0",
    "description": "The ProductOrder inventory publishes and consumes events on and from different topics. \n* Publish productOrderStateChanged event.\n* Publish productOrderAttributeValueChange event.\n* Receive real-time event about product delivery.\n* Receive commands message from product order capture microservice in order to update productOrder state and attributes into the POI.\n"
  },
  "servers": {
    "develop": {
      "url": "http://localhost:9092",
      "description": "Localhost development server",
      "protocol": "kafka"
    },
    "integration": {
      "url": "kafka-integration-disco.apps.fr01.paas.tech.orange",
      "description": "Integration server",
      "protocol": "kafka"
    }
  },
  "defaultContentType": "application/json",
  "channels": {
    "disco.order-management.productOrderChange-command": {
      "description": "The topic on which productOrder change command may be produced by another microservice and consumed by the product order inventory. This topic is internal to the OrderManagement component.",
      "publish": {
        "x-scs-function-name": "productOrderChangeCommandPublishEvent",
        "summary": "Commands to change the state or the attribute  of a particular productOrder.",
        "operationId": "publishProductOrderChangeCommand",
        "traits": [
          {
            "bindings": {
              "kafka": {
                "groupId": "output-group-1"
              }
            }
          }
        ],
        "message": {
          "name": "productOrderChangeCommand",
          "title": "Product order change command",
          "summary": "A command for the microservice \"product order inventory\" to update the state or any attribute of a specific product order.",
          "payload": {
            "$id": "https://orange.com/productOrderCommand.yaml",
            "$schema": "http://json-schema.org/draft-07/schema#",
            "title": "ProductOrderCommand",
            "type": "object",
            "allOf": [
              {
                "$id": "https://orange.com/command.yaml",
                "$schema": "http://json-schema.org/draft-07/schema#",
                "title": "Command",
                "type": "object",
                "properties": {
                  "eventId": {
                    "type": "string",
                    "description": "The identifier of the notification.",
                    "x-parser-schema-id": "<anonymous-schema-361>"
                  },
                  "title": {
                    "type": "string",
                    "description": "The title of the command",
                    "x-parser-schema-id": "<anonymous-schema-362>"
                  },
                  "description": {
                    "type": "string",
                    "description": "The description of the command",
                    "x-parser-schema-id": "<anonymous-schema-363>"
                  },
                  "eventTime": {
                    "type": "string",
                    "format": "date-time",
                    "description": "Time of the event occurrence.",
                    "x-parser-schema-id": "<anonymous-schema-364>"
                  },
                  "eventType": {
                    "type": "string",
                    "description": "The type of the notification.",
                    "enum": [
                      "OrderItemsAndOrderTotalPriceValueChangeCommand",
                      "PaymentValueChangeCommand",
                      "realizingResourceValueChangeCommand",
                      "ProductOrderStateChangeCommand",
                      "RelatedPartiesValueChangeCommand",
                      "BillingAccountValueChangeCommand",
                      "ProductValueChangeCommand",
                      "AppointmentValueChangeCommand",
                      "RequestedCompletionDateValueChangeCommand"
                    ],
                    "x-parser-schema-id": "<anonymous-schema-365>"
                  },
                  "priority": {
                    "type": "string",
                    "description": "The priority of this event.",
                    "x-parser-schema-id": "<anonymous-schema-366>"
                  }
                },
                "required": [
                  "eventId",
                  "eventType",
                  "eventTime"
                ]
              }
            ],
            "extends": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.allOf[0]",
            "properties": {
              "event": {
                "$id": "https://orange.com/productOrderPayloadCommand.yaml",
                "$schema": "http://json-schema.org/draft-07/schema#",
                "type": "object",
                "title": "ProductOrderPayloadCommand",
                "required": [
                  "productOrder"
                ],
                "properties": {
                  "productOrder": {
                    "$id": "https://event.com/productorder.yaml",
                    "$schema": "http://json-schema.org/draft-07/schema#",
                    "title": "ProductOrder",
                    "description": "A Product Order is a type of order which can be used to place an order between a customer and a service provider or between a service provider and a partner and vice versa.",
                    "required": [
                      "@type"
                    ],
                    "type": "object",
                    "existingJavaType": "com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder",
                    "properties": {
                      "id": {
                        "description": "Unique identifier.",
                        "type": "string",
                        "x-parser-schema-id": "<anonymous-schema-1>"
                      },
                      "href": {
                        "description": "Hyperlink reference.",
                        "type": "string",
                        "x-parser-schema-id": "<anonymous-schema-2>"
                      },
                      "cancellationDate": {
                        "description": "Date when the order is cancelled. This is used when order is cancelled.",
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-3>"
                      },
                      "cancellationReason": {
                        "description": "Reason why the order is cancelled. This is used when order is cancelled.",
                        "type": "string",
                        "x-parser-schema-id": "<anonymous-schema-4>"
                      },
                      "category": {
                        "description": "Used to categorize the order from a business perspective that can be useful for the OM system (e.g. \"enterprise\", \"residential\", ...)",
                        "type": "string",
                        "x-parser-schema-id": "<anonymous-schema-5>"
                      },
                      "completionDate": {
                        "description": "Date when the ProductOrder was completed.",
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-6>"
                      },
                      "description": {
                        "description": "Description of the product order.",
                        "type": "string",
                        "x-parser-schema-id": "<anonymous-schema-7>"
                      },
                      "expectedCompletionDate": {
                        "description": "Expected delivery date amended by the provider.",
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-8>"
                      },
                      "notificationContact": {
                        "description": "Contact attached to the order to send back information regarding this order.",
                        "type": "string",
                        "x-parser-schema-id": "<anonymous-schema-9>"
                      },
                      "priority": {
                        "description": "A way that can be used by consumers to prioritize orders in OM system (from 0 to 4 : 0 is the highest priority, and 4 the lowest)",
                        "type": "string",
                        "x-parser-schema-id": "<anonymous-schema-10>"
                      },
                      "requestedCompletionDate": {
                        "description": "Requested delivery date from the requestor perspective",
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-11>"
                      },
                      "requestedStartDate": {
                        "description": "Order fulfillment start date wished by the requestor. This is used when, for any reason, requestor cannot allow seller to begin to operationally begin the fulfillment before a date.",
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-12>"
                      },
                      "billingAccount": {
                        "description": "BillingAccount reference. A BillingAccount is a detailed description of a bill structure.",
                        "required": [
                          "id",
                          "@type"
                        ],
                        "type": "object",
                        "properties": {
                          "id": {
                            "description": "The identifier of the referred entity.",
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-14>"
                          },
                          "href": {
                            "description": "The URI of the referred entity.",
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-15>"
                          },
                          "name": {
                            "description": "Name of the referred entity.",
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-16>"
                          },
                          "@baseType": {
                            "description": "When sub-classing, this defines the super-class.",
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-17>"
                          },
                          "@schemaLocation": {
                            "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                            "type": "string",
                            "format": "uri",
                            "x-parser-schema-id": "<anonymous-schema-18>"
                          },
                          "@type": {
                            "description": "When sub-classing, this defines the sub-class Extensible name.",
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-19>"
                          },
                          "@referredType": {
                            "description": "The actual type of the target instance when needed for disambiguation.",
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-20>"
                          },
                          "ratingType": {
                            "description": "Indicates whether the account follows a specific payment option such as prepaid or postpaid.",
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-21>"
                          }
                        },
                        "x-parser-schema-id": "<anonymous-schema-13>"
                      },
                      "channel": {
                        "type": "array",
                        "items": {
                          "description": "Related channel to another entity. May be online web, mobile app, social ,etc..",
                          "required": [
                            "@type"
                          ],
                          "type": "object",
                          "properties": {
                            "@type": {
                              "description": "When sub-classing, this defines the sub-class Extensible name.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-24>"
                            },
                            "@baseType": {
                              "description": "When sub-classing, this defines the super-class.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-25>"
                            },
                            "@schemaLocation": {
                              "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                              "type": "string",
                              "format": "uri",
                              "x-parser-schema-id": "<anonymous-schema-26>"
                            },
                            "role": {
                              "description": "Role playing by the channel.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-27>"
                            },
                            "channel": {
                              "description": "The channel to which the resource reference to. e.g. channel for selling product offerings, channel for opening a trouble ticket etc..",
                              "required": [
                                "@type",
                                "id"
                              ],
                              "type": "object",
                              "properties": {
                                "@type": {
                                  "description": "When sub-classing, this defines the sub-class Extensible name.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-29>"
                                },
                                "@baseType": {
                                  "description": "When sub-classing, this defines the super-class.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-30>"
                                },
                                "@schemaLocation": {
                                  "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                  "type": "string",
                                  "format": "uri",
                                  "x-parser-schema-id": "<anonymous-schema-31>"
                                },
                                "href": {
                                  "description": "The URI of the referred entity.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-32>"
                                },
                                "id": {
                                  "description": "The identifier of the referred entity.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-33>"
                                },
                                "name": {
                                  "description": "Name of the referred entity.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-34>"
                                },
                                "@referredType": {
                                  "description": "The actual type of the target instance when needed for disambiguation.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-35>"
                                }
                              },
                              "x-parser-schema-id": "<anonymous-schema-28>"
                            }
                          },
                          "x-parser-schema-id": "<anonymous-schema-23>"
                        },
                        "x-parser-schema-id": "<anonymous-schema-22>"
                      },
                      "orderTotalPrice": {
                        "type": "array",
                        "items": {
                          "description": "An amount, usually of money, that represents the actual price paid by the customer for this item or this order.",
                          "required": [
                            "@type"
                          ],
                          "type": "object",
                          "properties": {
                            "description": {
                              "description": "A narrative that explains in detail the semantics of this order item price.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-38>"
                            },
                            "name": {
                              "description": "A short descriptive name such as \"Subscription price\".",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-39>"
                            },
                            "priceType": {
                              "description": "Indicate if the price is for recurrent or no-recurrent charge.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-40>"
                            },
                            "recurringChargePeriod": {
                              "description": "An amount in a given unit.",
                              "type": "object",
                              "properties": {
                                "amount": {
                                  "description": "Numeric value in a given unit.",
                                  "type": "number",
                                  "format": "float",
                                  "default": 1,
                                  "x-parser-schema-id": "<anonymous-schema-42>"
                                },
                                "units": {
                                  "description": "Unit.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-43>"
                                }
                              },
                              "x-parser-schema-id": "<anonymous-schema-41>"
                            },
                            "unitOfMeasure": {
                              "description": "Could be minutes, GB...",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-44>"
                            },
                            "billingAccount": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.billingAccount",
                            "price": {
                              "description": "Provides all amounts (tax included, duty free, tax rate), used currency and percentage to apply for Price and Price Alteration.",
                              "required": [
                                "@type"
                              ],
                              "type": "object",
                              "properties": {
                                "percentage": {
                                  "description": "Percentage to apply for ProdOfferPriceAlteration.",
                                  "type": "number",
                                  "format": "float",
                                  "x-parser-schema-id": "<anonymous-schema-46>"
                                },
                                "taxRate": {
                                  "description": "Tax rate.",
                                  "type": "number",
                                  "format": "float",
                                  "x-parser-schema-id": "<anonymous-schema-47>"
                                },
                                "dutyFreeAmount": {
                                  "description": "A base / value business entity used to represent money.",
                                  "type": "object",
                                  "properties": {
                                    "unit": {
                                      "description": "Currency (ISO4217 norm uses 3 letters to define the currency)",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-49>"
                                    },
                                    "value": {
                                      "description": "A signed floating point number, the meaning of the sign is according to the context of the API that uses this Data type.",
                                      "type": "number",
                                      "format": "float",
                                      "x-parser-schema-id": "<anonymous-schema-50>"
                                    }
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-48>"
                                },
                                "taxIncludedAmount": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.price.properties.dutyFreeAmount",
                                "@baseType": {
                                  "description": "When sub-classing, this defines the super-class.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-51>"
                                },
                                "@schemaLocation": {
                                  "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                  "type": "string",
                                  "format": "uri",
                                  "x-parser-schema-id": "<anonymous-schema-52>"
                                },
                                "@type": {
                                  "description": "When sub-classing, this defines the sub-class Extensible name.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-53>"
                                }
                              },
                              "x-parser-schema-id": "<anonymous-schema-45>"
                            },
                            "productOfferingPrice": {
                              "description": "ProductPriceOffering reference. An amount, usually of money, that is asked for or allowed when a ProductOffering is bought, rented or leased.",
                              "discriminator": "@type",
                              "oneOf": [
                                {
                                  "title": "InstallmentCharge",
                                  "description": "An object that stores installment Charge.",
                                  "required": [
                                    "id"
                                  ],
                                  "type": "object",
                                  "properties": {
                                    "interestRate": {
                                      "type": "number",
                                      "format": "float",
                                      "description": "Interest rate applied to the installment charge.",
                                      "x-parser-schema-id": "<anonymous-schema-57>"
                                    },
                                    "downPayment": {
                                      "type": "number",
                                      "format": "float",
                                      "description": "Down payment amount for the installment.",
                                      "x-parser-schema-id": "<anonymous-schema-58>"
                                    },
                                    "partner": {
                                      "type": "string",
                                      "description": "Partner associated with the installment charge.",
                                      "x-parser-schema-id": "<anonymous-schema-59>"
                                    },
                                    "externalId": {
                                      "type": "string",
                                      "description": "External identifier for the installment charge.",
                                      "x-parser-schema-id": "<anonymous-schema-60>"
                                    },
                                    "id": {
                                      "description": "The identifier of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-61>"
                                    },
                                    "href": {
                                      "description": "The URI of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-62>"
                                    },
                                    "name": {
                                      "description": "Name of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-63>"
                                    },
                                    "@baseType": {
                                      "description": "When sub-classing, this defines the super-class.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-64>"
                                    },
                                    "@schemaLocation": {
                                      "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                      "type": "string",
                                      "format": "uri",
                                      "x-parser-schema-id": "<anonymous-schema-65>"
                                    },
                                    "@referredType": {
                                      "description": "The actual type of the target instance when needed for disambiguation.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-66>"
                                    },
                                    "immediatePayment": {
                                      "description": "Used to specify whether a payment should be processed instantly (\"true\") or if it allows for a delay (\"false\")",
                                      "type": "boolean",
                                      "x-parser-schema-id": "<anonymous-schema-67>"
                                    },
                                    "version": {
                                      "description": "ProductOfferingPrice version.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-68>"
                                    },
                                    "price": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.price.properties.dutyFreeAmount",
                                    "recurringChargePeriodLength": {
                                      "description": "The period of the recurring charge: 1, 2, … .It sets to zero if it is not applicable.",
                                      "type": "integer",
                                      "format": "int32",
                                      "x-parser-schema-id": "<anonymous-schema-69>"
                                    },
                                    "recurringChargePeriodType": {
                                      "description": "The period to repeat the application of the price. Could be month, week…",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-70>"
                                    },
                                    "applicationDuration": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.recurringChargePeriod",
                                    "isBundle": {
                                      "description": "A flag indicating if this ProductOfferingPrice is composite (bundle) or not.",
                                      "type": "boolean",
                                      "x-parser-schema-id": "<anonymous-schema-71>"
                                    },
                                    "lastUpdate": {
                                      "description": "The last update time of this ProductOfferingPrice.",
                                      "type": "string",
                                      "format": "date-time",
                                      "x-parser-schema-id": "<anonymous-schema-72>"
                                    },
                                    "lifecycleStatus": {
                                      "description": "The lifecycle status of this ProductOfferingPrice.",
                                      "type": "string",
                                      "enum": [
                                        "launched",
                                        "unavailable",
                                        "retired",
                                        "obsolete"
                                      ],
                                      "x-parser-schema-id": "<anonymous-schema-73>"
                                    },
                                    "percentage": {
                                      "description": "Percentage to apply if this Product Offering Price is an Alteration (such as a Discount).",
                                      "type": "number",
                                      "format": "float",
                                      "x-parser-schema-id": "<anonymous-schema-74>"
                                    },
                                    "duration": {
                                      "description": "A time interval in a given unit of time.",
                                      "type": "integer",
                                      "format": "int32",
                                      "x-parser-schema-id": "<anonymous-schema-75>"
                                    },
                                    "priority": {
                                      "description": "Priority level for applying this alteration among all the defined alterations on the order item price.",
                                      "type": "integer",
                                      "format": "int32",
                                      "x-parser-schema-id": "<anonymous-schema-76>"
                                    },
                                    "priceType": {
                                      "description": "A category that describes the price, such as recurring, discount, allowance, penalty and so forth.",
                                      "type": "string",
                                      "enum": [
                                        "RC",
                                        "NRC",
                                        "Usage"
                                      ],
                                      "x-parser-schema-id": "<anonymous-schema-77>"
                                    },
                                    "unitOfMeasure": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.recurringChargePeriod",
                                    "validFor": {
                                      "description": "A period of time, either as a deadline (endDateTime only) a startDateTime only or both.",
                                      "type": "object",
                                      "properties": {
                                        "endDateTime": {
                                          "description": "End of the time period, using IETC-RFC-3339 format.",
                                          "type": "string",
                                          "format": "date-time",
                                          "x-parser-schema-id": "<anonymous-schema-79>"
                                        },
                                        "startDateTime": {
                                          "description": "Start of the time period, using IETC-RFC-3339 format.",
                                          "type": "string",
                                          "format": "date-time",
                                          "x-parser-schema-id": "<anonymous-schema-80>"
                                        }
                                      },
                                      "x-parser-schema-id": "<anonymous-schema-78>"
                                    },
                                    "description": {
                                      "description": "Description of the productOfferingPrice.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-81>"
                                    },
                                    "popRelationship": {
                                      "type": "array",
                                      "items": {
                                        "description": "Product Offering Prices related to this Product Offering Price, for example a price alteration such as allowance or discount.",
                                        "required": [
                                          "id",
                                          "@type"
                                        ],
                                        "type": "object",
                                        "properties": {
                                          "id": {
                                            "description": "Unique identifier.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-84>"
                                          },
                                          "href": {
                                            "description": "Hyperlink reference.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-85>"
                                          },
                                          "name": {
                                            "description": "Name of the referred entity.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-86>"
                                          },
                                          "version": {
                                            "description": "Version of the referred product offering price.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-87>"
                                          },
                                          "relationshipType": {
                                            "description": "Type of the relationship, for example override, discount, etc.",
                                            "type": "string",
                                            "enum": [
                                              "alteredBy",
                                              "replacedBy",
                                              "taxedBy"
                                            ],
                                            "x-parser-schema-id": "<anonymous-schema-88>"
                                          },
                                          "validFor": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.validFor",
                                          "@baseType": {
                                            "description": "When sub-classing, this defines the super-class.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-89>"
                                          },
                                          "@referredType": {
                                            "description": "The actual type of the target instance when needed for disambiguation.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-90>"
                                          },
                                          "@schemaLocation": {
                                            "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                            "type": "string",
                                            "format": "uri",
                                            "x-parser-schema-id": "<anonymous-schema-91>"
                                          },
                                          "@type": {
                                            "description": "When sub-classing, this defines the sub-class Extensible name.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-92>"
                                          }
                                        },
                                        "x-parser-schema-id": "<anonymous-schema-83>"
                                      },
                                      "x-parser-schema-id": "<anonymous-schema-82>"
                                    },
                                    "tax": {
                                      "type": "array",
                                      "items": {
                                        "description": "An amount of money levied on the price of a Product by a legislative body.",
                                        "required": [
                                          "@type"
                                        ],
                                        "type": "object",
                                        "properties": {
                                          "taxAmount": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.price.properties.dutyFreeAmount",
                                          "taxCategory": {
                                            "description": "Tax category.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-95>"
                                          },
                                          "taxRate": {
                                            "description": "Applied rate of the tax.",
                                            "type": "number",
                                            "format": "float",
                                            "x-parser-schema-id": "<anonymous-schema-96>"
                                          },
                                          "@baseType": {
                                            "description": "When sub-classing, this defines the super-class.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-97>"
                                          },
                                          "@schemaLocation": {
                                            "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                            "type": "string",
                                            "format": "uri",
                                            "x-parser-schema-id": "<anonymous-schema-98>"
                                          },
                                          "@type": {
                                            "description": "When sub-classing, this defines the sub-class Extensible name.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-99>"
                                          }
                                        },
                                        "x-parser-schema-id": "<anonymous-schema-94>"
                                      },
                                      "x-parser-schema-id": "<anonymous-schema-93>"
                                    }
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-56>"
                                },
                                {
                                  "title": "ProductOfferingPriceCharge",
                                  "description": "An object that stores Product Offering Price Charge .",
                                  "required": [
                                    "id"
                                  ],
                                  "type": "object",
                                  "properties": {
                                    "id": {
                                      "description": "The identifier of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-101>"
                                    },
                                    "href": {
                                      "description": "The URI of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-102>"
                                    },
                                    "name": {
                                      "description": "Name of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-103>"
                                    },
                                    "@baseType": {
                                      "description": "When sub-classing, this defines the super-class.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-104>"
                                    },
                                    "@schemaLocation": {
                                      "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                      "type": "string",
                                      "format": "uri",
                                      "x-parser-schema-id": "<anonymous-schema-105>"
                                    },
                                    "@referredType": {
                                      "description": "The actual type of the target instance when needed for disambiguation.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-106>"
                                    },
                                    "immediatePayment": {
                                      "description": "Used to specify whether a payment should be processed instantly (\"true\") or if it allows for a delay (\"false\")",
                                      "type": "boolean",
                                      "x-parser-schema-id": "<anonymous-schema-107>"
                                    },
                                    "version": {
                                      "description": "ProductOfferingPrice version.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-108>"
                                    },
                                    "price": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.price.properties.dutyFreeAmount",
                                    "recurringChargePeriodLength": {
                                      "description": "The period of the recurring charge: 1, 2, … .It sets to zero if it is not applicable.",
                                      "type": "integer",
                                      "format": "int32",
                                      "x-parser-schema-id": "<anonymous-schema-109>"
                                    },
                                    "recurringChargePeriodType": {
                                      "description": "The period to repeat the application of the price. Could be month, week…",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-110>"
                                    },
                                    "applicationDuration": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.recurringChargePeriod",
                                    "isBundle": {
                                      "description": "A flag indicating if this ProductOfferingPrice is composite (bundle) or not.",
                                      "type": "boolean",
                                      "x-parser-schema-id": "<anonymous-schema-111>"
                                    },
                                    "lastUpdate": {
                                      "description": "The last update time of this ProductOfferingPrice.",
                                      "type": "string",
                                      "format": "date-time",
                                      "x-parser-schema-id": "<anonymous-schema-112>"
                                    },
                                    "lifecycleStatus": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.lifecycleStatus",
                                    "percentage": {
                                      "description": "Percentage to apply if this Product Offering Price is an Alteration (such as a Discount).",
                                      "type": "number",
                                      "format": "float",
                                      "x-parser-schema-id": "<anonymous-schema-113>"
                                    },
                                    "duration": {
                                      "description": "A time interval in a given unit of time.",
                                      "type": "integer",
                                      "format": "int32",
                                      "x-parser-schema-id": "<anonymous-schema-114>"
                                    },
                                    "priority": {
                                      "description": "Priority level for applying this alteration among all the defined alterations on the order item price.",
                                      "type": "integer",
                                      "format": "int32",
                                      "x-parser-schema-id": "<anonymous-schema-115>"
                                    },
                                    "priceType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.priceType",
                                    "unitOfMeasure": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.recurringChargePeriod",
                                    "validFor": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.validFor",
                                    "description": {
                                      "description": "Description of the productOfferingPrice.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-116>"
                                    },
                                    "popRelationship": {
                                      "type": "array",
                                      "items": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.popRelationship.items",
                                      "x-parser-schema-id": "<anonymous-schema-117>"
                                    },
                                    "tax": {
                                      "type": "array",
                                      "items": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.tax.items",
                                      "x-parser-schema-id": "<anonymous-schema-118>"
                                    }
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-100>"
                                },
                                {
                                  "title": "ProductOfferingPriceRef",
                                  "description": "An object that stores Product Offering Price Ref .",
                                  "required": [
                                    "id"
                                  ],
                                  "type": "object",
                                  "properties": {
                                    "id": {
                                      "description": "The identifier of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-120>"
                                    },
                                    "href": {
                                      "description": "The URI of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-121>"
                                    },
                                    "name": {
                                      "description": "Name of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-122>"
                                    },
                                    "@baseType": {
                                      "description": "When sub-classing, this defines the super-class.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-123>"
                                    },
                                    "@schemaLocation": {
                                      "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                      "type": "string",
                                      "format": "uri",
                                      "x-parser-schema-id": "<anonymous-schema-124>"
                                    },
                                    "@referredType": {
                                      "description": "The actual type of the target instance when needed for disambiguation.",
                                      "x-parser-schema-id": "<anonymous-schema-125>"
                                    }
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-119>"
                                }
                              ],
                              "type": "object",
                              "properties": {
                                "@type": {
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-55>"
                                }
                              },
                              "x-parser-schema-id": "<anonymous-schema-54>"
                            },
                            "priceAlteration": {
                              "type": "array",
                              "items": {
                                "description": "A structure used to describe a price alteration.",
                                "required": [
                                  "@type"
                                ],
                                "type": "object",
                                "properties": {
                                  "description": {
                                    "description": "A narrative that explains in detail the semantics of this order item price alteration.",
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-128>"
                                  },
                                  "name": {
                                    "description": "Name of the order item price alteration.",
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-129>"
                                  },
                                  "priceType": {
                                    "description": "A category that describes the price such as recurring, one time and usage.",
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-130>"
                                  },
                                  "priority": {
                                    "description": "Priority level for applying this alteration among all the defined alterations on the order item price.",
                                    "type": "integer",
                                    "format": "int32",
                                    "x-parser-schema-id": "<anonymous-schema-131>"
                                  },
                                  "applicationDuration": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.recurringChargePeriod",
                                  "recurringChargePeriod": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.recurringChargePeriod",
                                  "unitOfMeasure": {
                                    "description": "Could be minutes, GB...",
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-132>"
                                  },
                                  "price": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.price",
                                  "productOfferingPrice": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice",
                                  "@baseType": {
                                    "description": "When sub-classing, this defines the super-class.",
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-133>"
                                  },
                                  "@schemaLocation": {
                                    "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                    "type": "string",
                                    "format": "uri",
                                    "x-parser-schema-id": "<anonymous-schema-134>"
                                  },
                                  "@type": {
                                    "description": "When sub-classing, this defines the sub-class Extensible name.",
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-135>"
                                  },
                                  "applicationOffset": {
                                    "description": "Number of months after the start of the recurring charge when the discount or price change begins",
                                    "type": "integer",
                                    "format": "int32",
                                    "x-parser-schema-id": "<anonymous-schema-136>"
                                  }
                                },
                                "x-parser-schema-id": "<anonymous-schema-127>"
                              },
                              "x-parser-schema-id": "<anonymous-schema-126>"
                            },
                            "@baseType": {
                              "description": "When sub-classing, this defines the super-class.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-137>"
                            },
                            "@schemaLocation": {
                              "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                              "type": "string",
                              "format": "uri",
                              "x-parser-schema-id": "<anonymous-schema-138>"
                            },
                            "@type": {
                              "description": "When sub-classing, this defines the sub-class Extensible name.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-139>"
                            },
                            "applicationDuration": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.recurringChargePeriod"
                          },
                          "x-parser-schema-id": "<anonymous-schema-37>"
                        },
                        "x-parser-schema-id": "<anonymous-schema-36>"
                      },
                      "payment": {
                        "type": "array",
                        "items": {
                          "description": "If an immediate payment has been done at the product order submission, the payment information are captured and stored (as a reference) in the order.",
                          "required": [
                            "id",
                            "@type"
                          ],
                          "type": "object",
                          "properties": {
                            "id": {
                              "description": "The identifier of the referred entity.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-142>"
                            },
                            "href": {
                              "description": "The URI of the referred entity.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-143>"
                            },
                            "name": {
                              "description": "Name of the referred entity.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-144>"
                            },
                            "@baseType": {
                              "description": "When sub-classing, this defines the super-class.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-145>"
                            },
                            "@schemaLocation": {
                              "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                              "type": "string",
                              "format": "uri",
                              "x-parser-schema-id": "<anonymous-schema-146>"
                            },
                            "@type": {
                              "description": "When sub-classing, this defines the sub-class Extensible name.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-147>"
                            },
                            "@referredType": {
                              "description": "The actual type of the target instance when needed for disambiguation.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-148>"
                            }
                          },
                          "x-parser-schema-id": "<anonymous-schema-141>"
                        },
                        "x-parser-schema-id": "<anonymous-schema-140>"
                      },
                      "productOrderItem": {
                        "type": "array",
                        "items": {
                          "description": "An identified part of the order. A product order is decomposed into one or more order items.",
                          "required": [
                            "@type"
                          ],
                          "type": "object",
                          "properties": {
                            "id": {
                              "description": "Identifier of the ProductOrder item (generally it is a sequence number 01, 02, 03, ...)",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-151>"
                            },
                            "quantity": {
                              "description": "Quantity ordered.",
                              "type": "integer",
                              "format": "int32",
                              "x-parser-schema-id": "<anonymous-schema-152>"
                            },
                            "action": {
                              "description": "Action to be performed on the entity managed by the item.",
                              "type": "string",
                              "enum": [
                                "add",
                                "delete",
                                "modify",
                                "noChange",
                                "migrate"
                              ],
                              "x-parser-schema-id": "<anonymous-schema-153>"
                            },
                            "billingAccount": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.billingAccount",
                            "itemPrice": {
                              "type": "array",
                              "items": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items",
                              "x-parser-schema-id": "<anonymous-schema-154>"
                            },
                            "itemTerm": {
                              "type": "array",
                              "items": {
                                "description": "Description of a productTerm linked to this orderItem. This represent a commitment with a duration.",
                                "required": [
                                  "@type"
                                ],
                                "type": "object",
                                "properties": {
                                  "description": {
                                    "description": "Description of the productOrderTerm.",
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-157>"
                                  },
                                  "name": {
                                    "description": "Name of the productOrderTerm.",
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-158>"
                                  },
                                  "duration": {
                                    "description": "A time interval in a given unit of time.",
                                    "type": "object",
                                    "properties": {
                                      "amount": {
                                        "description": "Time interval (number of seconds, minutes, hours, etc...)",
                                        "type": "integer",
                                        "format": "int32",
                                        "x-parser-schema-id": "<anonymous-schema-160>"
                                      },
                                      "units": {
                                        "description": "Unit of time (seconds, minutes, hours, etc...)",
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-161>"
                                      }
                                    },
                                    "x-parser-schema-id": "<anonymous-schema-159>"
                                  },
                                  "@baseType": {
                                    "description": "When sub-classing, this defines the super-class.",
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-162>"
                                  },
                                  "@schemaLocation": {
                                    "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                    "type": "string",
                                    "format": "uri",
                                    "x-parser-schema-id": "<anonymous-schema-163>"
                                  },
                                  "@type": {
                                    "description": "When sub-classing, this defines the sub-class Extensible name.",
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-164>"
                                  }
                                },
                                "x-parser-schema-id": "<anonymous-schema-156>"
                              },
                              "x-parser-schema-id": "<anonymous-schema-155>"
                            },
                            "payment": {
                              "type": "array",
                              "items": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.payment.items",
                              "x-parser-schema-id": "<anonymous-schema-165>"
                            },
                            "product": {
                              "description": "A product to be created defined by value or existing defined by reference.",
                              "discriminator": "@type",
                              "oneOf": [
                                {
                                  "title": "ProductRef",
                                  "description": "A reference to a Product entity.",
                                  "required": [
                                    "id"
                                  ],
                                  "type": "object",
                                  "properties": {
                                    "id": {
                                      "description": "The identifier of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-169>"
                                    },
                                    "href": {
                                      "description": "The URI of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-170>"
                                    },
                                    "name": {
                                      "description": "Name of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-171>"
                                    },
                                    "@baseType": {
                                      "description": "When sub-classing, this defines the super-class.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-172>"
                                    },
                                    "@schemaLocation": {
                                      "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                      "type": "string",
                                      "format": "uri",
                                      "x-parser-schema-id": "<anonymous-schema-173>"
                                    },
                                    "@referredType": {
                                      "description": "The actual type of the target instance when needed for disambiguation.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-174>"
                                    }
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-168>"
                                },
                                {
                                  "title": "Product",
                                  "description": "A product offering procured by a customer or other interested party playing a party role. A product is realized as one or more service(s) and / or resource(s).",
                                  "type": "object",
                                  "properties": {
                                    "id": {
                                      "description": "unique identifier.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-176>"
                                    },
                                    "@baseType": {
                                      "description": "When sub-classing, this defines the super-class.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-177>"
                                    },
                                    "href": {
                                      "description": "Hyperlink reference.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-178>"
                                    },
                                    "description": {
                                      "description": "Is the description of the product. It could be copied from the description of the Product Offering.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-179>"
                                    },
                                    "isBundle": {
                                      "description": "If true, the product is a ProductBundle which is an instantiation of a BundledProductOffering. If false, the product is a ProductComponent which is an instantiation of a SimpleProductOffering.",
                                      "type": "boolean",
                                      "x-parser-schema-id": "<anonymous-schema-180>"
                                    },
                                    "isCustomerVisible": {
                                      "description": "If true, the product is visible by the customer.",
                                      "type": "boolean",
                                      "x-parser-schema-id": "<anonymous-schema-181>"
                                    },
                                    "name": {
                                      "description": "Name of the product. It could be the same as the name of the product offering.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-182>"
                                    },
                                    "orderDate": {
                                      "description": "Is the date when the product was ordered.",
                                      "type": "string",
                                      "format": "date-time",
                                      "x-parser-schema-id": "<anonymous-schema-183>"
                                    },
                                    "productSerialNumber": {
                                      "description": "Is the serial number for the product. This is typically applicable to tangible products e.g. Broadband Router.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-184>"
                                    },
                                    "startDate": {
                                      "description": "Is the date from which the product starts.",
                                      "type": "string",
                                      "format": "date-time",
                                      "x-parser-schema-id": "<anonymous-schema-185>"
                                    },
                                    "terminationDate": {
                                      "description": "Is the date when the product was terminated.",
                                      "type": "string",
                                      "format": "date-time",
                                      "x-parser-schema-id": "<anonymous-schema-186>"
                                    },
                                    "product": {
                                      "type": "array",
                                      "items": {
                                        "description": "A product to be created defined by value or existing defined by reference.",
                                        "discriminator": "@type",
                                        "oneOf": [
                                          "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[0]",
                                          "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1]"
                                        ],
                                        "type": "object",
                                        "properties": {
                                          "@type": {
                                            "description": "When sub-classing, this defines the sub-class entity name.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-167>"
                                          }
                                        },
                                        "x-parser-schema-id": "<anonymous-schema-188>"
                                      },
                                      "x-parser-schema-id": "<anonymous-schema-187>"
                                    },
                                    "productCharacteristic": {
                                      "type": "array",
                                      "items": {
                                        "description": "Describes a given characteristic of an object or entity through a name/value pair.",
                                        "discriminator": "@type",
                                        "oneOf": [
                                          {
                                            "title": "BooleanCharacteristic",
                                            "description": "Represents a characteristic with a true/false value, used to describe binary states or conditions of an object or entity.",
                                            "type": "object",
                                            "properties": {
                                              "value": {
                                                "description": "Value of the characteristic.",
                                                "type": "boolean",
                                                "x-parser-schema-id": "<anonymous-schema-198>"
                                              }
                                            },
                                            "x-parser-schema-id": "<anonymous-schema-197>"
                                          },
                                          {
                                            "title": "IntegerCharacteristic",
                                            "description": "Represents a characteristic with a whole number value, used to describe countable quantities, discrete measurements, or numeric identifiers.",
                                            "type": "object",
                                            "properties": {
                                              "value": {
                                                "description": "Value of the characteristic.",
                                                "type": "integer",
                                                "format": "int32",
                                                "x-parser-schema-id": "<anonymous-schema-200>"
                                              }
                                            },
                                            "x-parser-schema-id": "<anonymous-schema-199>"
                                          },
                                          {
                                            "title": "StringCharacteristic",
                                            "description": "Represents a characteristic with a text value, used to describe textual attributes, labels, identifiers, or alphanumeric information.",
                                            "type": "object",
                                            "properties": {
                                              "value": {
                                                "description": "Value of the characteristic.",
                                                "type": "string",
                                                "x-parser-schema-id": "<anonymous-schema-202>"
                                              }
                                            },
                                            "x-parser-schema-id": "<anonymous-schema-201>"
                                          },
                                          {
                                            "title": "FloatCharacteristic",
                                            "description": "Represents a characteristic with a decimal numeric value, used to describe measurements, rates, or quantities requiring fractional precision.",
                                            "type": "object",
                                            "properties": {
                                              "value": {
                                                "description": "Value of the characteristic.",
                                                "type": "number",
                                                "format": "float",
                                                "x-parser-schema-id": "<anonymous-schema-204>"
                                              }
                                            },
                                            "x-parser-schema-id": "<anonymous-schema-203>"
                                          },
                                          {
                                            "title": "ObjectCharacteristic",
                                            "description": "Represents a characteristic with a complex structured value containing nested properties, used to describe composite attributes that require multiple interrelated fields.",
                                            "type": "object",
                                            "properties": {
                                              "value": {
                                                "description": "Value of the characteristic.",
                                                "type": "object",
                                                "x-parser-schema-id": "<anonymous-schema-206>"
                                              }
                                            },
                                            "x-parser-schema-id": "<anonymous-schema-205>"
                                          },
                                          {
                                            "title": "ValidityCharacteristic",
                                            "description": "Defines a characteristic with conditions and a time frame for its applicability.",
                                            "type": "object",
                                            "properties": {
                                              "value": {
                                                "title": "ValidityCharacteristic",
                                                "description": "Represents the rules and time frame during which a characteristic is applicable.",
                                                "type": "object",
                                                "properties": {
                                                  "value": {
                                                    "description": "Represents the actual value of the characteristic, typically a numeric or measurable quantity.",
                                                    "type": "integer",
                                                    "format": "int32",
                                                    "x-parser-schema-id": "<anonymous-schema-209>"
                                                  },
                                                  "unitOfMeasure": {
                                                    "description": "Specifies the unit associated with the value (e.g., hours, days, gb) to provide context and standardization.",
                                                    "type": "string",
                                                    "x-parser-schema-id": "<anonymous-schema-210>"
                                                  },
                                                  "validTo": {
                                                    "description": "Indicates the date and time until which the characteristic is valid or applicable.",
                                                    "type": "string",
                                                    "format": "date-time",
                                                    "x-parser-schema-id": "<anonymous-schema-211>"
                                                  },
                                                  "validFrom": {
                                                    "description": "Represents the date and time from which the characteristic becomes valid or applicable.",
                                                    "type": "string",
                                                    "format": "date-time",
                                                    "x-parser-schema-id": "<anonymous-schema-212>"
                                                  }
                                                },
                                                "x-parser-schema-id": "<anonymous-schema-208>"
                                              }
                                            },
                                            "x-parser-schema-id": "<anonymous-schema-207>"
                                          },
                                          {
                                            "title": "IntegerArrayCharacteristic",
                                            "description": "Represents a characteristic containing multiple whole number values in an ordered collection, used to describe sets of countable quantities or sequences of discrete values.",
                                            "type": "object",
                                            "properties": {
                                              "value": {
                                                "description": "Value of the characteristic.",
                                                "type": "array",
                                                "items": {
                                                  "type": "integer",
                                                  "format": "int32",
                                                  "x-parser-schema-id": "<anonymous-schema-215>"
                                                },
                                                "x-parser-schema-id": "<anonymous-schema-214>"
                                              }
                                            },
                                            "x-parser-schema-id": "<anonymous-schema-213>"
                                          },
                                          {
                                            "title": "StringArrayCharacteristic",
                                            "description": "Represents a characteristic containing multiple text values in an ordered collection, used to describe lists of textual attributes, tags, or categorical values.",
                                            "type": "object",
                                            "properties": {
                                              "value": {
                                                "description": "Value of the characteristic.",
                                                "type": "array",
                                                "items": {
                                                  "type": "string",
                                                  "x-parser-schema-id": "<anonymous-schema-218>"
                                                },
                                                "x-parser-schema-id": "<anonymous-schema-217>"
                                              }
                                            },
                                            "x-parser-schema-id": "<anonymous-schema-216>"
                                          },
                                          {
                                            "title": "FloatArrayCharacteristic",
                                            "description": "Represents a characteristic containing multiple decimal numeric values in an ordered collection, used to describe series of measurements, coordinates, or sets of fractional data.",
                                            "type": "object",
                                            "properties": {
                                              "value": {
                                                "description": "Value of the characteristic.",
                                                "type": "array",
                                                "items": {
                                                  "type": "number",
                                                  "format": "float",
                                                  "x-parser-schema-id": "<anonymous-schema-221>"
                                                },
                                                "x-parser-schema-id": "<anonymous-schema-220>"
                                              }
                                            },
                                            "x-parser-schema-id": "<anonymous-schema-219>"
                                          },
                                          {
                                            "title": "ObjectArrayCharacteristic",
                                            "description": "Represents a characteristic containing multiple complex structured values in an ordered collection, used to describe repeating composite attributes with consistent internal structure.",
                                            "type": "object",
                                            "properties": {
                                              "value": {
                                                "description": "Value of the characteristic.",
                                                "type": "array",
                                                "items": {
                                                  "type": "object",
                                                  "x-parser-schema-id": "<anonymous-schema-224>"
                                                },
                                                "x-parser-schema-id": "<anonymous-schema-223>"
                                              }
                                            },
                                            "x-parser-schema-id": "<anonymous-schema-222>"
                                          },
                                          {
                                            "title": "BooleanArrayCharacteristic",
                                            "description": "Represents a characteristic containing multiple true/false values in an ordered collection, used to describe sets of binary conditions or feature toggles.",
                                            "type": "object",
                                            "properties": {
                                              "value": {
                                                "description": "Value of the characteristic.",
                                                "type": "array",
                                                "items": {
                                                  "type": "boolean",
                                                  "x-parser-schema-id": "<anonymous-schema-227>"
                                                },
                                                "x-parser-schema-id": "<anonymous-schema-226>"
                                              }
                                            },
                                            "x-parser-schema-id": "<anonymous-schema-225>"
                                          },
                                          {
                                            "title": "AddressCharacteristic",
                                            "description": "An object that stores the address details provided by the customer for a service. It helps keep track of the address information related to each product or service.",
                                            "type": "object",
                                            "properties": {
                                              "addressId": {
                                                "type": "string",
                                                "description": "A unique identifier for the specific address. This helps in referencing and managing addresses within the system.",
                                                "x-parser-schema-id": "<anonymous-schema-229>"
                                              },
                                              "city": {
                                                "type": "string",
                                                "description": "The name of the city where the address is located.",
                                                "x-parser-schema-id": "<anonymous-schema-230>"
                                              },
                                              "country": {
                                                "type": "string",
                                                "description": "The name of the country for the address.",
                                                "x-parser-schema-id": "<anonymous-schema-231>"
                                              },
                                              "subUnitNumber": {
                                                "type": "string",
                                                "description": "The specific unit or suite number within a larger building or complex, such as an apartment, office or retail unit.",
                                                "x-parser-schema-id": "<anonymous-schema-232>"
                                              },
                                              "streetName": {
                                                "type": "string",
                                                "description": "The name of the street where the address is located.",
                                                "x-parser-schema-id": "<anonymous-schema-233>"
                                              },
                                              "postcode": {
                                                "type": "string",
                                                "description": "The postal code or ZIP code used to identify a specific geographic area for mail delivery.",
                                                "x-parser-schema-id": "<anonymous-schema-234>"
                                              }
                                            },
                                            "x-parser-schema-id": "<anonymous-schema-228>"
                                          },
                                          {
                                            "title": "DateCharacteristic",
                                            "description": "An object that stores date-related details provided by the customer or generated by the system for a service. It helps keep track of important temporal information associated with a product, order, or any service.",
                                            "type": "object",
                                            "properties": {
                                              "value": {
                                                "type": "string",
                                                "format": "date-time",
                                                "description": "Value of the characteristic.",
                                                "x-parser-schema-id": "<anonymous-schema-236>"
                                              }
                                            },
                                            "x-parser-schema-id": "<anonymous-schema-235>"
                                          }
                                        ],
                                        "required": [
                                          "@type"
                                        ],
                                        "type": "object",
                                        "properties": {
                                          "@type": {
                                            "description": "When sub-classing, this defines the sub-class Extensible name.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-191>"
                                          },
                                          "@baseType": {
                                            "description": "When sub-classing, this defines the super-class.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-192>"
                                          },
                                          "@schemaLocation": {
                                            "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                            "type": "string",
                                            "format": "uri",
                                            "x-parser-schema-id": "<anonymous-schema-193>"
                                          },
                                          "id": {
                                            "description": "Unique identifier of the characteristic.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-194>"
                                          },
                                          "name": {
                                            "description": "Name of the characteristic.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-195>"
                                          },
                                          "valueType": {
                                            "description": "Data type of the value of the characteristic.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-196>"
                                          }
                                        },
                                        "x-parser-schema-id": "<anonymous-schema-190>"
                                      },
                                      "x-parser-schema-id": "<anonymous-schema-189>"
                                    },
                                    "productOffering": {
                                      "description": "ProductOffering reference. A product offering represents entities that are orderable from the provider of the catalog, this resource includes pricing information.",
                                      "required": [
                                        "id",
                                        "@type"
                                      ],
                                      "type": "object",
                                      "properties": {
                                        "id": {
                                          "description": "The identifier of the referred entity.",
                                          "type": "string",
                                          "x-parser-schema-id": "<anonymous-schema-238>"
                                        },
                                        "href": {
                                          "description": "The URI of the referred entity.",
                                          "type": "string",
                                          "x-parser-schema-id": "<anonymous-schema-239>"
                                        },
                                        "name": {
                                          "description": "Name of the referred entity.",
                                          "type": "string",
                                          "x-parser-schema-id": "<anonymous-schema-240>"
                                        },
                                        "@baseType": {
                                          "description": "When sub-classing, this defines the super-class.",
                                          "type": "string",
                                          "x-parser-schema-id": "<anonymous-schema-241>"
                                        },
                                        "@schemaLocation": {
                                          "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                          "type": "string",
                                          "format": "uri",
                                          "x-parser-schema-id": "<anonymous-schema-242>"
                                        },
                                        "@type": {
                                          "description": "When sub-classing, this defines the sub-class Extensible name.",
                                          "type": "string",
                                          "x-parser-schema-id": "<anonymous-schema-243>"
                                        },
                                        "@referredType": {
                                          "description": "The actual type of the target instance when needed for disambiguation.",
                                          "type": "string",
                                          "x-parser-schema-id": "<anonymous-schema-244>"
                                        },
                                        "version": {
                                          "description": "Version of the product offering.",
                                          "type": "string",
                                          "x-parser-schema-id": "<anonymous-schema-245>"
                                        }
                                      },
                                      "x-parser-schema-id": "<anonymous-schema-237>"
                                    },
                                    "productRelationship": {
                                      "type": "array",
                                      "items": {
                                        "description": "Used to describe relationship between product.",
                                        "required": [
                                          "@type"
                                        ],
                                        "type": "object",
                                        "properties": {
                                          "id": {
                                            "description": "Id of the related product.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-248>"
                                          },
                                          "product": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.product.items",
                                          "relationshipType": {
                                            "description": "Relationship type as relies on, bundles, etc...",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-249>"
                                          },
                                          "@baseType": {
                                            "description": "When sub-classing, this defines the super-class.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-250>"
                                          },
                                          "@schemaLocation": {
                                            "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                            "type": "string",
                                            "format": "uri",
                                            "x-parser-schema-id": "<anonymous-schema-251>"
                                          },
                                          "@type": {
                                            "description": "When sub-classing, this defines the sub-class Extensible name.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-252>"
                                          },
                                          "href": {
                                            "description": "The URI of the referred entity.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-253>"
                                          },
                                          "name": {
                                            "description": "Name of the referred entity.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-254>"
                                          },
                                          "@referredType": {
                                            "description": "The actual type of the target instance when needed for disambiguation.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-255>"
                                          }
                                        },
                                        "x-parser-schema-id": "<anonymous-schema-247>"
                                      },
                                      "x-parser-schema-id": "<anonymous-schema-246>"
                                    },
                                    "productSpecification": {
                                      "description": "ProductSpecification reference. A product specification represents entities that are orderable from the provider of the catalog.",
                                      "required": [
                                        "id",
                                        "@type"
                                      ],
                                      "type": "object",
                                      "properties": {
                                        "id": {
                                          "description": "The identifier of the referred entity.",
                                          "type": "string",
                                          "x-parser-schema-id": "<anonymous-schema-257>"
                                        },
                                        "href": {
                                          "description": "The URI of the referred entity.",
                                          "type": "string",
                                          "x-parser-schema-id": "<anonymous-schema-258>"
                                        },
                                        "name": {
                                          "description": "Name of the referred entity.",
                                          "type": "string",
                                          "x-parser-schema-id": "<anonymous-schema-259>"
                                        },
                                        "version": {
                                          "description": "Version of the product specification.",
                                          "type": "string",
                                          "x-parser-schema-id": "<anonymous-schema-260>"
                                        },
                                        "@baseType": {
                                          "description": "When sub-classing, this defines the super-class.",
                                          "type": "string",
                                          "x-parser-schema-id": "<anonymous-schema-261>"
                                        },
                                        "@schemaLocation": {
                                          "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                          "type": "string",
                                          "format": "uri",
                                          "x-parser-schema-id": "<anonymous-schema-262>"
                                        },
                                        "@type": {
                                          "description": "When sub-classing, this defines the sub-class Extensible name.",
                                          "type": "string",
                                          "x-parser-schema-id": "<anonymous-schema-263>"
                                        },
                                        "@referredType": {
                                          "description": "The actual type of the target instance when needed for disambiguation.",
                                          "type": "string",
                                          "x-parser-schema-id": "<anonymous-schema-264>"
                                        }
                                      },
                                      "x-parser-schema-id": "<anonymous-schema-256>"
                                    },
                                    "productTerm": {
                                      "type": "array",
                                      "items": {
                                        "description": "Description of a productTerm linked to this product. This represent a commitment with a duration.",
                                        "required": [
                                          "@type"
                                        ],
                                        "type": "object",
                                        "properties": {
                                          "description": {
                                            "description": "Description of the productTerm.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-267>"
                                          },
                                          "name": {
                                            "description": "Name of the productTerm.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-268>"
                                          },
                                          "duration": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.itemTerm.items.properties.duration",
                                          "validFor": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.validFor",
                                          "@baseType": {
                                            "description": "When sub-classing, this defines the super-class.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-269>"
                                          },
                                          "@schemaLocation": {
                                            "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                            "type": "string",
                                            "format": "uri",
                                            "x-parser-schema-id": "<anonymous-schema-270>"
                                          },
                                          "@type": {
                                            "description": "When sub-classing, this defines the sub-class Extensible name.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-271>"
                                          }
                                        },
                                        "x-parser-schema-id": "<anonymous-schema-266>"
                                      },
                                      "x-parser-schema-id": "<anonymous-schema-265>"
                                    },
                                    "realizingResource": {
                                      "type": "array",
                                      "items": {
                                        "description": "Resource reference, for when Resource is used by other entities.",
                                        "required": [
                                          "id",
                                          "@type"
                                        ],
                                        "type": "object",
                                        "properties": {
                                          "id": {
                                            "description": "The identifier of the referred entity.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-274>"
                                          },
                                          "href": {
                                            "description": "The URI of the referred entity.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-275>"
                                          },
                                          "name": {
                                            "description": "Name of the referred entity.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-276>"
                                          },
                                          "value": {
                                            "description": "The resource value that can be used to identify a resource with a public key (e.g.: a tel nr, an msisdn)",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-277>"
                                          },
                                          "@baseType": {
                                            "description": "When sub-classing, this defines the super-class.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-278>"
                                          },
                                          "@schemaLocation": {
                                            "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                            "type": "string",
                                            "format": "uri",
                                            "x-parser-schema-id": "<anonymous-schema-279>"
                                          },
                                          "@type": {
                                            "description": "When sub-classing, this defines the sub-class Extensible name.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-280>"
                                          },
                                          "@referredType": {
                                            "description": "The actual type of the target instance when needed for disambiguation.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-281>"
                                          }
                                        },
                                        "x-parser-schema-id": "<anonymous-schema-273>"
                                      },
                                      "x-parser-schema-id": "<anonymous-schema-272>"
                                    },
                                    "realizingService": {
                                      "type": "array",
                                      "items": {
                                        "description": "Service reference, for when Service is used by other entities.",
                                        "required": [
                                          "id",
                                          "@type"
                                        ],
                                        "type": "object",
                                        "properties": {
                                          "id": {
                                            "description": "The identifier of the referred entity.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-284>"
                                          },
                                          "href": {
                                            "description": "The URI of the referred entity.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-285>"
                                          },
                                          "name": {
                                            "description": "Name of the referred entity.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-286>"
                                          },
                                          "@baseType": {
                                            "description": "When sub-classing, this defines the super-class.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-287>"
                                          },
                                          "@schemaLocation": {
                                            "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                            "type": "string",
                                            "format": "uri",
                                            "x-parser-schema-id": "<anonymous-schema-288>"
                                          },
                                          "@type": {
                                            "description": "When sub-classing, this defines the sub-class Extensible name.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-289>"
                                          },
                                          "@referredType": {
                                            "description": "The actual type of the target instance when needed for disambiguation.",
                                            "type": "string",
                                            "x-parser-schema-id": "<anonymous-schema-290>"
                                          }
                                        },
                                        "x-parser-schema-id": "<anonymous-schema-283>"
                                      },
                                      "x-parser-schema-id": "<anonymous-schema-282>"
                                    },
                                    "status": {
                                      "description": "Possible values for the status of the product.",
                                      "type": "string",
                                      "enum": [
                                        "aborted",
                                        "active",
                                        "cancelled",
                                        "created",
                                        "terminated",
                                        "pendingActive",
                                        "pendingTerminate",
                                        "suspended"
                                      ],
                                      "x-parser-schema-id": "<anonymous-schema-291>"
                                    },
                                    "@schemaLocation": {
                                      "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                      "type": "string",
                                      "format": "uri",
                                      "x-parser-schema-id": "<anonymous-schema-292>"
                                    },
                                    "billingAccount": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.billingAccount",
                                    "creationDate": {
                                      "description": "Date and time when the product was created.",
                                      "type": "string",
                                      "format": "date-time",
                                      "x-parser-schema-id": "<anonymous-schema-293>"
                                    }
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-175>"
                                }
                              ],
                              "type": "object",
                              "properties": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.product.items.properties",
                              "x-parser-schema-id": "<anonymous-schema-166>"
                            },
                            "productOffering": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productOffering",
                            "productOrderItemRelationship": {
                              "type": "array",
                              "items": {
                                "description": "Used to describe relationship between Order item. These relationship could have an impact on pricing and conditions.",
                                "required": [
                                  "@type"
                                ],
                                "type": "object",
                                "properties": {
                                  "id": {
                                    "description": "Id of the related Order item (must be in the same Order).",
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-296>"
                                  },
                                  "relationshipType": {
                                    "description": "Relationship type as relies on, bundles, etc...",
                                    "type": "string",
                                    "enum": [
                                      "bundles",
                                      "isChild",
                                      "sells",
                                      "isSold",
                                      "reliesOn",
                                      "reliesFrom",
                                      "migrateTo",
                                      "migrateFrom",
                                      "requires"
                                    ],
                                    "x-parser-schema-id": "<anonymous-schema-297>"
                                  },
                                  "@baseType": {
                                    "description": "When sub-classing, this defines the super-class.",
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-298>"
                                  },
                                  "@schemaLocation": {
                                    "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                    "type": "string",
                                    "format": "uri",
                                    "x-parser-schema-id": "<anonymous-schema-299>"
                                  },
                                  "@type": {
                                    "description": "When sub-classing, this defines the sub-class Extensible name.",
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-300>"
                                  }
                                },
                                "x-parser-schema-id": "<anonymous-schema-295>"
                              },
                              "x-parser-schema-id": "<anonymous-schema-294>"
                            },
                            "state": {
                              "description": "Possible values for the state of the product order item.",
                              "type": "string",
                              "enum": [
                                "acknowledged",
                                "assessingCancellation",
                                "cancelled",
                                "completed",
                                "failed",
                                "held",
                                "inProgress",
                                "pending",
                                "pendingCancellation",
                                "rejected",
                                "partial",
                                "accepted",
                                "draft"
                              ],
                              "x-parser-schema-id": "<anonymous-schema-301>"
                            },
                            "@baseType": {
                              "description": "When sub-classing, this defines the super-class.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-302>"
                            },
                            "@schemaLocation": {
                              "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                              "type": "string",
                              "format": "uri",
                              "x-parser-schema-id": "<anonymous-schema-303>"
                            },
                            "@type": {
                              "description": "When sub-classing, this defines the sub-class Extensible name.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-304>"
                            },
                            "appointment": {
                              "description": "Refers an appointment, such as a customer presentation or internal meeting or site visit.",
                              "required": [
                                "id",
                                "@type"
                              ],
                              "type": "object",
                              "properties": {
                                "id": {
                                  "description": "The identifier of the referred appointment.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-306>"
                                },
                                "href": {
                                  "description": "The reference of the appointment.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-307>"
                                },
                                "description": {
                                  "description": "An explanatory text regarding the appointment made with a party.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-308>"
                                },
                                "@baseType": {
                                  "description": "When sub-classing, this defines the super-class.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-309>"
                                },
                                "@schemaLocation": {
                                  "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                  "type": "string",
                                  "format": "uri",
                                  "x-parser-schema-id": "<anonymous-schema-310>"
                                },
                                "@type": {
                                  "description": "When sub-classing, this defines the sub-class Extensible name.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-311>"
                                },
                                "@referredType": {
                                  "description": "The actual type of the target instance when needed for disambiguation.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-312>"
                                }
                              },
                              "x-parser-schema-id": "<anonymous-schema-305>"
                            },
                            "isInstallable": {
                              "type": "boolean",
                              "description": "Indicates if the product will be installed when created.",
                              "x-parser-schema-id": "<anonymous-schema-313>"
                            },
                            "relatedContactMedium": {
                              "type": "array",
                              "items": {
                                "description": "Related contact of the customer.",
                                "type": "object",
                                "properties": {
                                  "id": {
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-316>"
                                  },
                                  "contactType": {
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-317>"
                                  },
                                  "role": {
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-318>"
                                  },
                                  "preferred": {
                                    "type": "boolean",
                                    "x-parser-schema-id": "<anonymous-schema-319>"
                                  },
                                  "validFor": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.validFor",
                                  "@baseType": {
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-320>"
                                  },
                                  "@schemaLocation": {
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-321>"
                                  },
                                  "@type": {
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-322>"
                                  }
                                },
                                "required": [
                                  "@type"
                                ],
                                "discriminator": "@type",
                                "oneOf": [
                                  {
                                    "description": "Related contact of the customer with email information.",
                                    "type": "object",
                                    "properties": {
                                      "emailAddress": {
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-324>"
                                      }
                                    },
                                    "x-parser-schema-id": "<anonymous-schema-323>"
                                  },
                                  {
                                    "description": "Related contact of the customer with phone contact information.",
                                    "type": "object",
                                    "properties": {
                                      "mobilePhone": {
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-326>"
                                      }
                                    },
                                    "x-parser-schema-id": "<anonymous-schema-325>"
                                  }
                                ],
                                "x-parser-schema-id": "<anonymous-schema-315>"
                              },
                              "x-parser-schema-id": "<anonymous-schema-314>"
                            },
                            "queryProductConfiguration": {
                              "description": "Refers a product configuration.",
                              "type": "object",
                              "properties": {
                                "queryProductConfigurationItemId": {
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-328>"
                                },
                                "queryProductConfigurationId": {
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-329>"
                                },
                                "queryProductConfigurationHref": {
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-330>"
                                },
                                "@type": {
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-331>"
                                }
                              },
                              "required": [
                                "@type"
                              ],
                              "x-parser-schema-id": "<anonymous-schema-327>"
                            }
                          },
                          "x-parser-schema-id": "<anonymous-schema-150>"
                        },
                        "minItems": 1,
                        "x-parser-schema-id": "<anonymous-schema-149>"
                      },
                      "relatedParty": {
                        "type": "array",
                        "items": {
                          "description": "RelatedParty reference. A related party defines party or party role or its reference, linked to a specific entity.",
                          "required": [
                            "@type"
                          ],
                          "type": "object",
                          "properties": {
                            "@type": {
                              "description": "When sub-classing, this defines the sub-class Extensible name.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-334>"
                            },
                            "@baseType": {
                              "description": "When sub-classing, this defines the super-class.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-335>"
                            },
                            "@schemaLocation": {
                              "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                              "type": "string",
                              "format": "uri",
                              "x-parser-schema-id": "<anonymous-schema-336>"
                            },
                            "role": {
                              "description": "Role played by the related party or party role in the context of the specific entity it is linked to. Such as 'initiator', 'customer', 'salesAgent', 'user'.",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-337>"
                            },
                            "partyOrPartyRole": {
                              "description": "A reference to either a Party or a PartyRole.",
                              "discriminator": "@type",
                              "oneOf": [
                                {
                                  "title": "PartyRef",
                                  "description": "A Party reference.",
                                  "required": [
                                    "id"
                                  ],
                                  "type": "object",
                                  "properties": {
                                    "@baseType": {
                                      "description": "When sub-classing, this defines the super-class.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-341>"
                                    },
                                    "@schemaLocation": {
                                      "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                      "type": "string",
                                      "format": "uri",
                                      "x-parser-schema-id": "<anonymous-schema-342>"
                                    },
                                    "href": {
                                      "description": "The URI of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-343>"
                                    },
                                    "id": {
                                      "description": "The identifier of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-344>"
                                    },
                                    "name": {
                                      "description": "Name of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-345>"
                                    },
                                    "@referredType": {
                                      "description": "The actual type of the target instance when needed for disambiguation.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-346>"
                                    }
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-340>"
                                },
                                {
                                  "title": "PartyRoleRef",
                                  "description": "Party role reference. A party role represents the part played by a party in a given context.",
                                  "required": [
                                    "id"
                                  ],
                                  "type": "object",
                                  "properties": {
                                    "partyId": {
                                      "description": "The identifier of the engaged party that is linked to the PartyRole object.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-348>"
                                    },
                                    "partyName": {
                                      "description": "The name of the engaged party that is linked to the PartyRole object.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-349>"
                                    },
                                    "@baseType": {
                                      "description": "When sub-classing, this defines the super-class.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-350>"
                                    },
                                    "@schemaLocation": {
                                      "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                                      "type": "string",
                                      "format": "uri",
                                      "x-parser-schema-id": "<anonymous-schema-351>"
                                    },
                                    "href": {
                                      "description": "The URI of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-352>"
                                    },
                                    "id": {
                                      "description": "The identifier of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-353>"
                                    },
                                    "name": {
                                      "description": "Name of the referred entity.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-354>"
                                    },
                                    "@referredType": {
                                      "description": "The actual type of the target instance when needed for disambiguation.",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-355>"
                                    }
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-347>"
                                }
                              ],
                              "required": [
                                "@type"
                              ],
                              "type": "object",
                              "properties": {
                                "@type": {
                                  "description": "When sub-classing, this defines the sub-class Extensible name.",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-339>"
                                }
                              },
                              "x-parser-schema-id": "<anonymous-schema-338>"
                            }
                          },
                          "x-parser-schema-id": "<anonymous-schema-333>"
                        },
                        "x-parser-schema-id": "<anonymous-schema-332>"
                      },
                      "state": {
                        "description": "Possible values for the state of the order.",
                        "type": "string",
                        "enum": [
                          "acknowledged",
                          "assessingCancellation",
                          "cancelled",
                          "completed",
                          "failed",
                          "held",
                          "inProgress",
                          "partial",
                          "pending",
                          "pendingCancellation",
                          "rejected",
                          "accepted",
                          "draft"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-356>"
                      },
                      "@baseType": {
                        "description": "When sub-classing, this defines the super-class.",
                        "type": "string",
                        "x-parser-schema-id": "<anonymous-schema-357>"
                      },
                      "@schemaLocation": {
                        "description": "A URI to a JSON-Schema file that defines additional attributes and relationships.",
                        "type": "string",
                        "format": "uri",
                        "x-parser-schema-id": "<anonymous-schema-358>"
                      },
                      "@type": {
                        "description": "When sub-classing, this defines the sub-class Extensible name.",
                        "type": "string",
                        "x-parser-schema-id": "<anonymous-schema-359>"
                      },
                      "creationDate": {
                        "description": "Date and time when the ProductOrder was created.",
                        "type": "string",
                        "format": "date-time",
                        "x-parser-schema-id": "<anonymous-schema-360>"
                      }
                    },
                    "AppointmentRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.appointment",
                    "BillingAccountRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.billingAccount",
                    "Characteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items",
                    "BooleanCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[0]",
                    "IntegerCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[1]",
                    "StringCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[2]",
                    "FloatCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[3]",
                    "ObjectCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[4]",
                    "ValidityCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[5]",
                    "AddressCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[11]",
                    "DateCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[12]",
                    "ValidityValue": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[5].properties.value",
                    "IntegerArrayCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[6]",
                    "StringArrayCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[7]",
                    "FloatArrayCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[8]",
                    "ObjectArrayCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[9]",
                    "BooleanArrayCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[10]",
                    "Money": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.price.properties.dutyFreeAmount",
                    "ItemActionType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.action",
                    "OrderItemRelationship": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.productOrderItemRelationship.items",
                    "OrderPrice": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items",
                    "OrderTerm": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.itemTerm.items",
                    "PaymentRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.payment.items",
                    "Price": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.price",
                    "PriceAlteration": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.priceAlteration.items",
                    "ProductOfferingPriceRefOrValue": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice",
                    "ProductOfferingPriceRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[2]",
                    "ProductOfferingPriceCharge": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[1]",
                    "InstallmentCharge": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0]",
                    "ProductOfferingPriceLifecycle": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.lifecycleStatus",
                    "PriceType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.priceType",
                    "ProductOfferingPriceRelationship": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.popRelationship.items",
                    "POPRelationshipType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.popRelationship.items.properties.relationshipType",
                    "TaxItem": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.tax.items",
                    "ProductOfferingRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productOffering",
                    "ProductOrderItem": {
                      "description": "An identified part of the order. A product order is decomposed into one or more order items.",
                      "required": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.required",
                      "type": "object",
                      "properties": {
                        "id": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.id",
                        "quantity": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.quantity",
                        "action": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.action",
                        "billingAccount": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.billingAccount",
                        "itemPrice": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.itemPrice",
                        "itemTerm": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.itemTerm",
                        "payment": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.payment",
                        "product": {
                          "description": "A product to be created defined by value or existing defined by reference.",
                          "discriminator": "@type",
                          "oneOf": [
                            "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[0]",
                            "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1]"
                          ],
                          "type": "object",
                          "properties": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.product.items.properties"
                        },
                        "productOffering": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productOffering",
                        "productOrderItemRelationship": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.productOrderItemRelationship",
                        "state": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.state",
                        "@baseType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.@baseType",
                        "@schemaLocation": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.@schemaLocation",
                        "@type": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.@type",
                        "appointment": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.appointment",
                        "isInstallable": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.isInstallable",
                        "relatedContactMedium": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.relatedContactMedium",
                        "queryProductConfiguration": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.queryProductConfiguration"
                      }
                    },
                    "ProductOrderItemStateType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.state",
                    "ProductOrderStateType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.state",
                    "ProductRefOrValue": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.product.items",
                    "ProductRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[0]",
                    "Product": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1]",
                    "ProductRelationship": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productRelationship.items",
                    "ProductSpecificationRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productSpecification",
                    "ProductStatusType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.status",
                    "ProductTerm": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productTerm.items",
                    "Quantity": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.recurringChargePeriod",
                    "Duration": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.itemTerm.items.properties.duration",
                    "RelatedChannel": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.channel.items",
                    "ChannelRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.channel.items.properties.channel",
                    "RelatedPartyRefOrPartyRoleRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.relatedParty.items",
                    "PartyRefOrPartyRoleRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.relatedParty.items.properties.partyOrPartyRole",
                    "PartyRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.relatedParty.items.properties.partyOrPartyRole.oneOf[0]",
                    "PartyRoleRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.relatedParty.items.properties.partyOrPartyRole.oneOf[1]",
                    "ResourceRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.realizingResource.items",
                    "ServiceRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.realizingService.items",
                    "RelatedContactMedium": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.relatedContactMedium.items",
                    "EmailContactMedium": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.relatedContactMedium.items.oneOf[0]",
                    "PhoneContactMedium": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.relatedContactMedium.items.oneOf[1]",
                    "QueryProductConfigurationRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.queryProductConfiguration",
                    "TimePeriod": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.validFor",
                    "RelationshipType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.productOrderItemRelationship.items.properties.relationshipType"
                  }
                }
              }
            },
            "required": [
              "event"
            ]
          }
        },
        "bindings": {
          "kafka": {
            "groupId": "output-group-1"
          }
        }
      }
    },
    "disco.order-management.productOrderAttributeValueChange-event": {
      "description": "The topic on which the product order attribute value change events may be produced or consumed.",
      "subscribe": {
        "x-scs-function-name": "subscribeProductOrderAttributeValueChange",
        "summary": "Events related to the attribute value change of the product order.",
        "operationId": "productOrderAttributeValueChange",
        "traits": [
          {
            "bindings": {
              "kafka": {
                "groupId": "input-group-1"
              }
            }
          }
        ],
        "message": {
          "name": "productOrderAttributeValueChangeEvent",
          "title": "Change event about the product order attribute value change.",
          "summary": "This event is about the product order attribute value change. This event is often published following a successful update of the product order attribute value.",
          "payload": {
            "$id": "https://orange.com/productOrderAttributeValueChangeEvent.yaml",
            "$schema": "http://json-schema.org/draft-07/schema#",
            "title": "ProductOrderAttributeValueChangeEvent",
            "type": "object",
            "allOf": [
              {
                "$id": "https://orange.com/basedEvent.yaml",
                "$schema": "http://json-schema.org/draft-07/schema#",
                "title": "BaseEvent",
                "type": "object",
                "properties": {
                  "eventId": {
                    "type": "string",
                    "description": "The identifier of the notification.",
                    "x-parser-schema-id": "<anonymous-schema-369>"
                  },
                  "eventTime": {
                    "type": "string",
                    "format": "date-time",
                    "description": "Time of the event occurrence.",
                    "x-parser-schema-id": "<anonymous-schema-370>"
                  },
                  "eventType": {
                    "type": "string",
                    "description": "The type of the notification.",
                    "x-parser-schema-id": "<anonymous-schema-371>"
                  },
                  "correlationId": {
                    "type": "string",
                    "description": "The correlation id for this event.",
                    "x-parser-schema-id": "<anonymous-schema-372>"
                  }
                },
                "required": [
                  "eventId",
                  "eventTime",
                  "eventType"
                ]
              }
            ],
            "extends": "$ref:$.channels.disco.order-management.productOrderAttributeValueChange-event.subscribe.message.payload.allOf[0]",
            "properties": {
              "event": {
                "$id": "https://orange.com/productOrderAttributePayloadEvent.yaml",
                "$schema": "http://json-schema.org/draft-07/schema#",
                "type": "object",
                "title": "ProductOrderAttributePayloadEvent",
                "required": [
                  "productOrder"
                ],
                "properties": {
                  "productOrder": {
                    "$id": "https://event.com/productorder.yaml",
                    "$schema": "http://json-schema.org/draft-07/schema#",
                    "title": "ProductOrder",
                    "description": "A Product Order is a type of order which can be used to place an order between a customer and a service provider or between a service provider and a partner and vice versa.",
                    "required": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.required",
                    "type": "object",
                    "existingJavaType": "com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder",
                    "properties": {
                      "id": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.id",
                      "href": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.href",
                      "cancellationDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.cancellationDate",
                      "cancellationReason": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.cancellationReason",
                      "category": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.category",
                      "completionDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.completionDate",
                      "description": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.description",
                      "expectedCompletionDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.expectedCompletionDate",
                      "notificationContact": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.notificationContact",
                      "priority": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.priority",
                      "requestedCompletionDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.requestedCompletionDate",
                      "requestedStartDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.requestedStartDate",
                      "billingAccount": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.billingAccount",
                      "channel": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.channel",
                      "orderTotalPrice": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice",
                      "payment": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.payment",
                      "productOrderItem": {
                        "type": "array",
                        "items": {
                          "description": "An identified part of the order. A product order is decomposed into one or more order items.",
                          "required": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.required",
                          "type": "object",
                          "properties": {
                            "id": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.id",
                            "quantity": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.quantity",
                            "action": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.action",
                            "billingAccount": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.billingAccount",
                            "itemPrice": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.itemPrice",
                            "itemTerm": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.itemTerm",
                            "payment": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.payment",
                            "product": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.product.items",
                            "productOffering": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productOffering",
                            "productOrderItemRelationship": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.productOrderItemRelationship",
                            "state": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.state",
                            "@baseType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.@baseType",
                            "@schemaLocation": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.@schemaLocation",
                            "@type": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.@type",
                            "appointment": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.appointment",
                            "isInstallable": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.isInstallable",
                            "relatedContactMedium": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.relatedContactMedium",
                            "queryProductConfiguration": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.queryProductConfiguration"
                          },
                          "x-parser-schema-id": "<anonymous-schema-368>"
                        },
                        "minItems": 1,
                        "x-parser-schema-id": "<anonymous-schema-367>"
                      },
                      "relatedParty": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.relatedParty",
                      "state": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.state",
                      "@baseType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.@baseType",
                      "@schemaLocation": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.@schemaLocation",
                      "@type": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.@type",
                      "creationDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.creationDate"
                    },
                    "AppointmentRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.appointment",
                    "BillingAccountRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.billingAccount",
                    "Characteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items",
                    "BooleanCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[0]",
                    "IntegerCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[1]",
                    "StringCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[2]",
                    "FloatCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[3]",
                    "ObjectCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[4]",
                    "ValidityCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[5]",
                    "AddressCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[11]",
                    "DateCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[12]",
                    "ValidityValue": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[5].properties.value",
                    "IntegerArrayCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[6]",
                    "StringArrayCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[7]",
                    "FloatArrayCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[8]",
                    "ObjectArrayCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[9]",
                    "BooleanArrayCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[10]",
                    "Money": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.price.properties.dutyFreeAmount",
                    "ItemActionType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.action",
                    "OrderItemRelationship": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.productOrderItemRelationship.items",
                    "OrderPrice": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items",
                    "OrderTerm": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.itemTerm.items",
                    "PaymentRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.payment.items",
                    "Price": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.price",
                    "PriceAlteration": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.priceAlteration.items",
                    "ProductOfferingPriceRefOrValue": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice",
                    "ProductOfferingPriceRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[2]",
                    "ProductOfferingPriceCharge": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[1]",
                    "InstallmentCharge": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0]",
                    "ProductOfferingPriceLifecycle": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.lifecycleStatus",
                    "PriceType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.priceType",
                    "ProductOfferingPriceRelationship": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.popRelationship.items",
                    "POPRelationshipType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.popRelationship.items.properties.relationshipType",
                    "TaxItem": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.tax.items",
                    "ProductOfferingRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productOffering",
                    "ProductOrderItem": "$ref:$.channels.disco.order-management.productOrderAttributeValueChange-event.subscribe.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items",
                    "ProductOrderItemStateType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.state",
                    "ProductOrderStateType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.state",
                    "ProductRefOrValue": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.product.items",
                    "ProductRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[0]",
                    "Product": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1]",
                    "ProductRelationship": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productRelationship.items",
                    "ProductSpecificationRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productSpecification",
                    "ProductStatusType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.status",
                    "ProductTerm": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productTerm.items",
                    "Quantity": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.recurringChargePeriod",
                    "Duration": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.itemTerm.items.properties.duration",
                    "RelatedChannel": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.channel.items",
                    "ChannelRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.channel.items.properties.channel",
                    "RelatedPartyRefOrPartyRoleRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.relatedParty.items",
                    "PartyRefOrPartyRoleRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.relatedParty.items.properties.partyOrPartyRole",
                    "PartyRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.relatedParty.items.properties.partyOrPartyRole.oneOf[0]",
                    "PartyRoleRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.relatedParty.items.properties.partyOrPartyRole.oneOf[1]",
                    "ResourceRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.realizingResource.items",
                    "ServiceRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.realizingService.items",
                    "RelatedContactMedium": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.relatedContactMedium.items",
                    "EmailContactMedium": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.relatedContactMedium.items.oneOf[0]",
                    "PhoneContactMedium": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.relatedContactMedium.items.oneOf[1]",
                    "QueryProductConfigurationRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.queryProductConfiguration",
                    "TimePeriod": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.validFor",
                    "RelationshipType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.productOrderItemRelationship.items.properties.relationshipType"
                  }
                }
              }
            },
            "required": [
              "event"
            ]
          }
        },
        "bindings": {
          "kafka": {
            "groupId": "input-group-1"
          }
        }
      }
    },
    "disco.order-management.productOrderStateChange-event": {
      "subscribe": {
        "x-scs-function-name": "productOrderEventConsumer",
        "description": "Consuming ProductOrderStateChangeEvent to create the product order follow-up event",
        "operationId": "productOrderEventConsumer",
        "traits": [
          "$ref:$.channels.disco.order-management.productOrderAttributeValueChange-event.subscribe.traits[0]"
        ],
        "message": {
          "name": "productOrderStateChangeEvent",
          "title": "Change event about the product order state.",
          "summary": "This event is about the productOrder  state change. This event is often published following a successful update of the product order state in the product order inventory.",
          "payload": {
            "$id": "https://orange.com/productOrderStateChangeEvent.yaml",
            "$schema": "http://json-schema.org/draft-07/schema#",
            "title": "productOrderStateChangeEvent",
            "type": "object",
            "allOf": [
              "$ref:$.channels.disco.order-management.productOrderAttributeValueChange-event.subscribe.message.payload.allOf[0]"
            ],
            "extends": "$ref:$.channels.disco.order-management.productOrderAttributeValueChange-event.subscribe.message.payload.allOf[0]",
            "properties": {
              "event": {
                "$id": "https://orange.com/productOrderPayloadEvent.yaml",
                "$schema": "http://json-schema.org/draft-07/schema#",
                "type": "object",
                "title": "ProductOrderPayloadEvent",
                "required": [
                  "productOrder"
                ],
                "properties": {
                  "productOrder": {
                    "$id": "https://event.com/productorder.yaml",
                    "$schema": "http://json-schema.org/draft-07/schema#",
                    "title": "ProductOrder",
                    "description": "A Product Order is a type of order which can be used to place an order between a customer and a service provider or between a service provider and a partner and vice versa.",
                    "required": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.required",
                    "type": "object",
                    "existingJavaType": "com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder",
                    "properties": {
                      "id": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.id",
                      "href": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.href",
                      "cancellationDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.cancellationDate",
                      "cancellationReason": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.cancellationReason",
                      "category": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.category",
                      "completionDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.completionDate",
                      "description": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.description",
                      "expectedCompletionDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.expectedCompletionDate",
                      "notificationContact": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.notificationContact",
                      "priority": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.priority",
                      "requestedCompletionDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.requestedCompletionDate",
                      "requestedStartDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.requestedStartDate",
                      "billingAccount": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.billingAccount",
                      "channel": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.channel",
                      "orderTotalPrice": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice",
                      "payment": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.payment",
                      "productOrderItem": {
                        "type": "array",
                        "items": {
                          "description": "An identified part of the order. A product order is decomposed into one or more order items.",
                          "required": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.required",
                          "type": "object",
                          "properties": {
                            "id": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.id",
                            "quantity": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.quantity",
                            "action": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.action",
                            "billingAccount": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.billingAccount",
                            "itemPrice": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.itemPrice",
                            "itemTerm": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.itemTerm",
                            "payment": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.payment",
                            "product": {
                              "description": "A product to be created defined by value or existing defined by reference.",
                              "discriminator": "@type",
                              "oneOf": [
                                "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[0]",
                                {
                                  "title": "Product",
                                  "description": "A product offering procured by a customer or other interested party playing a party role. A product is realized as one or more service(s) and / or resource(s).",
                                  "type": "object",
                                  "properties": {
                                    "id": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.id",
                                    "@baseType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.@baseType",
                                    "href": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.href",
                                    "description": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.description",
                                    "isBundle": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.isBundle",
                                    "isCustomerVisible": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.isCustomerVisible",
                                    "name": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.name",
                                    "orderDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.orderDate",
                                    "productSerialNumber": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productSerialNumber",
                                    "startDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.startDate",
                                    "terminationDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.terminationDate",
                                    "product": {
                                      "type": "array",
                                      "items": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.product.items",
                                      "x-parser-schema-id": "<anonymous-schema-377>"
                                    },
                                    "productCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic",
                                    "productOffering": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productOffering",
                                    "productRelationship": {
                                      "type": "array",
                                      "items": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productRelationship.items",
                                      "x-parser-schema-id": "<anonymous-schema-378>"
                                    },
                                    "productSpecification": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productSpecification",
                                    "productTerm": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productTerm",
                                    "realizingResource": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.realizingResource",
                                    "realizingService": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.realizingService",
                                    "status": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.status",
                                    "@schemaLocation": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.@schemaLocation",
                                    "billingAccount": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.billingAccount",
                                    "creationDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.creationDate"
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-376>"
                                }
                              ],
                              "type": "object",
                              "properties": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.product.items.properties",
                              "x-parser-schema-id": "<anonymous-schema-375>"
                            },
                            "productOffering": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productOffering",
                            "productOrderItemRelationship": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.productOrderItemRelationship",
                            "state": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.state",
                            "@baseType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.@baseType",
                            "@schemaLocation": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.@schemaLocation",
                            "@type": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.@type",
                            "appointment": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.appointment",
                            "isInstallable": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.isInstallable",
                            "relatedContactMedium": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.relatedContactMedium",
                            "queryProductConfiguration": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.queryProductConfiguration"
                          },
                          "x-parser-schema-id": "<anonymous-schema-374>"
                        },
                        "minItems": 1,
                        "x-parser-schema-id": "<anonymous-schema-373>"
                      },
                      "relatedParty": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.relatedParty",
                      "state": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.state",
                      "@baseType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.@baseType",
                      "@schemaLocation": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.@schemaLocation",
                      "@type": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.@type",
                      "creationDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.creationDate"
                    },
                    "AppointmentRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.appointment",
                    "BillingAccountRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.billingAccount",
                    "Characteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items",
                    "BooleanCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[0]",
                    "IntegerCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[1]",
                    "StringCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[2]",
                    "FloatCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[3]",
                    "ObjectCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[4]",
                    "ValidityCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[5]",
                    "AddressCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[11]",
                    "DateCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[12]",
                    "ValidityValue": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[5].properties.value",
                    "IntegerArrayCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[6]",
                    "StringArrayCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[7]",
                    "FloatArrayCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[8]",
                    "ObjectArrayCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[9]",
                    "BooleanArrayCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic.items.oneOf[10]",
                    "Money": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.price.properties.dutyFreeAmount",
                    "ItemActionType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.action",
                    "OrderItemRelationship": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.productOrderItemRelationship.items",
                    "OrderPrice": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items",
                    "OrderTerm": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.itemTerm.items",
                    "PaymentRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.payment.items",
                    "Price": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.price",
                    "PriceAlteration": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.priceAlteration.items",
                    "ProductOfferingPriceRefOrValue": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice",
                    "ProductOfferingPriceRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[2]",
                    "ProductOfferingPriceCharge": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[1]",
                    "InstallmentCharge": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0]",
                    "ProductOfferingPriceLifecycle": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.lifecycleStatus",
                    "PriceType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.priceType",
                    "ProductOfferingPriceRelationship": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.popRelationship.items",
                    "POPRelationshipType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.popRelationship.items.properties.relationshipType",
                    "TaxItem": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.tax.items",
                    "ProductOfferingRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productOffering",
                    "ProductOrderItem": {
                      "description": "An identified part of the order. A product order is decomposed into one or more order items.",
                      "required": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.required",
                      "type": "object",
                      "properties": {
                        "id": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.id",
                        "quantity": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.quantity",
                        "action": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.action",
                        "billingAccount": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.billingAccount",
                        "itemPrice": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.itemPrice",
                        "itemTerm": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.itemTerm",
                        "payment": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.payment",
                        "product": {
                          "description": "A product to be created defined by value or existing defined by reference.",
                          "discriminator": "@type",
                          "oneOf": [
                            "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[0]",
                            {
                              "title": "Product",
                              "description": "A product offering procured by a customer or other interested party playing a party role. A product is realized as one or more service(s) and / or resource(s).",
                              "type": "object",
                              "properties": {
                                "id": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.id",
                                "@baseType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.@baseType",
                                "href": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.href",
                                "description": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.description",
                                "isBundle": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.isBundle",
                                "isCustomerVisible": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.isCustomerVisible",
                                "name": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.name",
                                "orderDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.orderDate",
                                "productSerialNumber": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productSerialNumber",
                                "startDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.startDate",
                                "terminationDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.terminationDate",
                                "product": {
                                  "type": "array",
                                  "items": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.product.items"
                                },
                                "productCharacteristic": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productCharacteristic",
                                "productOffering": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productOffering",
                                "productRelationship": {
                                  "type": "array",
                                  "items": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productRelationship.items"
                                },
                                "productSpecification": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productSpecification",
                                "productTerm": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productTerm",
                                "realizingResource": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.realizingResource",
                                "realizingService": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.realizingService",
                                "status": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.status",
                                "@schemaLocation": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.@schemaLocation",
                                "billingAccount": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.billingAccount",
                                "creationDate": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.creationDate"
                              }
                            }
                          ],
                          "type": "object",
                          "properties": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.product.items.properties"
                        },
                        "productOffering": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productOffering",
                        "productOrderItemRelationship": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.productOrderItemRelationship",
                        "state": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.state",
                        "@baseType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.@baseType",
                        "@schemaLocation": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.@schemaLocation",
                        "@type": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.@type",
                        "appointment": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.appointment",
                        "isInstallable": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.isInstallable",
                        "relatedContactMedium": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.relatedContactMedium",
                        "queryProductConfiguration": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.queryProductConfiguration"
                      }
                    },
                    "ProductOrderItemStateType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.state",
                    "ProductOrderStateType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.state",
                    "ProductRefOrValue": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.product.items",
                    "ProductRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[0]",
                    "Product": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1]",
                    "ProductRelationship": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productRelationship.items",
                    "ProductSpecificationRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productSpecification",
                    "ProductStatusType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.status",
                    "ProductTerm": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.productTerm.items",
                    "Quantity": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.recurringChargePeriod",
                    "Duration": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.itemTerm.items.properties.duration",
                    "RelatedChannel": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.channel.items",
                    "ChannelRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.channel.items.properties.channel",
                    "RelatedPartyRefOrPartyRoleRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.relatedParty.items",
                    "PartyRefOrPartyRoleRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.relatedParty.items.properties.partyOrPartyRole",
                    "PartyRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.relatedParty.items.properties.partyOrPartyRole.oneOf[0]",
                    "PartyRoleRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.relatedParty.items.properties.partyOrPartyRole.oneOf[1]",
                    "ResourceRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.realizingResource.items",
                    "ServiceRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.product.oneOf[1].properties.realizingService.items",
                    "RelatedContactMedium": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.relatedContactMedium.items",
                    "EmailContactMedium": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.relatedContactMedium.items.oneOf[0]",
                    "PhoneContactMedium": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.relatedContactMedium.items.oneOf[1]",
                    "QueryProductConfigurationRef": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.queryProductConfiguration",
                    "TimePeriod": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.orderTotalPrice.items.properties.productOfferingPrice.oneOf[0].properties.validFor",
                    "RelationshipType": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message.payload.properties.event.properties.productOrder.properties.productOrderItem.items.properties.productOrderItemRelationship.items.properties.relationshipType"
                  }
                }
              }
            },
            "required": [
              "event"
            ]
          }
        },
        "bindings": {
          "kafka": {
            "groupId": "input-group-1"
          }
        }
      },
      "publish": {
        "description": "Publishing product order state change event",
        "x-scs-function-name": "productOrderEventConsumerPublishEvent",
        "operationId": "productOrderEventProducer",
        "traits": [
          "$ref:$.channels.disco.order-management.productOrderChange-command.publish.traits[0]"
        ],
        "message": "$ref:$.channels.disco.order-management.productOrderStateChange-event.subscribe.message",
        "bindings": {
          "kafka": {
            "groupId": "output-group-1"
          }
        }
      }
    },
    "disco.order-orchestration.orchestrationPlanNodeStateChange-event": {
      "subscribe": {
        "x-scs-function-name": "orchestrationPlanNodeProcess-in-0",
        "operationId": "OrchestrationPlanNodeStatusChangeEventConsumer",
        "traits": [
          "$ref:$.channels.disco.order-management.productOrderAttributeValueChange-event.subscribe.traits[0]"
        ],
        "message": {
          "name": "orchestrationPlanNodeStateChangeEvent",
          "title": "orchestration Plan Node event.",
          "summary": "Contains orchestration plan node event.",
          "payload": {
            "$id": "https://orange.com/orchestrationPlanNodeStateChangeEvent.yaml",
            "$schema": "http://json-schema.org/draft-07/schema#",
            "title": "OrchestrationPlanNodeStateChangeEvent",
            "type": "object",
            "allOf": [
              "$ref:$.channels.disco.order-management.productOrderAttributeValueChange-event.subscribe.message.payload.allOf[0]"
            ],
            "extends": "$ref:$.channels.disco.order-management.productOrderAttributeValueChange-event.subscribe.message.payload.allOf[0]",
            "properties": {
              "event": {
                "type": "object",
                "properties": {
                  "orchestrationPlanNode": {
                    "description": "Orchestration Plan Node",
                    "title": "OrchestrationPlanNode",
                    "required": [
                      "relatedProductOrderItem"
                    ],
                    "type": "object",
                    "properties": {
                      "id": {
                        "description": "Orchestration Plan Node's id",
                        "type": "string",
                        "x-parser-schema-id": "<anonymous-schema-381>"
                      },
                      "state": {
                        "description": "Orchestration Plan Node States",
                        "title": "OrchestrationPlanNodeStateEnum",
                        "type": "string",
                        "enum": [
                          "Initialized",
                          "Acknowledged",
                          "InProgress",
                          "InDelivery",
                          "Held",
                          "Aborted",
                          "Rejected",
                          "Failed",
                          "Completed"
                        ],
                        "x-parser-schema-id": "<anonymous-schema-382>"
                      },
                      "relatedServiceOrder": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-384>"
                          },
                          "orderItemId": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-385>"
                          },
                          "SOMRef": {
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-386>"
                          }
                        },
                        "title": "RelatedServiceOrder",
                        "x-parser-schema-id": "<anonymous-schema-383>"
                      },
                      "relatedProductOrder": {
                        "title": "RelatedProductOrder",
                        "description": "Related Product Order",
                        "required": [
                          "id"
                        ],
                        "type": "object",
                        "properties": {
                          "id": {
                            "description": "Related Product Order's id",
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-388>"
                          }
                        },
                        "x-parser-schema-id": "<anonymous-schema-387>"
                      },
                      "relatedProductOrderItem": {
                        "description": "Orchestration Plan Node's related product order item",
                        "type": "array",
                        "items": {
                          "title": "RelatedProductOrderItem",
                          "description": "Related Product Order Item",
                          "required": [
                            "id"
                          ],
                          "type": "object",
                          "properties": {
                            "id": {
                              "description": "Related Product Order Item's id",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-391>"
                            },
                            "action": {
                              "description": "Related Product Order Item's action",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-392>"
                            }
                          },
                          "x-parser-schema-id": "<anonymous-schema-390>"
                        },
                        "x-parser-schema-id": "<anonymous-schema-389>"
                      },
                      "relatedSupplyChainOrderItem": {
                        "title": "RelatedSupplyChainOrderItem",
                        "description": "Related Supply Chain Order Item",
                        "type": "object",
                        "properties": {
                          "id": {
                            "description": "Related Supply Chain Order Item's id",
                            "type": "string",
                            "x-parser-schema-id": "<anonymous-schema-394>"
                          }
                        },
                        "x-parser-schema-id": "<anonymous-schema-393>"
                      },
                      "relatedProduct": {
                        "description": "Orchestration Plan Node's related product",
                        "type": "array",
                        "items": {
                          "title": "RelatedProduct",
                          "description": "Related Product",
                          "type": "object",
                          "properties": {
                            "id": {
                              "description": "Related Product's id",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-397>"
                            },
                            "realisingService": {
                              "description": "Related Product's realising service",
                              "type": "array",
                              "items": {
                                "title": "RealisingService",
                                "description": "Realising Service",
                                "type": "object",
                                "properties": {
                                  "id": {
                                    "description": "Realising Service's id",
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-400>"
                                  },
                                  "href": {
                                    "description": "Realising Service's href",
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-401>"
                                  }
                                },
                                "x-parser-schema-id": "<anonymous-schema-399>"
                              },
                              "x-parser-schema-id": "<anonymous-schema-398>"
                            },
                            "relationshipType": {
                              "title": "RelatedProductRelationTypeEnum",
                              "description": "Related Product Relation Type Enum",
                              "type": "string",
                              "enum": [
                                "delivers",
                                "reliesOn",
                                "reliesFrom"
                              ],
                              "x-parser-schema-id": "<anonymous-schema-402>"
                            },
                            "productSpecification": {
                              "title": "ProductSpecification",
                              "description": "Product Specification",
                              "type": "object",
                              "properties": {
                                "id": {
                                  "description": "Product Specification's id",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-404>"
                                },
                                "name": {
                                  "description": "Product Specification's name",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-405>"
                                },
                                "href": {
                                  "description": "Product Specification's href",
                                  "type": "string",
                                  "x-parser-schema-id": "<anonymous-schema-406>"
                                },
                                "serviceSpecification": {
                                  "description": "Product Specification's service specification",
                                  "type": "array",
                                  "items": {
                                    "title": "ServiceSpecification",
                                    "description": "Service Specification",
                                    "type": "object",
                                    "properties": {
                                      "@baseType": {
                                        "description": "Service Specification's Base type",
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-409>"
                                      },
                                      "@referredType": {
                                        "description": "Service Specification's referred type",
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-410>"
                                      },
                                      "@schemaLocation": {
                                        "description": "Service Specification's schema location",
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-411>"
                                      },
                                      "@type": {
                                        "description": "Service Specification's type",
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-412>"
                                      },
                                      "href": {
                                        "description": "Service Specification's href",
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-413>"
                                      },
                                      "id": {
                                        "description": "Service Specification's id",
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-414>"
                                      },
                                      "name": {
                                        "description": "Service Specification's name",
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-415>"
                                      },
                                      "version": {
                                        "description": "Service Specification's version",
                                        "type": "string",
                                        "x-parser-schema-id": "<anonymous-schema-416>"
                                      }
                                    },
                                    "x-parser-schema-id": "<anonymous-schema-408>"
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-407>"
                                },
                                "stockItemType": {
                                  "title": "StockItemType",
                                  "description": "Stock Item Type",
                                  "type": "object",
                                  "properties": {
                                    "id": {
                                      "description": "Stock Item Type's id",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-418>"
                                    },
                                    "name": {
                                      "description": "Stock Item Type's name",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-419>"
                                    },
                                    "EAN": {
                                      "description": "Stock Item Type's EAN",
                                      "type": "string",
                                      "x-parser-schema-id": "<anonymous-schema-420>"
                                    }
                                  },
                                  "x-parser-schema-id": "<anonymous-schema-417>"
                                }
                              },
                              "x-parser-schema-id": "<anonymous-schema-403>"
                            },
                            "isInstallable": {
                              "type": "boolean",
                              "x-parser-schema-id": "<anonymous-schema-421>"
                            },
                            "@type": {
                              "type": "string",
                              "enum": [
                                "CFS",
                                "physicalProduct",
                                "shipmentProduct"
                              ],
                              "x-parser-schema-id": "<anonymous-schema-422>"
                            },
                            "productOrderItemId": {
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-423>"
                            },
                            "productCharacteristic": {
                              "type": "array",
                              "items": {
                                "type": "object",
                                "properties": {
                                  "name": {
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-426>"
                                  },
                                  "valueType": {
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-427>"
                                  },
                                  "value": {
                                    "type": "string",
                                    "x-parser-schema-id": "<anonymous-schema-428>"
                                  }
                                },
                                "x-parser-schema-id": "<anonymous-schema-425>"
                              },
                              "x-parser-schema-id": "<anonymous-schema-424>"
                            }
                          },
                          "x-parser-schema-id": "<anonymous-schema-396>"
                        },
                        "x-parser-schema-id": "<anonymous-schema-395>"
                      },
                      "relatedOrchestrationPlanNode": {
                        "description": "Orchestration Plan Node's related orchestration plan nodes",
                        "type": "array",
                        "items": {
                          "title": "RelatedOrchestrationPlanNode",
                          "description": "Related Orchestration Plan Node for specific node",
                          "type": "object",
                          "properties": {
                            "relatedNodeId": {
                              "description": "Related Orchestration Plan Node's id",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-431>"
                            },
                            "relationshipType": {
                              "title": "RelatedOrchestrationPlanNodeRelationTypeEnum",
                              "description": "Related Orchestration Plan Node RelationType",
                              "type": "string",
                              "enum": [
                                "DeliverWith",
                                "DeliverAfter"
                              ],
                              "x-parser-schema-id": "<anonymous-schema-432>"
                            }
                          },
                          "x-parser-schema-id": "<anonymous-schema-430>"
                        },
                        "x-parser-schema-id": "<anonymous-schema-429>"
                      },
                      "errorMessage": {
                        "description": "Orchestration Plan Node's error message if exists",
                        "type": "array",
                        "items": {
                          "title": "OrchestrationNodeErrorMessage",
                          "description": "Orchestration Node Error Message",
                          "type": "object",
                          "properties": {
                            "code": {
                              "description": "error code",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-435>"
                            },
                            "message": {
                              "description": "error message",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-436>"
                            },
                            "reason": {
                              "description": "error reason",
                              "type": "string",
                              "x-parser-schema-id": "<anonymous-schema-437>"
                            },
                            "timeStamp": {
                              "description": "time of error's occurrence",
                              "type": "string",
                              "format": "date-time",
                              "x-parser-schema-id": "<anonymous-schema-438>"
                            }
                          },
                          "x-parser-schema-id": "<anonymous-schema-434>"
                        },
                        "x-parser-schema-id": "<anonymous-schema-433>"
                      }
                    },
                    "x-parser-schema-id": "<anonymous-schema-380>"
                  }
                },
                "required": [
                  "orchestrationPlanNode"
                ],
                "title": "OrchestrationPlanNodeStateChangePayloadEvent",
                "x-parser-schema-id": "<anonymous-schema-379>"
              }
            },
            "required": [
              "event"
            ],
            "OrchestrationPlanNodeStateChangePayloadEvent": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event",
            "OrchestrationPlanNode": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode",
            "OrchestrationPlanNodeStateEnum": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.state",
            "RelatedProductOrder": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.relatedProductOrder",
            "RelatedProductOrderItem": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.relatedProductOrderItem.items",
            "ObjectId": {
              "type": "object",
              "properties": {
                "id": {
                  "type": "string"
                }
              },
              "title": "ObjectId",
              "required": [
                "id"
              ]
            },
            "RelatedServiceOrder": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.relatedServiceOrder",
            "RelatedSupplyChainOrderItem": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.relatedSupplyChainOrderItem",
            "RelatedProduct": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.relatedProduct.items",
            "ProductCharacteristic": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.relatedProduct.items.properties.productCharacteristic.items",
            "RelatedOrchestrationPlanNode": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.relatedOrchestrationPlanNode.items",
            "RelatedOrchestrationPlanNodeRelationTypeEnum": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.relatedOrchestrationPlanNode.items.properties.relationshipType",
            "OrchestrationNodeErrorMessage": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.errorMessage.items",
            "RealisingService": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.relatedProduct.items.properties.realisingService.items",
            "RelatedProductRelationTypeEnum": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.relatedProduct.items.properties.relationshipType",
            "RelatedProductTypeEnum": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.relatedProduct.items.properties.@type",
            "ProductSpecification": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.relatedProduct.items.properties.productSpecification",
            "ServiceSpecification": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.relatedProduct.items.properties.productSpecification.properties.serviceSpecification.items",
            "StockItemType": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message.payload.properties.event.properties.orchestrationPlanNode.properties.relatedProduct.items.properties.productSpecification.properties.stockItemType"
          }
        },
        "bindings": {
          "kafka": {
            "groupId": "input-group-1"
          }
        }
      }
    }
  },
  "components": {
    "operationTraits": {
      "input-group-1": "$ref:$.channels.disco.order-management.productOrderAttributeValueChange-event.subscribe.traits[0]",
      "input-group-2": {
        "bindings": {
          "kafka": {
            "groupId": "input-group-2"
          }
        }
      },
      "output-group-1": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.traits[0]"
    },
    "messageTraits": {
      "productOrderHeaders": {
        "headers": {
          "type": "object",
          "properties": {
            "correlationId": {
              "description": "Unique identifier for correlating related messages",
              "type": "string",
              "x-parser-schema-id": "<anonymous-schema-440>"
            },
            "productOrderId": {
              "description": "The id of the productOrder",
              "type": "string",
              "x-parser-schema-id": "<anonymous-schema-441>"
            }
          },
          "x-parser-schema-id": "<anonymous-schema-439>"
        }
      }
    },
    "messages": {
      "productOrderAttributeValueChange": "$ref:$.channels.disco.order-management.productOrderAttributeValueChange-event.subscribe.message",
      "productOrderChangeCommand": "$ref:$.channels.disco.order-management.productOrderChange-command.publish.message",
      "productOrderStateChangeEvent": "$ref:$.channels.disco.order-management.productOrderStateChange-event.subscribe.message",
      "orchestrationPlanNodeStateChangeEvent": "$ref:$.channels.disco.order-orchestration.orchestrationPlanNodeStateChange-event.subscribe.message"
    }
  },
  "x-parser-spec-parsed": true,
  "x-parser-api-version": 3,
  "x-parser-circular": true,
  "x-parser-spec-stringified": true
};
    const config = {"show":{"sidebar":true},"sidebar":{"showOperations":"byDefault"}};
    const appRoot = document.getElementById('root');
    AsyncApiStandalone.render(
        { schema, config, }, appRoot
    );
  