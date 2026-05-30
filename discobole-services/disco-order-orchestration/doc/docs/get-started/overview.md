---
title: Introduction
summary: Introduction
authors:
  - Omar Abdalla
---

## What & why

!!! abstract "What is DISCOBOLE Customer Order Orchestration and Delivery?"

    The Customer Order Orchestration & Distribution (COOD) is positioned as part of the Open Digital Architecture (ODA), in the Core Commerce Management layer and maps to a component called TMFC003 Product Order Delivery Orchestration and Management.

    It is in charge of:
    
    - __Creating a dynamic orchestration delivery plan__ for valid product orders (status accepted). The order items in this plan are linked on the product specification level of information available in the Product Catalog (ex: prerequisite links between product specifications, links between product and CFS specifications, …). 
    
    - __Executing the orchestration plan delivery process__ on the appropriate delivery date in the correct order based on the product specification level order items relationships.
    
    - __Delivering the product items__ by preparing a service order/shipment order to the production system in charge to the related CFS/tangible product specification each ordered product corresponds. Then, updating the status of the product specification level order items, and of the related product items. So it triggers the updates of the related inventories.

## Where to start

!!! success "Understand the use cases supported by Order Orchestration and Distribution (COOD)"

    - Understand the supported [*uses cases*](../use-cases/) and how it can be useful to you.

!!! success "Browse this site"

    - Discover the [__APIs__](../api-references/user-guides) exposed in Order Orchestration and Distribution (COOD) that you can apply in your projects
    - Check COOD's [__architecture__](../architecture/overall-architecture.md) and the [architecture decisions](../architecture/about-adr.md) that let to it
    - Discover the [__Administration UI__](https://discobole.ow2.io/doc/frontends/frontend-admin-ui/#order-orchestration-distribution-administration-ui)
  
!!! success "Jump to the code repository"

    - Browse the [code repository](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration) on GitLab.

## History & Perspectives

!!! info "History & Perspectives"

    - This suite of OpenSource components are based on TM Forum ODA component definition, implements the TM Forum APIs and is based on TM Forum SID data model.
    - The COOD architecture is microservices-based, cloud-native and hosted on a private cloud.

!!! info "Contact"

    - Want to know more? Please drop a message here
