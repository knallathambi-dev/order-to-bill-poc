# Implementation Tasks for Fresh Discobole-First Order-to-Bill POC

## Current Repository Baseline

Reference implementation and reusable Discobole code will be copied from:

```text
/Users/knallathambi/Engineering/projects/java-apps/discoble-monorepo
```

This repository is now intended to become the full POC mono-repo containing selected Discobole services, selected Discobole UI portals, POC-specific services, gateway code, infrastructure, scripts, seed data, and documentation.

## Phase 0: Scope Confirmation

- [x] Reframe POC as Discobole-first, not custom OMS-first
- [x] Identify Discobole UI portals to reuse
- [x] Confirm repository is now specs-only outside `spec/`
- [x] Review and confirm updated specs with user
- [x] Decide whether this repo will copy Discobole UI/service code into this mono-repo
  - Decision: copy selected Discobole services and UI portals from `/Users/knallathambi/Engineering/projects/java-apps/discoble-monorepo` into this repo during Phase 2. This repo will be the full POC mono-repo and required POC changes may be applied in-repo.
- [x] Decide gateway implementation style:
  - Decision: prefer extending/reusing `selfcare-ui/server` for portal session/proxy behavior; fall back to a lightweight Node/Express gateway if a standalone gateway is required.
  - Spring Boot gateway is not the Phase 0 default.

Phase 0 decision record: `spec/phase-0-decisions.md`.

## Phase 1: Fresh Repository Structure

- [x] Create root project layout
  - `infrastructure/`
  - `infrastructure/keycloak/`
  - `infrastructure/kafka/`
  - `infrastructure/mongodb/`
  - `discobole-runtime/`
  - `discobole-services/`
  - `discobole-ui/`
  - `custom-services/`
  - `custom-services/qualification-service/`
  - `custom-services/activation-service/`
  - `custom-services/billing-service/`
  - `gateway/`
  - `ui-overrides/`
  - `scripts/`
  - `docs/`
- [x] Add root README with fresh-start setup assumptions
- [x] Add `.env.example` for local ports, service URLs, Keycloak clients, and secrets
  - Kafka default: `apache/kafka:4.3.0` with KRaft mode and no ZooKeeper.
- [x] Add `.gitignore` for Java, Node, Docker, logs, generated data, and IDE files
- [x] Add a root `Makefile` or task runner script for common commands
  - Current command: `make verify-phase1`.
  - Updated mono-repo landing folders: `discobole-services/` and `discobole-ui/`.

## Phase 2: Discobole Runtime Strategy

- [ ] Inventory Discobole services required from the reference monorepo
  - Order Capture
  - Order Inventory
  - Product Catalog/Product Specification services
  - auth-userrole
  - COOD / orchestration-delivery
  - Delivery Management
  - Fallout/ProcessFlow
  - selected UI portals
- [ ] Define how each Discobole service will be run locally
  - copied code under this repo
  - Docker image built from this repo
  - external source monorepo retained only as upstream reference
- [ ] Document build order and dependency requirements for Discobole modules
- [ ] Create build scripts for selected Discobole images if images are built locally
- [ ] Copy required Discobole service source into `discobole-services/`
- [ ] Copy required Discobole UI portal source into `discobole-ui/`
- [ ] Track POC changes to copied Discobole source directly in this repo, keeping changes scoped and documented

## Phase 3: Infrastructure From Scratch

- [ ] Create root `docker-compose.yml`
- [ ] Create infrastructure-only compose profile
- [ ] Add MongoDB configured as replica set if required by Discobole outbox/Debezium
- [ ] Add Kafka
- [ ] Add Kafka UI
- [ ] Add Kafka Connect/Debezium if selected Discobole flow requires outbox routing
- [ ] Add Keycloak
- [ ] Add Keycloak import directory
- [ ] Add health checks for platform services
- [ ] Add topic creation/bootstrap script
- [ ] Add infrastructure verification script
- [ ] Document ports and access URLs

## Phase 4: Security Seed Data

- [ ] Create Keycloak realm export/import for local POC
- [ ] Create Keycloak clients
  - `selfcare-ui`
  - `admin-ui`
  - `poc-gateway`
  - `order-capture`
  - `order-inventory`
  - `orchestration-delivery`
  - `orchestration-delivery-management`
  - `orchestration-delivery-fallout`
  - `auth-userrole`
  - `product-catalog`
  - `qualification-service`
  - `activation-service`
  - `billing-service`
- [ ] Create users
  - `customer@otb.com`
  - `operator@otb.com`
  - `admin@otb.com`
- [ ] Seed auth-userrole component configuration
- [ ] Seed user roles and entitlements
  - `OTB_CUSTOMER`
  - `OTB_ORDER_OPERATOR`
  - `OTB_ORDER_MANAGER`
  - `OTB_FALLOUT_OPERATOR`
  - `OTB_CATALOG_ADMIN`
  - `OTB_ADMIN`
- [ ] Verify token issuance, refresh, and protected API access through gateway

## Phase 5: Discobole Core Services

- [ ] Add/run `auth-userrole`
- [ ] Add/run Order Capture
- [ ] Add/run Order Inventory
- [ ] Add/run Product Catalog/Product Specification services or a documented POC substitute
- [ ] Add/run COOD / orchestration-delivery
- [ ] Add/run Delivery Management
- [ ] Add/run Fallout/ProcessFlow
- [ ] Configure service URLs for local Docker networking
- [ ] Configure Kafka bootstrap servers and topics
- [ ] Configure MongoDB databases and credentials
- [ ] Configure Keycloak issuer/client settings
- [ ] Verify each service health endpoint
- [ ] Verify OpenAPI/Swagger endpoints where available

## Phase 6: Catalog and Product Model

- [ ] Define broadband ProductOffering seed data
  - Fiber Broadband 300 Mbps
  - Static IP Add-on
- [ ] Define ProductSpecification seed data
  - broadband service/product node
  - static IP service/product node
  - billing/invoice node if represented as product-driven orchestration
- [ ] Define offering/spec relationships for COOD decomposition
- [ ] Define static IP dependency on broadband activation
- [ ] Define billing dependency after service activation
- [ ] Add catalog seed scripts or import files
- [ ] Verify seeded offers are visible to customer UI
- [ ] Verify Order Capture accepts the seeded offer structure
- [ ] Verify accepted ProductOrder event can produce a COOD orchestration plan

## Phase 7: Reused Discobole UI Portals

### Customer UI: `selfcare-ui`

- [ ] Decide whether to run from reference monorepo or copy/adapt into this repo
- [ ] Configure `selfcare-ui` environment URLs through POC gateway
- [ ] Configure `selfcare-ui/server` if used as the gateway/session proxy
- [ ] Replace mobile/demo offer content with broadband content
- [ ] Adapt labels for broadband order capture
- [ ] Ensure order completion posts to Discobole Order Capture
- [ ] Ensure track-order reads Discobole Order Inventory
- [ ] Add/adapt customer-visible status labels
  - qualification
  - broadband activation
  - static IP activation
  - billing
  - invoice
  - completed

### Operator UI: `order-inventory-ui`

- [ ] Decide whether to run standalone or through `hostmode-ui`
- [ ] Configure service URLs through POC gateway
- [ ] Verify product order list
- [ ] Verify product order details
- [ ] Verify order-to-orchestration-plan linkage

### Orchestration UI: `order-orchestration-ui`

- [ ] Decide whether to run standalone or through `hostmode-ui`
- [ ] Configure COOD, Fallout, Product Inventory, and Order Inventory URLs
- [ ] Verify orchestration plan list
- [ ] Verify plan detail
- [ ] Verify orchestration node graph
- [ ] Verify timeline
- [ ] Verify fallout incidents

## Phase 8: POC Gateway

- [ ] Scaffold selected gateway implementation from scratch
- [ ] Add health endpoint
- [ ] Add reverse proxy routes to Discobole APIs
- [ ] Add token/session forwarding
- [ ] Add CSRF/CORS handling compatible with reused portals
- [ ] Add service-token support for backend-to-backend calls only
- [ ] Add optional customer timeline projection only if reused Discobole UI cannot show the required flow
- [ ] Add Dockerfile
- [ ] Add Compose service
- [ ] Add gateway smoke tests

## Phase 9: Simulator Services From Scratch

### qualification-service

- [ ] Scaffold Spring Boot service
- [ ] Add MongoDB persistence
- [ ] Add REST endpoint for serviceability debug/testing
- [ ] Integrate with Discobole qualification/order-capture or delivery flow
- [ ] Emit Discobole-compatible completion/failure status
- [ ] Add deterministic success/failure modes
- [ ] Add Dockerfile
- [ ] Add unit tests

### activation-service

- [ ] Scaffold Spring Boot service
- [ ] Add MongoDB persistence
- [ ] Implement broadband activation simulator
- [ ] Implement static IP activation simulator
- [ ] Emit Discobole-compatible service/delivery status
- [ ] Add forced failure mode
- [ ] Add retry-safe/idempotent behavior
- [ ] Add Dockerfile
- [ ] Add unit tests

### billing-service

- [ ] Scaffold Spring Boot service
- [ ] Add MongoDB persistence
- [ ] Implement billing account creation simulator
- [ ] Implement invoice generation simulator
- [ ] Emit Discobole-compatible service/delivery status
- [ ] Add debug lookup endpoints
- [ ] Add Dockerfile
- [ ] Add unit tests

## Phase 10: Event-Driven Flow Integration

- [ ] Create only Discobole-aligned Kafka topics for the primary flow
- [ ] Validate Order Capture/Inventory emits accepted ProductOrder state event
- [ ] Validate COOD consumes accepted ProductOrder event
- [ ] Validate COOD creates orchestration plan
- [ ] Validate COOD publishes delivery start events
- [ ] Validate Delivery Management invokes or coordinates simulator work
- [ ] Validate simulator completion updates COOD nodes
- [ ] Validate COOD plan completion updates visible order state
- [ ] Validate UI refreshes state from Discobole APIs
- [ ] Remove any dependency on legacy simplified topics such as `otb.*`

## Phase 11: Failure, Fallout, and Retry

- [ ] Force broadband activation failure
- [ ] Verify COOD marks node failed or held
- [ ] Verify Fallout/ProcessFlow incident is created
- [ ] Verify incident appears in `order-orchestration-ui`
- [ ] Implement retry/resume trigger
- [ ] Verify retry re-dispatches activation work
- [ ] Verify order completes after retry
- [ ] Document failure/retry demo script

## Phase 12: Testing and Documentation

- [ ] Add backend unit tests for simulator services
- [ ] Add gateway route/auth tests
- [ ] Add integration tests for Kafka event publishing/consumption
- [ ] Add Docker Compose smoke test script
- [ ] Add happy-path E2E script
- [ ] Add failure/retry E2E script
- [ ] Add setup guide
- [ ] Add seed data guide
- [ ] Add user/role guide
- [ ] Add troubleshooting guide
- [ ] Update README with fresh-start architecture and commands

## Completion Checklist

- [ ] Fresh repo structure created from specs
- [ ] Full Discobole-first stack starts from Docker Compose
- [ ] Customer order placed in reused `selfcare-ui`
- [ ] Order captured by Discobole Order Capture
- [ ] ProductOrder persisted by Discobole Order Inventory
- [ ] COOD orchestration plan created
- [ ] Delivery Management dispatches simulator work
- [ ] Qualification, activation, static IP, billing, and invoice steps complete
- [ ] Kafka event flow uses Discobole contracts
- [ ] Customer can track order in `selfcare-ui`
- [ ] Operator can inspect order in `order-inventory-ui`
- [ ] Operator can inspect orchestration/fallout in `order-orchestration-ui`
- [ ] Failure/retry is demonstrated through Fallout/ProcessFlow
- [ ] Setup and demo are repeatable on a clean checkout
