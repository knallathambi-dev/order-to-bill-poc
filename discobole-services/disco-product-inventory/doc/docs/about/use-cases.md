---
title: Use Cases
summary: Product inventory uses cases.
authors:
  - Hajer DAOUD
---

# List Of Use Cases

Below a none exhaustive list of use cases.

!!! success "Acquisition Use Case"
    - Validate offer’s identifiers in product catalog
    - Handle hierarchical product creation
    - Bulk  update of status hierarchy
    - Manage product main lifecycle
    - Manage product operational lifecycle
    - Multiple product research options

!!! success "Modification Use Case"
    - Add one or multiple products to an existing hierarchy
    - Bulk  update of status hierarchy
    - add relationship reliesOn/reliesFrom to an existing hierarchy
    - Fetch products by filtering (RelatedParty.ID and @type="contract")
    - Update productCharacteristic

!!! success "Tangible Product Delivery Use Case"
    - Create Tangible product with @Type PhysicalProduct
    - Create Tangible product with @Type ShipmentProduct
    - Validate resource’s attributes in resource inventory
    - Validate offer’s identifiers and specific attributes in catalog
    - Manage Tangible product main lifecycle
    - Manage Tangible product operational lifecycle

!!! success "Product End Date Use Case"
    - Create product with validity with specifc productCharacteristic
    - Calculation of expiry date
    - Terminate product that expire and publish event notification

!!! success "Export Use Case"
    - Export database with filtering criteria to flat file (json or csv)
    - Create task to planify an export

!!! success "Purge Use Case"
    - Create scheduled tasks to run the product or jobs purge. Those tasks physically delete products or jobs from the database based on given criteria

!!! success "Reporting Use Case"
    - Display and generate reports on products

!!! success "Migration Use Case"
    - Identify migrated from and migrated to products

!!! success "Installment Plans Use Case"
    - Support installment charge

!!! success "Discount Application Use Case"
    - Support discount application for specific time frame

!!! success "Price Rounding Rules Use Case"
    - Apply dynamic price rounding rules: Implement rounding according to currency-specific formats for both persisted and calculated prices
