<!--
Software Name: product-inventory-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Software Architecture Choices for CPIB

## Context and Problem Statement

To evaluate the most efficient approach for CPIB database integration, Design pattern, BPMN tool and to finalize the Spring Boot version to be used.

## Considered Options

Using https://www.jhipster.tech/  tool for creating the project boot strap and entities.

## Decision Outcome

__Frameworks to be used__

* for database ,seems a NoSQL/ mongo approach will be more efficient than a relational approach, as the product relations within CPIB appear to be too generalized, as they are all based on a single data type - 'Product.' This leads to a situation where various aspects such as product offering, product price, and product specification are all treated similarly as a single entity. This results in a semi-structured data model with multiple common properties. Therefore, it seems that a more appropriate choice for CPIB's data management system would be MongoDB, given its support for flexible and dynamic data structures.
*  for Spring boot version prefer 2.7
* for event bus [ this need a collective decision from the DISCO group - (../
* for development mode, CRUD operation seems tends to more stability for now, with considering to change it into event driven/CQRS in case we have a need in future development - but as for now the number of events and status report doesn't seem to have a need for aggregators and command consumers.
* the flow will be controlled with Spring state machine
* Kubernetes config with spring integration for deployment purpose
