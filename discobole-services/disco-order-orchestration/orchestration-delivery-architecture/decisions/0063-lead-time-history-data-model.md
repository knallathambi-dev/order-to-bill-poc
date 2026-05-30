<!--
Software Name: orchestration-delivery-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# ADR-63 - Lead Time History Data Model

## Context

This study is made to help store lead time statistics in the database to evaluate order delivery duration & accurate future dated plan execution


## Purpose

There are multiple approaches to model data in the database for collecting the lead time history statistics, in this ADR we're going to explore them and decide on an approach.

## Decision date

07/09/2025

## Objective

Decide on an approach to store historical records of actual lead times for each order item, tracked by node (specID), plan (contract name), and categorized by delivery factory (Mobile, Fixed, Fiber, Shipment). To enable tracking and visualization of delivery performance across different dimensions.

---

## Considered Options

|                        | **Record Per Delivery** | **Aggregated Record Per Sample Period (eg. hour)** |
|------------------------|------------------------|----------------------------------------------------|
| **Model** | **node-lead-time-history-statistics:**<br> - node id (optional only for debugging purposes)<br> - product spec id<br> - delivery factory name<br> - contract name<br> - actual completion date<br> - actual lead time<br><br>**contract-lead-time-history-statistics:**<br> - plan id (optional only for debugging purposes)<br> - contract name<br> - actual lead time<br> - actual completion date | **node-lead-time-history-sampled-statistics:**<br> - composite id (product_spec_id, delivery_factory_name)<br> - product spec id<br> - delivery factory name<br> - contract name<br> - min actual lead time<br> - max actual lead time<br> - average actual lead time<br> - sample size (number of products in the sample window)<br> - sample window (hour at which this data was aggregated by actual completion time)<br><br>**contract-lead-time-history-sampled-statistics:**<br> - contract name<br> - min actual lead time<br> - max actual lead time<br> - average actual lead time<br> - sample time (hour at which this data was aggregated) |
| **Statistics Coverage** | - Doesn't lose any history statistic storage.<br>- History statistics stored have one to one mapping to the different plans/nodes processed. | - Aggregated statistics will only be available after the aggregation job runs (e.g., hourly or daily), so real-time statistics are not possible for the most recent data.<br>- Loses detailed record statistics (should be fine in our case). |
| **Implementation Effort** | More effort for querying and generating statistics | Generating statistics is straightforward |
| **Performance Considerations** | Slower performance, because:<br>- Number of plans/nodes is a direct factor in storage and querying performance.<br>- Database documents are going to increase rapidly as it's basically one record per node.<br>- Querying is going to have performance issues because of the number of records. | Faster performance, because:<br>- It's not affected by number of plans/nodes.<br>- Database records are only going to be 24 * (number of contract names + number of spec ids + number of delivery factories) records for per day.<br>- Querying is going to be much faster because of low number of records. |
| **Trade Offs** | **Pros:**<br>- Full detail, real-time stats.<br>**Cons:**<br>- High storage, slow queries. | **Pros:**<br>- Fast queries, low storage.<br>**Cons:**<br>- Delayed, less granular stats. |

---

## Not Considered Options

- Having multiple collections for statistics one per criteria (product_spec_id_delivery_statistics, delivery_factory_delivery_statistics, etc...) is going to use joins for join queries which is not optimal for no sql (Mongo) and complicates the statistics collection logic, and it doesn't offer any advantage.

---

## Decision

**Aggregated Record Per Sample Period was chosen** due to its significant performance and storage benefits. While this approach loses some detailed real-time statistics, this trade-off is acceptable for our reporting and analytics needs.

