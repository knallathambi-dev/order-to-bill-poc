---
title: Use Cases
summary: Use Cases.
authors:
  - Omar Abdalla
  - Catherine DAGUISE
---

# List of Use Cases

Below a non exhaustive list of use cases. Offers can be mobile, fiber or convergent offers.

!!! success "Offer Acquisition"
    - Based on received product order event for a given customer, COOD creates dynamically an orchestration delivery plan taking into account all received different order items and consults the Product Catalog to build their relationships on product spec level.
    - COOD executes the delivery plan and validates the state of the to be delivered products at the customer Product Inventory, as well as any needed prerequisites before preparing the service/shipment order request that will be sent to the production systems to initiate the delivery of CFS/tangible products.
    - COOD updates the customer Product Inventory with the service/shipment order status update and triggers a change state event for other components.
  
!!! success "Offer Modification"
    - Customer can customize their existing contracts according to their evolving preferences. This includes the ability to :
      - add new options
      - adjust existing options characteristics
      - terminating options
    - COOD creates an orchestration delivery plan taking into account the specific changes made to the existing contract in the order.
    - COOD executes the delivery plan and validates the state of the to be delivered products at the customer Product Inventory, as well as any needed prerequisites/dependents before preparing the service order request that will be sent to the production systems to initiate the delivery/modification/termination of CFS.
    - COOD updates the customer Product Inventory with the product system service order status update and triggers a change state event for other components.

!!! success "Offer Termination"
    - Customer can terminate their existing contract.
    - COOD creates an orchestration delivery plan taking into account the order of CFS termination by starting with the dependents.
    - COOD executes the delivery plan and validates the state of the to be delivered products at the customer Product Inventory, as well as the state of the dependents before preparing the service order request that will be sent to the production systems to initiate the termination of CFS.
    - COOD updates the customer Product Inventory with the product system service order status update and triggers a change state event for other components.
    - COOD doesn't handle tangible product termination.

!!! success "Offer Migration"
    - Customer can migrate their existing contract/bundle/product to another one.
    - COOD creates an orchestration delivery plan taking into to deliver the new product with the target characteristics and/or modify existing products.
    - For commercial migration, COOD updates the product status in the Product Inventory without launching a SOM request, while in operational migration normal delivery process is followed and product status is updated in the Product Inventory after SOM request if fulfilled.
    - COOD executes the delivery plan after checking the state of the dependents including the MigratedFrom product, before submitting the SOM request.
    - There is no migration for tangible product.

!!! success "Multi-Purchase Offer"
    - Customer can subscribe to an offer or bundle multiple times.
    - COOD creates an orchestration delivery and builds the relationships between different order items, COOD makes use of the relations defined in the order in addition to the product specifications from the Catalog.
    - The main evolution takes place in the orchestration plan creation.
