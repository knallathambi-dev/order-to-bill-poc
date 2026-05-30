---
title: Roadmap
summary: Describe post/current/future items
authors:
  - Omar Abdalla
  - Mohammad Aly
---

# Customer Order Orchestration & Delivery Roadmap

## Implementation

??? Success "Release 10 – March 2026"

    - Delivery Management Enhancements : Improve Orchestration Delivery Management by achieving a clear Separation between planning and Execution in COOD modules
    
        - Orchestration Management: Decide WHAT should be delivered
        - Delivery Management: Decide HOW delivery is executed
        - Support Universal delivery Modes through dedicated handlers and introducing the Delivery Order and Relevant Events to fulfill the service and tangible products delivery
    - Provide lead-time estimates for both node and plan
    - Enhanced management for future-dates orders by introducing adaptive scheduling and smooth launching for Planned orchestration plans
    - Enhancements for tangible product delivery by grouping tangible products that have the same shipping characteristics
    - Performance Testing and Optimization





??? Success "Release 9 – November 2025"

    - Support characteristic @type 

        - Support new characteristic type (String, Object, validity, Address, Date)        
    
    - Including time parameters for both orchestration plan and orchestration node

        - Adding: start/actual start/actual completion (node and Plan level)
        - Introduce lead time history (COOD system level)

    - Enhancements for Multi-purchase use case: Rejected Plans due to lack of specs in ODACAT or lack or relations in received order

        - Validates sufficient inputs in the received Order for multi-purchase use case
        - Enhance FO behavior to make nodes rejected for unresolved incidents in case of product specs  

    - Admin portal  
    
        - Visualizing the orchestration plan schedule
        - LeadTime reports per product (specID), Contract Name, Delivery Factory

    - Enhance Data Lifecycle following Eco-design recommendations 

    - Performance Testing and Optimization

??? Success "Release 8 - June 2025"
    - Evolve the Orchestration plan creation to support Multipurchase
    - Updating mock server for multi-purchase mobile offer (mobile offer)
    - Updating the configuration for Multi-Delivery Factory
    - CFS Specification for Fiber V2
    - Fix COOD behaviour for Node Status and product status update
    - Software tasks required to open-source readiness
    - Keycloak upgrade
    - Update User roles for COOD
    - Update tests to use records instead of data tables (Orchestration Delivery, Delivery Management, Fallout)
    - Admin UI (COOD part): Keycloak upgrade to V26

??? Success "Release 7 - March 2025"

    - Evolve Orchestration Plan Creation to Support Migration use case: 
    
        - Introduce MigratedFrom relationship
        - Create a New Related product "Migrated From Product"
        - Ignore Order Item with action `NoChange`
        - Manage Dependencies and Relationships for Nodes with Action Migrate
        - Update Product order by Retrieving delivery-focused characteristics from Catalogue
    
    - Evolve Execute Orchestration Plan to Support Migration use case:
    
        - Manage future dated planes (requested completion date)
        - Status management of Orchestration plans
        - Update (verify Product Relationships) for Migration Action
        - Support Commercial Migration Use Case "no change in Characteristics": deliver Commercial Migration and Activate Migrated Product without SOM request and Adaptation for the Node life cycle (allow transition from inProgress to Completed)
        - Support Operational Migration Use Case: Update SOM request to use action (Modify) instead of (Migrate) and Update Follow up process for migrate action
    
    - Product Order upgrade: TMF 622 Upgrade from V4 to V5 
    - Support non-string Characteristics: Support receiving object characteristics by storing them as string, then change to object again when updating CPIB
    - Update CPIB operational status to meet the orchestration node status changes
    - Fallout Enhancement: Allow automatic external fallout resolution for held Orchestration Nodes
    - Enhance the Admin UI Portal for COOD
    
        - Adapt UI to accommodate the migration information
        - Show action (modify) in the service order in the UI for migration use case
        - Update the labels in fallout incident details
        - Display the contract name related to the offer in the orchestration plan details
        - Implement logout from the Admin UI
        - Open the accordion of node that causes the fallout when navigating to plan details
        - Add filters to query params in URL for easy link sharing Bug fixing
        
    - Software Tasks
 
        - Refactor delivery status consumer to separate node state actions from CPIB actions
        - Improvements of the flow between the orchestration delivery management and the orchestration delivery
        - Create micro-frontend for UI COOD component
        - Enforce nodes life cycle state changes
        - Kafka connect disconnect the traces
        
??? Success "Release 6 - October 2024"
    - Fallout Management
    - Enrich fallout incident with information about the failed event
    - Enhance the fallout data model to support different owners of the fallout
    - Implement automatic retries for failed events, sending exhausted and recoverable events to the DLT.
    - Enhance The Resilience In The Orchestration Delivery Process To Support Fallout Management By:
    - Make all COOD consumers idempotent
    - Introduce a categorization of exceptions to drive the fallout process
    - Enhance error management with business-specific details
    - Enable automatic recovery for held orchestration nodes/plans by addressing failure reasons and resuming operations after manual fallout incident resolution.
    - Update the lifecycle of the orchestration plan and orchestration nodes to reflect unresolved fallouts.
    - Enhance The Admin UI Portal For COOD
    - New Reports are introduced (Status and History for the Orchestration plans)
    - Manually complete fallout incident as resolved
    - Enhance existing orch. plan detail page with new information displayed Improve the purge mechanism to clean up old fallouts.
    - Increase COOD TMF API Compliance Percentage.
    - Introduce COOD in Event Catalog Website.
    - Enforce quality gate checks

??? Success "Release 5 - June 2024"
    - Implement purge mechanism to clean up old orchestration plans automatically and enforce data retention policy
    - Implement toggle to control Customer Product Inventory (TMF637) integration with COOD, to enable/disable sending the POST request when updating the products
    - Fallout Management:
    - Initiate fallout incident for failed plans
    - Automatically process fallout incidents
    - Handle fallout incidents that require manual intervention
    - Enhance COOD admin UI portal
    - Enhance existing screen with new information displayed
    - List the plan with fallout incident
    - Manually complete fallout incident (as unresolved)

??? Success "Release 4 - March 2024"
    - Enhance COOD Delivery Flow to avoid sending requests to Order Management and Product Catalog.
    - Separation Delivery Management logic from COOD into a new microservice.
    - Support tangible products delivery through TMF-700 Shipping Order Management API: 
    - Combine both order items (tangible product + shipment) into a single tangibleProductShipment node during plan creation
    - Create shipment order using the shipping API TMF700 (should include the productOrderId reference) & capture the shipment order id during plan execution
    - Listen to ShippingOrderStateChangeEvent & update orchestration plan node tangibleProductShipment status accordingly
    - Update both tangible product status in Customer Product Inventory (TMF637) & fire orchestrationNodeStateChangeEvent (and orchestrationPlanStateChangeEvent if needed)
    - Support multiple CFS Delivery Factories (multiple service ordering management & service catalogs URLs):
    - Fetch from the Product Catalog when building reliesOn relationships during orchestration plan creation & save in plan the following info:
    - productSpecification.serviceSpecification.href to identify from which Service Catalog COOD needs to request CFS details (characteristics ids) to prepare for service order request.  (API: service catalog Management TMF-633. Method: GET , url: /serviceSpecification?id=x1)
    - productSpecification.relatedResource.href to identify which SOM to send the service order request to. (API: service Ordering Management TMF-641 Method: POST url: /serviceOrder)
    - Support contract termination use case
    - Develop Temporal PoC for the Delivery Flow and comparative analysis
    - Admin Portal UI:
    - New UI design that separates each component with a tooltip in component menu
    - Support of authorization based on user role (hide component menu if needed)
    - Ability to refresh directly within the app in orchestration plan details page
    - Enhance filter feature UI in orchestration plan list page
    - Enhance graphical representation (display node action & status legend, different background)
    - Enhance Online Documentation
    - Technical Tasks:
    - Switched from Spring Cloud Stream Kafka to Spring Kafka for easier and more flexible configuration and for better community support

??? Success "Release 3 - December 2023"
    - Implementation of modification of existing mobile contracts, allowing to add new options or adjust existing options characteristics or __to delete existing options__:
    - Verifying to be delivered products and their prerequisites/dependents state (depending on node action) at the Customer Product Inventory (TMF637) before sending order request to Service Ordering Management (TMF641)
    - Verifying that all prerequisites identified in the Product Catalog are satisfied after extraction of products from the Customer Product Inventory (TMF637)
    - Updating product characteristics at the the Customer Product Inventory (TMF637) if service order event state was completed and action was modify.
    -  Admin Portal UI:
    - Enhanced Orchestration Plans List by allowing user to select which fields to display and sort by
    - Added filters feature with many criteria in Orchestration Plan List page
    - Enhanced View Orchestration Node details page by displaying more data
    - Enhanced Orchestration plan details page by hiding certain node details sections when there is no data yet (node not executed yet)
    - Added a preview node details feature by clicking on any node in the graph representation which can redirect to more details in node details tab
    - Technical Tasks :
    - Adapt fetching related products from Customer Product Inventory (TMF637) to their new get products listing response (flat list - now returns first level relation product id)
    - Update the COOD schema of the orchestration node to support multiple realizing services for each product
    - Publish online documentation for COOD
    - Optimized performance for Product Details Retrieval from the Customer Product Inventory (TMF637) by using GET with multiple ids
    - Optimized performance for Products Specifications Details Retrieval from the Product Catalog by using GET with multiple ids
    - Implement authentication and authorization with keycloak for COOD
    - Implement a gateway to redirect Admin Portal UI to COOD backend
    - Generating code from AsyncAPI
    - Documenting COOD Events Using AsyncAPI Specification
    - Improve Node Query and Field Extraction in Plans
    - Add static code analysis tools related to Static Code Analysis study
    - Fix Sonar issues and enable quality gate
    - Refactor sorting and pagination of get plans API to comply with REST API - Query Extension for TMF-630
    - Connect Swagger with COOD Controller to comply with REST API - Contract first development implementation
    - Replaced old DTO classes with newly generated ones and used them in our COOD controller - AsyncAPI - Specification and Code Generation
    - Initiate The Implementation Of The Contract Termination Use Case:
    - Adjusted orchestration plan creation to check for delete action and build relationships in an reverse order.
    - Fallout Management:
    - Gathered all possible error messages
    - Testing Tasks:
    - Created BDDs for all happy scenarios of main COOD flows (Create / Execute / Deliver) in a separate Testing Gitlab Repo

??? Success "Release 2 - September 2023"
    - Execution Of Immediate Orchestration Plans:
    - Execution of the orchestration plan with starting point OrchestrationPlanStateChangeEvent
    - The initiation of delivery of the leaf nodes (those without prerequisites) and then their related dependent nodes by publishing orchestrationPlanNodeStateChangeEvent.
    - Handling different outcomes of prerequisites delivery execution state and reflecting this on the remaining dependents nodes states (flow triggered by and ends with orchestrationPlanNodeStateChangeEvent)
    - Execution Of Planned Orchestration Plans:
    - cronjob to update plan state based on delivery date and fire an orchestrationPlanStateChangeEvent.
    - Delivery Of The Selected Orchestration Plan Nodes:
    - Execution of the orchestration plan with starting point OrchestrationPlanNodeStateChangeEvent
    - Grouping tangible and non tangible deliveries.
    - Fetching order item characteristics details from Order Management and fetching service specs details from mocked Service Catalog (TMF633).
    - Preparing service order request for each CFS to be delivered to Service Ordering Management (TMF641).
    - Implementation of modification of existing mobile contracts, allowing to add new options or adjust existing options characteristics:
    - Verifying to be delivered products and their prerequisites state at the Customer Product Inventory (TMF637) before sending order request to Service Ordering Management (TMF641)
    - Mock Service Ordering Management (TMF641) ServiceOrderStateChangeEvent to connect delivery followup flow with delivery flow.
    - Mock Tangible Products Delivery (mark as delivered automatically)
    - Admin Portal UI:
    - List of orchestration plans with its details
    - View details of a selected plan with all nodes details
    - View graphical representation of a selected delivery plan with different nodes states

??? success "Release 1 - July 2023"
    - Creation of Orchestration Plan For A Mobile Offer (MP1) Acquisition Use `case:
    - Evaluating Order Management (TMF622) Event to create plan nodes
    - Fetching specifications and relations from Product Catalog (TMF620) to build relationships and more data related to delivery factories
    - Delivery Followup:
    - Updating Customer Product Inventory (TMF637) with service order status update
    - Firing an orchestrationPlanNodeStateChangeEvent (& orchestrationPlanStateChangeEvent if needed)
    - Initial Deployment & Integration Within DISCOBOLE Ecosystem

## Work in Progress

!!! info "Release 11 - July 2026"
     - Enhancements for COOD - SOM integration
     - Universal delivery support for multiple delivery modes
     - Introducing Manual delivery mode
  
## Backlog

!!! example "Release 12 - Nov 2026"
    - Support SIM swap and MSISDN change use cases
    - Managing orders with different priority levels
    - Automatic allowance update on existing service based on new subscribed offer
    - Study: order cancelation and modification
    - Study: bulk provisioning
