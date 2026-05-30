---
title: Introduction
summary: 
authors:
  - Sumit
  - Akshay Sahni
---

# Introduction

## What & Why

!!! abstract "What is [DISCOBOLE](https://discobole.ow2.io/doc/) - User role & permission management"

    The DISCOBOLE User role & permission management is a key component responsible for managing users, user roles and entitlements to the services of DISCOBOLE components. It works in conjunction with the Keycloak server to ensure secure and managed access to resources.

    Role-based access control is an approach to restricting system access to authorized users. It restricts network access based on an end user's role.

    - The definition of a user remains independent of their role.
    - The user (an entity with a username and password) can be mapped to user roles.
    - The user roles can be defined based on requirements like product catalog admin, admin portal admin, etc., based on business needs.
    - A user can be mapped to multiple roles.

    This enables control over what end-users can do at both broad and granular levels.

## Where to Start

!!! success "Understand the [_uses cases_](../use-cases/) covered by User Role and Permission Management and how it can be useful to you."

!!! success "Browse this site"
    - Check [DISCOBOLE](https://disco.pages.gitlab.ow2.org/discobole-website/) User role & permission management [__architecture__](../architecture/overall-architecture.md) and the [architecture decisions](../architecture/about-adr.md) that led to it.
    - Interact with an [__already deployed instance__](../deployments/live-environments.md), understand its [development ecosystem](../deployments/development-ecosystem.md) and deploy your own User role & permission management.
  
!!! success "Jump to the [code repository](https://gitlab.ow2.org/discobole/disco-oda-components/disco-security) on GitLab"
    Browse the [code repository](https://gitlab.ow2.org/discobole/disco-oda-components/disco-security) on GitLab.

## History and Perspectives

!!! info "History and Perspectives"
    - The project is based on TM Forum ODA component definition, implements the TMF Forum APIs and is based on TMF SID data model while complying with Orange’s functional architecture principles.
    - The architecture is a single micro-service based, hosted on a private cloud and deployed using an open source authorization component namely Keycloak.
    - The ODA component, on which the component is based upon is TMFC035 - Party Roles and Permissions Management.
    - The resources of the User Role component are based on the User Role Permission Management v4.0.1 (TMF672 API).
    - The component is continuously being evolved as of date with new features with the target to have a central solution for authentication and authorization for the DISCOBOLE suite.
