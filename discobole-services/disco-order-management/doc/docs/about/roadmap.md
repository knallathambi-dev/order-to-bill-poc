---
title: Roadmap
summary: Order management upcoming features.
authors:
  - Abir BRADAI
  - Catherine DAGUISE
  - Mohamed Amine MACHERKI
---

# Order Management Roadmap

## Implementation

??? success "Release 10 - March 2026"
    - Enable purchase of devices with installment payment options
    - Apply discounts starting from a configurable date, available for a specified period and frequency
    - Dynamically manage price rounding rules across the entire DISCOBOLE suite, including currency-specific decimals
    - Complete security and performance tests
    - Performance improvements and bug fixes
    - Security vulnerabilities fixes
    - Eco design best practices implementation
    - Integrate microfrontend architecture within the admin portal

??? success "Release 9 - November 2025"
    - Support nominal migration use case
    - Support Technical eligibility step in order capture process for fiber offer
    - Start performance tests
    - Security audit with penetration tests completed
    - Support price and discount display with tax management
    - Security vulnerabilities fixes
    - Eco design best practices implementation

??? success "Release 8 - June 2025"
    - Open Source Ready under MIT License under OW2 community within [DISCOBOLE](https://discobole.ow2.io/doc/)
    - Support for multiple Offer Purchases by allowing users to purchase the same offer multiple times at once
    - Improve price management business logic for better accuracy

??? success "Release 7 - March 2025"
    - Support complex offer ordering for acquisition, modification and termination use cases
    - Manage end-of-bill cycle information to improve postpaid processes
    - Enable to turn on/off configurables process tasks

??? success "Release 6 - October 2024"
    - Evolve postpaid use cases capabilities
    - Support partner products ordering

??? success "Release 5 - June 2024"
    - Evolution in order capture FE to support modification use case
    - Enable/disable order capture process automated task through UI
    - Expand order administration view with order details
    - Support TMF model for product configuration
    - Support acquisition of accessories
    - Support postpaid use case

??? success "Release 4 - March 2024"
    - Support  termination use case (terminate contract)
    - Support  new & prospect customer use cases
    - Support  tangible product ordering
    - Evolution of the order capture FE
    - Expand order administration view

??? success "Release 3 - December 2023"
    - Support  modification use case (add, modify and delete options)
    - Order inventory graphical view through and administration tool
    - Keycloak integration

??? success "Release 2 - September 2023"
    - Initial order capture front-end for demo
    - Expose TMF622 productOrdering API

??? success "Release 1 - July 2023"
     - Implementation of order capture and order follow-up processes for a mobile offer acquisition use case
     - Product order data storage and management
     - Initial deployment & integration

## Work in Progress

??? abstract "Release 11 - July 2026"
    - Enable order modifications: Customers can modify their orders after confirmation or validation, enhancing flexibility
    - eSIM offer acquisition: Customers can order offers in either SIM or eSIM formats
    - Purge product order inventory: Implement processes to remove product orders from inventory based on configurable criteria
    - Single Shot Order process flow a simplified process that allows automatic order creation without customer interaction

## Backlog

??? info "Release 12 - November 2026"
    - Allow swap options: Enable customers to switch between SIM and eSIM
    - MSISDN change
    - Allow Mobile Number Portability (MNP): Support port-in and port-out processes
    - Allow MSISDN selection: Enable customers to choose their preferred number during the order capture journey
