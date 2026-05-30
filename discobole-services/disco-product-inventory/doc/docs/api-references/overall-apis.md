---
title: API Reference Guides
summary: Describe the usage of the APIs.
authors:
  - Hajer DAOUD
  - Mohamed ABDELGAWAD
---

# APIs Overall

## Exposed API

| Name                       | Id   | Function                                                                                                                                                                   | Resource                  | Verb                  |
|:-----------------------------|:-------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------|:------------------------|
| Product Inventory Management | TMF637 | Provides standardized mechanism for product inventory management such as creation, partial or full update and retrieval of the representation of a product in the inventory. | Product                   | GET, POST, PATCH      |

## Consumed APIs

| Name                                | Id     | Function                                                                                               | Resource             | Verb        |
|:------------------------------------|:-------|:-------------------------------------------------------------------------------------------------------|:---------------------|:------------|
| User role and permission management | TMF672 | Provides the model definition and operations to manage user roles and permissions for manageable assets. | User role            | GET         |
| Product Catalog Management          | TMF620 | Is used to validate products to make sure that they are in the catalog.                                | Product Catalog      | GET /{id}   |
| Resource Inventory Management       | TMF639 | Is used to validate tangible products.                                                                 | Resource Inventory   | GET /{id}   |

## Exposed Events

| Name                          | TMF code | Description                                                                                                                                                                       | state                        |
|:------------------------------|:---------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-----------------------------|
| ProductAttributeValueChange   | TMF637   | This event is generated within the Product Inventory. Currently only when product termination date is set.                                                                        | Attribute Value Change event |
| ProductStateChange            | TMF637   | When product validity expires, the batch mechanism terminates the active products and subsequently product inventory publishes a ProductStateChange event.                         | State Change event           |
| ProductCreate                 | TMF637   | This event is generated within the Product Inventory when a new product is successfully created.                                                                                  | Create event                 |
| ProductDelete                 | TMF637   | This event is generated within the Product Inventory when an existing product is successfully deleted.                                                                            | Delete event                 |
