<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Target DB for Product Order data persistence

## Context and Problem Statement

Choose a suitable database for saving the order management data.

## Decision Drivers

* The resource Data Model of Product ordering is a relational schema.
* The schema can evolve over time when new versions of the TMF specifications are released, or requirements demand that the data model be extended with custom-defined data. 
* The relationships between ProductOrder, ProductOrderItem, and OrderItemRelationShip are the most complicated in the order data model. 
* There are some Order management use cases that require handling concurrency.   

Order management requirements for data persisting:

* The schema is relational and predefined. so SQL database is most suitable. But this can be handled by Document (NOSQL)database.
* The schema can evolve over time when new versions of the TMF specifications are released, or requirements demand that the data model be extended with custom-defined data. in this case, the NoSQL database offers this flexibility. 
* There are concurrent transactions each determining what they are writing based on reading data that overlaps what the other is writing. In this case, the database must be able to guarantee isolation between transactions.: As mentioned, this can be guaranteed by  SQL database but also MongoDB offers concurrency control measures to prevent multiple clients from modifying the same piece of data simultaneously.
* There aren't complex queries on the OM database: To have an efficient query with NoSQL, data can be stored in one Document, so there’s no need for cross-referencing. 
* For scalability reasons, NOSQL database is the most suitable because it can scale horizontally very efficiently, making it possible to accommodate large stores of distributed data, while supporting increased levels of traffic.
* There are no recommendations by TMF for database choice but some articles talk about the adoption of Mongodb by CSPS to deliver TM Forum-compliant microservices:
* This is because MongoDB's data model allows related data to be stored together in a single document. So the development teams can rapidly develop TMF-compliant microservices because there is no need to model TMF entity models in a relational database.

## Considered Options

There are two types of databases: 

* SQL databases
* NoSQL databases

## Decision Outcome

NoSQL database, especially the Document Database "MongoDB" seems the most suitable for Order management thanks to its flexibility, scalability, and the support of ACID transactions from version 4.0.

MongoDB was chosen.

## Pros and Cons of the Options

### SQL databases

* Require predefined schema.
* Suitable for a relational schema.
* Limit flexibility and does not easily accommodate changing requirements.
* Deliver a high degree of data integrity, adhering to the principles of atomicity, consistency, isolation, and durability (ACID).
* Scale vertically, which means they can be easily scaled up by adding resources such as CPUs or memory.
* It is easier to perform complex queries against structured data.
* Struggle with super-sized databases.

### NoSQL databases

* Uses a dynamic schema that requires no predefined data structure.
* It makes it easier for development teams to get started on their projects because they’re not locked into rigid schema structures.
* It can be difficult for NoSQL databases to deliver the same level of data integrity as SQL databases: data in a distributed environment might be temporarily inconsistent. But with the last versions of MongoDB (which is a NOSQL DB), the consistency principle is improved.
* Scale horizontally very efficiently across systems and locations, making it possible to accommodate large stores of distributed data, while supporting increased levels of traffic.
* Require more work to query data, particularly as query complexity increases: This can be solved by saving data into a document database because  Document databases provide fast queries.
* Document databases provide a structure well-suited for handling big data and flexible indexing. 

