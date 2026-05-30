<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Getting Started

## Introduction

Welcome to the **User Role & Permission management** API! This guide will help you get started with using our API.

## API Description

1. User Role & Permission Management is a key component in an authentication and authorization framework.
2. It is responsible for managing user entitlements, including the rights and privileges granted to users.
3. It handles the mapping of entitlements to specific roles, ensuring appropriate access control.
4. This component works in conjunction with the Keycloak server.
5. It ensures secure and well-managed access to protected resources.
6. Plays a crucial role in maintaining security, compliance, and user access governance.

### Summary of Resources

#### ComponentConfiguration Resource

To manage the name of the components which are managed by the userRole API

| Field | Mandatory/Optional | Values for implementation |
|:----------------|:----------------|:----------------|
| id | M | String | 
| componentName | M | String |

#### Entitlement Resource

An Entitlement defines access levels to operate over a given function that can be included in an asset.

| Field | Mandatory/Optional | Values for implementation |
|:----------------|:----------------|:----------------|
| id | O | String |
| @type | O | String |
| @schemaLocation | O | String |
| @baseType | O | String |
| function | O | String |
| action | O | String |

#### FunctionConfiguration Resource

Used for configuration of "functions" in the entitlements object of the user role API.

| Field | Mandatory/Optional | Values for implementation |
|:----------------|:----------------|:----------------|
| id | M | String |
| functionName | M | String |


#### Permission Resource

The Permission resource represents the entitlement given by an individual (granter) to another individual (user) to get access to a set of his owned manageable assets. One single permission resource can hold information referring to privileges granted for multiple manageable assets.

| Field | Mandatory/Optional | Values for implementation |
|:----------------|:----------------|:----------------|
| id | O | String |
| @type | O | String |
| @schemaLocation | O | String |
| @baseType | O | String |
| validFor | M | TimePeriod |
| user | M | RelatedParty |
| privilege | O | Privilege |
| granter | O | RelatedParty |
| assetUserRole | O | AssetUserRole |
| description | O | String |
| creationDate | O | string |
| href | O | string |

#### UserRole Resource

A UserRole defines access levels to operate over a given function that can be included in an asset.

| Field | Mandatory/Optional | Values for implementation |
|:----------------|:----------------|:----------------|
| id | O | String |
| @type | O | String |
| @schemaLocation | O | String |
| @baseType | O | String |
| involvementRole | O | String |
| entitlement | O | Entitlement |
| component | O | String |
| href | O | String |

### Sample 200 response for User Role API

```json
Example Value of get Permisson 
Schema
[
  {
    "id": "string",
    "href": "string",
    "creationDate": "1970-01-01T00:00:00.000Z",
    "description": "string",
    "assetUserRole": [
      {
        "manageableAsset": {
          "id": "string",
          "href": "string",
          "name": "string",
          "@baseType": "string",
          "@schemaLocation": "http://example.com",
          "@type": "string",
          "@referredType": "string"
        },
        "userRole": {
          "id": "string",
          "href": "string",
          "@baseType": "string",
          "@schemaLocation": "http://example.com",
          "@type": "string",
          "@referredType": "string"
        },
        "@baseType": "string",
        "@schemaLocation": "http://example.com",
        "@type": "string"
      }
    ],
    "granter": {
      "id": "string",
      "href": "string",
      "name": "string",
      "role": "string",
      "@baseType": "string",
      "@schemaLocation": "http://example.com",
      "@type": "string",
      "@referredType": "string"
    },
    "privilege": [
      {
        "action": "string",
        "function": "string",
        "manageableAsset": {
          "id": "string",
          "href": "string",
          "name": "string",
          "@baseType": "string",
          "@schemaLocation": "http://example.com",
          "@type": "string",
          "@referredType": "string"
        },
        "@baseType": "string",
        "@schemaLocation": "http://example.com",
        "@type": "string"
      }
    ],
    "user": {
      "id": "string",
      "href": "string",
      "name": "string",
      "role": "string",
      "@baseType": "string",
      "@schemaLocation": "http://example.com",
      "@type": "string",
      "@referredType": "string"
    },
    "validFor": {
      "endDateTime": "1970-01-01T00:00:00.000Z",
      "startDateTime": "1970-01-01T00:00:00.000Z"
    },
    "@baseType": "string",
    "@schemaLocation": "http://example.com",
    "@type": "string"
  }
]
```

```json
Example Value of get UserRole
Schema
[
  {
    "id": "string",
    "href": "string",
    "involvementRole": "string",
    "entitlement": [
      {
        "action": "string",
        "function": "string",
        "@baseType": "string",
        "@schemaLocation": "http://example.com",
        "@type": "string",
        "id": "string"
      }
    ],
    "@baseType": "string",
    "@schemaLocation": "http://example.com",
    "@type": "string",
    "component": "string"
  }
]
```

### Authentication

In case the security is enabled, bearer token is used for authentication and authorization. Our API is a resource server based on OAuth2.

For more details, please consult [User Role & Permission management component](https://discobole.ow2.io/disco-oda-components/disco-security/doc/api-references/specifications/user-role-and-permissions/).

The APIs must follow the TMF 672 rest guidelines.

### History of Document

| Version of the document | modification date | description of modifications  |
|:------------------------|:------------------|:------------------------------|
| 1.0                     | 04/10/2023        | initialization                |
| 1.1                     | 04/12/2023        | improving patch documentation |
| 2.0                     | 09/06/2024        | adding task api               |


