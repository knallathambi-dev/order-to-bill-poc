<!--
Software Name: orchestration-delivery-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# ADR-62 - Lead Time History Collection Approach

## Context

This study is made to help collect lead time statistics to evaluate order delivery duration & accurate future dated plan execution

## Purpose
There are multiple approaches to calculate and store the lead time history statistics, we need to study them.

## Decision date
02/09/2025

## Objective
Decide on an approach that is going to be used to implement the lead time history statistics.

---

## Considered Options

| Option | Integrating the calculation with node state updates (sync) | Separate the calculation to be subscribed on node state events (async) | Cron job (async) |
|--------|-----------------------------------------------------------|------------------------------------------------------------------------|------------------|
| **Description** | Update the lead time history statistics within the code of the different functions that create lead time (ie. when setting node state to completed update the lead time history statistics). | Create a new consumer per lead time creation event (ie. on node state change event to completed then update the lead time history statistics for the node). | Create a cron job that extracts the history statistics and stores it into our collection. |
| **Statistics Coverage** | - Doesn't lose any history statistic storage.<br>- History statistics stored match exactly the different plans/nodes processed. | - History statistics stored are delayed from the actual processed plans/nodes.<br>- Might lose some statistics. | - History lead times are delayed one hour from the actual updates.<br>- Statistics for the last hour are not generated (which should be fine in our business use case). |
| **Error Handling Mode** | Strict. | Loose. | Loose. |
| **Error Handling Description** | Failure to update the statistics stops the flow of the product order because the node is going to be stopped, if we're going to make it optional then the async approach is the same and easier to implement. | Failure to update the statistics could be handled in two ways:<br>- Ignore failed updates as they are very low and has negligible effect on the statistics outcome.<br>- Create dead letter topic + alarms for failed events to fix them and reprocess.<br>We could have it in two phases doing the first approach then evolve it to the second approach if needed. | - Errors could be handled through retries.<br>- May lose some hours but on the overall time period it's not going to affect the outcome.<br>- As a second phase we could implement a backward parsing mechanism for missed lead time history. |
| **Implementation Effort** | - More complex implementation (comparing to approach 1 and 2)<br>- Bug prone and less maintainable, since code needs to be integrated with the original business flows code. | - Simpler than approach 1, but more complex than approach 2.<br>- Lead time statistics code is separated into consumers, so code is more maintainable. | - The simplest approach as it only needs analysis of existing data and how to generate its statistics.<br>- The most maintainable and debuggable solution, because all logic is encapsulated in a single function that could be triggered on demand for debugging, and it's not affected by external factors. |
| **Out Come** | Stores single history records only | Stores single history records only | Able to store:<br>- Single records<br>- Aggregate data |

---

## Not Considered Options

- Implementing business logic using database pipelines or hooks (e.g., MongoDB triggers) was not considered, as this approach hides logic from the application layer, tightly couples the application to the database, and can lead to maintainability and portability issues.
- Relying on existing collections to generate statistics data on demand was not considered, as this would require a separate data lifecycle for lead time history statistics and tightly bind the results to the lifecycle of the existing collections.

---

## Decision

We're going with the **cron jobs solution** as it's the simplest, and offers great flexibility for any data model for lead time history storage.

---

## PO comment

1. The cron job has the lowest delay among the proposed solutions, and it also needs to define the impact on error handling (not very clear) can be implemented as a quick win (not a target solution) ?
2. For the following solution (as a target solution):  
   Integrating the calculation with node state updates (sync) and skipping the time update in case of error (node is not held in case time statistics information is not written correctly) will this pay-off? (considering complexity)
3. To consider solution 2:  
   - Need to elaborate more about the difference in level of complexity and maintainability between solution 1 and solution 2.
   - Introducing a new dedicated event is not meaningful?
   - What is the expected delay range, e.g in minutes or hours?

