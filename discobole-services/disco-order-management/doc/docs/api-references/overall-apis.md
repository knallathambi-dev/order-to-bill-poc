---
title: API & Event Reference Guides
summary: describe the usage of the APIs.
authors:
  - Hassene OULED SGHAIER
  - Mohamed Amine MACHERKI
---

# APIs Overall

## Exposed APIs

| **Name** | **Id** | **Function** | **Resource** | **Verb** |
|:-----------------------------------|:--------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------|:--------------------------|
| Product Order                      | TMF622        | Provides a standardized mechanism for placing a product order with all the necessary order parameters. The API consists of a simple set of operations that interact with CRM/Order negotiation systems in a consistent manner. A product order is created based on a product offering that is defined in a catalog. The product offering identifies the product or set of products that are available to a customer and includes characteristics such as pricing, product options and market. | Product Order               | GET, GET /{id}, POST      |
| Process Flow                       | TMF701        | The Process Flow API allows management of business process. It provides all required information to achieve business task requiring manual action.                                                                                                                                                                                                                                                                                                                                            | Process Flow, Task Flow     | POST, PATCH               |

## Consumed APIs

| **Name** | **Id** | **Function** | **Resource** | **Verb** |
|:------------------------------------------|:--------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------------------------------------|:--------------------------------|
| User role and permission management       | TMF672  | Provides the model definition and operations to manage user roles and permissions for manageable assets.                                                                        | User role                              | GET, GET /{id}                  |
| Product Catalog Management                | TMF620  | Provides a standardized solution for rapidly adding partners products to existing Catalog for Service Providers to directly feed partners systems.                              | Product Catalog                        | GET                             |
| Product Inventory Management              | TMF637  | Provides standardized mechanism for product inventory management such as creation, partial or full update and retrieval of the representation of a product in the inventory.    | Product                                | GET, GET /{id}, POST, PATCH     |
| Party Role Management                     | TMF669  | A standardized mechanism for general party roles and includes operations such as creation, update, retrieval, deletion and notification of events.                              | Party Role                             | GET, POST                       |
| Product Offering Qualification Management | TMF679  | Product Offering Qualification API is one of Pre-Ordering Management API Family. Product Offering Qualification API goal is to provide Product Offering commercial eligibility. | Product Offering Qualification         | GET                             |
| Product Configuration Management          | TMF760  | The Product Configuration API uses product catalog data, policy data, and existing product inventory data to assist engagement managent systems with product configuration.     | Product Configuration                  | GET                             |

## Exposed Events

| **Name**                         | **Id** | **Function**                                                                                                                                                                       | **State**                    |
|:---------------------------------|:-------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-----------------------------|
| ProductOrderStateChange          | TMF622 | This event is generated within the order management system (validated order) to initiate the delivery of product orders. It serves as the starting point for the delivery process. | State Change event           |
| ProductOrderAttributeValueChange | TMF622 | This event is about the product order attribute value change. It is often published following a successful update of the product order attribute value.                             | Attribute Value Change event |

## Consumed Events

| **Name**                         | **Id**  | **Component**                                       | **Function**                                                                                                                                                         | **Listener**       |
|:---------------------------------|:--------|:----------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-------------------|
| OrchestrationPlanNodeStateChange | TMFC003 | Product Order Delivery Orchestration and Management | Consumed to create the product order follow-up event. It reflects changes in the state of orchestration plan nodes as COOD progresses through its orchestration plan. | State Change event |
