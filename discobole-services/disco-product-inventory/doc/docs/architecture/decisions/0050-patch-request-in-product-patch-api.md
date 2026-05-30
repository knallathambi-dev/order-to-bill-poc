<!--
Software Name: product-inventory-architecture
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# PATCH request in Product patch API (../

## Context and Problem Statement

{Describe the context and problem statement, e.g., in free form using two to three sentences or in the form of an illustrative story.
 You may want to articulate the problem in form of a question and add links to collaboration boards or issue management systems.}

## Considered Options

* RFC7386 (../
* RFC6902 (../
* JSON Patch Query (../6902)

## Decision Outcome

To use 3rd option with the jsonpath as path and make patch request by our code not by JsonPatch liberary (../

Apply patch on jsonpath by code, may be enhanced, not tested well

```java
String writeValueAsString = objectMapper.writeValueAsString(../
DocumentContext context = JsonPath.parse(../
List<List<String>> operations = List.of(../
        "replace", "ABORTED"));
for (../
    String path = operation.get(../0);
    String op = operation.get(../1);
    Object value = operation.get(../2);
    switch (../
        case "add":
            context = context.add(../
            break;
        case "replace":
            context = context.set(../
            break;
        case "remove":
            context = context.delete(../
            break;
        case "test":
            // Handle the "test" operation
            if (../
                // The test failed, you can handle it as needed
                throw new Exception(../
            }
            break;
        default:
            // Handle unsupported operations
            break;

    }
}
// Convert the updated JSON string back to a map and save it after map to an existing object in db as mapping target
Map<String, Object> updatedDocument = context.read(../
ProductEntity updatedProduct = objectMapper.convertValue(../
productMapper.toSameEntity(../
mongoTemplate.save(../
```

## Pros and Cons of the Options

| Aspect | RFC7386 (../6902 (../6902) | 
| ------ | -------------------------- | -------------------- | --------------------------------------- | 
| Purpose | Merge partial updates into a JSON object | Apply a set of operations to a JSON object | Extend JSON Patch with conditional ops |
| Patch Document Format | JSON object representing changes to merge | JSON array of operation objects | JSON array of operation objects |
| How to update item in array | replace the old array with the new one | via array index pointer  | via query parameter to allow identifying uniquely the element within an array |
| Operation Types | <p>Replace if key already exists</p><p><p>add if key is not exists before</p>delete if value is null</p><p>fields that not added in patch will be effected</p> | Add, remove, replace, copy, move, test and you should provide the path of the  field that you will apply the change, and the value of the change | Same as RFC6902 plus query capability instead of mention the item index |
| Idempotence | Generally idempotent | may be idempotent | may be Idempotent |
| Test Operation | Not applicable (../
| Complexity |  | Comprehensive, supports various operations | Adds conditional operations |
| Use Case | Merge partial changes into a JSON document | General-purpose patching | Conditional updates |
| Content Type | `application/merge-patch+json` or `application/json` | `application/json-patch+json` | `application/json-patch-query+json` |
| TMF compliency | default patch impl for TMF | yes but you MUST define content-type | yes but you MUST define content-type |
| Implementation complexity | Simple, can be achieved using jsonPatch lib as below reference https://www.javacodegeeks.com/2021/06/spring-endpoint-to-handle-json-patch-and-json-merge-patch.html | Simple, can be achieved using jsonPatch lib as below reference https://www.baeldung.com/spring-rest-json-patch to reference array items https://jsonpatch.com/#json-pointer | More complex, it needs more effort to handle array query not simple as query array by its pointer |
| Granularity of Changes | Good for high level changes | Specifies individual operations for changes as an array, all operations succeed or all failed. | Specifies individual operations for changes as an array, all operations succeed or all failed. |

**Examples**

* RFC7386 (../

```json
Target JSON: {"name": "John", "age": 30}
Merge Patch: {"age": 31}
Result: {"name": "John", "age": 31}
```

* RFC6902 (../

```json
Target JSON: {"name": "John", "age": 30}
JSON Patch: [{"op": "replace", "path": "/age", "value": 31}]
Result: {"name": "John", "age": 31}
```

* JSON Patch Query (../6902)

```json
Target JSON: 
{"id": "1", "correlationId": "TT53482", "note": [
{ "date": "2013-07-24T09:55:30.0Z","author": "Arthur Evans" },
{ "date": "2013-07-25T08:55:12.0Z", "author": "John Doe" }] }
JSON Patch: [{op": "add", "path": "/note/text?note.author=John Doe", "value":"Informed"} ]
Result: {"id": "1", "correlationId": "TT53482", "note": [
{ "date": "2013-07-24T09:55:30.0Z","author": "Arthur Evans" },
{ "date": "2013-07-25T08:55:12.0Z", "author": "John Doe", "text":"Informed }] }
```

> JSON Patch Query (../6902): other format of path it can use JSON path as mentioned in TMF-630 example

Regarding 3rd option, 

* JsonPatch lib that already supported by springboot use jsonPointer.
* TMF 630 part 5 needs to use Json pointer with added query capabilities of JsonPath
* TMF json patch query example tp be supported:
"path": "/orderItem/quantity?orderItem.productOffering.id=1513&orderItem.product.relatedParty.role=customer&orderItem.product.relatedParty.name=Mary
* TMF 630 part 6 support jsonpath for patch operations, it will be helpful to use it directly as standard in json parsing `"path": "note[?(../

**JSON Pointer**

* JSON Pointer is a simple string notation for identifying a specific value within a JSON document.
* It's primarily used for addressing specific elements within JSON, not for querying or filtering.
* JSON Pointer uses "/" to indicate levels within a JSON document.

Example JSON Pointer: `"/property/innerProperty"`

* Simpler and more focused on addressing specific elements.
* Typically used with JSON Patch operations to specify the target location for operations like add, replace, remove, etc.
* Uses a limited set of characters, primarily "/" and "~", to escape special characters.
* Does not provide filtering or querying capabilities.
* Suitable for pinpointing specific values within a JSON document.

**JSONPath**

* JSONPath is a query language for JSON data that allows you to navigate, filter, and extract data from a JSON document.
* It uses a more complex syntax with various operators for filtering, wildcards, and conditions.
* JSONPath is used for querying JSON data and is commonly used in libraries and tools for data extraction and manipulation.

Example JSONPath: $.property[?(../

* More versatile and expressive for querying and filtering JSON data.
* Used for querying JSON data based on various criteria, including conditions, wildcards, and filters.
* Supports a richer syntax with operators like @, *, ?(../
* Suitable for querying, extracting, and filtering data from JSON documents.
* Commonly used in libraries and tools for data transformation and extraction.

