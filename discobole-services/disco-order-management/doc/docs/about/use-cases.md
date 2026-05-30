---
title: Use Cases
summary: use cases.
authors:
  - Abir BRADAI & Hassene OULED SGHAIER
---
#  List of Use Cases

## Business Use Cases

Below a non-exhaustive list of the possible use cases so far:

!!! success "Prepaid Mobile Offer Acquisition"
    User can seamlessly acquire new mobile offer launched in commercial catalog.

    Acquisition could be performed through a consistent cross channel ordering journey.  
    Acquisition could be performed in a logged or a non-logged mode.  
    Acquisition could include or not a tangible product such as a physical SIM card or a handset.  
    
    
    OM runs the order capture process:

    - Validate the selected offer  
    - Check commercial eligibility  
    - Launch configuration session  
    - Create/update order  
    - Validate order  
    - Reserve resource(s) (if required)  
    - Validate order payment (if required)  
    - Create/update products  
    - Publish order accepted event  
    
    OM runs the order follow-up process: 

    - Capture the product delivery event(s)  
    - Update order and order item(s) status accordingly  
    - Update product(s) accordingly  

!!! success "Postpaid Mobile Offer Acquisition"
      In the postpaid acquisition scenario, users can easily subscribe to new mobile offers available in the commercial catalog.  
      This acquisition process ensures a seamless experience through a consistent, cross-channel ordering journey, whether the user is logged in or not.  
      The offer may include digital or physical products, such as a SIM card or handset.

!!! success "Standalone Accessories Ordering"
      In the Standalone Accessories Ordering use case, users can independently purchase accessories from the catalog without needing to bundle them with other products.  
      This process provides a streamlined, cross-channel experience and can be completed in either logged-in or guest mode.

!!! success "Migration"
    Users can migrate their offers to eligible target offers.
    Migration involves transferring contract offers and may include both bundle and atomic offer migrations as potential actions within the overall contract offer migration process.

!!! success "Fiber"
    Users can seamlessly acquire new fiber offers launched in the commercial catalog. The acquisition process is designed to be consistent across multiple channels, providing a smooth ordering experience. Specific checks at the order capture stage for fiber offers include:

    - Verification of technical eligibility  
    - Appointment booking

!!! success "Mobile Offer Modification"
    Customer has the ability to customize his mobile contract according to his evolving preference.  
    This includes the dynamic ability to add new option(s), modify option(s) characteristic(s), and/or terminate option(s).

    OM runs the order capture process:

    - Validate the selected contract identifier  
    - Create/Update order  
    - Validate order  
    - Reserve resource(s) (if required)  
    - Validate order payment (if required)  
    - Create/update products  
    - Publish order accpeted event  
    
    OM runs the order follow-up process:

    - Capture the product delivery event(s)  
    - Update order and order item(s) status accordingly  
    - Update product(s) accordingly  

!!! success "Managing Order Changes"
    In the use case acquisition or modification or termination or migration the customers should be able to modify their choices and offer configurations after they have already confirmed or validated their order.


!!! success "Mobile Contract Termination"
    Customer has the ability to terminate his mobile contract including all the related products.

    OM runs the order capture process:

    - Validate the selected contract identifier  
    - Create order for contract termination  
    - Validate order  
    - Validate order payment (if required)  
    - Update product to the termination order item reference  
    - Publish order accepted event  
    
    OM run the order follow-up process:

    - Capture the product delivery event(s)  
    - Update order and order item(s) status accordingly  
    - Update product(s) accordingly  

!!! success "Complex offer ordering"
    Users can subscribe to, modify, or terminate complex offers, including:

    - Multiple bundles  
    - Multi-level bundles  
    - Optional bundles  
    
    The business logic dynamically supports the ordering process, order creation, and order updates for nearly all types of offer structures in acquisition, modification, or termination use cases.

    **Examples**:

    - Purchase a contract offer that includes a mobile bundle with a mobile line, connectivity, and a SIM card, as well as a device bundle that includes a handset, a case and device insurance.  
    - Purchase a contract that includes a mobile bundle, which contains a device bundle as a child (demonstrating multiple levels of bundles). 


!!! success "Installment Plans"

    - Enable the purchase of devices with installment payment

    - Provide a comprehensive pricing breakdown within the order details, including the upfront payment, down payment, selected installment period, interest rate, and the partner responsible for the financing

    - Provide administrators the ability to dynamically enable or disable a configurable "Check Financial Eligibility" step during the order validation process

    - When the "Check Financial Eligibility" option is enabled, this validation step utilizes the TMF 632 API, "Party Management," to verify the user's rating score and confirm their eligibility for the installation offer before proceeding with order capture


!!! success "Discount Applications for Specific Time Frames"

    - Apply promotional discount pricing granted exclusively during specific time frames

    - Manage both non-recurring and recurring discounts that begin from a designated month and continue for a configurable duration

    - Calculate and validate price validity periods dynamically based on when the discount becomes active and its intended duration

!!! success "Price Rounding Rules"

    - Implement flexible, currency-specific rounding rules to ensure consistent pricing calculations and user experience across the system

    - Utilize built-in coding language libraries to identify the ISO 4217 currency code and dynamically enforce the appropriate decimal formatting

    - Restrict the decimal points for all individual prices persisted in the database during order creation and update operations

    - Calculate total recurring and non-recurring charges using pre-formatted prices, universally applying a half-up rounding mode to guarantee data consistency

    - Enforce strict decimal point display restrictions on front-end administration interfaces for all received and calculated monetary amounts

## Administration Use Cases
!!! success "View Order Inventory and Details"

    - Consult the order inventory through a graphical interface with filtering options
    - View detailed order information
    - Navigate through various views (orchestration plan and contract view) when applicable

!!! success "Manage Order Capture Process Setting"
    Enables administrators to dynamically activate or deactivate configurable checks in real-time, ensuring seamless order processing without downtime.


## Additional Features
OM provides the additional features below
!!! success "Order Capture Front-end for Demo"

    - A dedicated front-end to demonstrate the ordering use case implemented on [DISCOBOLE](https://discobole.ow2.io/doc/) suite
    - Manage the entire customer journey from offer selection to purchase, including modifying, migrating, and terminating subscriptions to ensure a seamless and flexible experience
