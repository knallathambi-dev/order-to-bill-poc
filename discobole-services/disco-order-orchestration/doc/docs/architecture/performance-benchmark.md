---
title: Performance Benchmark 
summary: COOD Performance Benchmark 
authors:
  - Mohammad Aly
---
# COOD Performance Benchmark

## Overview

This page provides an overview of the performance test conducted for COOD module. The performance test has covered main functionalities of COOD module in two separate scenarios:

1. Orchestration creation and delivery

2. Fallout incidents creation and resolving

For the traffic parameters, performance test has been run for COOD module considering a traffic level of 7200 accepted order per hour (the peak hour), i.e. **TPS = 2 orders per second**

In the first scenario, received orders are simulated via incoming events (orderstatechangeevent with status accepted) and carrying three categories of orders, and all orders have acquisition use case (add action only) and considering the below three orders with different order size.

- **Simple orders**: Mobile Package Basic: (4 order Items), represents	50% of total orders 

- **Medium orders**: Test offer Medium contract,  represents 40% of total orders       

- **Complex orders**: Mobile Smart Contract 12 order Items, represents 10% of total order

In the second scenario, fallout incidents are created with predefined error. Then a manual process to the DB is required to make the required correction such that COOD becomes able to resolve the fallout incidents successfully. The last step, is the resolution for the fallout incidents.


## Testing results

### Orchestration and Delivery use case
Order events are published along one hour and then results are collected after all order are executed.
Below table to summary the status of orders publishing and plan creation
[results for plans creation](../img/plans-creation.png)

Results collected after plans’ execution indicated Zero errors (all orders are orchestrated and delivered successfully)

[results for plans status](../img/plan-status.png)

The percentage of successfully executed orders is 100%.

| Status      | Order Count |  Percentage |
| ----------- | ----------- | ----------- |
| Executed      | 7173       | 100% |



### Fallout use case

Fallout is tested by creating an incident (with statis plan data) and then manually fix the error and finally resolve the fallout incident 

[results for fallout](../img/fallout-results.png)

## Next steps: Testing Orchestration delivery with 10% fallout ratio 
To go an extra mile in testing both orchestration delivery and fallout all together, will consider creating a scenario with orchestration plans that 10% errors that takes place during the delivery.

In this scenario, 90% of the orders should be successfully orchestrated and delivered, while 10% will result in fallout incidents that needs manual fixing then incident resolution. aftre resolution, the held plans will continue and  orchestration plan execution will be completed.


## Notes on testing environment

-	Delivery factories are mocked with immediate response. So, no delay is considered in the SOM response and hence orchestration plans are executed rapidly
-	Same mock server is used to simulate the product inventory, where product status validation are done immediately without delays.
-	Purge mechanism is applied with high rate, so that COOD DB is always clean up (including orchestration plans as well as events)

