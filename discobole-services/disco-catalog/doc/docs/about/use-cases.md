---
title: Use Cases
summary: Product Catalog Use Cases.
author:
  - Sumit
  - Akshay Sahni
---

# List of use cases

User can seamlessly create & launch new offerings in commercial catalog, which shall be available for consumption by consuming components.

Management of product model having a three level product offering hierarchy : Contract, Bundles, Atomic offerings.

- Modelling optional atomic and bundled product offerings.
- Modelling of charges, alterations, taxes and installment charges  
- Modelling relationships between entities.
- Modelling charges and discounts based on commercial actions.
- Modelling of tangible product offerings and shipment for them.
- Modelling policy rules for pricing, discounting, migration and eligibility.

## Operations Supported

!!! info "Note"
    - Operations listed below are supported via both UI and exposed APIs.

!!! success "For Product Offering Entities"
    - Product Offering creation & modification.
    - Management of lifecycle status for Product Offerings.
    - Retrieve Product Offering by id.
    - Retrieve Product Offering by name.
    - Retrieve Product Offering by brand.
    - Retrieve Product Offering by market segment.
    - Retrieve Product Offering by category.
    - Retrieve Product Offering by channel.
    - Retrieve Product Offering by type eg:- bundle,atomic and contract.
    - Management of product offerings with a commitment period (product offering term).
    - Mannagement of allowed actions per channel for contracts.

!!! success "For Product Specification Entities"
    - Product Specification creation & modification.
    - Management of lifecycle status for Product Specifications.
    - Retrieve Product Specification by id.
    - Retrieve Product Specification by name.
    - Retrieve Product Specification by brand.
    - Retrieve Product Specification by support entity id.
    - Retrieve Product Specification by support enttity type.
    - Retrieve Product Specification by related party.
    - Management of shipping product specifications, to manage shipment of tangible products.
    - Replication of delivery factory(ies) for orchestration.

!!! success "For Product Offering Price Entities"
    - Product Offering Price creation.
    - Management of relationship(s) between Product Offering Prices to associate multiple charges and taxes.
    - Managing applicability of Product Offering Price using validity
    - Definition of charges, alterations, tax & installment charges.
    - Retrieve Product Offering Price by id.
    - Retrieve Product Offering Price by type.
    - Retrieve Product Offering Price by validity.
    - Retrieve Product Offering Price by proration type.
       
!!! success "For Category Entities"
    - Category creation.
    - Retrieve categories.
    - Retrieve category by id.
    - Retrieve subcategory by id.
    - Retrieve root categories.
    - Retrieve categories filtered by Product Offering id.
    - Retrieve list of Root categories.

!!! success "Management of lifecycle of Product Offering, Product Specification and Category"
    - Management of lifecycle status of entites.

!!! success "Policy Rules"
    - Policy rule creation.
    - Modification of an existing policy rule.
    - De-association of a policy rule.
    - Delete a policy rule entity.

!!! success "Export Job: export of the following entities"
    - product offerings
    - product specifications
    - service specifications
    - stock items

!!! success "Product Catalog Administration"
    - Define & manage currencies
    - Define & manage units
    - Define & manage market segments
    - Define & manage channels
    - Define & manage restrictions on PS relationships, checks on installed base


