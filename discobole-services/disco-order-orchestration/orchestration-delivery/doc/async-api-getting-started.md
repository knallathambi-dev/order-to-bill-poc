<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Documenting using ASYNC API Getting Started Guide

Welcome to the **Disco Customer Order Orchestration & Distribution (COOD)** documenting using Async Api! This guide will help you get started to use async api for documenting events.

## Prerequisites

Before you begin using the COOD ASYNC API, make sure you have the following prerequisites in place:

- **Install async api generator**: by running this command `npm install -g @asyncapi/generator`.

- **Install async api cli**: need to install async api command line by running this command `npm install -g @asyncapi/cli`.

- ***Note**: you might need to run this command using `sudo`.* 

## Usage

After adding/modifying yaml file(s) under `orchestration-delivery-service/src/main/resources/asyncapi-files`

For generating web page for async api, we use command line

1. We go into **async-api** directory using `cd`
2. For generating the html page, we use `asyncapi generate fromTemplate COOD_Async_api.yaml  @asyncapi/html-template --param version=0.0.2  -o ../public/async-api  -i --force-write`
3. After it finishes, you will find under **public** directory html file with name index.html you can either :
   - Rename it to `async-api.html`
   - Copy data inside the file into `async-api.html` and delete the index.html

### ASYNC API Description

#### Summary of Resources
We are documenting them in following table

| Topic                                                            | Type      | Payload                               | Group Id       |
|:-----------------------------------------------------------------|:----------|:--------------------------------------|:---------------|
| disco.order-management.productOrderStateChange-event             | Subscribe | productOrderStateChangeEvent          | input-group-1  |
| disco.order-orchestration.orchestrationPlanStateChange-event     | Subscribe | OrchestrationPlanStateChangeEvent     | input-group-2  |
| disco.order-orchestration.orchestrationPlanStateChange-event     | publish   | OrchestrationPlanStateChangeEvent     | output-group-1 |
| disco.service-order-management.serviceOrderStateChange-event     | Subscribe | serviceOrderEvent                     | input-group-1  |
| disco.shipping-order-management.shippingOrderStateChange-event   | Subscribe | ShippingOrderStateChangeEvent         | input-group-1  |
| disco.order-orchestration.orchestrationPlanNodeStateChange-event | publish   | orchestrationPlanNodeStateChangeEvent | output-group-1 |
| disco.order-orchestration.orchestrationPlanNodeStateChange-event | Subscribe | orchestrationPlanNodeStateChangeEvent | input-group-1  |


### History of Document
| Version of the document | modification date | description of modifications |
|:------------------------|:------------------|:-----------------------------|
| 1.0                     | 17/10/2023        | initialization               |
| 1.1                     | 04/05/2025        | open source changes          |




