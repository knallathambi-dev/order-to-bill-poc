---
title: API Reference Guides
summary: Describe the specification of the APIs.
authors:
  - Mostafa Abdelwahab
  - Hajer DAOUD
---

# API Specifications

???+ info "OpenAPI Specifications"
    The Product Inventory Component exposes the TMF637 Product Inventory Management API REST Specification that has been enriched.

    This API enables inventory items creation, inventory organization, inventory search or filter, inventory monitoring and tracking, inventory control and inventory auditing.

???+ info "AsyncAPI Specifications"
    The Product Inventory Component exposes The Product Inventory AsyncAPI that allows you to publish or consume Product change events, as follows:

    - Publish `productAttributeValueChangeEvent` events
    - Publish `productDeleteEvent` events
    - Publish `productCreateEvent` events
    - Publish `productStateChangeEvent` events
    - Consume `productOrderStateChangeEvent` events
