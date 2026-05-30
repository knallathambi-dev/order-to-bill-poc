<!--
Software Name: orchestration-delivery-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Approach Evaluation for COOD Database, Design Patterns, and Microservices

## Objective

The objective of this Architecture Decision Record (../

## Considered Options

We considered two design patterns: CQRS (../

**CQRS Pattern**:

Pros:
- Allows for better scalability and performance by separating read and write operations.
- Enables easier maintenance and modification of the codebase.
- Simplifies testing and debugging processes.
- Facilitates the implementation of event-driven architectures.

Cons:
- May introduce additional complexity into the system.
- Requires more development effort to implement.
- May not be suitable for small or simple systems.
- Requires a deep understanding of the domain and the business requirements.

**CRUD Pattern**:

Pros:
- Simple and straightforward to implement.
- Suitable for small or straightforward systems.
- Requires less development effort than CQRS.
- Easy to learn and understand for developers who are new to the system.

Cons:
- Can become difficult to manage and maintain as the system grows in complexity.
- May not perform well under heavy load.
- Can lead to data inconsistency and integrity issues.
- May not be suitable for systems with complex business logic.

## COOD Software Architecture - IPCEI CIS - Confluence for Orange

[Link to Proposed Solution of COOD Component](../

## Supporting Statements for Not Using a State Machine

- There doesn't seem to be a need for managing different states in the proposed solution. The integration of components is primarily done through API calls, except for the status update.
- Implementing a state machine would add unnecessary complexity to the solution without providing significant benefits.

## Using CRUD Design Pattern and MongoDB

- The proposed solution involves fetching data from different sources and creating an orchestration plan. This aligns well with the CRUD design pattern, suitable for managing data operations.
- MongoDB, as a NoSQL database, offers flexibility in handling unstructured data and allows for easy scaling. It efficiently stores and retrieves orchestration plans.
- MongoDB's document-oriented nature aligns with the architecture, making it easier to save, update, and retrieve orchestration plans.
- MongoDB's indexing and querying capabilities enable efficient data retrieval, crucial for handling large volumes of orchestration plans.

In summary, not using a state machine simplifies implementation, and utilizing the CRUD design pattern with MongoDB aligns well with the proposed solution's requirements, providing flexibility, scalability, and efficient data handling.

## Decision

1. The chosen message bus for communication between components is Kafka, shared among all components.
2. A single microservice will be developed in Java, with three separate listeners.
3. Listeners will handle different topics: OM Topic (../
4. CRUD design pattern is preferred over CQRS as it handles data operations like fetching, creating, and updating orchestration plans and delivery status.
5. The microservice will be event-driven, triggering actions based on received events.
6. As part of the MVP scope, there will be no exposed microservice endpoint from COOD.
7. MongoDB will be utilized as the database for the MVP phase, with potential exploration of alternative options like Neo4j in the future.
8. Components not available in the MVP will be mocked for implementation.

## References
