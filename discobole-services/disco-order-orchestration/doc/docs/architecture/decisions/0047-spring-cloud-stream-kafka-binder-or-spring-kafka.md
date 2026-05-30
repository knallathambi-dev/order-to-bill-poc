<!--
Software Name: orchestration-delivery-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Decision on Spring Kafka vs. Spring Cloud Stream Kafka Binder

## Objective

Decide whether to use Spring Kafka or Spring Cloud Stream Kafka Binder for integrating with Kafka.

## Root Cause Analysis

Our project requires Kafka integration, and we are considering two options: Spring Kafka and Spring Cloud Stream Kafka Binder.

## Decision Drivers

1. Minimize complexity and maintain direct Kafka integration.
2. Leverage a mature solution with extensive documentation and support.
3. Prioritize flexibility for specific Kafka features.
4. Ensure transparent Kafka operations.

## Considered Options

### Spring Kafka

- **Integration**: Direct integration with Kafka components.
- **Boilerplate Code**: May require more boilerplate code.
- **Learning Curve**: Steeper learning curve.
- **Maturity and Documentation**: Extensive documentation.
- **Community Support**: Strong support.
- **Flexibility**: High, allows direct interaction.
- **Transparency**: Full visibility into Kafka operations.

### Spring Cloud Stream Kafka Binder

- **Integration**: Abstracts Kafka interactions.
- **Boilerplate Code**: Reduces boilerplate code.
- **Learning Curve**: Easier due to abstraction.
- **Maturity and Documentation**: Less extensive documentation.
- **Community Support**: Focused support.
- **Flexibility**: Limited customization.
- **Transparency**: Additional layers may affect transparency.

## When to Use

- **Spring Kafka**:
  - Requires precise control.
  - Prefers simplicity without performance overhead.

- **Spring Cloud Stream Kafka Binder**:
  - Potentially switching message middleware in the future.
  - Integrating other message middleware with Kafka.
  - Planning to migrate to public cloud services.

## Decision

We have decided to use Spring Kafka for our Kafka integration.

## Decision Implemented?

Not yet.

## References

- [Stack Overflow - Spring Kafka vs. Spring Cloud Stream Kafka](../47312542/spring-kafka-vs-spring-cloud-stream-kafka)
- [Confluent Blog - Spring for Apache Kafka Deep Dive](../2-apache-kafka-spring-cloud-stream/)

## Purpose

The decision is to use Spring Kafka, as outlined in the ADR.
