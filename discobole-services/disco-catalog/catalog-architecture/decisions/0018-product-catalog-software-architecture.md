# Software Architecture choices for Product Catalogue

## Context and Problem Statement

To evaluate the most efficient approach for Product Catalogue database , Design pattern, BPMN tool.

## Considered Options

CQRS (Command Query Responsibility Segregation) and CRUD (Create, Read, Update, Delete) are two  design patterns are considered options 

* {title of option 1}
* {title of option 2}
* {title of option 3}
* … <!-- numbers of options can vary -->

## Decision Outcome

After consideration of the nature of the data, data modeling requirements, and performance requirements, it has been decided to use the CQRS design pattern to develop microservices and the MongoDB NoSQL database for the product catalog. Additionally, the Spring State Machine has been chosen for the BPMN to manage the workflow and transitions of states with the event sourcing approach.

<!-- This is an optional element. Feel free to remove. -->
### Consequences

* Good, because {positive consequence, e.g., improvement of one or more desired qualities, …}
* Bad, because {negative consequence, e.g., compromising one or more desired qualities, …}
* … <!-- numbers of consequences can vary -->

## Decision Drivers

A detailed study has been conducted to investigate why CQRS, Domain-Driven Design, and Event Sourcing were chosen, as well as what use cases were considered when making this decision two years ago. The study provides a comprehensive overview of the reasons behind the selection of these approaches, and includes a thorough analysis of their benefits and drawbacks in various contexts.

<!-- This is an optional element. Feel free to remove. -->
## Validation

* Define the domain model for the product catalog, including entities, relationships, and business rules.
* Separate read and write operations into two different models: a command model for updating the catalog and a query model for retrieving it.
* Implement the command model to handle write operations, validate data, and enforce business rules.
* Implement the query model to handle read operations, optimize for performance and scalability.
* Use event sourcing to store all changes to the product catalog as a sequence of events.
* Benefits of using CQRS for product catalog: scalability, improved performance, better fault tolerance, clear separation of concerns.
* CQRS enables scaling read and write operations independently, which is essential for high-traffic applications.
* Separating read and write operations can significantly improve performance by enabling different data stores and optimization techniques for each model.
* CQRS enables designing fault-tolerant systems by replicating the read and write models to different nodes and implementing event sourcing.
* CQRS enables a clear separation of concerns between the command and query models, making the code easier to maintain and understand.
* Applying CQRS to a product catalog, following TMF guidelines, can result in a highly performant, scalable, and fault-tolerant system.

## Pros and Cons of the Options

### CQRS Pattern

#### Pros 

* Allows for better scalability and performance by separating read and write operations.
* Enables easier maintenance and modification of the codebase.
* Simplifies testing and debugging processes.
* Facilitates the implementation of event-driven architectures.

#### Cons

* May introduce additional complexity into the system.
* Requires more development effort to implement.
* May not be suitable for small or simple systems.
* Requires a deep understanding of the domain and the business requirements.

### CRUD Pattern

#### Pros 

* Simple and straightforward to implement.
* Suitable for small or straightforward systems.
* Requires less development effort than CQRS.
* Easy to learn and understand for developers who are new to the system.

#### Cons

* Can become difficult to manage and maintain as the system grows in complexity.
* May not perform well under heavy load.
* Can lead to data inconsistency and integrity issues.
* May not be suitable for systems with complex business logic.
