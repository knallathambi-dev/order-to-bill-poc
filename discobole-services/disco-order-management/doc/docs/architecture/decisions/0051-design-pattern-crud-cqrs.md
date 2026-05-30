<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Design pattern(CRUD or CQRS) for Order management(Product order domain)

## Context and Problem Statement

Choose a suitable design pattern for order management(`CRUD` or `CQRS`).

## Decision Drivers

* The load for update and read operations is almost the same for both. So the read and write databases don't need to be scaled separately.
* There are no multiple users who manage the order.
* There is no complexity associated with the read operations. Product Ordering can be retrieved by filtering by productOrderId or relatedParty.id.
* There is no need for an event journal about all updates on the product order domain.
  * `CRUD` pattern is the suitable pattern for order management.
  * This decision is taken with the assumption that there is no need for a third microservice that aggregates data from order capture and order follow-up microservices that will be decided later.

## Considered Options

There are two design patterns that could be used: 

* `CRUD`
* `CQRS`

## Decision Outcome

We choose to implement order management with the `CRUD` pattern since it covers order management requirements described above.


## Pros and Cons of the Options

### CRUD

* It stands for create, read, update, and delete.
* In `CRUD` all data is stored in one database and the microservice will remain stateless, which means that all queries irrespective of reading or writing will be answered by this single database.     
* This pattern is best suited for:
  * Simple business domains with few complex relationships between domain entities and their operations.
  * How you query your data is not much different from the way you manage, update, and manipulate data.

**Advantages:**

* Very easy to implement.
* Any changes we do it will definitely be reflected in the next query. There is no delay, which is very important for critical applications. So, `CRUD` has Strong Consistency.
* It requires relatively less infrastructure/components than CQRS.

### CQRS

* `CQRS` describes a way of developing applications that cleanly separate making changes to the application from simply querying the application.
* This is especially important for applications that have high scalability requirements for the read side.
* Three problems that can be solved by using `CQRS`
  * Using the API composition pattern to retrieve data scattered across multiple services results in expensive, inefficient in-memory joins.
  * The service that owns the data stores the data in a form or in a database that does not efficiently support the required query.
  * The need to separate concerns means that the service that owns the data isn’t the service that should implement the query operation.