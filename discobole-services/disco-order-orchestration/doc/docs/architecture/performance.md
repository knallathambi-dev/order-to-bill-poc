---
title: Performance Benchmarks 
summary: Performance Benchmarks 
authors:
  - Mohammad Aly
  - Catherine DAGUISE
---

# Performance Benchmark

Performance test campaigns have been conducted on each DISCOBOLE component on Release 10 to assess the capacities on each component. Only for fallout service it was tested based on Release 9. Various hypothesis have been taken depending on the component in terms of number of users, transactions, offer complexity etc…

New component performance test campaign will be performed for each major release.

E2E performance test campaign on the whole DISCOBOLE chain is ongoing, results will be shared once completed.

Below are the performance benchmark results for DISCOBOLE COOD component.

## ⚙️ SLA Details

| Metric  |  Description |
|:------------|:--------------------|
| Response Time for Plan Creation | `PublishProductOrderStateChangeEvent` |
| Response Time for Checking Plan Status | `CheckPlanState_isExecuted` |
| Response Time for Creating Fallout Incident | `Create_Process_Flow_Manually` |
| Response Time for Resolving Fallout Incident | `Patch_Process_Flow_with_Resolution` |
|Peak Hour Traffic Level | number of orders received and translated to Orchestration Plans with full Orchestration and Delivery Cycle |
| SOM response time | the average delivery factory response time for each service order request |

## ☁️ Application & Database Resources

### 💻 Orchestration delivery Service

| Resource  |limits |
|:----------|:------------|
| CPU | 2 |
| Memory  | 3Gi |

### 💻 Delivery management Service

| Resource  | limits |
|:----------|:------------|
| CPU | 1 |
| Memory | 1Gi |

### 📚 Orchestration and Delivery MongoDB

| Resource  | limits |
|:----------|:------------|
| CPU | 2 |
| Memory | 1500Mi |

### 💻 Fallout Service

| Resource  | limits |
|:----------|:------------|
| CPU | 2 |
| Memory  | 3Gi |

### 📚 Fallout MongoDB

| Resource | limits |
|:----------|:------------|
| CPU | 2 |
| Memory  | 1500Mi |

## 📊 Results

### API response time

| Metric  | Achieved Values @ 7200 Orders Per Hour |
|:------------|:--------------------|
| Response Time for Plan Creation | 99% < 0.9 second and 95% < 0.6 second and 90% < 0.4 second |
| Response Time for Checking Plan Status | 99% < 0.8 second and 95% < 0.5 second and 90% < 0.3 second |
| Response Time for Creating Fallout Incident  | 99% < 0.3 second and 90% < 0.2 second |
| Response Time for Resolving Fallout Incident  | 99% < 0.2 second  and 90% < 0.1 second |

### Orders Orchestration and Delivery at Different Traffic Levels

| Metric |  Achieved Values for Peak hour traffic |
|:------------|:--------------------|
| **Plan creation and execution-1 hour**  |  traffic level: **7200 orders/hr** |
|                                  |  traffic level: **9000 orders/hr** |
|                                  |  traffic level: **12000 orders/hr** |
| **Endurance test - Plan creation and execution** - **4 hour**  |  traffic level: **7200 orders/hr**  |

### Introducing SOM Reponse Delay

| Metric      | Achieved Values      |
|:------------|:---------------------|
| Traffic level +  SOM response time | **7200 orders/hr + 30 min SOM delay**|
|                                    | **7200 orders/hr + 60 min SOM delay** |

### Focus on Fallout Service

| Metric |  Achieved  Values |
|:------------|:--------------------|
| **Fallout incidents creation and resolving**  |  TPS (creation/resolution) : 7200 incidents/hr |
| **Plan creation and execution with 10% fallout**  |  traffic level: 7200 incidents/hr |

**Notes for fallout:**

- Test conducted on R9
- The first test has been done with fallout API for incident creation and resolution
- The second test has been done with plan creation and execution including manual resolution for the system created incidents

## 🧪 Test Bench Details

| Metric |  Achieved Values |
|:------------|:--------------------|
| Total Tests Executed | 4 |
| Release Version | R10 |
| APIs Tested |  TMF 637 product inventory management API <br /> TMF 633 Service catalogue management API <br /> TMF622 Product Ordering Management API  <br /> TMF641 Service Ordering Management <br /> TMF 620 Product catalogue management API <br /> TMF701 Process flow API 
| Tool Used | Apache JMeter 5.6.3 |
| Observability | Grafana, OpenShift Console |
| Cloud Platform | Openshift Flexible Engine |

## Next Steps

- Testing the impact of the DB size on the overall performance
- Enhancing the lead time at high traffic levels

## Notes on Testing Environment

- Delivery factories are mocked with immediate response for al tests. while SOM response delay is considered in one test to simulate the actual operational conditions.
- Same mock server is used to simulate the product inventory, where product status validation are done immediately without delays.
- Purge mechanism is applied with high rate, so that COOD database is always cleaned up.
- all Performance tests: The suscribed offers related to the orders are distributed such that 50% are simple, 40% are medium and 10 % are complex.
