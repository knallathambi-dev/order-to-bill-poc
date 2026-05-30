<!--
Software Name: orchestration-delivery-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Architecture Enhancement Decision

## Objective

The objective of this ADR is to enhance the Kafka topic names used in the Order Orchestration Service (../

## Root Cause Analysis

The current Kafka topic names are as follows:

- **OM Topic**: Used to trigger the Order Delivery process.
- **Orchestration Topic**: General orchestration-related communication.
- **Delivery topic**: Used for communication.
- **Delivery status topic**: Used for tracking delivery status.

## Decision

The decision to enhance the topic names and utilize state change events is rooted in the need for a more structured, semantic, and standardized communication model. The incorporation of TMF concepts and state change events aligns the system with industry best practices, simplifying the management of state transitions. This decision also readies the architecture for the potential addition of a delivery management service.

The proposed architectural enhancements include the following changes to Kafka topic names:

- **OM Topic**: Will remain unchanged and continue to trigger the Order Delivery process.
- **Orchestration Plan State Change Topic**: Introduced to handle state changes within orchestration plans.
- **Orchestration Plan Node State Change Topic**: Introduced to manage node state changes within orchestration plans.
- **SOM State Change Topic**: Introduced to indicate the status of Container Freight Station (../
- **Delivery Topic**: Will serve as a communication channel after the delivery management service is added.

In addition to renaming Kafka topics, the service will leverage state change events for orchestration plan execution and delivery. The transition between node states will be accomplished by actively monitoring and responding to the "Orchestration Plan Node State Change" event.

# Decision implemented?

Yes

## Purpose

The purpose of this ADR is to enhance event handling and Kafka topic naming, aligning the system with industry standards and best practices.

