<!--
Software Name: orchestration-delivery-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Decision on Using Process Flow for Fallout Management

## Objective

In the context of our system architecture COOD, we are initiating the fallout process within the COOD and DM (Delivery Management) modules. To efficiently manage this workflow, we need to introduce a process flow to handle the fallout process.

## Decision Drivers

1. **Use of TMF 701 Standard**: Use TMF 701 standard for managing processes.
2. **Separation of Concerns**: Fallout management represents a distinct domain separate from order orchestration within COOD. By introducing a dedicated microservice, we ensure clear separation of concerns and maintain a modular and scalable architecture.
3. **Conflict Resolution**: Attempts to integrate the process flow as a module within COOD encountered difficulties, particularly related to conflicts with dependencies, notably Axon's CommandGateway bean. These conflicts prevent the seamless integration of fallout management within the existing COOD microservice.

## Decision

We will use a process flow within a new service inside the COOD component (orchestration-delivery-fallout).

## Purpose

Decide to use process flow for fallout management.

## Issue

Managing the fallout process efficiently and resolving conflicts with dependencies within the COOD module.

## Decision Date

30 April 2024
