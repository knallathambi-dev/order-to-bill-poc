---
title: Performance Benchmarks
summary: Describes the Performance Benchmarks
author:
  - Khaoula Ben Atitallah
  - Mohamed Amine MACHERKI
---

# Performance Benchmarks

Performance test campaigns have been conducted on the **Product Inventory (CPIB)** component as part of the **DISCO project**. Tests have been designed to reflect a **B2C-oriented environment**, covering realistic user volumes, transaction distributions, and offer complexity across mobile plans.

| Assumption | Value |
|:-----------|:------|
| Total number of customers | 10 million |
| Average write operations per day (POST/PATCH) | 60,000 |
| Average consultation requests per day | 1,000,000 |
| Write operations during rush hour (~20% of daily total) | 12,000 |
| Consultation requests during rush hour (~30% of daily total) | 300,000 |

## 📦 Offer Profiles Tested

| Offer | Profile | Products Created |
|:------|:--------|:----------------|
| Mobile Package Basic | Low complexity | 4 products |
| Test Offer Medium Contract | Medium complexity | 8 products |
| Mobile Smart Contract | High complexity | 12 products |

## 🎯 Offer Distribution

| Offer | Distribution |
|:------|:------------|
| Mobile Package Basic | 50% |
| Test Offer Medium Contract | 40% |
| Mobile Smart Contract | 10% |

## 🔄 Scenarios Tested per Offer

| Offer | Scenarios |
|:------|:----------|
| Mobile Package Basic | Acquisition, migration to smart contract and termination |
| Test Offer Medium Contract | Acquisition, modification, migration to smart contract and termination |
| Mobile Smart Contract | Acquisition, modification, migration to medium and termination |

## 📊 Expected & Achieved SLA Details

| Metric | Expected Values | Achieved Values |
|:-------|:----------------|:----------------|
| Response time for write/update calls (POST/PATCH) | Less than 300 ms | 15 ms (avg) / 37 ms (95th percentile) |

## 🔢 TPS Requirements & Results

| Operation Type | Expected TPS | Calculation Basis | Achieved TPS |
|:--------------|:-------------|:-----------------|:-------------|
| Write (POST/PATCH) | 3.5 TPS | 12,000 req/rush hour ÷ 3,600 s | 5.55 TPS (scenario-wise) / 130.2 TPS (overall) |

## ⏳ Test Campaign Status

| Campaign | Status |
|:---------|:-------|
| CPIB Write — Nominal & Limit Tests | ✅ Completed |

## ☁️ Application & Database Resources

### 💻 Application 

| Resource | Allocation |
|:---------|:-----------|
| CPU      | 3 |
| Memory   | 3000Mi |

### 📚 MongoDB 

| Resource | Allocation |
|:---------|:-----------|
| CPU      | 2 |
| Memory   | 2Gi |

## 📊 Results

| Metric | Expected Values | Achieved Values |
|:-------|:----------------|:----------------|
| Avg Response Time (Write) | Less than 300 ms | 15 ms |
| 95th Percentile Response Time (Write) | Less than 300 ms | 37 ms |
| Error Rate | 0% | 0% |
| Write throughput (POST/PATCH) | 3.5 TPS | 5.55 TPS (scenario-wise) / 130.2 TPS (overall) |

## 🧪 Test Bench Details

| Metric | Values |
|:-------|:-------|
| Total Tests Executed | 2  |
| Release Version | R10 |
| APIs Tested | TMF637 - Product Inventory API |
| Tool Used | Apache JMeter |
| Observability | Grafana, Prometheus, Jaeger, OpenShift Console |
| Cloud Platform | DIOD Flexible Engine (OpenShift) |
