<!--
Software Name: orchestration-delivery-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Decision on Collection Structure for Plan and Nodes

## Context

We need to decide whether to split our MongoDB collection into separate collections for plans and nodes or keep them combined. This is to enhance performance and query efficiency.

## Objective

Determine the optimal collection structure to improve performance while maintaining code simplicity.

## Root Cause

Current performance issues stem from having two nested objects in MongoDB, with one being queried frequently and the other not.

## Considered Options

### Separate Collections

- **Transactions**: Requires handling multi-document transactions.
- **Reading**:
    - Pros: Fetching only necessary nodes.
    - Cons: Potential N+1 problem.
- **Updating**:
    - Pros: Simple updates with the save() method.
    - Cons: Complex plan state updates and maintaining relationships.
- **Effort**: Significant refactoring needed.
- **Performance**: More efficient updates and reads.

### Embedded Documents

- **Transactions**: Single-document operations.
- **Reading**:
    - Pros: Single fetch for all data.
    - Cons: Fetches unnecessary data.
- **Updating**:
    - Cons: Complex queries and risk of lost updates.
- **Effort**: Refactoring needed for node-specific operations.
- **Performance**: Efficient with tailored queries.

## Decision

We will keep a single collection for both nodes and plans, focusing on optimizing how nodes are accessed and updated within this structure. This approach balances performance with maintainability.

## Implementation Status

The decision is being implemented, focusing on saving only necessary changes while still fetching the whole plan (future improvement needed).