---
title: Performance Benchmarks 
summary: Describes the Performance Benchmarks
author:
  - Sumit
  - Catherine DAGUISE
---

# Performance Benchmarks

Performance test campaigns have been conducted on each DISCOBOLE component on Release 10 to assess the capacities on each component. Various hypothesis have been taken depending on the component in terms of number of users, transactions, offer complexity etc…

New component performance test campaign will be performed for each major release.

E2E performance test campaign on the whole DISCOBOLE chain is ongoing, results will be shared once completed.

Below are the performance results for DISCOBOLE Product Catalog (ODACAT).

## ⚙️ SLA Details

| Metric                                       | Target Values  | Achieved Values   |
|----------------------------------------------|----------------|-------------------|
| GET TMF620 Response Time                     | 100 ms         | 25 ms             |
| Event service (CFS & stockItem replication)  | 1000 ms        | 15 ms             |
| POST/PATCH TMF701 Process Flow Response Time | 1000 ms        | 1000 ms           |

## ☁️ Application & Database Resources

### TMF620 Product Catalog Management API - GET

#### 💻 Application

| Resource | Allocation |
|----------|------------|
| CPU      | 3 Core     |
| Memory   | 3 Gi       |

#### 📚 MongoDB

| Resource  | Limits     |
|-----------|------------|
| CPU       | 7 Core     |
| Memory    | 6 Gi       |

### Event Service (CFS & stockItem replication)

#### 💻 Application

| Resource | Allocation |
|----------|------------|
| CPU      | 200 m      |
| Memory   | 1024 Mi    |

#### 📚 MongoDB

| Resource   | Limits     |
|------------|------------|
| CPU        | 2 Core     |
| Memory     | 2 Gi       |

### TMF701 Process Flow API - POST/PATCH

#### 💻 Application

| Resource | Allocation |
|----------|------------|
| CPU      | 3 Core     |
| Memory   | 3 Gi       |

#### 📚 MongoDB

| Resource  | Limits     |
|-----------|------------|
| CPU       | 1000 m Core|
| Memory    | 1536 Mi    |

## 📊 Results

| Metric                                                                   | Target Values                                        | Achieved Values                                      |
|--------------------------------------------------------------------------|------------------------------------------------------|------------------------------------------------------|
| Number of Get TMF620 Product Catalog Management API calls                | 500 TPS                                              | 516 TPS                                              |
| Event service (CFS & stockItem replication)                              | 10 TPS                                               | 10 TPS                                               |
| Number of users configuring offerings simultaneously TMF701 Process Flow | 30 users performing concurrent operations per second | 35 users performing concurrent operations per second |

## 🧪 Test Bench Details

| Metric               | Achieved Values            |
|----------------------|----------------------------|
| Total Tests Executed | 9                          |
| Release Version      | R10                        |
| APIs Tested          | 1. TMF620 - Product Catalog API<br>2. TMF701 - Process Flow Management API |
| Tool Used            | Apache JMeter 5.6.3        |
| Observability        | Grafana, OpenShift Console |
| Cloud Platform       | Openshift Flexible Engine  |
