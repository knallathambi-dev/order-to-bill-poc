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

Decide on an approach to store historical records of actual lead times for each order item, tracked by node (../

---

## Considered Options

|                        | **Record Per Delivery** | **Aggregated Record Per Sample Period (../
|------------------------|------------------------|----------------------------------------------------|
| **Model** | **node-lead-time-history-statistics:**<br> - node id (../
| **Statistics Coverage** | - Doesn't lose any history statistic storage.<br>- History statistics stored have one to one mapping to the different plans/nodes processed. | - Aggregated statistics will only be available after the aggregation job runs (../
| **Implementation Effort** | More effort for querying and generating statistics | Generating statistics is straightforward |
| **Performance Considerations** | Slower performance, because:<br>- Number of plans/nodes is a direct factor in storage and querying performance.<br>- Database documents are going to increase rapidly as it's basically one record per node.<br>- Querying is going to have performance issues because of the number of records. | Faster performance, because:<br>- It's not affected by number of plans/nodes.<br>- Database records are only going to be 24 * (../
| **Trade Offs** | **Pros:**<br>- Full detail, real-time stats.<br>**Cons:**<br>- High storage, slow queries. | **Pros:**<br>- Fast queries, low storage.<br>**Cons:**<br>- Delayed, less granular stats. |

---

## Not Considered Options

- Having multiple collections for statistics one per criteria (../

---

## Decision

**Aggregated Record Per Sample Period was chosen** due to its significant performance and storage benefits. While this approach loses some detailed real-time statistics, this trade-off is acceptable for our reporting and analytics needs.

