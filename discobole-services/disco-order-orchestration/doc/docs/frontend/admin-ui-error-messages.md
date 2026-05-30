---
title: Error Messages
summary: Error Messages.
authors:
  - Omar Abdalla
---

# UI Error Messages

???+ abstract "Error Messages"
    Context |   Error Code  |   Error Message   | Error Reason
    --- | --- | --- | ---
    When COOD is calling CPIB to retrieve product details (to verify state) during delivery | `CPIB_NOT_REACHABLE` | "Internal Server error, can't reach to CPIB to get product details" | 
    When COOD is verifying the to be delivered product state retrieved from CPIB | `PRODUCT_STATE_NOT_VALID` | "Product should have state = {expectedState}" | "Action required = {action} and the Product to be delivered id {productId} state = {actualState} and should be {expectedState}"
    When COOD is verifying the prerequisite product state retrieved from CPIB | `PREREQUISITE_PRODUCT_NOT_ACTIVE` | "Prerequisite Products should have state = Active" | "Prerequisite Product with id = {productId} state = {actualState} and should be Active"
    When COOD is verifying the dependent product state retrieved from CPIB | `DEPENDENT_PRODUCT_STATE_INVALID` | "Dependents Products should have state = Terminated or Cancelled or Aborted" | "Dependent Product with id = {productId}, state = {actualState} and should be Terminated or Cancelled or Aborted"
    After COOD has retrieved the products ids from the CPIB and validated the states, COOD checks if all product prerequisites (predefined at Product Catalog) were matched in CPIB. | `PREREQUISITES_PRODUCT_SPECIFICATION_NOT_SATISFIED` | "All ProductSpecificationRelationships with relationship Type = reliesOn defined in the Product Catalog should be realized by a ProductRelationship on the installed product with relationship Type = reliesOn and the related product should be active" | "the required relationship for product Specification with id = {specificationId} isn't realized by an active product"
    When COOD is consuming the SOM event (order followup) and tries to retrieve the orchestration plan related to the service order in the SOM update event to update orchestration plan node status based on service order | | "orchestration not exist" |
    <ol><li>When COOD is trying to retrieve a product from CPIB <ul><li>by ProductOrderId and OrderItemsId</li><li>by ProductId</li></ul></li><li> When COOD is trying to update a product in CPIB by productId</li> <li>When COOD is retrieving  product specifications from Product Catalog by spec id </li></ol> __if any of the params in the 3 cases (order id, product id, item id, spec id) are null (not valid) - validation failure before executing the request__ | `INVALID_PRODUCTS_PARAMETERS` | "Invalid product parameters" | 
    When COOD tries to fetch product specification by spec id from the product catalog during orchestration plan creation phase and any error occurs | `COULD_NOT_GET_PRODUCT_SPECIFICATION` | "Error getting product Specification from catalog" |
    When any error occurs while COOD is trying to update the plan based on SOM update after delivery is executed. | | "orchestration plan node with id : {}, state is not updated" |
    When any error occurs while COOD  is trying to prepare the CPIB PATCH request body before sending the request to CPIB. | | "Cannot modify product status" |
    When any error occurs while COOD is updating product state (PATCH) in CPIB. | | "Product cannot be updated" | "An error occurred while making the PATCH request"
    When any error occurs while COOD is retrieving product order details by product order id from OM during delivery before preparing service catalog request | | "Failed to retrieve Product Order by ID: productOrderId" |
    When any error occurs while COOD is checking that all the orchestration plan order items were successfully retrieved from OM during delivery before preparing service catalog request | | "Product order Item with id: " + id + " not found in the Product order with id: " + productOrderId" |
    When any error occurs while COOD is retrieving service spec from service catalog by product id during delivery, before creation of service order. | | "Error retrieving service catalog Specifications with ids: " + productsIds" | 
    When COOD can't find the product (retrieved from orchestration plan) in the OM response while preparing the service order request to send it to the SOM | | "product not found" |
    When COOD can't find the service spec (retrieved from orchestration plan) in the service catalog response while preparing the service order request to send it to the SOM | | "serviceSpecification not found" |
    When any error occurs while COOD is getting product by byProductOrderId and OrderItemsId from CPIB | | "Error retrieving product with ProductOrderId {id} and ProductOrderItemId {id}" |
    When OM product order state change event is fired, COOD validates the event params before creating the orchestration plan | `PRODUCT_ORDER_INVALID_EVENT` | "Invalid product order event" |
    When COOD is creating the orchestration plan, if any error occurs | `ORCHESTRATION_PLAN_CREATING_ERROR` | "Error creating Orchestration plan" |
    In creation phase while COOD can't find the related node in the orchestration plan when building relationships in the plan | | "Can not find related Node for the : " + relatedProductOrderItemDTOId" |
    In creation phase while get orchestration plan node by order Item Id | | "Can not find Node of the orderItem ID: " + productOrderItemDTO.getId()" |
    When any error occurred during the orchestration plan creation  (general scope of handling OM event by creating plan) | `PRODUCT_ORDER_ERROR_EVENT` | "Error when handling product order event" |
    Order management response if 400 "not handled yet" | `BAD_REQUEST` | "Supplement spaces cannot be included on fields" | "Invalid query-string parameter value"
    Order management response if 400 "not handled yet" | `BAD_REQUEST` | "orderDate cannot be added as filter parameter" | "Invalid query-string parameter value"
    Order management response if 400 "not handled yet" | `BAD_REQUEST` | "limit value is not a valid integer" | "Invalid query-string parameter value"
    Order management response if 400 "not handled yet" | `BAD_REQUEST` | "offset value is not a valid integer" | "Invalid query-string parameter value"
    Order management response if 400 "not handled yet" | `BAD_REQUEST` | "aa not included in product order fields" | "Invalid query-string parameter value"
