# Discobole-First Architecture for Order-to-Bill POC

## 1. Architecture Principles

- Discobole is the source of truth for order capture, order state, orchestration, delivery state, fallout, and authorization.
- Custom services simulate telecom and billing backends; they do not replace Discobole OMS/COOD.
- Kafka events drive backend state transitions.
- UI reuses Discobole portals wherever possible.
- The POC gateway provides local routing/session glue and optional projections, not core order logic.

## 2. High-Level Component View

```mermaid
graph TB
    subgraph "Discobole UI Portals"
        SELFCARE[selfcare-ui<br/>Customer order capture/tracking]
        ORDERUI[order-inventory-ui<br/>Operator order monitoring]
        COODUI[order-orchestration-ui<br/>Plan/fallout monitoring]
    end

    subgraph "POC Edge"
        GATEWAY[POC Gateway/API<br/>proxy, auth/session glue, projections]
    end

    subgraph "Discobole Core"
        OC[Order Capture<br/>TMFC002 + TMF701]
        OI[Order Inventory<br/>ProductOrder lifecycle]
        COOD[COOD<br/>Orchestration Delivery]
        DM[Delivery Management]
        FALLOUT[Delivery Fallout<br/>ProcessFlow]
        AUTH[auth-userrole<br/>TMF672]
        CATALOG[Catalog/Product Specification]
    end

    subgraph "POC Backend Simulators"
        QUAL[qualification-service]
        ACT[activation-service]
        BILL[billing-service]
    end

    subgraph "Platform"
        KEYCLOAK[Keycloak]
        KAFKA[Kafka]
        MONGO[MongoDB]
        CONNECT[Kafka Connect/Debezium]
    end

    SELFCARE --> GATEWAY
    ORDERUI --> GATEWAY
    COODUI --> GATEWAY

    GATEWAY --> OC
    GATEWAY --> OI
    GATEWAY --> COOD
    GATEWAY --> FALLOUT
    GATEWAY --> CATALOG
    GATEWAY --> AUTH

    OC --> OI
    OC --> CATALOG
    OC --> AUTH
    OI --> KAFKA
    COOD --> KAFKA
    DM --> KAFKA
    FALLOUT --> KAFKA

    KAFKA --> COOD
    KAFKA --> DM
    KAFKA --> GATEWAY

    DM --> QUAL
    DM --> ACT
    DM --> BILL
    QUAL --> KAFKA
    ACT --> KAFKA
    BILL --> KAFKA

    OC --> MONGO
    OI --> MONGO
    COOD --> MONGO
    DM --> MONGO
    FALLOUT --> MONGO
    AUTH --> MONGO
    CONNECT --> KAFKA
    MONGO --> CONNECT

    KEYCLOAK --> AUTH
```

## 3. Runtime Flow

```mermaid
sequenceDiagram
    participant Customer
    participant UI as selfcare-ui
    participant GW as POC Gateway
    participant OC as Discobole Order Capture
    participant OI as Discobole Order Inventory
    participant Kafka
    participant COOD
    participant DM as Delivery Management
    participant Qual as qualification-service
    participant Act as activation-service
    participant Bill as billing-service
    participant Fallout

    Customer->>UI: Select Fiber 300 + Static IP
    UI->>GW: Submit order/process flow
    GW->>OC: Forward authenticated order capture request
    OC->>OC: Validate order via ProcessFlow/catalog rules
    OC->>OI: Create/update ProductOrder
    OI->>Kafka: ProductOrderStateChangeEvent accepted
    Kafka->>COOD: Consume accepted ProductOrder
    COOD->>COOD: Build orchestration plan/nodes
    COOD->>Kafka: OrchestrationPlanStateChangeEvent
    COOD->>Kafka: deliveryStart-event for first eligible node
    Kafka->>DM: Consume deliveryStart-event
    DM->>Qual: Invoke qualification backend
    Qual->>Kafka: Service/delivery status completed
    Kafka->>COOD: Node state update
    COOD->>Kafka: deliveryStart-event for activation nodes
    Kafka->>DM: Consume activation delivery start
    DM->>Act: Activate broadband/static IP
    Act->>Kafka: Service/delivery status completed or failed
    Kafka->>COOD: Node state update
    alt activation failure
        COOD->>Fallout: Create fallout/process flow
        Fallout->>Kafka: FalloutIncidentStateChangeEvent
        Kafka->>COOD: Retry/resume signal
    end
    COOD->>Kafka: deliveryStart-event for billing
    Kafka->>DM: Consume billing delivery start
    DM->>Bill: Create account and invoice
    Bill->>Kafka: Service/delivery status completed
    Kafka->>COOD: Plan completed
    COOD->>Kafka: OrchestrationPlanStateChangeEvent completed
    UI->>GW: Track order / refresh
    GW->>OI: Read ProductOrder state
    GW->>COOD: Read orchestration plan
    GW->>UI: Display current status
```

## 4. UI Architecture

```mermaid
graph LR
    subgraph "Customer"
        SELFCARE[selfcare-ui]
    end

    subgraph "Operator/Admin"
        HOST[hostmode-ui optional shell]
        ORDERUI[order-inventory-ui]
        COODUI[order-orchestration-ui]
        CATALOGUI[product-catalog-ui optional]
    end

    subgraph "Gateway"
        PROXY[POC Gateway / portal proxy]
    end

    SELFCARE --> PROXY
    ORDERUI --> PROXY
    COODUI --> PROXY
    CATALOGUI --> PROXY
    HOST --> ORDERUI
    HOST --> COODUI
```

`selfcare-ui` is the customer-facing portal. `order-inventory-ui` and `order-orchestration-ui` provide operational proof that Discobole services own order and orchestration state.

## 5. Event Architecture

```mermaid
graph TB
    OI[Order Inventory] -->|ProductOrderStateChangeEvent| PO_TOPIC[disco.order-management.productOrderStateChange-event]
    PO_TOPIC --> COOD[COOD]

    COOD -->|OrchestrationPlanStateChangeEvent| PLAN_TOPIC[disco.order-orchestration.orchestrationPlanStateChange-event]
    COOD -->|OrchestrationPlanNodeStateChangeEvent| NODE_TOPIC[disco.order-orchestration.orchestrationPlanNodeStateChange-event]
    COOD -->|DeliveryOrderEvent| START_TOPIC[disco.delivery-management.deliveryStart-event]

    START_TOPIC --> DM[Delivery Management]
    DM -->|ServiceOrderEvent| SOM_TOPIC[disco.service-order-management.serviceOrderStateChange-event]
    DM -->|DeliveryStatusEvent| STATUS_TOPIC[disco.delivery-management.deliveryStatus-event]

    QUAL[qualification-service] --> SOM_TOPIC
    ACT[activation-service] --> SOM_TOPIC
    BILL[billing-service] --> SOM_TOPIC

    SOM_TOPIC --> COOD
    STATUS_TOPIC --> COOD

    FALLOUT[Fallout] -->|FalloutIncidentStateChangeEvent| F_TOPIC[Fallout state topic]
    F_TOPIC --> COOD
```

## 6. Data Ownership

| Data | Owner |
| --- | --- |
| Customer order/ProductOrder | Discobole Order Inventory |
| Order capture process/tasks | Discobole Order Capture + ProcessFlow |
| Orchestration plans/nodes | COOD |
| Delivery orders/status | Delivery Management |
| Fallout incidents | Orchestration Delivery Fallout |
| Users/roles/permissions | Keycloak + auth-userrole |
| Broadband qualification record | qualification-service |
| Activation records | activation-service |
| Billing account/invoice records | billing-service |
| UI session/proxy state | POC gateway/selfcare server |

## 7. Deployment Topology

Local Docker Compose must include:

- Keycloak
- auth-userrole
- MongoDB replica set
- Kafka
- Kafka Connect/Debezium if required by Discobole outbox event publication
- Order Capture
- Order Inventory
- Catalog/Product Specification services or POC-backed catalog seed service
- COOD
- Delivery Management
- Fallout/ProcessFlow
- POC simulator services
- POC gateway
- `selfcare-ui`
- `order-inventory-ui`
- `order-orchestration-ui`

## 8. Integration Boundaries

- UI portals talk only to the gateway/proxy in local POC deployment.
- Discobole services communicate over their native REST and Kafka contracts.
- POC simulator services integrate through Delivery Management or Discobole-compatible service-order events.
- Any direct custom event topics are internal implementation details and must not be required for the primary flow.
