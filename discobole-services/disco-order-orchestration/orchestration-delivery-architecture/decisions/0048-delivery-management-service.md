<!--
Software Name: orchestration-delivery-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Decision on Architecture and Pattern for Delivery Management Service

## Objective

Decide on the architecture and pattern for the delivery management service in the context of DISCO.

## Root Cause Analysis

In light of the feature to add multiple delivery factories in DISCO, there is a need to separate the delivery functionalities from orchestration and plan initialization processing.

## Decision Drivers

1. **Separation of Concerns:**
   - Clearly distinguish delivery management from order orchestration.
   - Enhance modularity and maintainability by isolating these distinct functionalities.

2. **Centralized Delivery Process:**
   - Consolidate the management of the delivery process within a dedicated microservice.
   - Promote efficiency and ease of maintenance through a centralized approach.

3. **Unified Interfaces for Coordination:**
   - Establish standardized interfaces for initiating delivery and retrieving delivery status.
   - Simplify integration efforts by ensuring a consistent approach, minimizing the need for adaptations.

## Decision

Implementation of a New Microservice for Delivery Management

### Overview

Recognizing the need for a dedicated microservice to streamline and enhance our delivery management processes, we have decided to introduce a central coordinator microservice. This microservice will play a crucial role in orchestrating the delivery of nodes that have completed their flow in the COO.

### Key Components

1. **MongoDB Integration:**
   - Employ MongoDB as the primary data store for the microservice.

2. **Event-Driven Architecture with Kafka:**
   - Introduce two new Kafka topics - "start delivery" and "delivery status" - for communication with the COO.

3. **Technology Stack:**
   - Utilize Spring Kafka for efficient message consumption and production.

4. **Orchestration Microservices Design Pattern:**
   - Adopt the Orchestration Microservices Design Pattern for structuring the central coordinator microservice.

5. **Generic Utilities for Delivery Factories:**
   - Develop generic utilities within the microservice to facilitate the quick addition of new delivery factories.

### Expected Benefits

- **Enhanced Coordination:**
   - The microservice, following the Orchestration Microservices Design Pattern, will improve the overall coordination of delivery processes, complementing the existing order orchestration service.

- **Efficient Data Management:**
   - Leveraging MongoDB ensures reliable storage and retrieval of critical delivery information.

- **Seamless Communication:**
   - Kafka topics and the Spring Kafka framework will facilitate seamless communication between the microservice and the COO.

- **Flexibility and Scalability:**
   - The use of WebClient and generic utilities enhances flexibility and scalability, allowing for the quick addition of new delivery methods.

### Next Steps

- **External Orchestration with Temporal:**
   - Explore the use of Temporal as an external orchestrator for the delivery microservice.

- **COO Name Change:**
   - COOD will be changed to COO as the delivery is moved to our new microservice.

## References

- https://camunda.com/blog/2023/02/orchestration-vs-choreography/
- https://medium.com/@robindarby/temporal-5ac0be6af28e
- https://waswani.medium.com/microservices-workflows-orchestration-coordination-pattern-9fe246f8f10b

## Purpose

Based on this study on Delivery Management System Design, we will separate the delivery part from COOD as a new microservice.

## Decision implemented?

yes, but with use same db, next step to seperate the db 