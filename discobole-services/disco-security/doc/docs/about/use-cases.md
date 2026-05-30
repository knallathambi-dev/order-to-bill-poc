---
title: Use Cases
summary: User Role & Permission Management Use Cases.
authors:
  - Sumit
---

# Use Cases

!!! info "User Roles and Entitlements Management"
    - Add and manage users- The management of users and their credentials is done using the Keycloak front end. New users can be created, deleted and their password changed using the  Keycloak OTB interface.
    - Roles Creation- Roles are created and managed in the Keycloak server. The API for creating a role in Keycloak is/realm/clients/{id}/roles as per the Keycloak Admin REST API.
    - Entitlements- Entitlements represent the permissions or privileges assigned to a role. These are stored
    and managed in the authentication database. Entitlements specify actions and functions that a role is permitted to perform.

!!! info "APIs for User Role Management"
    - Create User Role:
      - Endpoint: POST /userRole
      - Description: This API allows the creation of a user role by associating it with specific entitlements.
      - Data Flow: When called, this API creates a role in Keycloak and persists the entitlement data with the user role in the DISCO-AUTH database.
    - Retrieve User Role
      - Endpoint:GET /userRole/{id}
      - Description: Retrieves the details of a specific user role, including its entitlements.
      - Example Request:GET serverRoot/payment/v4/userRole/413920
    - List User Roles
      - Endpoint:GET /userRole
      - Description:Lists all user roles, potentially filtered and with specific fields
      - Example Request:GET serverRoot/payment/v4/userRole?fields=id,href,name,description,state&relatedParty.id=34
    - Patch Role:
      - Endpoint:PATCH /userRole/{id}
      - Description: Updates specific attributes of a user role.
