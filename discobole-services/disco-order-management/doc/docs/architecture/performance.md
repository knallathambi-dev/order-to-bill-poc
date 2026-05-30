---
title: Performance Benchmarks
summary: Describes the Performance Benchmarks for Order Management component
authors:
  - Mohamed Amine MACHERKI
---

# Performance Benchmarks

Performance test campaigns have been conducted on the **Order Management (OM)** component as part of the **DISCO project**. Tests have been designed to reflect a **B2C-oriented environment**, covering realistic order capture and consultation volumes across simple, medium, and complex contract offers.

| Assumption | Value |
|:-----------|:------|
| Total number of customers | 10 million |
| Average captured orders per day | 60,000 |
| Average accepted orders per day | 60,000 |
| Expected captured orders during peak hour (~15% of daily) | 9,000 |
| Average consultation requests per day | 1,000,000 |
| Consultation requests during rush hour (~30% of daily total) | 300,000 |

## 🧾 Services in Scope

| Service | Description | Status |
|:--------|:------------|:-------|
| Order Consultation Service | Order consultation APIs (TMF622 / TMF701) | ✅ Tested |
| Order Capture Service | Order capture & write APIs (TMF622 / TMF701) | ✅ Tested |

## 📊 Expected & Achieved SLA Details

| Metric | Expected Values | Achieved Values |
|:-------|:----------------|:----------------|
| Response time for GET (consultation) | Less than 200 ms | 13 ms (avg) / 28 ms (95th percentile) |
| Response time for write/update calls (POST/PATCH) | Less than 1 s (without eligibility) / Less than 2 s (with) | Validation in progress |

## 📋 Order Capture SLA per API

| API | SLA |
|:----|:----|
| CreateOrderCaptureProcessFlow | 1 s |
| CreateProductConfiguration | 1 s |
| PatchShippingDetails | 1 s |
| ConfirmConfiguration | 1 s |
| ValidateOrder | 2 s |
| CompleteOrder | 2 s |

## 🎯 Offer Distribution (Order Capture)

| Offer Type | Distribution |
|:-----------|:------------|
| Basic | 50% |
| Comfort | 40% |
| Smart | 10% |

## ☁️ Application & Database Resources

### 💻 Application

| Resource | Allocation |
|:---------|:-----------|
| CPU      | 4 |
| Memory   | 4Gi |

### 📚 MongoDB

| Resource | Allocation |
|:---------|:---------|
| CPU      | 3        | 
| Memory   | 2Gi      |

## 📊 Results

| Metric | Expected Values | Achieved Values |
|:-------|:----------------|:----------------|
| Avg Response Time (consultation) | Less than 200 ms | 13 ms |
| 95th Percentile Response Time (consultation) | Less than 200 ms | 28 ms |
| Error Rate | 0% | 0% |
| Consultation throughput | 15 TPS per API / 120 TPS cumulative | 131.3 TPS overall |

## 🔢 Consultation TPS Breakdown

| Metric | Expected | Achieved |
|:-------|:---------|:---------|
| TPS per API | 15 TPS | ~16.4 TPS |
| Cumulative TPS (8 APIs) | 120 TPS | 131.3 TPS |

## 🧪 Test Bench Details

| Metric | Values |
|:-------|:-------|
| Total Tests Executed | 7 |
| Release Version | R10 |
| APIs Tested | TMF622 - Product Order API / TMF701 - Process Flow API |
| Tool Used | Apache JMeter |
| Observability | Grafana, Prometheus, Jaeger, OpenShift Console |
| Cloud Platform | DIOD Flexible Engine (OpenShift) |
