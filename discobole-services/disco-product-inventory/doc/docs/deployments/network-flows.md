---
title: Network Flows
summary: Product Inventory live environments.
authors:
  - BADAWY Mohamed
---

# Network Flows

## Outgoing Flows

| Destination         | Port  | Protocol |
|:--------------------|:------|:---------|
| MongoDB             | 27017 | TCP      |
| Kafka               | 9092  | TCP      |
| keycloak            | 443   | HTTPS    |
| jaeger              | 14250 | HTTP     |
| Elasticsearch       | 9200  | HTTP     |
| catalog             | 8080  | HTTP     |
| resource-inventory  | 8080  | HTTP     |
| user-role-retrieval | 8080  | HTTP     |

## Incoming Flows

| Source                 | Port | Protocol |
|:-----------------------|:-----|:---------|
| order-management       | 8080 | HTTP     |
| qualification          | 8080 | HTTP     |
| orchestration-delivery | 8080 | HTTP     |
| gateway                | 8080 | HTTP     |
| prometheus             | 9000 | HTTP     |
