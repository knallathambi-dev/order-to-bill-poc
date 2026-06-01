# Discobole Deep Dive: Architecture & Customization

## 1. What Is Discobole?

**Discobole** is an open-source suite of telecom operations components built by **Orange** and hosted at [OW2](https://discobole.ow2.io). It implements **TM Forum (TMF) Open APIs** — the industry-standard REST and event specifications for telecom operations.

Discobole is **not a monolithic application**. It is a set of **independent microservices** that communicate via **Kafka events** and **REST APIs**, each owning a specific domain concern:

- Order Capture (TMF641, TMF701)
- Order Inventory (TMF622)
- Product Catalog (TMF620, TMF634)
- Product Inventory (TMF639)
- Customer Order Orchestration & Delivery — COOD
- Delivery Management
- Delivery Fallout (incident/retry management)
- Auth & User Role management (TMF672)

The philosophy: **data-driven configuration, not hardcoded logic**. The same engine handles broadband, mobile, fiber, or any product type — what changes is the catalog data and configuration you feed it.

---

## 2. High-Level Architecture

```mermaid
graph TB
    subgraph "Discobole UI Portals"
        SELFCARE[selfcare-ui<br/>Customer order capture/tracking]
        ORDERUI[order-inventory-ui<br/>Operator order monitoring]
        COODUI[order-orchestration-ui<br/>Plan/fallout monitoring]
    end

    subgraph "Discobole Core Services"
        OC[Order Capture<br/>TMF641 / TMF701]
        OI[Order Inventory<br/>TMF622 / ProductOrder lifecycle]
        COOD[COOD<br/>Orchestration Delivery]
        DM[Delivery Management<br/>Node dispatch]
        FALLOUT[Delivery Fallout<br/>Incident / ProcessFlow]
        AUTH[auth-userrole<br/>TMF672]
        CATALOG[Catalog Services<br/>TMF620 / TMF634 / TMF648]
        CPIB[Product Inventory<br/>TMF639]
    end

    subgraph "Platform Infrastructure"
        KAFKA[Apache Kafka]
        MONGO[MongoDB ReplicaSet]
        KEYCLOAK[Keycloak]
        CONNECT[Kafka Connect / Debezium]
    end

    subgraph "Custom POC Backends"
        QUAL[qualification-service]
        ACT[activation-service]
        BILL[billing-service]
    end

    SELFCARE --> OC
    SELFCARE --> OI
    ORDERUI --> OI
    COODUI --> COOD
    COODUI --> FALLOUT

    OC --> OI
    OC --> CATALOG
    OC --> AUTH

    OI -->|ProductOrderStateChangeEvent| KAFKA
    COOD -->|OrchestrationPlan events| KAFKA
    COOD -->|deliveryStart-event| KAFKA
    DM -->|deliveryStatus-event| KAFKA
    FALLOUT -->|FalloutIncident events| KAFKA

    KAFKA --> COOD
    KAFKA --> DM
    KAFKA --> FALLOUT

    DM --> QUAL
    DM --> ACT
    DM --> BILL

    OC --> MONGO
    OI --> MONGO
    COOD --> MONGO
    DM --> MONGO
    FALLOUT --> MONGO
    AUTH --> MONGO
    CATALOG --> MONGO
    CPIB --> MONGO

    MONGO --> CONNECT --> KAFKA

    KEYCLOAK --> AUTH
```

---

## 3. Core Components (Deep Dive)

### 3.1 Order Capture

**Purpose**: The front door for customer orders. Validates input, runs process flows, creates ProductOrders.

**Key contracts**: TMF641 (Service Ordering), TMF701 (Process Flow)

**How it works**:

1. Receives an order request (from selfcare-ui or API)
2. Runs a **state machine** (Spring State Machine) defined in `application.yml`:
   - ~50 states, ~70 transitions
   - Two regions: `MainRegion` (order lifecycle) and `CancelRegion` (cancellation)
3. Each transition is guarded by a **Java `@Component` bean** that decides "can we proceed?"
4. Guards query the catalog, validate eligibility, check technical feasibility
5. On acceptance, persists the `ProductOrder` to **Order Inventory** via REST
6. Order Inventory emits a `ProductOrderStateChangeEvent` on Kafka

**State machine excerpt** (from `application.yml`):

```yaml
package-element:
  state-machines:
    - name: OrderCapture
      region: MainRegion
      states:
        - initialAutomatedTask
        - selectOfferOrContract
        - checkEligibilityChoice
        - validateOrder
        - completeOrder
      transitions:
        - source: initialAutomatedTask
          target: selectOfferOrContract
          guard: isAnonymousCustomerGuard
        - source: selectOfferOrContract
          target: checkEligibilityChoice
          guard: isExistProductOfferingGuard
```

### 3.2 Order Inventory

**Purpose**: The source of truth for `ProductOrder` lifecycle. Persists orders, tracks state, emits state change events.

**Key contracts**: TMF622 (Product Order Management)

**How it works**:

- REST endpoints for CRUD on ProductOrders
- Each state change (created → accepted → inProgress → completed → rejected) emits a Kafka event
- Events consumed by COOD to trigger orchestration
- Events consumed by UIs to show order status

### 3.3 COOD — Customer Order Orchestration & Delivery

**Purpose**: The orchestration brain. Consumes accepted ProductOrders, builds dependency graphs of "nodes," and drives execution.

**How orchestration plans are built** (from `OrchestrationPlanInitServiceImpl.java`):

```mermaid
flowchart TD
    A[COOD consumes<br/>ProductOrderStateChangeEvent] --> B[Fetch product specs<br/>from Catalog]
    B --> C[Classify each order item<br/>by spec type]
    C --> D{Spec type?}
    D -->|CFSSpec| E[Build CFS node<br/>Core service activation]
    D -->|ShippingProductSpec| F[Build delivery node<br/>Physical goods]
    D -->|Migration spec| G[Build migration node]
    E --> H[Read spec relationships<br/>requires / reliesOn / bundles]
    F --> H
    G --> H
    H --> I[Link nodes with<br/>prerequisite dependencies]
    I --> J[Set config flags<br/>prerequisite validation<br/>CPIB update etc.]
    J --> K[Persist OrchestrationPlan<br/>+ OrchestrationPlanNodes]
    K --> L[Publish OrchestrationPlanStateChangeEvent]
    L --> M[Publish deliveryStart-event<br/>for first eligible node]
```

**Node lifecycle**:

```text
pending → inProgress → completed
                       → failed → fallout incident → retry → inProgress → completed
                       → held (by threshold)
```

**Configuration** (environment-variable-driven):

| Variable | Effect |
|---|---|
| `enablePrerequisiteValidation` | Wait for prerequisite nodes before starting dependent |
| `enableUpdateCpibProduct` | Update customer product inventory on completion |
| `adaptiveOrchestrationPlanScheduling` | Dynamic vs static scheduling |
| `heldNodeProcessFlowCountThreshold` | Max failures before fallout triggers |

### 3.4 Delivery Management

**Purpose**: The dispatcher. Listens for `deliveryStart-event` on Kafka and calls the appropriate backend service.

**How it works**:

- Consumes `disco.delivery-management.deliveryStart-event`
- Takes the delivery factory reference from the orchestration node
- Calls the configured `serviceOrderingUrl` endpoint
- Publishes `deliveryStatus-event` when the backend responds
- Routes work to qualification, activation, billing services based on configuration

### 3.5 Fallout / ProcessFlow

**Purpose**: Handles failures. When a node fails, COOD creates a Fallout incident. Operators can inspect, resume, or retry.

**How it works**:

```mermaid
sequenceDiagram
    participant COOD
    participant Fallout
    participant Kafka
    participant Operator
    participant Simulator

    COOD->>Fallout: Create fallout incident (node failed)
    Fallout->>Kafka: FalloutIncidentStateChangeEvent
    Kafka->>COOD: Retry/resume signal
    Operator->>Fallout: Inspect incident in UI
    Operator->>Fallout: Trigger retry
    Fallout->>Kafka: Incident resolved
    Kafka->>COOD: Resume node
    COOD->>Kafka: deliveryStart-event (retry)
    Kafka->>Simulator: Re-dispatch
    Simulator->>Kafka: Completed
    Kafka->>COOD: Node completed
```

### 3.6 Catalog Services

**Purpose**: Define what products exist, their characteristics, and how they relate.

**Sub-components**:

| Service | TMF API | Purpose |
|---|---|---|
| `catalog-product-specification` | TMF648 | Product specs (technical definitions) |
| `catalog-product-offering` | TMF620 | Product offerings (what customers see) |
| `catalog-product-catalog` | TMF634 | Grouping of offerings |
| `catalog-product-offering-price` | TMF671 | Pricing |
| `catalog-product-category` | TMF620 | Categories |
| `catalog-product-lifecycle-management` | — | Lifecycle states |

### 3.7 Product Inventory

**Purpose**: Tracks which products are assigned to which customers. Updated by COOD when orchestration completes.

**Key contracts**: TMF639 (Product Inventory Management)

### 3.8 Auth / Security

**Purpose**: Authentication via Keycloak (OAuth2/OIDC), authorization via auth-userrole (TMF672).

**Key concepts**:

- **Components**: registered services (order-capture, COOD, etc.)
- **Functions**: actions a component can perform (e.g., `orderCreate`, `planRead`)
- **Entitlements**: permission to perform a function (e.g., `ENT_ORDER_CREATE`)
- **Roles**: named collections of entitlements (e.g., `OTB_CUSTOMER`)
- **Users**: assigned roles, which grant entitlements

---

## 4. Event Architecture

All communication between Discobole services is **asynchronous via Kafka**. This ensures loose coupling — services only need to agree on event schemas, not know about each other.

```mermaid
graph TB
    subgraph "Order Management Domain"
        OI[Order Inventory]
    end

    subgraph "Orchestration Domain"
        COOD
        DM[Delivery Management]
        FALLOUT
    end

    subgraph "Custom Backend Domain"
        QUAL[qualification-service]
        ACT[activation-service]
        BILL[billing-service]
    end

    OI -->|ProductOrderStateChange-event<br/>disco.order-management.productOrderStateChange-event| COOD

    COOD -->|OrchestrationPlanStateChange-event<br/>disco.order-orchestration.orchestrationPlanStateChange-event| OI
    COOD -->|OrchestrationPlanNodeStateChange-event<br/>disco.order-orchestration.orchestrationPlanNodeStateChange-event| OI
    COOD -->|deliveryStart-event<br/>disco.delivery-management.deliveryStart-event| DM

    DM -->|deliveryStatus-event<br/>disco.delivery-management.deliveryStatus-event| COOD
    DM -->|serviceOrderStateChange-event<br/>disco.service-order-management.serviceOrderStateChange-event| COOD

    QUAL -->|serviceOrderStateChange-event| COOD
    ACT -->|serviceOrderStateChange-event| COOD
    BILL -->|serviceOrderStateChange-event| COOD

    FALLOUT -->|FalloutIncidentStateChange-event<br/>disco.order-orchestration.falloutIncidentStateChange-event| COOD
```

### Debezium / Outbox Pattern

Discobole uses the **MongoDB outbox pattern** for reliable event publication:

```mermaid
flowchart LR
    A[COOD] -->|Write to MongoDB outbox collection| B[(MongoDB)]
    C[Debezium connector] -->|Tail MongoDB oplog| B
    C -->|Publish to Kafka| D[Kafka topic]
    D -->|Consumed by| E[Other services]
```

This ensures **at-least-once delivery**: if Kafka is down, the event sits in the outbox collection until Debezium picks it up.

---

## 5. Customization Patterns

Discobole is designed to be customized without modifying its Java code. The customization formula has **four layers**:

### Layer 1: Catalog Data (Product Model)

**What you configure**: Product specifications, offerings, categories, relationships.

**How**: REST API calls (via Bruno/Postman) to `productCatalogManagement/v1/*` endpoints.

**What it controls**:
- What products exist (broadband, mobile, fiber, etc.)
- How products relate (bundles, requires, reliesOn)
- Product characteristics (bandwidth, data cap, etc.)
- Which service specifications to use for activation

**Example relationship tree** for the broadband POC:

```text
Fiber Broadband 300 Mbps (BundleProductOffering)
├── bundles → Static IP Add-on (AtomicProductOffering)
│                └── reliesOn → Fiber Broadband 300 Mbps
└── reliesOn → Billing Initiation (ProductSpecificationRef)
```

### Layer 2: ProcessFlow State Machine (Order Behavior)

**What you configure**: States, transitions, and guard references.

**How**: Edit `application.yml` in the `order-capture` module.

**What it controls**:
- The order lifecycle path
- What validation steps run
- When external services (qualification, catalog) are called
- What error paths exist

**Design pattern**: The state machine is **product-agnostic**. It doesn't say "if broadband, do X." Instead, guards query the catalog at runtime to determine behavior.

### Layer 3: Guard Beans (Validation Logic)

**What you configure**: Java `@Component` beans referenced by name in the state machine YAML.

**Examples** (`orchestration-delivery/src/main/java/.../guard/`):

| Guard bean | Purpose |
|---|---|
| `isEligibleAcquisitionGuard` | Can this customer acquire this offering? |
| `isExistProductOfferingGuard` | Does the offering exist in catalog? |
| `validateOrderGuard` | Is the order structurally valid? |
| `catalogDrivenTasksGuard` | Are catalog-driven tasks complete? |

**Customization**: Write a new Spring `@Component` implementing `org.squirrelframework.foundation.fsm.StateMachine` guard interface, reference it by name in the YAML.

### Layer 4: Environment Configuration (Runtime Behavior)

**What you configure**: Environment variables set in Docker Compose.

**What it controls**:
- Service URLs (catalog, inventory, activation, etc.)
- Feature flags (prerequisite validation, CPIB update, etc.)
- MongoDB connection strings
- Kafka broker addresses
- Keycloak issuer URIs
- Security settings

---

## 6. How Custom Orders Work

```mermaid
sequenceDiagram
    participant Customer
    participant UI as selfcare-ui
    participant OC as Order Capture
    participant Catalog
    participant Auth
    participant OI as Order Inventory
    participant Kafka

    Customer->>UI: Selects Fiber 300 + Static IP
    UI->>OC: POST /productOrder (TMF701)
    
    Note over OC: State machine starts
    
    OC->>OC: initialAutomatedTask
    OC->>Auth: Check user entitlements
    OC->>Catalog: Validate offering exists
    OC->>OC: isExistProductOfferingGuard → true
    
    OC->>OC: checkEligibilityChoice
    OC->>Catalog: Fetch qualification requirements
    OC->>OC: isEligibleAcquisitionGuard → true
    
    OC->>OC: validateOrder
    OC->>Catalog: Validate product specs
    OC->>OC: validateOrderGuard → true
    
    OC->>OC: completeOrder
    OC->>OI: POST /productOrder (persist)
    OI->>Kafka: ProductOrderStateChangeEvent (accepted)
```

**Key point**: The state machine never knows it's handling "broadband." It handles "a product offering with certain characteristics." The catalog data drives the differentiation.

---

## 7. How Custom COOD Works

```mermaid
flowchart TB
    subgraph "Catalog Seed Data"
        OFFERING[Product Offerings<br/>Fiber 300, Static IP]
        SPEC[Product Specifications<br/>Broadband CFS, Static IP CFS, Billing CFS]
        REL[Relationships<br/>bundles, reliesOn, requires]
    end

    subgraph "COOD Plan Derivation"
        EVENT[Consume ProductOrder] --> FETCH[Fetch specs<br/>from Catalog]
        FETCH --> CLASSIFY[Classify each order item]
        CLASSIFY --> BUILD[Build nodes per item]
        BUILD --> LINK[Link nodes by<br/>spec relationships]
        LINK --> PLAN[Persist OrchestrationPlan]
    end

    subgraph "Resulting Nodes"
        N1[Qualification node]
        N2[Broadband activation node]
        N3[Static IP activation node]
        N4[Billing initiation node]
    end

    subgraph "Dependencies"
        N2 -->|depends on| N1
        N3 -->|depends on| N2
        N4 -->|depends on| N2
        N4 -->|depends on| N3
    end

    REL -.->|Read by COOD| FETCH
    OFFERING -.->|Seeded to| CATALOG_SVC[Catalog Service]
    SPEC -.->|Seeded to| CATALOG_SVC
```

### How Node Sequencing Works

1. COOD reads the `ProductSpecificationRelationship` from the catalog for each ordered product
2. If spec A has `relationshipType: "requires"` pointing to spec B, node A gets a prerequisite on node B
3. The config flag `enablePrerequisiteValidation: true` ensures COOD won't start node A until node B completes
4. When a node's prerequisites are all met, COOD publishes a `deliveryStart-event`

### How Nodes Are Dispatched

Each orchestration node has a `relatedDeliveryFactoryRef` that points to a service to invoke:

```yaml
# Orchestration-delivery-management config
config:
  serviceOrderingUrl: "${SERVICE_ORDERING_URL}/serviceOrdering/v1/serviceOrder"
```

When Delivery Management receives a `deliveryStart-event`:

1. Reads the delivery factory reference from the node
2. Constructs a `ServiceOrder` payload
3. POSTs it to the configured `serviceOrderingUrl`
4. The backend service (qualification, activation, billing) processes it
5. The backend publishes `serviceOrderStateChange-event` or `deliveryStatus-event` on Kafka
6. COOD consumes the event and updates the node state

---

## 8. Security Model

```mermaid
sequenceDiagram
    participant User
    participant UI
    participant Gateway
    participant Keycloak
    participant API
    participant Auth as auth-userrole

    User->>UI: Access portal
    UI->>Gateway: Redirect to login
    Gateway->>Keycloak: Authenticate
    Keycloak->>Gateway: JWT token
    Gateway->>UI: Set session cookie

    UI->>Gateway: Request with session
    Gateway->>API: Forward JWT bearer token
    API->>Keycloak: Validate JWT (JWKS)
    API->>Auth: Resolve user roles & entitlements
    Auth->>API: Entitlements for this component
    API->>API: Check function entitlement
    API->>Gateway: 200 OK or 403 Forbidden
    Gateway->>UI: Response
```

### Role Hierarchy (POC-specific)

```
OTB_ADMIN
├── OTB_CATALOG_ADMIN (manage catalog)
├── OTB_ORDER_MANAGER (manage all orders)
│    ├── OTB_ORDER_OPERATOR (view all orders)
│    └── OTB_FALLOUT_OPERATOR (view/resolve fallout)
└── OTB_CUSTOMER (own orders only)
```

### Entitlements (POC-specific)

| Entitlement | Effect |
|---|---|
| `ENT_ORDER_CREATE` | Create customer orders |
| `ENT_ORDER_READ_OWN` | View own orders |
| `ENT_ORDER_READ_ALL` | View any order |
| `ENT_ORDER_UPDATE` | Modify order state |
| `ENT_ORCHESTRATION_READ` | View orchestration plans |
| `ENT_ORCHESTRATION_RETRY` | Trigger retry on failed nodes |
| `ENT_FALLOUT_READ` | View fallout incidents |
| `ENT_FALLOUT_RESOLVE` | Resolve fallout incidents |
| `ENT_CATALOG_MANAGE` | Create/modify catalog data |
| `ENT_SECURITY_MANAGE` | Manage users, roles, entitlements |

---

## 9. Configuration Architecture

### How Services Discover Each Other

Discobole services do NOT use a service registry (Eureka, Consul). They use **environment-variable-configured URLs**:

```yaml
# Example from order-capture application.yml
config:
  productOfferingUrl: "${PRODUCT_CATALOG_SERVICE}/productCatalogManagement/v1/productOffering"
  productSpecificationUrl: "${PRODUCT_CATALOG_SERVICE}/productCatalogManagement/v1/productSpecification"
  productInventoryUrl: "${PRODUCT_INVENTORY_SERVICE}/productInventoryManagement/v1/product"
  orderInventoryUrl: "${ORDER_INVENTORY_SERVICE}/productOrderingManagement/v1/productOrder"
```

In Docker Compose, these are resolved to service names:

```yaml
# docker-compose.yml
services:
  order-capture:
    environment:
      PRODUCT_CATALOG_SERVICE: "http://product-catalog:8080"
      PRODUCT_INVENTORY_SERVICE: "http://product-inventory:8080"
      ORDER_INVENTORY_SERVICE: "http://order-inventory:8080"
```

### Feature Flags (Environment Variables)

| Variable | Component | Effect |
|---|---|---|
| `enablePrerequisiteValidation` | COOD | Validate dependencies before starting nodes |
| `enableUpdateCpibProduct` | COOD | Update customer product inventory |
| `adaptiveOrchestrationPlanScheduling` | COOD | Dynamic node scheduling |
| `heldNodeProcessFlowCountThreshold` | COOD | Failure threshold before fallout |

### Spring Profiles

| Profile | Purpose |
|---|---|
| `dev` | Development settings, relaxed security |
| `plaintextconsole` | Plaintext Kafka console |
| `no-security` | Disable OAuth2 for local dev |

---

## 10. Summary: The Data-Driven Philosophy

| Concern | How It's Customized | Not Hardcoded |
|---|---|---|
| What products exist | Catalog seed data | Java code |
| Order lifecycle path | State machine YAML | Java code |
| Validation rules | Guard beans (Java, but swappable) | Inline logic |
| Orchestration sequence | Product spec relationships | Orchestration engine |
| Node dispatch targets | Environment URLs | Service code |
| Failure/retry behavior | Feature flags | Orchestration logic |
| Roles and permissions | auth-userrole seed JSON | Application code |
| Kafka topics | Configuration files | Service code |

This means you can take the same Discobole binaries and run them for:
- Broadband (like this POC)
- Mobile phone plans
- Fiber optic business lines
- IoT device provisioning
- Any product mix

The only thing that changes is **what data you put in the catalog, what guards you write, and how you configure the URLs and flags**.
