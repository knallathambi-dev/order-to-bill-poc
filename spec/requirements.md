# Telecom Broadband Order-to-Bill POC using Discobole

## Detailed POC Scope, Architecture, Design and Discobole Reusability Assessment

---

# 1. Executive Summary

This Proof of Concept (POC) aims to evaluate whether the Discobole Suite can serve as a Telecom Order Management (TOM) platform for Broadband Order-to-Bill orchestration.

The primary focus is not Broadband provisioning itself, but validating Discobole's ability to:

* Capture customer orders
* Perform order decomposition
* Orchestrate fulfillment workflows
* Manage long-running business processes
* Correlate events across multiple services
* Track order state and fulfillment progress
* Coordinate billing initiation
* Provide end-to-end visibility

The POC will follow Discobole's native architecture:

* Event Driven Architecture
* Kafka-based messaging
* MongoDB persistence
* Workflow orchestration through Discobole components
* Domain event correlation

---

# 2. POC Objectives

## Business Objectives

Demonstrate an end-to-end Broadband Order-to-Bill journey.

Customer purchases:

```text
Fiber Broadband 300 Mbps
+
Static IP Add-on
```

System should:

1. Capture customer order
2. Validate serviceability
3. Decompose customer order
4. Fulfill broadband service
5. Fulfill static IP service
6. Create billing account
7. Generate invoice
8. Complete order

---

## Technical Objectives

Validate Discobole capabilities for:

| Capability          | Validation Goal                |
| ------------------- | ------------------------------ |
| Workflow Management | Long-running orchestration     |
| Order Management    | Parent-child order hierarchy   |
| Event Correlation   | Kafka event driven progression |
| State Management    | Lifecycle tracking             |
| Persistence         | MongoDB document model         |
| Resilience          | Failure and retry handling     |
| Visibility          | End-to-end order tracking      |

---

# 3. Business Use Case

## New Broadband Connection

Customer orders:

```text
Fiber Broadband 300 Mbps
Static IP
```

Through:

```text
Customer Portal
```

Order should be fulfilled automatically.

---

# 4. Telecom Order Management Concepts Demonstrated

This POC intentionally includes core Telecom OMS concepts.

---

## Customer Order

Commercial order submitted by customer.

Example:

```json
{
  "orderId": "ORD-10001",
  "product": "FIBER300",
  "staticIp": true
}
```

---

## Order Decomposition

Customer Order decomposed into fulfillment orders.

```text
Customer Order
      |
      +--------------------+
      |                    |
      V                    V

Broadband Service    Static IP Service
Order               Order

      |
      V

Billing Order
```

This is the key telecom capability being evaluated.

---

## Fulfillment Orchestration

Discobole coordinates execution of:

```text
Qualification

Broadband Activation

Static IP Activation

Billing
```

---

## Order Completion

Parent order completes only when all child orders complete.

---

# 5. Scope

## Included

### Order Capture

Create customer order.

---

### Service Qualification

Check broadband availability.

---

### Order Decomposition

Create child fulfillment orders.

---

### Broadband Activation

Simulated service activation.

---

### Static IP Activation

Simulated add-on activation.

---

### Billing Account Creation

Create billing account.

---

### Invoice Generation

Generate first invoice.

---

### Order Completion

Complete parent order.

---

### Failure Handling

Activation failure.

Retry activation.

Resume workflow.

---

# Excluded

These will be deferred to Phase 2.

### Inventory

```text
Router Allocation
```

### Network Inventory

```text
OLT Allocation
VLAN Allocation
Port Allocation
```

### Workforce Management

```text
Technician Scheduling
```

### Payment Collection

```text
Payment Gateway
```

### CRM

```text
Customer Profile Management
```

---

# 6. Discobole Reusability Strategy

The POC should maximize usage of Discobole capabilities and minimize custom orchestration code.

---

## Discobole Responsibilities

### Workflow Lifecycle

Discobole owns:

```text
CREATED

QUALIFIED

DECOMPOSED

ACTIVATING

BILLING

COMPLETED
```

---

### Order Correlation

Discobole correlates:

```text
OrderCreated

QualificationCompleted

ActivationCompleted

BillingCompleted
```

back to workflow instances.

---

### Event Processing

Discobole consumes and produces Kafka events.

---

### State Persistence

Workflow state persisted in MongoDB.

---

### Retry Management

Workflow resumes after failures.

---

## Custom Services Responsibilities

Only business logic.

### Qualification Service

```text
Is broadband available?
```

---

### Activation Service

```text
Activate broadband
Activate static IP
```

---

### Billing Service

```text
Create account
Generate invoice
```

No orchestration logic should exist in these services.

---

# 7. Target Architecture

```text
+------------------------------------------------+
|               Customer Portal                  |
+-----------------------+------------------------+
                        |
                        V

+------------------------------------------------+
|                 Discobole OMS                  |
|                                                |
|  Workflow Engine                              |
|  Order Decomposition                          |
|  Event Correlation                            |
|  State Management                             |
+-----------------------+------------------------+

                        |
                        V

==================== KAFKA ======================

OrderCreated

QualificationCompleted

BroadbandActivated

StaticIPActivated

BillingCompleted

InvoiceGenerated

=================================================

      |                  |                  |
      V                  V                  V

Qualification      Activation         Billing
Service            Service            Service

      \                 |                /
       \                |               /
        +---------------+--------------+
                        |
                        V

                    MongoDB
```

---

# 8. Order Lifecycle

## State Model

```text
CREATED

QUALIFICATION_PENDING

QUALIFIED

DECOMPOSITION_COMPLETED

ACTIVATION_IN_PROGRESS

PARTIALLY_ACTIVATED

ACTIVATED

BILLING_IN_PROGRESS

COMPLETED
```

---

## Failure States

```text
QUALIFICATION_FAILED

ACTIVATION_FAILED

BILLING_FAILED
```

---

# 9. Order Decomposition Design

## Parent Order

```json
{
  "orderId": "ORD-10001",
  "type": "CUSTOMER_ORDER"
}
```

---

## Child Orders

### Broadband Service Order

```json
{
  "serviceOrderId": "SO-10001",
  "type": "BROADBAND"
}
```

---

### Static IP Order

```json
{
  "serviceOrderId": "SO-10002",
  "type": "STATIC_IP"
}
```

---

### Billing Order

```json
{
  "serviceOrderId": "SO-10003",
  "type": "BILLING"
}
```

---

## Dependency Model

```text
Qualification
      |
      V

+----------------------+
| Broadband Activation |
+----------------------+

+----------------------+
| Static IP Activation |
+----------------------+

      |
      V

Billing

      |
      V

Complete Order
```

Billing cannot start until both activation orders finish.

---

# 10. Kafka Event Model

## OrderCreated

```json
{
  "eventType": "OrderCreated",
  "orderId": "ORD-10001"
}
```

---

## QualificationCompleted

```json
{
  "eventType": "QualificationCompleted",
  "orderId": "ORD-10001"
}
```

---

## BroadbandActivated

```json
{
  "eventType": "BroadbandActivated",
  "serviceOrderId": "SO-10001"
}
```

---

## StaticIPActivated

```json
{
  "eventType": "StaticIPActivated",
  "serviceOrderId": "SO-10002"
}
```

---

## BillingCompleted

```json
{
  "eventType": "BillingCompleted",
  "serviceOrderId": "SO-10003"
}
```

---

## OrderCompleted

```json
{
  "eventType": "OrderCompleted",
  "orderId": "ORD-10001"
}
```

---

# 11. MongoDB Data Model

## Orders Collection

```json
{
  "_id": "ORD-10001",
  "status": "ACTIVATED",
  "product": "FIBER300"
}
```

---

## Service Orders Collection

```json
{
  "_id": "SO-10001",
  "parentOrderId": "ORD-10001",
  "type": "BROADBAND",
  "status": "COMPLETED"
}
```

---

## Workflow Collection

```json
{
  "_id": "WF-10001",
  "orderId": "ORD-10001",
  "currentState": "BILLING"
}
```

---

## Event Collection

```json
{
  "orderId": "ORD-10001",
  "eventType": "BroadbandActivated",
  "timestamp": "..."
}
```

---

# 12. Happy Path Execution

```text
Order Created

      |
      V

Qualification Passed

      |
      V

Order Decomposition

      |
      +-------------------+
      |                   |
      V                   V

Broadband         Static IP
Activation        Activation

      |
      +----------+
                 |
                 V

Billing

      |
      V

Invoice

      |
      V

Order Complete
```

---

# 13. Failure Scenario

Activation Failure

```text
Order Created

Qualification Passed

Broadband Activation Failed

Workflow Suspended

Retry Activation

Activation Success

Workflow Resumed

Billing

Order Complete
```

This validates Discobole's long-running orchestration capability.

---

# 14. POC Success Criteria

The POC is successful if it demonstrates:

### Telecom Capability

✅ Customer Order

✅ Order Decomposition

✅ Parent/Child Order Relationships

✅ Dependency Management

✅ Fulfillment Orchestration

✅ Billing Dependency

---

### Discobole Capability

✅ Workflow Execution

✅ Kafka Event Processing

✅ MongoDB Persistence

✅ Event Correlation

✅ Retry Handling

✅ State Management

---

### Demo Capability

A dashboard showing:

```text
Order ORD-10001

✓ Created

✓ Qualified

✓ Decomposed

✓ Broadband Activated

✓ Static IP Activated

✓ Billing Completed

✓ Invoice Generated

✓ Completed
```

plus visibility into:

* Parent Order
* Child Service Orders
* Workflow State
* Kafka Events
* MongoDB Documents

This demonstrates not only that Discobole can orchestrate a broadband Order-to-Bill process, but also that it can model the fundamental telecom OMS pattern of customer order decomposition into fulfillment orders while remaining aligned with Discobole's native event-driven architecture.
