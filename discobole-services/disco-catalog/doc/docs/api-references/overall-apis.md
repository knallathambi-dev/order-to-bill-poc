---
title: API Reference Guides
summary: Describe the usage of the APIs.
author:
  - Sumit
---

# APIs overall

## **Exposed APIs**

| **Name**                    | **Id**        | **Function**                                                                                                                                       | **Resource**                                                                                                                | **Verb**                                                                                       |
|: -----------------------------------|: --------------|: --------------------------------------------------------------------------------------------------------------------------------------------------|: ----------------------------------------------------------------------------------------------------------------------------|: ------------------------------------------------------------------------------------------------|
| Product Catalog Management      | TMF620        | Provides a standardized solution for rapidly adding partners products to existing Catalog for Service Providers to directly feed partners systems.  | - Product Offering <br> - Product Specification <br> - Product Offering Price <br> - Category                                   | - GET <br> - GET /id <br> - POST <br> - PATCH <br> - DELETE                                     |
| Process Flow                       | TMF701        | The Process Flow API allows management of business process. It provides all required information to achieve business task requiring manual action.  | - Process Flow             <br> - Task Flow                                                                                                                         | - POST <br> - PATCH                                                                             |
| Policy Rule                        | TMF723        | The Policy API provides a consistent way to define policy specification. It contains data on how the Policy Rule is used in a managed environment.  | - policyRule <br> - policyDomain <br> - policyEvent <br> - policyCondition <br> - policyAction                              | - GET <br> - GET /id <br> - POST <br> - PATCH <br> - DELETE                                     |
| Product Catalog Administration  | not a TMF API | Provides management that includes CRUD operations for entities like catalog constants (currencies, units of measures, etc.). Not a TMF prescribed API. | - cpibConfiguration <br> - currency <br> - frequencyTypes <br> - Product Catalog Administration <br> - productCatalogAdministrationError  <br> - unit           | - POST <br> - PATCH <br> - PUT <br> - DELETE                                                    |

Service Catalog Management and Stock Item APIs are exposed as mock APIs.

## **Consumed APIs**

| **Name**                   | **Id**        | **Function**                                                                                                                    | **Resource**                                                                                  | **Verb**                                                                                       |
|: -----------------------------------|: --------------|: --------------------------------------------------------------------------------------------------------------------------------|: ----------------------------------------------------------------------------------------------|: ------------------------------------------------------------------------------------------------|
| User Role and Permission Management| TMF672        | Provides the model definition and operations to manage user roles and permissions for manageable assets.                         | - Permission <br> - User Role                                                                  | - GET <br> - GET /id <br> - POST <br> - PATCH <br> - DELETE                                     |

## **Consumed Events**

| **Name**         | **Id**     | **Function**                                                    | **Listener/Hub**                                                                                         |
|: ------------------|: -----------|: -----------------------------------------------------------------|: ----------------------------------------------------------------------------------------------------------|
| TMF688 Event Management | TMF688 | Manage Topic <br> Any Event creation entry point (POST Event)   | Hub: Used to subscribe to an API notification <br> A hub will be created for each listener.                 |
