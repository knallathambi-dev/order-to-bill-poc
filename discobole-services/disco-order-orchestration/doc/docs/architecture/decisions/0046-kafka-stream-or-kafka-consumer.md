<!--
Software Name: orchestration-delivery-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Decision on Using Kafka Streams or Kafka Consumers

## Context

We are using Spring Cloud with Kafka Binder and need to decide whether to adopt Kafka Streams or continue with Kafka Consumers for COOD, which processes messages in a straightforward, stateless manner.

## Considered Options

### Kafka Streams

- **Complexity**: Supports complex, stateful operations but introduces unnecessary complexity for COOD.
- **Message Order**: Not guaranteed.
- **Resource Use**: Higher resource consumption.
- **Deployment**: Requires separate application management.
- **Learning Curve**: Steep, with new concepts to learn.
- **Scalability**: Built-in support for scaling.

### Kafka Consumers

- **Complexity**: Simple, stateless processing.
- **Message Order**: Guaranteed sequential processing.
- **Resource Use**: Efficient resource usage.
- **Deployment**: Simple deployment model.
- **Learning Curve**: Easier, leveraging existing knowledge.
- **Scalability**: Manual intervention may be required.

## Decision

We will use Kafka Consumers due to:

1. **Simplicity**: COOD has straightforward message processing needs.
2. **Order**: Messages are processed sequentially.
3. **Familiarity**: The team is already familiar with Kafka Consumers.
4. **Efficiency**: More resource-efficient for COOD’s needs.
5. **Deployment**: Simpler deployment without a separate application.

## Consequences

This decision aligns with COOD’s needs, leverages existing expertise, and avoids Kafka Streams' complexity. We will monitor for scalability and revisit if necessary.

## Decision Implemented?

Yes, we retained the existing Kafka Consumers setup.
