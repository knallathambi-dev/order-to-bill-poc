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

Welcome to the DISCOBOLE Catalog API! This guide will help you get started with using our API to query and show the installed.

## API Description

Product Catalog Management API provides a set of interfaces and operations for:
  - Creating, retrieving, updating, and deleting product catalog data.
  - Supporting functionalities related to product offerings within a digital ecosystem.

### Summary of resources

APIs have different resource based on the entities.

| **Resource**              | **Definitions**                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
|---------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Product Specification     | It is a detailed description of a tangible or intangible object made available externally in the form of a ProductOffering to customers or other parties playing a party role.                                                                                                                                                                                                                                                                                                                                          |
| ProductOffering           | Represents entities that are orderable from the provider of the catalog, this resource includes pricing information.                                                                                                                                                                                                                                                                                                                                                             |
| ProductOfferingPrice      | Is based on both the basic cost to develop and produce products and the enterprise's policy on revenue targets. This price may be further revised through discounting (productOfferPriceAlteration). The price, applied for a productOffering, may also be influenced by the productOfferingTerm the customer selected, e.g., a productOffering can be offered with multiple terms, like commitment periods for the contract. A productOffering may be cheaper with a 24-month commitment than with a 12-month commitment. |
| Category                  | The category resource is used to group product offerings, service and resource candidates in logical containers. Categories can contain other categories and/or product offerings, resource or service candidates.                                                                                                                                                                                                                                                                   |
| Lifecycle                 | The lifecycle status of the catalog entity (product offering, product offering price, category, product specification).                                                                                                                                                                                                                                                                                                                                                          |
| Export job                | Represents a task used to export resources to a file.                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |


### Summary of methods and URLs

#### ProductOffering Entity

| Use case using the method                                         | Method | URL                                                                                                                                 |
|:------------------------------------------------------------------|:-------|:------------------------------------------------------------------------------------------------------------------------------------|
| **Get all Resource**                              | GET    | [http://{{baseURL}}/productCatalogManagement/{{{version}}/productOffering]                                                                          |
| **Get Resource filtered by state**               | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productOffering?state=Active]                                                            |
| **Get Resource filtered by id**                   | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productOffering/{{id}}]                                                  |
| **Get Resource filtered by name**                | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productOffering?name={{name}}]                                              |   
| **Get Resource by brand field only**              | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productOffering?fields=name,brand]      |
| **Get Resource with pagination**                  | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productOffering?limit=5&offset=0]                                                         |

#### ProductSpecification Entity

| Use case using the method                          | Method | URL                                                                                                              |
|:-------------------------------------------------- |:-------|:---------------------------------------------------------------------------------------------------------------- |
| **Get all Resource**                               | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productSpecification]                                   |
| **Get Resource filtered by state**                | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productSpecification?state=Active]                     |
| **Get Resource filtered by id**                   | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productSpecification/{{id}}]                           |
| **Get Resource filtered by name**                 | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productSpecification?name={{name}}]                    |
| **Get Resource by brand field only**              | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productSpecification?fields=name,brand]                |
| **Get Resource with pagination**                  | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productSpecification?limit=5&offset=0]                 |

#### ProductOfferingPrice Entity

| Use case using the method                          | Method | URL                                                                                                              |
|:-------------------------------------------------- |:-------|:---------------------------------------------------------------------------------------------------------------- |
| **Get all Resource**                               | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productOfferingPrice]                                   |
| **Get Resource filtered by state**                | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productOfferingPrice?state=Active]                     |
| **Get Resource filtered by id**                   | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productOfferingPrice/{{id}}]                           |
| **Get Resource filtered by name**                 | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productOfferingPrice?name={{name}}]                    |
| **Get Resource by brand field only**              | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productOfferingPrice?fields=name,brand]                |
| **Get Resource with pagination**                  | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/productOfferingPrice?limit=5&offset=0]                 |

#### ServiceSpecification Entity

| Use case using the method                          | Method | URL                                                                                                              |
|:-------------------------------------------------- |:-------|:---------------------------------------------------------------------------------------------------------------- |
| **Get all Resource**                               | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/serviceSpecification]                                   |
| **Get Resource filtered by state**                | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/serviceSpecification?state=Active]                     |
| **Get Resource filtered by id**                   | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/serviceSpecification/{{id}}]                           |
| **Get Resource filtered by name**                 | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/serviceSpecification?name={{name}}]                    |
| **Get Resource by brand field only**              | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/serviceSpecification?fields=name,brand]                |
| **Get Resource with pagination**                  | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/serviceSpecification?limit=5&offset=0]                 |

#### Category Entity

| Use case using the method                                         | Method | URL                                                                                                                                 |
|:------------------------------------------------------------------|:-------|:------------------------------------------------------------------------------------------------------------------------------------|
| **Get all Resource**                              | GET    | [http://{{baseURL}}/productCatalogManagement/{{{version}}/category]                                                                          |
| **Get Resource filtered by state**               | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/category?state=Active]                                                            |
| **Get Resource filtered by id**                   | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/category/{{id}}]                                                  |
| **Get Resource filtered by name**                | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/category?name={{name}}]                                              |   
| **Get Resource by brand field only**              | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/category?fields=name,brand]      |
| **Get Resource with pagination**                  | GET    | [http://{{baseURL}}/productCatalogManagement/{{version}}/category?limit=5&offset=0]                                                         |

## Exception Handling 

 Within the Product Catalog, exception codes play a key role in controlling how business rules are enforced and deviations are handled across different entities. By associating exception codes with the appropriate catalog entities, the system can automatically detect irregular scenarios and present meaningful error messages to users on the UI. The following is the exhaustive list handled for each of the entities:

### Product Offering Error Codes

| HTTPS Response Code | ID | Error Code | Error Message | Error Reason | Descriptive Scenario | Request Scenario |
|:-----------|:---|:-----------|:--------------|:-------------|:---------------------|:----------------|
| 400 | `DISCO_PO_INVALID_PS_STATE` | 1001 | Please select a support entity whose lifecycle status is active or launched. | Support entity lifecycleStatus should be active/launched. | The associated Product Specification has a lifecycle status other than 'ACTIVE' or 'LAUNCHED', which is required for performing this operation. | While Selecting Support Entity, Adding PO Description (Atomic), Picking Characteristics (Atomic), Defining Operation Spec, Validating Entity Operation, Associating POP, Modifying association of POP, Modifying PO Description (Atomic) |
| 400 | `DISCO_PO_CHARACTRISTICS_ID_OR_NAME_REPEATED` | 1002 | Characteristic Id or Name entered is repeated. ID: <<char.ID>> | Characteristic Id/Name repeated | A characteristic (id/name) included multiple times from related Product Specs. Each must be unique. | While Picking PO Characteristics (Atomic) |
| 400 | `DISCO_PO_MINCARDINALITY_LESSOREQUAL_MAXCARDINALITY` | 1003 | Min cardinality cannot be greater than max cardinality. Characteristic ID: <<char.ID>> | min ≤ max rule | Max cardinality must be ≥ min cardinality. | While Picking PO Characteristics (Atomic) |
| 400 | `DISCO_PO_INVALID_CATEGORY` | 1004 | Category ID added for association is invalid. | Invalid Category ID | Associated category doesn’t exist. | Selecting/Modifying Category (Atomic/Bundle/Contract) |
| 400 | `DISCO_PO_INVALID_CATEGORY_LIFECYCLE` | 1005 | Category lifecycle must be active or launched <category.ID> | Invalid lifecycle | Selected category not ACTIVE/LAUNCHED. | Selecting/Modifying Category |
| 400 | `DISCO_PO_NOT_EXSISTS` | 1006 | Product Offering does not exist for ID: <<ID>> | PO not found | Requested PO ID not present. | Selecting/Updating/Validating PO |
| 400 | `DISCO_PO_NOT_CPO_CANNOTBE_CHILDFOR_PO` | 1007 | Contract PO cannot be child of another PO | Invalid relation | Contract PO used as child in bundle. | Bundle/Contract creation |
| 400 | `DISCO_PO_IDENTITY_DATA_CANNOT_NULL` | 1008 | Missing data for attributes (name, brand, description, Installable/Visible/Sellable) | Null Identity Data | Null identityData fields provided. | Defining/Modifying Identity Data (Atomic/Bundle/Contract) |
| 400 | `DISCO_PO_PROVIDE_IDORNAME` | 1009 | Missing "ID"/"name" for related party | Missing ID or name | Related parties given but ID and name empty. | Defining/Modifying Identity Data |
| 400 | `DISCO_PO_VALIDITY_MUST_NOTNULL` | 1010 | Validity Period object cannot be null | Null validity | Validity Period missing. | Defining/Modifying Identity Data |
| 400 | `DISCO_BPO_GLOBAL_MAX_CARDINALITY_GREATER_THAN_CUMULATIVE_UPPER_LIMIT` | 1011 | Global max cardinality > sum(maxCardinality) | Invalid GlobalMax | GlobalMax > sum of maxCardinality. | Bundle/Contract creation |
| 400 | `DISCO_BPO_GLOBAL_MAX_CARDINALITY_LESS_THAN_CUMULATIVE_LOWER_LIMIT` | 1012 | Global max cardinality < sum(minCardinality) | Invalid GlobalMax | GlobalMax < sum of minCardinality. | Bundle/Contract creation |
| 400 | `DISCO_PO_BPO_MINMAX_CARDINALITY` | 1013 | Global max must be greater than Global min | Invalid min/max | GlobalMax < GlobalMin. | Bundle/Contract creation |
| 400 | `DISCO_PO_INVALID_PO_LIFECYCLE` | 1014 | Entities with Launched/Retired cannot be modified | Invalid lifecycle | PO lifecycle = Launched/Retired. | Modifying PO data |
| 400 | `DISCO_PO_EXPIRED_PO` | 1015 | Underlying PO expires before validity of PO | Expired PO | Underlying PO endDate < new PO startDate. | Bundle/Contract creation |
| 400 | `DISCO_PO_INVALID_PO` | 1016 | Selected PO validity ended | Expired PO | Underlying PO expired at system time. | Bundle/Contract creation |
| 400 | `DISCO_PO_EXPIRED_PS` | 1017 | PS expires before PO validity | Expired PS | PS endTime < PO startTime. | Defining Identity Data |
| 400 | `DISCO_PO_INVALID_PS` | 1018 | Selected PS expired | Expired PS | PS validity ended at system time. | Selecting Support Entity for PO |
| 400 | `DISCO_PO_INVALID_ATTRIBUTE` | 1019 | Sellable offering cannot be hidden | Invalid attributes | isVisible=false while isSellable=true. | Defining/Modifying Identity Data |
| 400 | `DISCO_PO_INVALID_BPO_LIFECYCLE_STATUS` | 1020 | Underlying PO not launched | Invalid lifecycle | Child PO not Launched. | Modifying Bundle/Contract |
| 400 | `DISCO_PO_INVALID_PO_LIFECYCLE_SELECTED` | 1021 | Invalid PO lifecycle selected | Invalid lifecycle | Lifecycle not valid next state. | Modifying Identity Data |
| 400 | `DISCO_PO_INVALID_BPO_OPERATION_LIMIT` | 1022 | Invalid cardinality rule | Invalid limits | min ≤ default ≤ max violated. | Selecting PO for Bundle/Contract |
| 400 | `DISCO_PO_INVALID_PO_CHARACTRISTICS` | 1023 | Invalid PO characteristic selected | Invalid characteristic | ID not matching PS, invalid min/max cardinality. | Picking/Modifying Characteristics |
| 400 | `DISCO_PO_INVALID_PO_CHANNEL` | 1024 | Invalid channel IDs selected | Invalid channels | Channels not in valid list. | Adding/Modifying Description/Identity Data |
| 400 | `DISCO_PO_INVALID_PO_OPERATION` | 1025 | Invalid operations <<op.id>> | Invalid operations | Operation IDs not in PS. | Defining/Modifying Operation Spec |
| 400 | `DISCO_PO_INVALID_PO_VALID_FOR` | 1026 | Invalid validity dates | Invalid dates | Start > End or outside PS validity. | Defining/Modifying Operation/Identity/Characteristic |
| 400 | `DISCO_PO_INVALID_PO_RELATION` | 1027 | Relationship cannot be self-referencing | Self-referencing | Same PO ID for source/target. | Selecting Relation |
| 400 | `DISCO_PO_INVALID_PO_RELATIONSHIP_SELECTED` | 1028 | Invalid PO selected for relationship | Invalid relation | Wrong type, self-relation, missing. | Selecting/Modifying Relation |
| 400 | `DISCO_PO_INVALID_PO_STATE` | 1029 | Related PO must be Active/Launched | Invalid lifecycle | Related PO not in valid state. | Selecting Relation |
| 400 | `DISCO_PO_INVALID_PO_STATE_INTEST_ACTIVE_LAUNCHED` | 1030 | Lifecycle must be InTest/Active/Launched | Invalid lifecycle | PO lifecycle not in valid states. | Modifying PO/Validating Entity Operation |
| 400 | `DISCO_PO_INVALID_PO_STATE_INSTUDY` | 1031 | Lifecycle must be InStudy | Invalid lifecycle | Cancellation attempted outside InStudy. | Cancelling PO creation |
| 400 | `DISCO_PO_INVALID_POP_ID` | 1032 | Invalid Product Offering Price ID | Invalid POP ID | POP ID not found in DB. | Associating POP to Operation Spec |
| 400 | `DISCO_PO_INVALID_PO_OPERATION_ID` | 1033 | Invalid Commercial Operation ID | Invalid OperationSpec | OperationSpec not found. | Associating POP to Operation Spec |
| 400 | `DISCO_PO_INVALID_POP_STATUS` | 1034 | POP status should not be retired/obsolete/unavailable | Invalid POP status | Invalid POP lifecycle state. | Associating POP to Operation Spec |
| 400 | `DISCO_PO_INVALID_PO_CHARACTRISTICS_VALUE` | 1035 | Invalid PO Characteristic value | Invalid value | Values outside PS allowed range. | Picking/Modifying Characteristics |
| 400 | `DISCO_PO_INVALID_STATUS_NOT_MODIFIED` | 1036 | Aggregate not Found | Invalid PO entered | Invalid PO in modification. | - |
| 400 | `DISCO_PO_INVALID_POP_ASSOCIATION` | 1037 | Immediate payment must be true | Invalid POP association | NRC POPs must have immediatePayment=true. | Associating POPs to Operation Spec |
| 400 | `DISCO_PO_INVALID_PO_CHARACTERISTICS_VALUE_TIME_RANGE_INVALID` | 1038 | Invalid PO characteristics time range | Invalid time range | PO char validFrom/validTo outside PS range. | Picking/Modifying Characteristics |
| 400 | `DISCO_PO_DURATION_ATTRIBUTE_MANDATORY` | 1039 | PO Term duration must not be null | Null duration | POTerm duration is null. | Defining Identity Data |
| 400 | `DISCO_PO_DURATION_SHOULD_BE_UNIQUE` | 1040 | Unique PO Term duration required | Duplicate duration | POTerm duration repeated. | Defining Identity Data |
| 400 | `DISCO_PO_INVALID_TERM_ASSOCIATE_CHARGE_STEP` | 1041 | PO Term not defined in Identity Data | Invalid POTerm | POP associated with undefined POTerm. | Associating POP to Operation Spec |
| 400 | `DISCO_PO_INVALID_TERM_UNIT` | 1042 | Invalid term unit | Invalid unit | POTerm unit not in Frequency Types. | Adding/Modifying Identity Data |
| 400 | `DISCO_INVALID_PO_HIERARCHY` | 1043 | Duplicate PO in hierarchy | Invalid hierarchy | PO bundled multiple times or cycle exists. | Bundle/Contract creation |
| 400 | `DISCO_PO_INVALID_BUNDLING_PO_DOES_NOT_EXIST` | 1044 | Invalid PO provided for bundling | Invalid PO | Bundled PO not present in DB. | Bundling Step |
| 400 | `DISCO_BPO_GLOBAL_MIN_CARDINALITY_LESS_THAN_CUMULATIVE_LOWER_LIMIT` | 1045 | GlobalMin < sum(minCardinality) | Invalid GlobalMin | GlobalMin less than sum of child minCardinality. | Bundle/Contract creation |
| 400 | `DISCO_BPO_GLOBAL_MIN_CARDINALITY_GREATER_THAN_CUMULATIVE_UPPER_LIMIT` | 1046 | GlobalMin > sum(maxCardinality) | Invalid GlobalMin | GlobalMin greater than sum of child maxCardinality. | Bundle/Contract creation |
| 400 | `DISCO_PO_INVALID_PO_MARKET_SEGMENT` | 1047 | Invalid Market Segment IDs selected | Invalid Market Segment | Market Segment IDs not valid. | Adding/Modifying Description/Identity Data |
| 400 | `DISCO_PO_INVALID_PS_LIFECYCLE_STATUS` | 1048 | PS must be launched | Invalid PS lifecycle | PO launched while PS not launched. | Validating APO modification |
| 400 | `DISCO_PO_INVALID_SUPPORT_ENTITY_TYPE` | 1049 | Invalid support entity type | Invalid support entity | Must be CFSSpec, StockItemType, SupplierProduct, ProductSpec. | Selecting Support Entity |
| 400 | `DISCO_PO_INVALID_OPERATION_ID` | 1050 | Invalid Operation ID | Operation ID mismatch | Selected operation ID invalid. | Selecting Operation Specification |
| 400 | `DISCO_PO_INVALID_OPERATION_NAME` | 1051 | Invalid Operation Name | Operation name mismatch | OperationSpec ID and name mismatch. | Selecting Operation Specification |
| 400 | `DISCO_PO_INVALID_PO_TYPE` | 1052 | Invalid Product Offering Type | Invalid PO type | Must be Atomic, Bundle, or Contract. | Creating/Modifying PO |

### Product Specification Error Codes

| HTTPS Response Code | ID | Error Code | Error Message | Error Reason | Descriptive Scenario | Request Scenario |
|:-----------|:---|:-----------|:--------------|:-------------|:---------------------|:----------------|
| 400 | `DISCO_PS_SERVICE_SPECIFICATION_NOT_FOUND` | 2001 | Service Specification not found | Service Specification not found <<Service Spec.ID>> | If the service spec (supporting entity of Product Specification) does not exist (fetches servicespec via id from db) | While selecting support Entity for Product Specification |
| 400 | `DISCO_PS_INVALID_SERVICESPEC_STATE` | 2002 | Please validate lifecycle status of service specification | Invalid lifecycle status of service specification | If the service spec state is not 'Active' or 'Launched' | While selecting support entity for product specification; While defining identity data; While defining/modifying characteristics; While validating product spec; While modifying PS |
| 400 | `DISCO_PS_STOCK_ITEM_NOT_FOUND` | 2003 | Stock item not Found | Stock item not Found | If the stock item (supporting entity of Product Specification) does not exist | While selecting support entity or PS; While modifying PS |
| 400 | `DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED` | 2004 | Please enter correct characteristic values | Invalid Product Specification Characteristics Selected | If incorrect characteristic values are passed (valueFrom, valueTo, or unitOfMeasure) | While defining characteristics for Product Specification (cfs spec based) |
| 400 | `DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_NAME` | 2005 | Please enter correct characteristic name | Invalid Product Specification Characteristics Selected | If characteristic ID exists but name does not match expected | While defining/modifying characteristics (cfs spec based) |
| 400 | `DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_ID` | 2006 | Please enter correct characteristic id | Invalid Product Specification Characteristics Selected | If characteristic IDs are not present in corresponding Service Spec | While defining/modifying characteristics (cfs spec based) |
| 400 | `DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALUERANGE` | 2007 | Please enter correct characteristic value or range | Invalid Product Specification Characteristics Selected | If valueFrom/valueTo are null or invalid | While defining/modifying characteristics (cfs spec based) |
| 400 | `DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_MULTIPLE_VALUE` | 2008 | Duplicate reference values for characteristic | Invalid Product Specification Characteristics Selected | If duplicate reference values are provided | While defining/modifying characteristics (cfs spec based) |
| 400 | `DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALIDFOR_RANGE` | 2009 | Invalid Product Specification ValidFor Range Selected | Invalid Product Specification Characteristics Selected | Validity period mismatch with Supporting Entity Spec | While defining/modifying Identity Data or characteristics |
| 400 | `DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALUE` | 2010 | Please provide characteristic value | Invalid Product Specification Characteristics Selected | If characteristic value is null or less than required | While defining/modifying characteristics (cfs or stock based) |
| 400 | `DISCO_PS_INVALID_STOCKITEM_LIST_FOUND` | 2011 | Stock Item List not found | Stock Item List not found | If valid Stock Item list not found for Stock Item Type | While defining/modifying characteristics (stock based) |
| 400 | `DISCO_PS_INVALID_STOCKITEM_TYPE_FOUND` | 2012 | Stock Item Type not found | Stock Item Type not found | If stock item type does not exist | While defining/modifying characteristics (stock based) |
| 400 | `DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_DISTINCT_IDNAME` | 2013 | Please enter distinct characteristic id and name | Invalid Product Specification Characteristics Selected | If duplicate characteristics are selected | While selecting/modifying characteristics |
| 400 | `DISCO_PS_INVALID_PS_USAGE_SELECTED` | 2014 | Invalid Product Specification Usage Selected | Invalid Product Specification Usage Selected | Usage specs mismatch or invalid validity | While defining/modifying characteristics (cfs spec based) |
| 400 | `DISCO_PS_INVALID_PS_ID` | 2015 | Product Specification ID not valid | Product Spec id not valid | If relationship references non-existent PS ID | While defining/modifying entity relationship |
| 400 | `DISCO_PS_INVALID_PS_PARTY_SELECTED` | 2016 | Please enter Related Party IDs present in Service Specification | Invalid Product Specification Party Selected | If related party has non-null referredType but null role | While defining Identity Data |
| 400 | `DISCO_PS_INVALID_PS_PARTYROLE_SELECTED` | 2017 | Please enter Party Role present in Service Specification | Invalid Product Specification Party Role Selected | If related party role is null | While modifying Identity Data |
| 400 | `DISCO_PS_INVALID_PS_LIFECYCLE` | 2018 | Product Specification must not be in launched state | Invalid Product Specification lifecycle | Errors during cancellation or modification when PS in Launched/Retired | While cancelling or modifying PS |
| 400 | `DISCO_PS_INVALID_PS_LIFECYCLE_INTEST` | 2019 | Please select PS having lifecycle status INTEST | Invalid Product Specification Selected | If PS not in valid states (InTest, Active, Launched, Retired) | While selecting or modifying PS |
| 400 | `DISCO_PS_INVALID_PS_NOTFOUND` | 2020 | Product Specification not found | Product Specification not found | If selected PS doesn’t exist | While selecting/modifying PS |
| 400 | `DISCO_PS_INVALID_STOCKITEM_STATE` | 2021 | Please validate lifecycle status of Stock Item | Invalid StockItem State | If Stock Item not in Active or Launched | While selecting or modifying PS |
| 400 | `DISCO_PS_INVALID_SERVICE_SPEC_HREF` | 2022 | Href cannot be null/empty | Invalid service specification href received | If service spec href is null/empty | While processing external events |
| 400 | `DISCO_PS_INVALID_SERVICE_SPEC_RELATED_RESOURCE` | 2023 | Please enter valid related resource | Invalid service specification related resource | If related resource missing id, role, name, or href | While processing external events |
| 400 | `DISCO_PS_INVALID_SERVICE_SPEC_NOT_EXISTS` | 2024 | Please replicate the service specification | No Service Specification exists with ID | If Service Spec ID not found | While processing external events |
| 400 | `DISCO_PS_SERVICE_SPECIFICATION_RECEIVED_ALREADY` | 2025 | Service Specification already updated | Service Specification already received | Duplicate or old events received | While processing external events |
| 400 | `DISCO_PS_INVALID_SERVICE_SPECIFICATION_STATUS_RECEIVED` | 2026 | Invalid service specification status | Invalid service specification status | Invalid lifecycle transitions | While processing external events |
| 400 | `DISCO_PS_INVALID_STOCKITEM_STATUS_RECEIVED` | 2027 | Invalid stock item status | Invalid stock item status | If Stock Item not Active/Launched | While processing external events |
| 400 | `DISCO_PS_STOCKITEM_NOT_EXISTS` | 2028 | Please replicate Stock Item with given ID | No Stock Item exists with ID | Stock Item Spec ID not found | While processing external events |
| 400 | `DISCO_PS_STOCKITEM_RECEIVED_ALREADY` | 2029 | Stock Item already updated | Stock Item already received | Duplicate or old events received | While processing external events |
| 400 | `DISCO_PS_PICKCHATACTERISTICSPEC_CANNOT_EMPTY` | 2030 | PickCharacteristicSpecification cannot be empty | Missing Body | If characteristics are null | While defining/modifying characteristics |
| 400 | `DISCO_PS_MINTOMAX_CARDINALITY_LESSOREQUAL` | 2031 | Min cardinality ≤ Max cardinality | Invalid min/max cardinality | If min > max | While defining/modifying characteristics (cfs spec based) |
| 400 | `DISCO_PS_DEFAULTTOMAX_CARDINALITY_LESSOREQUAL` | 2032 | Defaults ≤ Max cardinality | Invalid defaults | If default > max | While defining/modifying characteristics (cfs spec based) |
| 400 | `DISCO_PS_IDENTITYDATA_MUST_NOTNULL` | 2033 | Identity data must not be null | identityData must not be null | If identity data is null | While defining/modifying Identity Data |
| 400 | `DISCO_PS_PROVIDE_IDORNAME` | 2034 | Related party ID/name is mandatory | Please enter id or name | If related resource/party id or name is null | While defining/modifying Identity Data |
| 400 | `DISCO_PS_VALIDITY_MUST_NOTNULL` | 2035 | Validity must not be null | validityPeriod must not be null | If validityPeriod is null | While defining/modifying Identity Data |
| 400 | `DISCO_PS_INVALID_PS_STATE` | 2036 | Please enter valid PS lifecycle | Invalid Product Specification lifecycle | If next lifecycle status invalid | While modifying Identity Data |
| 400 | `DISCO_PS_DEFINERELATIONSHIP_CANNOT_EMPTY` | 2037 | DefineRelationship cannot be empty | Missing Body | If no relationships are defined | While defining/modifying entity Relationship |
| 400 | `DISCO_PS_VALID_REFERREDTYPE` | 2038 | Please enter valid ReferredType | Invalid ReferredType | If related party type not valid (Organization/Individual) | While defining Identity Data |
| 400 | `DISCO_PS_VALID_ROLE` | 2039 | Please enter valid role | Invalid Role | If related party role not valid | While defining Identity Data |
| 400 | `DISCO_PS_INVALID_SUPPORT_ENTITY_TYPE` | 2040 | Invalid support entity type | Invalid support entity | If support entity not CFSSpec/StockItemType/SupplierProduct/ProductSpec | While selecting support Entity |
| 400 | `DISCO_PS_INVALID_STATUS_NOT_MODIFIED` | 2041 | Entities with unavailable status can't be modified | Invalid PS entered | Aggregate not found (never created or deleted) | While modifying PS |
| 400 | `DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALUE_OUT_OF_RANGE` | 2042 | Characteristic value outside allowed range | Invalid value | If value outside CFS allowed range | While defining/modifying characteristics |
| 400 | `DISCO_PS_INVALID_CFS` | 2043 | Please select valid CFS | Invalid CFS | If CFS validity expired before PS creation | While selecting support Entity |
| 400 | `DISCO_PS_EXPIRED_CFS` | 2044 | CFS expires before PS validity | Invalid CFS | If CFS expires before PS startDateTime | While defining Identity Data |
| 400 | `DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_CFS_REFERENCE_VALUE_MUST_BE_NOT_NULL` | 2045 | Missing CFS reference for characteristic | Missing mapping | If characteristic has validFrom/To but missing reference | While defining/modifying characteristics |
| 400 | `DISCO_PS_INVALID_PS_CHARACTERISTICS_VALUE_TIME_RANGE_INVALID` | 2046 | Invalid validity time range for characteristic | Invalid time range | Overlapping PS characteristic validity with CFS | While defining/modifying characteristics |
| 400 | `DISCO_PS_DUPLICATE_SERVICE_SPEC` | 2047 | Duplicate Service Specification | Duplicate Service Specification | ServiceSpec ID already exists or invalid lifecycle | While processing external events |
| 400 | `DISCO_PS_DUPLICATE_STOCKITEM` | 2048 | Duplicate Stock Item | Duplicate Stock Item | StockItem ID already exists or invalid lifecycle | While processing external events |
| 400 | `DISCO_PS_INVALID_RELATIONSHIP` | 2049 | PS relationship invalid | Invalid relationship | If related PS not aligned with CFS relationships | While defining relationships |
| 400 | `DISCO_PS_INVALID_VALIDFOR_STARTTIME_ENDTIME` | 2050 | Validity end time > start time required | Invalid validity period | If startTime > endTime | While defining relationships |
| 400 | `DISCO_PS_INVALID_VALIDFOR_BEFORE_CURRENT_TIME` | 2051 | Start time must be after current time | Invalid validity period | If relationship startTime < current time | While defining relationships |
| 400 | `DISCO_PS_RELATIONSHIP_INVALID_VALIDFOR_STARTTIME` | 2052 | Relationship start time < PS start time | Invalid relationship validity | If relationship startTime < PS validity start | While defining relationships |

### Product Offering Price Error Codes

| HTTPS Response   Code | ID | Error Code | Error Message | Error Reason | Descriptive Scenario | Request Scenario |
|:-----------|:---|:-----------|:--------------|:-------------|:---------------------|:----------------|
| 400 | `DISCO_POP_INVALID_POP_IDENTITYDATA` | 3001 | Please provide valid unit and value in price of Product Offering Price identity data | Price in product offering price is invalid. Please provide non null values in 'unit' and 'value' in price. | The 'unit' and 'value' parameters in Identity Data must be provided. | While defining/modifying POP Charge Identity Data |
| 400 | `DISCO_POP_INVALID_POP_TYPE` | 3002 | Please provide correct price type | Invalid price type. | The Price Type (priceType) provided in Identity Data is not from the valid list: Recurring (RC) or Non-Recurring (NRC). | While defining POP Charge Identity Data; While defining POP Alteration Identity Data |
| 400 | `DISCO_POP_INVALID_POP_TYPE_RC` | 3003 | The recurringChargePeriodType and recurringChargePeriodLength are mandatory and applicable only on recurring Charges. | Invalid Product Offering price type with recurring charge | In the Identity data, (a) the recurring charge details are provided for other priceType (NRC) or (b) if priceType = RC and then recurring charge details cannot be null. | While defining POP Charge Identity Data; While modifying POP Charge Identity Data (only scenario a)) |
| 400 | `DISCO_POP_INVALID_IMMEDIATE_PAYMENT_WITH_CURRNET_PRICE_TYPE` | 3004 | Please provide correct Immediate Payment | Invalid Immediate Payment with current price type | In the Identity data, Immediate payment is set true for Price Type RC. Immediate Payment is allowed only with NRC. | While defining/modifying POP Charge Identity Data; While defining POP Alteration Identity Data |
| 400 | `DISCO_POP_INVALID_POPC_LIFECYCLE_STATUS` | 3005 | Product offering price lifeCycleStatus should be in either unavailable/launched while creating | Invalid Product offering price lifeCycleStatus | In the Identity Data, the Lifecycle Status is either passed as 'Obsolete' or left null. Must be 'Launched', 'Unavailable', or 'Retired'. | While defining/modifying POP Charge Identity Data; While defining/modifying POP Alteration Identity Data; While defining POP Tax Identity Data |
| 400 | `DISCO_POP_INVALID_POP_VALIDFOR` | 3006 | startDateTime must be less than endDateTime | Invalid Product Offering price validFor | In validFor, either startTime is null, or endTime is set earlier than startTime. | While defining/modifying POP Charge Identity Data; While defining/modifying POP Alteration Identity Data; While defining POP Tax Identity Data |
| 400 | `DISCO_POP_INVALID_POPC_RELATIONSHIP_POPA_PRIORITY` | 3007 | The "priority" of the associated alterations to a charge must be unique. Charges with conflicting priorities: <<[POP.ID]>> | In POPC relationship, more POPA have the same priority | Multiple POPAs with duplicate priority values in the same relationship. | While defining/modifying POP Charge Identity Data |
| 400 | `DISCO_POP_INVALID_POPA_TYPE` | 3008 | Please provide correct alteration type | Invalid Product Offering Alteration type | Price Alteration Type must be 'Price' or 'Percentage'. | While defining/modifying POP Alteration Identity Data; While defining POP Tax Identity Data |
| 400 | `DISCO_POP_PRICE_TYPE_NOT_RC` | 3009 | Duration attributes must not be valued | If price type is not RC, duration attributes must not be valued | If PriceType != RC but duration is set. | While defining POP Alteration Identity Data |
| 400 | `DISCO_POP_INVALID_POP_TYPE_APPLICATION_DURATION` | 3010 | If the price type is RC, application duration attributes must be valued | Invalid Product Offering price type | NOT USED | |
| 400 | `DISCO_POP_DIFFERENT_POPA_PRICETYPE` | 3011 | POPC and POPA must have same price type for defining relationship | Different Product Offering Price Alteration Price Type | If POPA type differs from POP priceType. | While defining POP Charge Identity Data; While modifying POP Charge Identity Data |
| 400 | `DISCO_POP_INVALID_POPA_LIFECYCLE_STATUS` | 3012 | POPA life cycle status must be launched | Invalid POPA life cycle status | Related POPAs not in 'Launched' state. | While defining POP Charge Identity Data |
| 400 | `DISCO_POP_INCONSISTENT_USEOF_POPA_RELATIONSHIP_TYPE` | 3013 | POPA must be defined with price if relationship type is replacedBy | Inconsistent use of Product Offering Price relationship type | If relationship type = replacedBy but percentage provided. | While defining/modifying POP Charge Identity Data |
| 400 | `DISCO_POP_INVALID_POPC_LIFECYCLE_STATUS_UNAVAILABLE` | 3014 | ProductOfferingPrice lifecycle must be unavailable | Invalid Product Offering price lifecycle | POP creation can only be cancelled when lifecycle is 'Unavailable'. | While cancelling POP creation |
| 400 | `DISCO_POP_INVALID_POP_TYPE_DURATION` | 3015 | Only possible price type RC with non-zero duration | Invalid Product Offering price type with duration | (1) If priceType=RC but duration not set. (2) If priceType=NRC but duration set. | While modifying POP Alteration Identity Data |
| 400 | `DISCO_POP_POP_NOT_FOUND` | 3016 | POP ID not found | POP not found | POP for given Id not found for modification or validation. | While selecting POP for modification; While selecting POP for validation |
| 400 | `DISCO_POP_INVALID_CHARACERISTICS_VALUE` | 3017 | Invalid POP Type selected | Invalid characteristic value | Invalid POP Type (not in valid list). | While selecting POP type for creation; While modifying POP type |
| 400 | `DISCO_POP_INVALID_POPATYPE_VALUE` | 3018 | Price Alteration Type must be either 'Price' or 'Percentage' | Invalid priceAlterationType value | Invalid value provided for priceAlterationType. | While defining POP Alteration Identity Data; While defining/modifying POP Tax Identity Data |
| 400 | `DISCO_POP_INVALID_PERCENTAGE_VALUE` | 3019 | Percentage field can hold only numeric values (0–100) | Invalid percentage value | Percentage not numeric or not in 0–100. | While defining/modifying POP Alteration Identity Data; While defining POP Tax Identity Data |
| 400 | `DISCO_POP_INVALID_STATUS_NOT_MODIFIED` | 3020 | Entities with unavailable status can't be modified. | Invalid Product Offering Price entered | Selected POP not in 'Launched' state. | While selecting POP for modification |
| 400 | `DISCO_POP_INVALID_POP_LIFECYCLE` | 3021 | Entities with launched status can't be modified. | Invalid Product Offering Price | Selected POP is in 'Launched' state. | While selecting POP for modification |
| 400 | `DISCO_POP_INVALID_PRORATIONTYPE` | 3022 | For NRC type POPs, only prorationType=noProration is allowed | Invalid prorationType | For NRC, prorationType must be 'noProration'. | While defining POP Charge Identity Data; While defining POP Alteration Identity Data |
| 400 | `DISCO_POP_INVALID_CYCLECHARGE_NRC` | 3023 | For NRC type POPs, chargeCycle is not modifiable | Invalid chargeCycle | If priceType=NRC but chargeCycle is defined. | While defining POP Charge Identity Data |
| 400 | `DISCO_POP_INVALID_POPA_PRICE` | 3024 | Alteration price must be less than POPC price | Invalid alteration price | Alteration price > POPC price. | While defining POP Charge Identity Data |
| 400 | `DISCO_POP_BOTH_PRICE_PERCENTAGE_DEFINED` | 3025 | Both percentage and amounts for a tax alteration cannot be defined | Invalid tax alteration | Both price and percentage set. | While defining POP Tax Identity Data |
| 400 | `DISCO_POP_NEITHER_PRICE_PERCENTAGE_DEFINED` | 3026 | At least amount or percentage must be defined for a tax alteration | Invalid tax alteration | Neither price nor percentage set. | While defining POP Tax Identity Data |
| 400 | `DISCO_POP_PERCENTAGE_DEFINED_IN_RANGE` | 3027 | A Product Offering Price Alteration cannot have percentage more than 100 | Invalid percentage | Percentage not between 0–100. | While defining/modifying POP Alteration Identity Data |
| 400 | `DISCO_POPA_BOTH_PRICE_PERCENTAGE_DEFINED` | 3028 | A Product Offering Price Alteration cannot have both percentage and money values | Invalid alteration definition | Both percentage and money set. | While defining/modifying POP Alteration Identity Data |
| 400 | `DISCO_POP_ASSOCIATED_WITH_TAXPOPA_THROUGH_ALTERED_BY` | 3029 | Only alteredBy relationship is applicable for tax alterations | Invalid relationship | Tax alterations wrongly linked by 'replacedBy'. | While defining/modifying POP Charge Identity Data |
| 400 | `DISCO_POP_INVALID_RELATION_MULTIPLE_REPLACEDBY` | 3030 | A POPC cannot have multiple replacements. | Invalid relationship | Multiple replacedBy relationships defined. | While defining/modifying POP Charge Identity Data |
| 400 | `DISCO_POP_INVALID_RELATION_BOTH_ALTEREDBY_AND_REPLACEDBY_PRESENT` | 3031 | A POPC with 'replacedBy' cannot also have 'alteredBy' relationship | Invalid configuration | Both alteredBy and replacedBy relationships present. | While defining/modifying POP Charge Identity Data |
| 400 | `DISCO_POP_INVALID_POPA_UNIT` | 3032 | The currencies in associated POP must match. Invalid POPA: <<POPA.ID>> | Invalid currency | POP currency mismatch with alteration POP. | While defining/modifying POP Charge Identity Data |
| 400 | `DISCO_POP_INVALID_POP_FREQUENCY` | 3033 | Frequencies must be subset of administratively defined values | Invalid frequency | Frequency not in allowed list. | While defining/modifying POP Alteration Identity Data |
| 400 | `DISCO_POP_INVALID_POP_CURRENCY` | 3034 | Currency must be subset of administratively defined values | Invalid currency | Currency not valid in administrative set. | While defining/modifying POP Charge Identity Data |

### Lifecycle State Management Error Codes

| HTTPS Response Code | ID | Error Code | Error Message | Error Reason | Descriptive Scenario | Request Scenario |
|:-----------|:----------------------------------|:-----------|:-------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------|:---------------------------------------------------|
| 400 | `DISCO_LS_INVALID_ENTITY_ID` | 4001 | Please enter valid entity Id as entity does not exist for ID: <<Entity.ID>> | Invalid entity Id: <<Entity.ID>> | During the lifecycle state management of an entity (PS, APO, BPO, CPO, or POP), if the entity corresponding to the provided entity ID does not exist. | While selecting entity for lifecycle state management |
| 400 | `DISCO_LS_INVALID_LIFECYCLE_STATUS` | 4002 | The selected lifecycle status is not valid for this transition. Please enter correct lifecycle status | Invalid Product Lifecycle Status Entered | During an entity's lifecycle state change, an error is triggered if the specified next state is invalid according to the current state and CPIB usage. | While updating status of a selected entity |
| 400 | `DISCO_LS_INVALID_ENTITY_TYPE` | 4003 | Please enter correct entity type | Invalid entity type | During entity lifecycle state management, an error is triggered if entity type entered is not among "BundleProductOffering","AtomicOffer","ProductSpecification","Contract". | While selecting an entity for lifecycle state management |
| 400 | `ENTIY_IS_IN_USE_IN_CPIB` | 4004 | The entity "product.ID"/"productOfferingPrice.ID" is in active status in product inventory. The entity cannot be marked as obsolete. | The entity is in active status in product inventory. The entity cannot be marked as obsolete. ID: | During an entity's lifecycle state transition, an error is triggered if the next state is specified as 'Obsolete' but the entity is currently referenced by a CPIB. | While updating status of a selected entity |
| 400 | `DISCO_LS_INVALID_PS_LIFECYCLE_STATUS` | 4005 | Please change the status of PS to launched: <<productSpecification.ID>> | Product offering cannot be launched as the underlying PS is not launched | An error occurs during the lifecycle state change of an APO if the next state is set to 'Launched' but the supporting PS is not in the 'Launched' state. | While updating status of a selected entity (APO) |
| 400 | `DISCO_LS_INVALID_BPO_LIFECYCLE_STATUS` | 4006 | Product offering cannot be launched as the underlying bundled offering is not launched with ID : <<[productOffering.ID]>> (multiple IDs possible) | Product offering cannot be launched as the underlying bundled offering is not launched with ID : | An error occurs during the lifecycle state change of a BPO or CPO if the next state is set to 'Launched' but one or more bundled POs are not in the 'Launched' state. | While updating status of a selected entity (BPO/CPO) |
| 400 | `DISCO_LS_INVALID_CHARACTERISTIC_VALUE` | 4007 | Please enter correct lifecycle status | Invalid Product Lifecycle Status Entered. Valid States: ['inStudy', 'inDesign', 'rejected', 'inTest', 'active', 'launched', 'unavailable', 'retired', 'obsolete']. | During an entity's lifecycle state change, an error is triggered if the specified next state is invalid and not among valid list of lifecycle states. | While updating status of a selected entity |

### Category Error Codes

| HTTPS Response  Code | ID | Error Code | Error Message | Error Reason | Descriptive Scenario | Request Scenario |
|:-----------|:-------------------------------------------|:-----------|:----------------------------------------------------------------------------|:---------------------------------------------------------------|:-------------------------------------------------------------------------------------------------------|:-----------------------------------------------------|
| 400 | `DISCO_CATEGORY_AND_DESCRIPTION_CANANNOT_BENUL` | 5001 | Please provide category name and description | Category name and description cannot be null | During category creation/modification, if the name or description in Identity data is passed as null (this won't arise, as POJO validation occurs beforehand). | While adding/modifying category's Identity Data |
| 400 | `DISCO_CATEGORY_PO_STATUS_SHOULD_ACTIVE_OR_LAUNCHED` | 5002 | Status of Product Offering should be active or launched | Status of Product Offering should be active or launched | During category creation or modification, an error occurs when a Product Offering (PO) is selected for the category but the PO is not in the 'Active' or 'Launched' state. | While Defining/modifying associating Entities list for category |
| 400 | `DISCO_CATEGORY_PO_ID_NOTVALID` | 5003 | Invalid Product Offering selected with ID: <<ID>> | Product offering id is not valid | During category creation or modification, an error occurs if a selected Product Offering (PO for category) does not exist. | While Defining/modifying associating Entities list for category |
| 400 | `DISCO_CATEGORY_SUBCATEGORY_DOESNOTEXSIST` | 5004 | The selected child category does not exist. IDs: <<ID>> | The selected child category does not exist | During category creation/modification, in Identity data, if a category (by ID) passed as a subcategory doesn't exist. | While adding/modifying category's Identity Data |
| 400 | `DISCO_CATEGORY_SUBCATEGORY_ANOTCATEGORY` | 5005 | A root category cannot have a parent. Invalid sub category ID: | A root category cannot have a parent | During category creation/modification, if in Identity data a category (by ID) passed as a subcategory was originally created as a root category, it cannot have a parent. | While adding/modifying category's Identity Data |
| 400 | `DISCO_CATEGORY_PARENT_CANNOTNOTNULL` | 5006 | Parent Category cannot be null | Parent Category cannot be null | During category creation, if in Identity data a category is being created as a subcategory (isRoot=false), then the parent Category ID cannot be null or blank. | While adding category's Identity Data |
| 400 | `DISCO_CATEORY_CATEORY_NOT_FOUND` | 5007 | Category not found | Category not found | If the category does not exist for the provided ID. | While selecting category for modification; While selecting category for deletion; While cancelling the category delete event; While deleting a validated category (confirmation needed) |
| 400 | `DISCO_CATEGORY_ENTITY_TYPE_NOT_MATCHING_WITH_CATEGORYTYPE` | 5008 | Only product offerings are allowed to be associated to categories | Entity type is not matching with category type (When entity other than "Product Offering" is associated) | If a category's type does not match the type of its subcategories as defined in Identity data. When the category's type does not match the associated entities' types | While defining/modifying identity data(desc1); While associating/modifying association entities (desc 2) |
| 400 | `DISCO_CATEGORY_INVALID_INPUT_PARAMETERS` | 5009 | Case 1: Category with ID: <category.ID> has invalid data; Case 2: Product offering with ID: <productOffering.ID> has invalid data | Invalid input parameters | If the subcategories' type defined in Identity data is null. If the associating entities' type is defined as null | While defining/modifying identity data(desc1); While associating/modifying association entities (desc 2) |

### Export Job Error Codes

| HTTPS Code | ID | Error Code | Error Message | Error Reason | Descriptive Scenario | Request Scenario |
|:-----------|:-------------------------------|:-----------|:------------------------------------------------------------|:-----------------------------------------------------------|:-------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------|
| 404 | `DISCO_CPO_NOT_FOUND` | 6001 | Resource not found | Resource not found | If no contract POs are found with the provided filters or query parameters | While generating WorkBook of contract PO Export Job. |
| 400 | `MISSING_MANDATORY_FIELDS_NOT_FOUND` | 24 | Missing mandatory attribute: | Invalid body field | If 'query' or 'path' (URL of the root resource acting as the source for streaming content to the file specified by the export job) is passed as null or blank | While generating WorkBook of contract PO Export Job. |
| 400 | `DISCO_CPO_WORKBOOK_EXCEPTION` | 6002 | Data parsing failed from backend. | Data parsing failed from backend. | If any exception arises at backend | While generating WorkBook of contract PO Export Job. |
| 400 | `DISCO_BPO_NOT_FOUND` | 6003 | Child offering not found: | Child offering not found: | If child bundle offerings of a contract PO are not found | While generating WorkBook of contract PO Export Job. |
| 400 | `DISCO_APO_NOT_FOUND` | 6004 | Child offering not found: | Child offering not found: | If child atomic offerings of a contract PO are not found | While generating WorkBook of contract PO Export Job. |
| 400 | `DISCO_INVALID_QUERY_FORMAT` | 6005 | Invalid query string format. | Invalid query string format. | If the query is passed in an incorrect format (check for valid key pair of query param also) | While generating WorkBook of contract PO Export Job. |
| 400 | `DISCO_CPO_ID_NOT_FOUND` | 6006 | Invalid product offering ID. | Invalid product offering ID. | If contract POs for the requested filter cannot be fetched | While generating WorkBook of contract POExport Job. |
| 400 | `DISCO_PS_NOT_FOUND` | 6007 | Referenced product specification not found: | Referenced product specification not found: | If supporting product specifications for the child atomic POs of a contract PO are not found | While generating WorkBook of contract PO Export Job. |
| 400 | `DISCO_POP_NOT_FOUND` | 6008 | Referenced product offering price not found: | Referenced product offering price not found: | If associated POPs (for all parent and child POs) are not found | While generating WorkBook of contract PO Export Job. |
| 400 | `DISCO_STOCKITEM_NOT_FOUND` | 6009 | Referenced stock item not found: | Referenced stock item not found: | If supporting StockItem Spec (for all Product Spec) are not found | While generating WorkBook of contract PO Export Job. |
| 400 | `DISCO_CFS_NOT_FOUND` | 6010 | Referenced service specification not found: | Referenced service specification not found: | If supporting CFS Spec (for all Product Spec) are not found | While generating WorkBook of contract PO Export Job. |
| 400 | `DISCO_CATEGORY_NOT_FOUND` | 6011 | Referenced category not found. | Referenced category not found. | If PO's category (for all POs) is not found | While generating WorkBook of contract PO Export Job. |
| 400 | `INVALID_PATH` | 6012 | Path does not contain any valid resource name | Path does not contain any valid resource name | If the path URL contains a resource name other than 'productSpecification' or 'productOffering' | While generating the workbook for a contract PO export job or a PS export job. |
| 400 | `DISCO_PS_NOT_FOUND` | 6013 | Referenced product specification not found | Referenced product specification not found | The requested Product Specification do not exist in the database | While generating the workbook of a PS export job. |

### Authentication

In case the security is enabled, bearer token is used for authentication and authorization. Our API is a resource server based on OAuth2.

For more details, please consult [User Role & Permission management component](https://discobole.ow2.io/disco-oda-components/disco-security/doc/api-references/specifications/user-role-and-permissions/.)


###### Example of a get Product Specification

 ```json
   {
        "id": "cba5c6e2-a2a6-440d-a4d0-97ab8e8378dc",
        "brand": "orange",
        "description": "Cisco",
        "isBundle": false,
        "lastUpdate": "2024-03-12T09:41:28.556Z",
        "lifecycleStatus": "active",
        "name": "Mobile Line",
        "productNumber": "123",
        "version": "1.0.0",
        "supportEntity": "CFSSpec",
        "policyRuleRef": [],
        "productSpecCharacteristic": [
            {
                "id": "73c5d2ac-09ca-497e-859b-b955f2f7036e.1",
                "configurable": true,
                "description": "Type of SIM use-able for this mobile access",
                "extensible": null,
                "isUnique": null,
                "maxCardinality": 2,
                "minCardinality": 0,
                "name": "SIM Type",
                "regex": null,
                "valueType": "string",
                "productSpecCharRelationship": [],
                "productSpecCharacteristicValue": [
                    {
                        "isDefault": true,
                        "rangeInterval": null,
                        "regex": null,
                        "unitOfMeasure": null,
                        "valueFrom": null,
                        "valueTo": null,
                        "valueType": "Integer",
                        "validFor": {
                            "endDateTime": null,
                            "startDateTime": "2023-04-12T23:20:50.520Z"
                        },
                        "value": "SIM Alias",
                        "@baseType": null,
                        "@schemaLocation": null,
                        "@type": null,
                        "supportEntity.characteristic.value": "SIM"
                    }
                ],
                "validFor": {
                    "endDateTime": null,
                    "startDateTime": "2024-01-01T23:20:50.520Z"
                },
                "@baseType": null,
                "@schemaLocation": null,
                "@type": null
            }
        ],
        "productSpecificationRelationship": [],
        "relatedParty": [],
        "serviceSpecification": [
            {
                "id": "Valid_CFS_J",
                "href": "http://localhost:3050/test",
                "name": "Mobile Access",
                "version": null,
                "@baseType": null,
                "@schemaLocation": null,
                "@type": null,
                "@referredType": null
            }
        ],
        "validFor": {
            "endDateTime": null,
            "startDateTime": "2024-01-01T23:21:00.520Z"
        },
        "relatedResource": [
            {
                "id": "MobileSOM",
                "href": "http://serviceDelivery",
                "name": "SOM Application in charge of the Service Delivery",
                "role": "serviceDelivery",
                "@baseType": null,
                "@schemaLocation": null,
                "@type": null,
                "@referredType": null
            },
            {
                "id": "SDC",
                "href": "http://serviceCatalog",
                "name": "Application in charge of the Service Description",
                "role": "serviceCatalog",
                "@baseType": null,
                "@schemaLocation": null,
                "@type": null,
                "@referredType": null
            }
        ],
        "operationSpecification": [
            {
                "id": "27f4bb8b-b9cd-486e-a601-224a80c07164.1",
                "name": "Create",
                "validFor": {
                    "endDateTime": null,
                    "startDateTime": "2020-10-11T23:20:50.520Z"
                },
                "description": "Provision a new mobile access on Orange Network",
                "isQualificationRequested": false,
                "@type": null,
                "@baseType": null,
                "@schemaLocation": null
            },
            {
                "id": "8e60a325-bbe2-47b6-b956-72553d35e65e.2",
                "name": "Modify",
                "validFor": {
                    "endDateTime": null,
                    "startDateTime": "2020-10-11T23:20:50.520Z"
                },
                "description": "Modify mobile access configuration",
                "isQualificationRequested": false,
                "@type": null,
                "@baseType": null,
                "@schemaLocation": null
            },
            {
                "id": "11eeafe9-fa1d-47f1-8839-611d6a0c4b92.3",
                "name": "Delete",
                "validFor": {
                    "endDateTime": null,
                    "startDateTime": "2020-10-11T23:20:50.520Z"
                },
                "description": "Remove an existing Mobile access from the Orange Network",
                "isQualificationRequested": false,
                "@type": null,
                "@baseType": null,
                "@schemaLocation": null
            }
        ],
        "@baseType": "ShippingProductSpecification",
        "@type": "ProductSpecification",
        "usageSpecification": []
      }  

 ```

 ###### Example value of get product with extraction of field  brand

```json
  [
     {
        "id": "849789bc-367f-461b-bf54-73782f1668ad",
        "brand": "Orange"
     },
     {
        "id": "5ce9899f-5bb5-4f91-bd3f-2f67004a1431",
        "brand": "Orange"
     },
     {
        "id": "5f630465-1ac9-4836-8d3e-e55e374146ad",
        "brand": "Orange"
     }
  ]

```

###### Example request  a task to export Contract (Product Offerings)


  Use case using the method                                         | Method | URL                                                                                                                                 |
  |:------------------------------------------------------------------|:-------|:------------------------------------------------------------------------------------------------------------------------------------|
  | To export **Contract Enity**                      | Post    | [{{baseURL}}/productCatalogManagement/v1/exportJob]                            

**Request body :**

```json
{
    "contentType": "ODF",
    "query": "id=dbbc92f9-faf2-4886-8749-0f07abfc1971",
    "path": "contractProductOffering",
    "@type": "ExportJob"
 }
 ```
| Description                             | Method  | Endpoint                                                    |
|-----------------------------------------|---------|------------------------------------------------------------|
| To **export Entity filtered by state** | POST    | [{{baseURL}}/productCatalogManagement/v1/exportJob]        |
                          
**Request body :**

```json
{
    "contentType": "ODF",
    "query": "status=active",
    "path": "contractProductOffering",
    "@type": "ExportJob"
 }

```

 | Description                             | Method  | Endpoint                                                    |
 |-----------------------------------------|---------|------------------------------------------------------------|
 | To **export Entity filtered by date**  | POST    | [{{baseURL}}/productCatalogManagement/v1/exportJob]        |

**Request body :**

```json
{
    "contentType": "ODF",
    "query": "startDateTime=2024-06-05T12:12:00.000+05:30&endDateTime=2024-06-12T12:13:00.000+05:30",
    "path": "contractProductOffering",
    "@type": "ExportJob"
 }

```

### History of Document
| Version of the document | modification date | description of modifications  |
|:------------------------|:------------------|:------------------------------|
| 1.0                     | 04/10/2023        | initialization                |
| 1.1                     | 04/12/2023        | improving patch documentation |
| 2.0                     | 09/06/2024        | adding task api               |



