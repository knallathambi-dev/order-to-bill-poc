<!--
Software Name: orchestration-delivery-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Reliable Database and Messaging Integration

**Created by:** Mohamed BADAWEY (EXTERNE)  
**Decision date:** To Be Defined

## Context and Problem Statement

A service needs to create, update, or delete entities in the database and send messages/events to a message broker reliably. How can we atomically update the database and send messages to a message broker? What if a service writes an update to the database but fails to send a message to the broker?

## Decision

For services that need this:

1. Use MongoDB transactions to atomically write both event and data updates to the database.
2. Use Debezium connector as a message relay to move messages from MongoDB to Kafka.

## Decision Drivers and Outcome

- **Simplicity of Solution**
- **Reliable Messaging Guarantees**

## Other Considered Options

- **Using Embedded Debezium**: This solution lacks message relay capability, meaning an application would need to manually move messages from the database to the broker, which is tricky to implement and negates the need for Debezium.
- **Not Using This Solution**: If a service doesn't need reliable messaging or doesn't have the problem of atomic writes to the database and message broker, it doesn't gain much from this solution.

## Related Links

- [Transactional Outbox Pattern](https://microservices.io/patterns/data/transactional-outbox.html)
- [Debezium Connector POC from CPIB](https://debezium.io/documentation/reference/connectors/mongodb.html)
- [ADR Tracker in Gitlab](https://gitlab.tech.orange/disco/disco-project/architecture-kanban/-/issues/101)
