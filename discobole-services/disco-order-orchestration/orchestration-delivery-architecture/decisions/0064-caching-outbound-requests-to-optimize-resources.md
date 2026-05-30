<!--
Software Name: orchestration-delivery-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# ADR-64 - Caching Outbound Requests to Optimize Resources

## Context

During one design analysis we discussed the possibility of making a study about caching outbound requests to optimize carbon footprint.

## Purpose

Analyze the possibility of caching to reduce outbound HTTP calls within CODO.

## Decision date

23/12/2023

## Objective

Reach a conclusion of whether it’s possible to make an optimization in resources by using caching and in which areas in CODO.

---

## Outbound Services

| Service           | Is Cacheable | Notes                                                                                 |
|-------------------|-------------|---------------------------------------------------------------------------------------|
| CM                | No          | CM service communicates with its own through events of order creation which is not cacheable. |
| Product Inventory | No          | Offer products are changed often and no query other than offer updates, so no data changing constantly. |
| Product Catalog   | Yes         | Product catalog is less likely to change so it can be cacheable, but we need to take two things into consideration: <br> - Setting up a cache TTL.<br> - Handle cache invalidation requirements. |
| Service Catalog   | Yes         | Service catalog is in the same case as the product catalog.                           |
| Delivery Factory  | No          | We don’t get data or requests from the delivery factories, we only send to them post requests and wait for delivery. |

---

## Considered Options

|                        | In App Memory Caching         | Centralized Caching (Redis)        |
|------------------------|------------------------------|------------------------------------|
| Resources Overhead     | Uses memory per node for each service.<br>A small portion of memory added to each service for caching. | Redis Cluster Resources<br>Managed centralized caching that could be used by all the replicas of our services.<br>Requests are only made once then cached in centralized caching reducing overall resources and carbon footprint. |
| Production Grade       | No<br>On having multiple replicas cache invalidation and resource overhead is going to increase per replica.<br>Increases number of requests to actual services before caching thus increasing carbon footprint. | Yes<br>**Not for Development environment.**<br>**Extra resources not necessary that increase the carbon footprint.**<br>**Production environment.**<br>**Suitable as a first layer of caching.**<br>**Second layer after an in-memory first caching layer.** |
| Suitable Environments  | Development environment first layer. | Production environment. |
| Spring Support         | Yes                          | Yes                                |

---

## Future Considerations

- Cross-service cache invalidation
- Policy-oriented invalidation aligned with TMF standard events.
- Spring bean hooks
- Support label binding, participating services must implement conditional requests and return HTTP 304 Not Modified to indicate no refresh is needed.
- Expose cache hit/miss metrics and visualize them in Grafana.
- Caching architecture:
  - When Redis is enabled, use layered caching: in-memory (L1) with Redis as L2 to reduce network hops to the Redis cluster.
- On read cache TTLs:
  - E.g., service catalog requests have more TTL than product catalog data.
- Read the cache first, on miss, call the source service and populate the cache.
- Performance check:
  - This should have small but positive impact on our performance tests in terms of the average time from delivery start to executed delivery.

---

## Notes To Consider

- Use Spring Cache annotations.
- Use caffeine for Redis, nodes is sharded by url properties.
- Use Caffeine dependency for in-memory caching to enable TTL setup.
- Introduce basic TTL for product catalog and service catalog.
- Add caching for the product catalog and service catalog using caffeine in our services.
- Add Redis caching with a flag to choose between in-memory and Redis caching.
- Help with product catalog cache invalidation methods.

---

## Decision

Use Spring Cache annotations and introduce both in-memory (Caffeine) and Redis caching for product and service catalog, with TTL and cache invalidation strategies. Centralized Redis caching is preferred for production, while in-memory caching is suitable for development and as a first layer.

---

## Actions

- Implement Spring Cache annotations.
- Use Caffeine for in-memory caching with TTL.
- Add Redis caching with configuration flag.
- Visualize cache hit/miss metrics in Grafana.
- Document cache invalidation strategies.


