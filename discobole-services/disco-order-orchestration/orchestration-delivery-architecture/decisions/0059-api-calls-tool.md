<!--
Software Name: orchestration-delivery-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

## API Calls tool

In COOD, we need to integrate with several external APIs using HTTP calls.

## Decision drivers

### Flexibility:
We require a solution that offers custom handling for complex and non-standard API interactions.
### Performance:
Minimizing overhead is crucial for our performance-sensitive application.
### Dependency Management:
Keeping the project lightweight by avoiding additional dependencies is a priority.
### Error Handling:
Full control over custom error handling and logging mechanisms is essential for our robustness.
### Maintainability:
A simpler, more manual approach aligns better with our project needs, despite potential auto-update conveniences.

## Considered Options
Web client vs Swagger client
### Flexibility
High: Full control over request/response handling.	Moderate: Auto-generated code based on API spec.
### Type Safety
Low: Manual handling of serialization/deserialization.	High: Strongly-typed interfaces generated.
### Ease of Use
Moderate: Requires manual setup for each request.	High: Simplifies interaction with the API.
### Maintainability
Moderate: Requires manual updates when API changes.	High: Automatically reflects API changes when regenerated.
### Performance
High: Can optimize requests and responses as needed.	Moderate: Abstraction layers may introduce overhead.


## Decision
After considering the above factors, we have decided to use WebClient for API integration in our project. This decision aligns with our need for flexibility, performance, and minimal dependency management.

## Purpose
Decide to use Webclient or swagger api clients

