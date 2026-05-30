<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Microservice architecture

## Context and Problem Statement

Describe the suitable microservice architecture for the order management component.

## Considered Options

There are three options: 

* Option 1: Order Management component includes two microservices order capture and order followUp.
* Option 2: Order Management component as only one microservice.
* Option 3: Decomposing Order Management into three microservices(Order Capture, Order FollowUp, Product order inventory).

## Decision Outcome

The most suitable option is the option 3: Decomposing order management into three microservices: Order Capture, Order followUp, and product order inventory.
The communication between all microservices could be synchronous and asynchronous, it depends on the use case.

## Pros and Cons of the Options

### Option 1

Order Management component includes two microservices order capture and order followUp:
The following risks could not be accepted, which is why this option is rejected.

Cons:
* Data duplication of productOrder.
* Two endpoints for TMF API 622.
* A complexity to synchronizing product order data between OC and OF microservices.

### Option 2

Order Management component as only one microservice.
It handles Order capture, Order follow-up processes and product order management.
It provides the  TMF API 701 and the TMF API 622.

The following risks could not be accepted, which is why this option is rejected.
Cons:
* The code might be complicated in the future. The complexity may increase because it contains many business objectives.
* In case of failure, all the order management services will be unavailable.

### Option 3

Decomposing Order Management into three microservices:
* Order Capture microservice is responsible for the order capture processing. It provides TMF API 701. It manages a process DB of type order capture.
* Order FollowUp microservice is responsible for the order followUp processing. It provides TMF API 701. It manages a process DB of type order followUp.
* Product order inventory microservice is responsible for managing the (CRUD) of productOrder database and calculating the state of the productOrderItems hierarchy. It provides the TMF API 622.

This option is the most suitable architecture thanks to its advantages:

Pros:

* No data duplication of productOrder.
* Each microservice is responsible for a small function and data.
* Resilience: If order capture or Order followUp ms shuts down, the API TMF 622 will continue to be accessible and vice versa.
* Scaling the microservices independently.

Cons:

* As we need a third microservice, this requires more resources.
* Some use cases require synchronous communications that cause coupling between the  “Order capture” and  “product order inventory” microservices.

=> To guarantee data consistency and avoid strong coupling between Order followUp and ProductOrder inventory microservices,  the calculation of the state of the productOrderItems hierarchy is no longer maintained by the order followUp microservice but it will be  maintained by the productOrder inventory microservice.
