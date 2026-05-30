---
title: API Reference Guides
summary: Describe the specification of the APIs.
author:
  -  Sumit
---

# APIs Specifications

???+ info "OpenAPI Specifications"
    The Product Catalog Management Component exposes several TMF APIs:

    - **Product Catalog API**: this API is an implementation of the TMF620 Product Catalog Management API REST Specification, responsible for the reading of a catalog of products. It is exposed as several microservices (a microservice per REST entities)
    - **Product Catalog Management API**: this API is an implementation of the TMF701 Process Flow Management API REST Specification, responsible for the creation/update/deletion of a catalog of products; it manages all entity types of the catalog (product offering, category, product specification, product offering price, product lifecycle)
    - **Policy Rule API**: this API is an implementation of the TMF723 Policy Management API REST Specification, responsible for the management of policies
    - **Event Management API**: this API is a mock of the TMF688 Event Management API REST Specification, responsible for providing a standardized client interface to the enterprise event management system. 
    - **Product Lifecycle Management API**: this API is an implementation of the TMF701 Process Flow Management  API REST Specification, responsible for the management of the product offering lifecycle.
