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

Welcome to the **Product Catalog Administration** API. This guide will help you get started with using our API.

## API Description

- The **Product Catalog Administration API** is used for managing entities in a product catalog.
- It supports **CRUD operations** (Create, Read, Update, Delete) for various catalog entities.
- The entities include:
  - Currencies
  - Units of measure
  - Frequency types
  - etc.

:information_source: This API is **not a TMF (TeleManagement Forum) prescribed API**.

### Summary of Resources

APIs have different resource based on the entities.

#### CpibConfiguration

| Field | Mandatory/Optional | Values for ODACAT implementation |
|:----------------|:----------------|:----------------|
| id | O | string |
| @type | M | string |
| @baseType | O | string |
| schemaLocation | O | string |
| href | O | string |
| lastModifiedDate | M | date |
| cpibCheck | M | boolean |

#### Currency

| Field | Mandatory/Optional | Values for ODACAT implementation |
|:----------------|:----------------|:----------------|
| id | O | string |
| @type | M | string |
| @baseType | O | string |
| schemaLocation | O | string |
| href | O | string |
| label | M | string |
| code | M | string |

#### ProductCatalogAdministration

| Field | Mandatory/Optional | Values for ODACAT implementation |
|:----------------|:----------------|:----------------|
| id | O | string |
| @type | M | string |
| @baseType | O | string |
| schemaLocation | O | string |
| href | O | string |

#### FrequencyTypes

| Field | Mandatory/Optional | Values for ODACAT implementation |
|:----------------|:----------------|:----------------|
| id | O | string |
| @type | M | string |
| @baseType | O | string |
| schemaLocation | O | string |
| href | O | string |
| frequencyCode | M | string |
| frequencyLabel | M | string |
| numberOfDays | M | int |

#### Unit

| Field | Mandatory/Optional | Values for ODACAT implementation |
|:----------------|:----------------|:----------------|
| id | O | string |
| @type | M | string |
| @baseType | O | string |
| schemaLocation | O | string |
| href | O | string |
| unitOfMeasure | M | string |

### Authentication

 In case the security is enabled, bearer token is used for authentication and authorization. Our API is a resource server based on OAuth2.

 For more details, please consult [User Role & Permission management component](https://discobole.ow2.io/disco-oda-components/disco-security/doc/api-references/specifications/user-role-and-permissions/).

### History of Document

| Version of the document | modification date | description of modifications  |
|:------------------------|:------------------|:------------------------------|
| 1.0                     | 04/10/2023        | initialization                |
| 1.1                     | 04/12/2023        | improving patch documentation |
| 2.0                     | 09/06/2024        | adding task api               |
