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

Welcome to the **POLICY RULE** API! This guide will help you get started with using our API.

## API Description

1. The Policy API offers a standardized method for defining policy specifications.  
2. It encompasses data outlining the utilization of Policy Rules within a managed environment.  
3. Additionally, it includes specifications dictating the behavior of managed entities affected by these rules.
4. The API ensures consistency in how policies are defined and applied across various contexts.

### Summary of Resources

A PolicyRule is an intelligent data container. It contains data that define how the PolicyRule is used in a managed environment as well as a specification of behavior that dictates how the managed entities that it applies to will interact.

#### PolicyEvent Resource

A group of events that can be used to trigger the evaluation of the condition clause of a policy rule,

| Field | Mandatory/Optional | Values for ODACAT implementation |
|:----------------|:----------------|:----------------|
| creationDate | M | [dateTime] provided by system | 
| description | O | String |
| Href | O | String (hyperlink of the resource/system gen.) |
| Id | O | String |
| name | O | User entered name of event |
| query | M | JSON path query |
| @baseType | O | String |
| @schemaLocation | O | String |
| @type | O | String |

#### PolicyCondition Resource

A group of conditions aggregated by the PolicyRule, and (4) a group of actions aggregated by the PolicyRule.

| Field | Mandatory/Optional | Values for ODACAT implementation |
|:----------------|:----------------|:----------------|
| creationDate | M | [dateTime] provided by system |
| description | O | String |
| Href | O | String (hyperlink of the resource/system gen.) |
| Id | M | String |
| name | O | User entered name of event |
| query | O | JSON path query |
| @baseType | O | String |
| @schemaLocation | O | String |
| @type | O | String |
| isConjustiveNormalForm | true | default |
| policyConditionStrategy | 1 | Default to “1” for V1 |

#### PolicyAction Resource

A group of actions aggregated by the PolicyRule.

| Field | Mandatory/Optional | Values for ODACAT implementation |
|:----------------|:----------------|:----------------|
|  |  |  |
| actionSequence | M | Serially system generated |
| actionStrategy | M | String |
| creationDate | M | System gen |
| Description | O | Not used for v1 |
| href | O | System gen. |
| id | M | System gen. |
| name | O | Not used for v1 |
| version | O | Not used for v1 |
| @baseType | O | String |
| @schemaLocation | O | String |
| @type | O | String |

### Summary of Methods and URLs

#### Sample 200 response for Policy Rule API

```json
[
    {
        "id": "string",
        "href": "string",
        "creationDate": "2024-06-03T14:42:15.552Z",
        "description": "string",
        "executionStrategy": "string",
        "isConjustiveNormalForm": true,
        "name": "string",
        "sequencedAction": 0,
        "sequencedValue": 0,
        "state": "string",
        "version": "string",
        "note": [
            {
                "id": "string",
                "author": "string",
                "date": "2024-06-03T14:42:15.552Z",
                "text": "string",
                "@baseType": "string",
                "@schemaLocation": "string",
                "@type": "string"
            }
        ],
        "policyAction": [
            {
                "id": "string",
                "href": "string",
                "name": "string",
                "@baseType": "string",
                "@schemaLocation": "string",
                "@type": "string",
                "@referredType": "string"
            }
        ],
        "policyCondition": {
            "id": "string",
            "href": "string",
            "name": "string",
            "@baseType": "string",
            "@schemaLocation": "string",
            "@type": "string",
            "@referredType": "string"
        },
        "policyDomain": [
            {
                "id": "string",
                "href": "string",
                "name": "string",
                "@baseType": "string",
                "@schemaLocation": "string",
                "@type": "string",
                "@referredType": "string"
            }
        ],
        "policyEvent": {
            "id": "string",
            "href": "string",
            "name": "string",
            "@baseType": "string",
            "@schemaLocation": "string",
            "@type": "string",
            "@referredType": "string"
        },
        "relatedParty": [
            {
                "id": "string",
                "href": "string",
                "name": "string",
                "role": "string",
                "@baseType": "string",
                "@schemaLocation": "string",
                "@type": "string",
                "@referredType": "string"
            }
        ],
        "@baseType": "string",
        "@schemaLocation": "string",
        "@type": "string"
    }
]
```

### Authentication

 In case the security is enabled, bearer token is used for authentication and authorization. Our API is a resource server based on OAuth2.

 For more details, please consult [User Role & Permission management component](https://discobole.ow2.io/disco-oda-components/disco-security/doc/api-references/specifications/user-role-and-permissions/).


The apis must follow the TMF 723 rest guidelines.

### History of Document
| Version of the document | modification date | description of modifications  |
|:------------------------|:------------------|:------------------------------|
| 1.0                     | 04/10/2023        | initialization                |
| 1.1                     | 04/12/2023        | improving patch documentation |
| 2.0                     | 09/06/2024        | adding task api               |


