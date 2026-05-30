<!--
Software Name: orchestration-delivery-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# ADR-66 - Batching Outbound Requests to Optimize Network Calls

## Context

During eco design analysis we discussed the possibility of making a study about batching outbound requests to optimize carbon footprint.

## Purpose

Analyse the possibility of batching outbound requests to reduce outbound HTTP calls within COOD.

## Jira

[IPCESECOOD-1025 - EcoDesign - software study for Back end Cache: Analyze requests to identify where caching could reduce redundant calls](https://jira-link-placeholder) `DONE`

## Decision date

07/01/2026

## Objective

Reach a conclusion of whether it’s possible to make an optimization in resources by batching outbound requests and in which areas in COOD.

## Non-Goal

- Changing business transactional operations.
- Introduction of new resources for batching and grouping.

---

## Current Outbound Requests

| Service           | Batching | Note |
|-------------------|----------|------|
| **Product Inventory** | N/A | - Nodes and Products are coupled and updated together one by one.<br>- We can't batch because we can't break the coupling between updating the product and the node update.<br>- In order to batch and maintain it we're going to need to introduce extra queues and syncing mechanisms that are going to increase the processing.<br>- Since we can't calculate the extra resources needed vs the network resources, it's safer to leave as is for now.<br>- In the future we're going to work on some batch deliveries to the delivery factory that might enable us to batch a group of nodes together instead of sending them one by one.<br>- It's going to be considered in the future. |
| **Product Catalog** | OK | We're already querying the product catalog by list of ids. |
| **Service Catalog** | OK | Already implemented in the delivery management. |
| **Delivery Factory** | N/A | Could be batched if delivery factory supports multiple requests in body at once.<br>But this use case is not supported currently. |
| **Fallout** | N/A | Following the current use case on errors we open a fallout incident using an http request, so it's not batchable. |

---

## Future Considerations

| Service           | Notes |
|-------------------|-------|
| **Delivery Factory** | - This could be batched in the future based on a separate process that groups the requests together before sending.<br>- It's going to only reduce resources on production not development.<br>- It's on our road map so we can implement it when it's required. |

---

## Closing Notes

As it stands for now the only solution for batching extra requests involves changing the process by introducing extra resources to group and send requests together.

- This doesn’t make sense in most cases from a business point of view.
- The introduction of the new resources to group and send batched requests adds extra resources that are going to affect carbon footprint and it’s hard to estimate the impact as well.
- On production in the future an internal layer inside each replica that batches requests from different consumers to the same service could be implemented but this requires another study.
- We need to introduce a power efficiency measuring tool such as Kepler to measure actual current power usage in our Kubernetes cluster and what is consuming the resources.

---

## Decision

- All requests that are batchable as of now are already batched.
- We're not going to introduce extra batching as of now.
- In the future we may reconsider if an opportunity arises.

