# Discobole-First Implementation Plan for Order-to-Bill POC

## 1. Objective

This POC validates whether the Discobole Open Source Suite can be used as the primary platform for a Telecom Broadband Order-to-Bill flow.

The implementation must use Discobole services as much as possible:

- **Order Capture** for customer order intake and validation
- **Order Inventory** for ProductOrder persistence and lifecycle
- **Customer Order Orchestration Delivery (COOD)** for orchestration plan generation and execution
- **Delivery Management** for delivery-node execution handoff
- **Fallout + ProcessFlow** for failure and retry handling
- **Catalog/Product Specification services** for product-driven decomposition
- **Security/auth-userrole + Keycloak** for authentication and authorization
- **Discobole UI portals** for customer and operator experience

Custom POC code should only fill telecom-specific gaps: broadband qualification, broadband activation, static IP activation, billing simulation, gateway/proxy glue, and small UI adaptations.

## 2. Target Business Flow

Customer orders:

```text
Fiber Broadband 300 Mbps
+
Static IP Add-on
```

Expected event-driven journey:

1. Customer uses Discobole `selfcare-ui` to select broadband offer and submit an order.
2. UI posts to Discobole Order Capture through the POC gateway.
3. Order Capture validates the order using ProcessFlow/TMF701 and configured catalog/qualification endpoints.
4. Order Inventory persists the accepted ProductOrder and emits ProductOrder state changes.
5. COOD consumes the accepted ProductOrder event and creates an orchestration plan.
6. COOD starts delivery nodes through Delivery Management.
7. Delivery Management routes work to POC backend simulators:
   - qualification-service
   - activation-service
   - billing-service
8. Simulators publish Discobole-compatible service/delivery status events.
9. COOD updates orchestration node and plan state.
10. Order Inventory/Order Management reflect order item progress.
11. Customer and operator UIs show order status, orchestration progress, fallout, retry, billing, and completion.

## 3. Discobole Components to Reuse

| Component | POC Role | Usage |
| --- | --- | --- |
| `order-capture` | Customer order capture and validation | Use as authoritative order intake |
| `order-inventory` | ProductOrder persistence and query | Use for customer/operator order status |
| `orchestration-delivery` / COOD | Orchestration plan and node state management | Use as main workflow/orchestration engine |
| `orchestration-delivery-management` | Delivery node dispatch | Use to invoke simulated delivery backends |
| `orchestration-delivery-fallout` | Fallout incident lifecycle | Use for activation failure/retry |
| `process-flow` | TMF701 task/process flows | Use through Order Capture and Fallout |
| `auth-userrole` | TMF672 user role/permission service | Use for role and entitlement checks |
| Keycloak | Identity provider | Use for all UIs and APIs |
| Product Catalog/Product Specification | Product-driven order model | Seed Fiber 300 and Static IP catalog entities |
| `selfcare-ui` | Customer UI | Reuse and adapt |
| `order-inventory-ui` | Operator order monitoring | Reuse |
| `order-orchestration-ui` | Orchestration/fallout monitoring | Reuse |

## 4. Custom POC Components

### 4.1 POC Gateway/API

Purpose:

- Single local entry point for UI requests
- Reverse proxy to Discobole services
- Session/token handling where existing portal server support is insufficient
- Optional UI-friendly read projections for customer timelines

The gateway must not become the order-management source of truth. Discobole remains authoritative.

### 4.2 qualification-service

Purpose:

- Simulate broadband serviceability checks
- Integrate with Discobole Order Capture/Product Offering Qualification or Delivery Management, depending on the final catalog/process-flow mapping
- Persist qualification records for traceability
- Publish or return Discobole-compatible completion/failure status

### 4.3 activation-service

Purpose:

- Simulate Fiber Broadband activation
- Simulate Static IP activation
- Support forced failure and retry for fallout demonstration
- Publish Discobole-compatible ServiceOrder/delivery status events

### 4.4 billing-service

Purpose:

- Simulate billing account creation
- Simulate first invoice generation
- Persist account/invoice records
- Publish completion status back into the Discobole-driven flow

## 5. Discobole UI Reuse Plan

### Customer UI

Use `discoble-monorepo/disco-ui-portals/selfcare-ui`.

Required adaptations:

- Configure environment URLs to route through the POC gateway.
- Replace demo/mobile content with broadband offers:
  - Fiber Broadband 300 Mbps
  - Static IP Add-on
- Ensure the order completion flow creates a valid Discobole ProductOrder/ProcessFlow.
- Extend tracking labels for broadband qualification, activation, static IP, billing, and invoice status.

### Operator UI

Use `discoble-monorepo/disco-ui-portals/disco-admin-ui/order-inventory-ui`.

Required adaptations:

- Configure gateway/service URLs for local Docker Compose.
- Verify order list/detail supports the seeded broadband ProductOrder structure.

### Orchestration/Fallout UI

Use `discoble-monorepo/disco-ui-portals/disco-admin-ui/order-orchestration-ui`.

Required adaptations:

- Configure COOD/fallout/order/product URLs.
- Verify orchestration graph, node timeline, failed/held nodes, and retry/fallout incidents for the broadband flow.

## 6. Event Contracts

The POC must align with Discobole event names and schemas instead of invented simplified events.

Key topics:

| Topic | Purpose |
| --- | --- |
| `disco.order-management.productOrderChange-command` | Order management command channel |
| `disco.order-management.productOrderStateChange-event` | ProductOrder lifecycle events consumed by COOD |
| `disco.order-orchestration.orchestrationPlanStateChange-event` | COOD plan lifecycle events |
| `disco.order-orchestration.orchestrationPlanNodeStateChange-event` | COOD node lifecycle events |
| `disco.delivery-management.deliveryStart-event` | COOD starts a delivery node |
| `disco.delivery-management.deliveryStatus-event` | Delivery Management returns delivery status |
| `disco.service-order-management.serviceOrderStateChange-event` | Production/backend service order status |
| Fallout state-change topic | Fallout incident updates consumed by COOD |

Legacy POC topics such as `disco.order.created`, `otb.qualification.completed`, and `otb.billing.completed` should be removed or retained only as internal debug/projection topics, never as the main integration contract.

## 7. Catalog and Decomposition Model

Seed catalog/configuration data so COOD can derive a real orchestration plan from Discobole product relationships.

Minimum entities:

- Product offering: `Fiber Broadband 300 Mbps`
- Product offering: `Static IP Add-on`
- Product specification: broadband CFS/product node
- Product specification: static IP CFS/product node
- Optional product/order node representing billing initiation if COOD requires a product-spec-backed node
- Relationships:
  - customer contract/root offering bundles broadband
  - static IP depends on broadband activation
  - billing depends on service activation completion

If Discobole catalog modelling cannot express billing as a product node cleanly, document the local extension and keep it POC-scoped.

## 8. Infrastructure Plan

Docker Compose must run:

- MongoDB as replica set where required by Discobole outbox/Debezium
- Kafka
- Kafka Connect/Debezium if required by selected Discobole services
- Keycloak with imported realm, users, clients, and roles
- auth-userrole
- Order Capture
- Order Inventory
- Product Catalog/Product Specification services or configured mock/catalog seed services
- COOD
- Delivery Management
- Fallout/ProcessFlow
- POC gateway
- Custom simulator services
- Discobole UI portals

Health checks and startup ordering must be documented, but services should tolerate delayed dependencies.

## 9. Completion Criteria

The POC is complete when:

- A customer can place a broadband + static IP order using adapted Discobole `selfcare-ui`.
- The order is captured and persisted by Discobole Order Capture/Inventory.
- COOD creates and executes an orchestration plan.
- Delivery Management dispatches qualification, activation, static IP, and billing work to simulator services.
- Status changes are event-driven through Kafka using Discobole contracts.
- Customer can track order state in `selfcare-ui`.
- Operator can inspect the order in `order-inventory-ui`.
- Operator can inspect orchestration plan, nodes, and fallout in `order-orchestration-ui`.
- Failure and retry are demonstrated through Fallout/ProcessFlow.
- Documentation contains repeatable local setup, seeded data, and test scripts.

## 10. Implementation Order

1. Fix spec alignment and remove custom-first assumptions.
2. Fix current build blockers in generated custom service files.
3. Build or run the required Discobole services from the reference monorepo.
4. Create Docker Compose for the full Discobole-first stack.
5. Seed Keycloak roles/users/clients and auth-userrole permissions.
6. Seed product catalog/product specs for broadband/static IP.
7. Configure/adapt `selfcare-ui`, `order-inventory-ui`, and `order-orchestration-ui`.
8. Implement simulator backends against Discobole delivery/service event contracts.
9. Connect failure/retry to Fallout/ProcessFlow.
10. Add E2E verification scripts and update README.
