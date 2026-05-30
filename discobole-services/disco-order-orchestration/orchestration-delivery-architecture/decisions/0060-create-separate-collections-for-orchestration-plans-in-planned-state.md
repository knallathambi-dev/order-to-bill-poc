<!--
Software Name: orchestration-delivery-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

## Create separate collection for orchestration plans in "planned" state

Improve performance on querying "planned" state for the cron job that acknowledges plans on delivery date arrival

### Considered Options 

Effort	Creating the separate collection requires more effort than the combined collection.	
uses the same code and structure that we have currently,

we only need to add an index which is a DB migration script

### Performance	
On state change: Considerably worse than the combined index, because the states change often,

which is going to lead to delete + write operation every time the state changes,

which adds more latency if we have other indexes in any of the two collections.

### On reading:
Reading offers no performance improvement as an index in the combined collection offers the same speed

### State changes:
More efficient as it requires only one operation which is write

### Reading:
having an index is the same efficiency as separate collection

### Decision

We are going to keep the combined collection for all the plans with the different states, and create an index on the plan state IPCEISCOOD-633

### References 

Practical Guide To Analyzing Slow MongoDB Queries


### Purpose

Study if separating orchestration plans in "planned" state offers performance improvements

### Jira:
IPCEISCOOD-582 - Study: Manage <Scheduled Orchestration Plans> Pool Closed

### Decision date:
30/Oct/23

