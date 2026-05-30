---
title: Introduction
summary: Introduction to Product Catalog
author:
  - Sumit
  - Akshay Sahni
---

# Introduction

## What & Why

!!! abstract "What is DISCOBOLE Product Catalog?"
    The Product Catalog component of the [DISCOBOLE Suite](https://discobole.ow2.io/doc) is responsible for organizing the collection of Products and  Product Offerings specifications that identify and define all requirements of a product or a product offering that can be commercialized.

    - The Product Catalog Management component has the functionality that enables present a customer-facing view, so users can search and select products and product offerings they need. The Product Catalog Management component enables define Product specifications based on CFS specifications published by the Service Catalog Management component - or for tangible products on Resource specifications published by the Resource Catalog Management component.
    - It ensures consistency between the characteristics and their possible values at service and product level. The Product Catalog Management component has functionalities that include creation of new product/product offering specifications, managing these specifications, administering the lifecycle of products/product offerings, describing relationships between product specifications (such as pre-requisite), between product offerings (such as packaging rules) and between product specifications and product offering specification (such as product offerings that commercialize a product specification and the related conditions of pricing or configuration restrictions).
    - It is also in charge of reporting on product/product offering specification and their changes and facilitating easy and systematic indexing and access to product/product offering, as well as faciliating  the product order capture process - and the articulation with the delivery orchestration process.
    - The Product Catalog component also provides for a front-end for end users which is access controlled to manage the entities defined in the product catalog. The front end helps manage the application for administrative tasks like granting access, performing global changes and also enables management of product catalog data.
    
!!! abstract "ODACAT"
    ODACAT is a pseudonym for the [DISCOBOLE](https://discobole.ow2.io/doc/) implementation of product catalog. The component derives this friendly name from the amalgamation of ODA - Open Digital Architecture and CATalogue (ODACAT). Any references to ODACAT or product catalog mean the same component throughout this documentation.

???+ summary "Product Catalog is part of the [DISCOBOLE](https://discobole.ow2.io/doc/) Suite."
    [DISCOBOLE](https://discobole.ow2.io/doc/) stands for Digital & Innovative OpenSource Platform for Core commerce management, it represents a suite of components covering the "Order To Bill" and Order orchestration scope within the ODA "Open Digital Architecture" core commerce management domain.

## Where to Start

!!! success "Understand the [_uses cases_](https://discobole.ow2.io/disco-oda-components/disco-security/doc/about/use-cases/) covered by User Role and how it can be useful to you"

!!! success "Product Catalog UI to administer the product catalog"
     - [__Production Environment__](https://catalog-ui-disco.apps.fr01.paas.tech.orange/product-catalog-ui)

!!! success "Browse this site"
     - Check [DISCOBOLE](https://discobole.ow2.io/doc/) Product Catalog [__architecture__](https://discobole.ow2.io/disco-oda-components/disco-catalog/doc/architecture/overall-architecture/) and the [architecture decisions](https://discobole.ow2.io/disco-oda-components/disco-catalog/doc/architecture/about-adr/) that led to it.
     - Interact with an already deployed instance, understand its [development ecosystem](https://discobole.ow2.io/disco-oda-components/disco-catalog/doc/deployments/development-ecosystem/) and deploy your own catalog.

!!! success "Jump to the [code repository](https://gilab.ow2.org/discobole/disco-catalog) on GitLab"
    Browse the [code repository](https://gilab.ow2.org/discobole/disco-catalog) on GitLab.

## History and Perspectives

!!! info "History and Perspectives"
    - The project is based on TM Forum ODA component definition, implements the TMF Forum APIs and is based on TMF SID data model.
    - The architecture is a micro-service based, hosted on a private cloud and is based on the [CQRS design pattern](https://discobole.ow2.io/disco-oda-components/disco-catalog/doc/architecture/overall-architecture/#design-pattern-and-frameworks), Event sourcing and ADR.
    - The functional use case or the eTOM function that the component implements is Product Catalog Management.
    - The resources of the Product Catalog component are based on the Product Catalog Management TMF620 API v4.0.1 and consumes the TMF701 Process Flow API to create and modify catalog entities.
    - The component is continuously being evolved as of date with new features with the target to have a complete catalog driven solution.
