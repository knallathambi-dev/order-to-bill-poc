---
title: API Specifications
summary: describe the specification of the APIs.
authors:
  - Abir BRADAI
---

# API Specifications

???+ info "OpenAPI Specifications"
    The Order Management Component exposes 3 TMF APIs:

    - **Ordering Inventory API**: this API is responsible for the product order inventory; it is derived from the TMF 622 Product Ordering Manangement API REST Specification
    - **Order Capture API**: this API is responsible of the customer journey to capture orders; it is derived from the TMF 701 Process Flow API REST specification
    - **Order Follow-up API**: this API is responsible of the order capture follow-up; it is derived from the TMF 701 Process Flow API REST specification

???+ info "AsyncAPI Specifications"
    The Product Order Inventory application publishes and consumes events on and from different topics:

    - Publish `productOrderStateChange` event.
    - Publish `productOrderAttributeValueChange` event.
    - Receive real-time event about the product delivery.
    - Receive commands message from the product order capture service in order to update  the product order state and attributes into the product order inventory.
