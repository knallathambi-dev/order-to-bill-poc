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

- [x] Inventory Discobole services required from the reference monorepo
  - Order Capture
  - Order Inventory
  - Product Catalog/Product Specification services
  - auth-userrole
  - COOD / orchestration-delivery
  - Delivery Management
  - Fallout/ProcessFlow
  - selected UI portals
- [x] Define how each Discobole service will be run locally
  - copied code under this repo
  - Docker image built from this repo
  - external source monorepo retained only as upstream reference
- [x] Document build order and dependency requirements for Discobole modules
  - Documented in `docs/phase-2-discobole-runtime.md`.
- [x] Create build scripts for selected Discobole images if images are built locally
  - Added `scripts/build-discobole-images.sh`.
- [x] Copy required Discobole service source into `discobole-services/`
- [x] Copy required Discobole UI portal source into `discobole-ui/`
- [x] Track POC changes to copied Discobole source directly in this repo, keeping changes scoped and documented
  - Phase 2 copied source without behavior changes; later POC changes should be committed directly against copied source with notes in `docs/`.

## Phase 3: Infrastructure From Scratch

- [x] Create root `docker-compose.yml`
- [x] Create infrastructure-only compose profile
- [x] Add MongoDB configured as replica set if required by Discobole outbox/Debezium
- [x] Add Kafka
  - Kafka default: `apache/kafka:4.3.0` with KRaft mode and no ZooKeeper.
- [x] Add Kafka UI
- [x] Add Kafka Connect/Debezium for copied Discobole order-orchestration outbox routing
  - Decision recorded in `docs/phase-3-debezium-decision.md`.
  - Keep Kafka KRaft-only; do not reuse upstream ZooKeeper-based Debezium sample Compose.
- [x] Add Keycloak
- [x] Add Keycloak import directory
- [x] Add health checks for platform services
- [x] Add topic creation/bootstrap script
- [x] Add infrastructure verification script
- [x] Document ports and access URLs
  - Documented in `docs/phase-3-infrastructure.md`.

## Phase 4: Security Seed Data

- [x] Create Keycloak realm export/import for local POC
- [x] Create Keycloak clients
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
- [x] Create users
  - `customer@otb.com`
  - `operator@otb.com`
  - `admin@otb.com`
- [x] Seed auth-userrole component configuration
- [x] Seed user roles and entitlements
  - `OTB_CUSTOMER`
  - `OTB_ORDER_OPERATOR`
  - `OTB_ORDER_MANAGER`
  - `OTB_FALLOUT_OPERATOR`
  - `OTB_CATALOG_ADMIN`
  - `OTB_ADMIN`
- [x] Verify token issuance, refresh, and protected API access through gateway
  - Added `scripts/verify-keycloak-security.sh` for Keycloak token issuance.
  - Gateway protected API verification is deferred until the gateway exists in Phase 8.

## Phase 5: Discobole Core Services

- [x] Add/run `auth-userrole`
- [x] Add/run Order Capture
- [x] Add/run Order Inventory
- [x] Add/run Product Catalog/Product Specification services or a documented POC substitute
  - Added product catalog, product specification, product offering, and product inventory services to the `core` Compose profile.
- [x] Add/run COOD / orchestration-delivery
- [x] Add/run Delivery Management
- [x] Add/run Fallout/ProcessFlow
  - ProcessFlow is packaged as a library dependency; fallout service is included as the runtime process/fallout component.
- [x] Configure service URLs for local Docker networking
- [x] Configure Kafka bootstrap servers and topics
- [x] Configure MongoDB databases and credentials
- [x] Configure Keycloak issuer/client settings
- [x] Verify each service health endpoint
  - Added `scripts/verify-core-services.sh`.
- [x] Verify OpenAPI/Swagger endpoints where available
  - `scripts/verify-core-services.sh` probes selected `/v3/api-docs` endpoints and reports availability.

## Phase 6: Catalog and Product Model

- [x] Define broadband ProductOffering seed data
  - Fiber Broadband 300 Mbps
  - Static IP Add-on
- [x] Define ProductSpecification seed data
  - broadband service/product node
  - static IP service/product node
  - billing/invoice node if represented as product-driven orchestration
- [x] Define offering/spec relationships for COOD decomposition
- [x] Define static IP dependency on broadband activation
- [x] Define billing dependency after service activation
- [x] Add catalog seed scripts or import files
- [x] Add seed data guide
- [x] Verify seeded offers are visible to customer UI
  - Verified through the Phase 7 selfcare bridge/catalog route and selfcare offer-loader fallback for `Fiber Broadband 300 Mbps` and `Static IP Add-on`.
- [x] Verify Order Capture accepts the seeded offer structure
  - Verified via Order Capture `selectOfferOrContract` flow using the seeded Fiber Broadband offer; the flow advanced to `OrderCapture.confirmConfiguration`.
  - POC note: request payload must include `channel` so ProcessFlow can populate the `CHANNEL_ID` variable.
- [x] Verify accepted ProductOrder event can produce a COOD orchestration plan
  - Verified with accepted ProductOrder `phase6-1780226407`.
  - COOD created orchestration plan `0920ab23-f4f3-4271-91af-8a47099bcf5f` in `Acknowledged` state with three nodes: broadband, static IP, and billing.
  - POC note: inter-service auth was bypassed for Product Catalog, COOD, and COOD Fallout to unblock the local demo flow.
  - POC note: COOD plan initialization was made null-safe for seeded product specs that do not include `relatedResource`.

## Phase 7: Reused Discobole UI Portals

### Customer UI: `selfcare-ui`

- [x] Decide whether to run from reference monorepo or copy/adapt into this repo
- [x] Configure `selfcare-ui` environment URLs through POC gateway
- [x] Configure `selfcare-ui/server` if used as the gateway/session proxy
- [x] Replace mobile/demo offer content with broadband content
- [x] Adapt labels for broadband order capture
- [ ] Ensure order completion posts to Discobole Order Capture
- [ ] Ensure track-order reads Discobole Order Inventory
- [x] Add/adapt customer-visible status labels
  - qualification
  - broadband activation
  - static IP activation
  - billing
  - invoice
  - completed

### Operator UI: `order-inventory-ui`

- [x] Decide whether to run standalone or through `hostmode-ui`
- [x] Configure service URLs through POC gateway
- [x] Verify product order list
  - Bridge route authorizes and returns a valid list response; the current local inventory dataset was empty after service recreate.
- [ ] Verify product order details
- [ ] Verify order-to-orchestration-plan linkage

### Orchestration UI: `order-orchestration-ui`

- [x] Decide whether to run standalone or through `hostmode-ui`
- [x] Configure COOD, Fallout, Product Inventory, and Order Inventory URLs
- [x] Verify orchestration plan list
  - Bridge route returns current COOD plan data.
- [ ] Verify plan detail
- [ ] Verify orchestration node graph
- [ ] Verify timeline
- [x] Verify fallout incidents
  - Bridge route returns a valid fallout incident list response.

## Phase 8: POC Gateway

- [x] Scaffold selected gateway implementation from scratch
- [x] Add health endpoint
- [x] Add reverse proxy routes to Discobole APIs
- [x] Add token/session forwarding
- [x] Add CSRF/CORS handling compatible with reused portals
- [x] Add service-token support for backend-to-backend calls only
- [x] Add optional customer timeline projection only if reused Discobole UI cannot show the required flow
  - Decision: do not add a timeline projection in Phase 8. Reused `selfcare-ui` should continue to read Discobole Order Inventory directly unless Phase 7 browser testing proves a projection is needed.
- [x] Add Dockerfile
- [x] Add Compose service
- [x] Add gateway smoke tests
  - Added `gateway/` as a standalone Node/Express service with Selfcare-safe auth behavior.
  - Added route normalization for both `/api/*` and non-`/api` portal paths.
  - Added `scripts/verify-phase8-gateway.sh`, `scripts/smoke-gateway.sh`, and gateway unit tests.

## Phase 9: Simulator Services From Scratch

### qualification-service

- [x] Scaffold Spring Boot service
- [x] Add MongoDB persistence
- [x] Add REST endpoint for serviceability debug/testing
- [ ] Integrate with Discobole qualification/order-capture or delivery flow
- [x] Emit Discobole-compatible completion/failure status
- [x] Add deterministic success/failure modes
- [x] Add Dockerfile
- [x] Add unit tests

### activation-service

- [x] Scaffold Spring Boot service
- [x] Add MongoDB persistence
- [x] Implement broadband activation simulator
- [x] Implement static IP activation simulator
- [x] Emit Discobole-compatible service/delivery status
- [x] Add forced failure mode
- [x] Add retry-safe/idempotent behavior
- [x] Add Dockerfile
- [x] Add unit tests

### billing-service

- [x] Scaffold Spring Boot service
- [x] Add MongoDB persistence
- [x] Implement billing account creation simulator
- [x] Implement invoice generation simulator
- [x] Emit Discobole-compatible service/delivery status
- [x] Add debug lookup endpoints
- [x] Add Dockerfile
- [x] Add unit tests

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
