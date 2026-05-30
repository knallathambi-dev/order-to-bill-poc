---
title: APIs Overall
summary: Describe the usage of the APIs.
authors:
  - Ayatullah Abdulhakim
  - Mohamed Issa
---

# APIs Overall

## Exposed APIs

| Name                                    | Function                                                                                                                                                            | Resource                                | Verb                               |
|:----------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------------------|:-----------------------------------|
| Customer Order Orchestration & Delivery | Retrieve orchestration plans by id or List orchestration plans by filter                                                                                            | Orchestration Plan                      | GET /orchestrationPlan/{id} GET    |
| Lead Time                               | Retrieve orchestration plans lead time statistics by product spec id, or contract name                                                                              | Orchestration Plan Lead Time Statistics | GET /leadTimeHistoryStatistics GET |
| Fallout Management                      | List orchestration plans                                                                                                                                            | Fallout Incident                        | GET /falloutIncident/{id} GET      |
| Process Flow	                           | The Process Flow API allows management of business process(Fallout Incident). It provides all required information to achieve business task requiring manual action | Process Flow / Task Flow                | POST PATCH/{id} GET/{id} GET       |

## Consumed APIs

| Name                        | Function                                                                                                                                                                                                                | Resource              | Verb     |
|:----------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------|:---------|
| Product Catalog Management  | Provides a standardized solution for rapidly adding partners products to existing Catalog for Service Providers to directly feed partners systems.	                                                                     | Product Specification | GET      |
| Service Catalog Management  | Centralize and organize all available services, enabling users to discover, request, and consume them efficiently. It ensures consistent service delivery, governance, and lifecycle management across the organization | Service Catalog       | GET/{id} |
| Service Ordering Management | Standardizes the creation, tracking, and fulfillment of service orders. It ensures seamless integration with service catalogs and inventory systems for accurate and efficient service delivery                         | Service Order         | POST     |
| Shipping Management         | Standardizes the creation, tracking, and coordination of shipping orders. It ensures seamless integration with inventory and ordering systems for efficient and transparent delivery processes.                         | Shipping Order        | POST     |

## Exposed Events

| Name                                    | Description                                                                                                                                     | State                                                            |
|:----------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------|
| Customer Order Orchestration & Delivery | COOD itself generates this event as it progresses through its orchestration plan. It reflects changes in the state of orchestration plan nodes. | State change events, internal events (Delivery Start and Status) |

## Consumed Events

| Name                      | Function                                                                                                                                                                                        | Listener           |
|:--------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-------------------|
| Product Order Management  | Standardizes the creation, tracking, and fulfillment of product orders. It ensures seamless integration with product catalogs and inventory systems for accurate and efficient order processing | State Change event |
| Service Order Management  | Standardizes the creation, tracking, and fulfillment of service orders. It ensures seamless integration with service catalogs and inventory systems for accurate and efficient service delivery | State Change event |
| Shipping Order Management | Standardizes the creation, tracking, and coordination of shipping orders. It ensures seamless integration with inventory and ordering systems for efficient and transparent delivery processes  | State Change event |
