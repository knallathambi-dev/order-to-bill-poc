// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.exception.model.constants;

public enum ExceptionCode {
    COOD_DB_EXCEPTION("COOD_DB_EXCEPTION", "Database connection error: %s", "Database connection failure"),
    COOD_HTTP_FAILED_EXCEPTION("COOD_HTTP_FAILED_EXCEPTION", "HTTP request failed: %s", "HTTP communication failure"),
    COOD_MAPPING_EXCEPTION("COOD_MAPPING_EXCEPTION", "Mapping error: %s", "Data mapping issue"),
    COOD_NOT_FOUND_EXCEPTION("COOD_NOT_FOUND_EXCEPTION", "Resource not found: %s", "Resource not available"),
    CBIP_NOT_FOUND_EXCEPTION("CBIP_NOT_FOUND_EXCEPTION", "Resource not found: %s", "Resource not available"),
    COOD_SECURITY_EXCEPTION("COOD_SECURITY_EXCEPTION", "Security violation: %s", "Security breach detected"),
    COOD_TECHNICAL_EXCEPTION("COOD_TECHNICAL_EXCEPTION", "Technical error: %s", "Technical failure"),
    COOD_UNEXPECTED_STATE_EXCEPTION("COOD_UNEXPECTED_STATE_EXCEPTION", "Unexpected state: %s", "Unexpected system state"),
    COOD_VALIDATION_EXCEPTION("COOD_VALIDATION_EXCEPTION", "Validation error: %s", "Validation failed"),
    HTTP_REQUEST_TIMEOUT("REQUEST_TIMEOUT", "Unable to connect to %s", "Exhausted retry attempts to %s"),
    COULD_NOT_GET_PRODUCT_SPECIFICATION_IDS("COULD_NOT_GET_PRODUCT_SPECIFICATION_IDS", "Error retrieving product specifications: IDs list is empty: %s", "Couldn't find product specification ids needed to request specification details from the product catalog"),
    COULD_NOT_GET_PRODUCT_SPECIFICATION("COULD_NOT_GET_PRODUCT_SPECIFICATION", "Error encountered when fetching products specification ids: {} from the Product Catalog for product order id: {}", "Failed to fetch the product specification details from the product catalog"),
    HTTP_TOO_EARLY("HTTP_TOO_EARLY", "%s responded with a 425 Too Early error and message %s", "Server refused to process the request"),
    HTTP_TOO_MANY_REQUESTS("HTTP_TOO_MANY_REQUESTS", "%s responded with a 429 Too Many Requests error and message %s", "too many requests are sent to the server in a given amount of time"),
    HTTP_BAD_GATEWAY("HTTP_BAD_GATEWAY", "%s  responded with a 502 Bad Gateway and message %s", " Gateway received an invalid response from the upstream serve"),
    HTTP_SERVICE_UNAVAILABLE("HTTP_SERVICE_UNAVAILABLE", "%s responded with a 503 Service Unavailable and message %s", "Service unavailable"),
    HTTP_GATEWAY_TIMEOUT("HTTP_GATEWAY_TIMEOUT", "%s responded with a 504 Gateway timeout and message %s", "Gateway did not receive a timely response from the upstream server"),
    HTTP_NO_CONTENT("HTTP_NO_CONTENT", "{%s} responded with a 204 No Content and message {%s}", "The server successfully processed the request, and is not returning any content."),
    HTTP_BAD_REQUEST("HTTP_BAD_REQUEST", "{%s} responded with a 400 bad request and message {%s}", "The server cannot or will not process the request due to an apparent error in the request."),
    HTTP_NOT_FOUND("HTTP_BAD_REQUEST", "{%s} responded with a 404 Not found and message {%s}", "The requested resource could not be found."),
    HTTP_INVALID_RESPONSE("HTTP_INVALID_RESPONSE", "{%s} responded with a status code {%s} and message {%s}", "http invalid response"),
    NO_SESSION_FOUND("NO_SESSION_FOUND_ERROR", "No session found while retrieve it in kafka consumer, requestAttributes is empty. ", "kafka session is not initialized."),
    HTTP_INTERNAL_SERVER_ERROR("HTTP_INTERNAL_SERVER_ERROR", "{%s} responded with a 500 internal server error and message {%s}", "Exception occurred on the consumed service."),
    ORCHESTRATION_PLAN_NOT_FOUND("COOD_ORCHESTRATION_PLAN_NOT_FOUND_EXCEPTION", "No orchestrationPlan found with id: %s", "orchestration plan doesn't exist"),
    ORCHESTRATION_PLAN_NODE_ID_NOT_FOUND("COOD_ORCHESTRATION_PLAN_NODE_ID_NOT_FOUND_EXCEPTION", "No orchestrationPlan found with node id: %s", "orchestration plan doesn't exist"),
    DUPLICATES_IN_PRODUCTS_ERROR("COOD_TECHNICAL_EXCEPTION", "Error while retrieve products from cpib, there are duplicated products when search with item id {%s}, action {%s} order id {%s}", "Cannot map products to order items correctly."),
    PRODUCT_ORDER_MAPPING_EXCEPTION("PRODUCT_ORDER_MAPPING_ERROR", "Error while mapping product order to orchestration plan with product order id %s", "Technical Exception"),
    PRODUCT_ORDER_ITEM_MAPPING_EXCEPTION("PRODUCT_ORDER_INVALID_EVENT", "Error while mapping product order Item to orchestration plan node with product order item id {%s}, | %s", "Product order Item not found"),
    PRODUCT_ORDER_ITEM_MAPPING_ERROR("PRODUCT_ORDER_ITEM_MAPPING_ERROR", "Error while mapping characteristics in order item with order id {%s} and item id {%s} to related product characteristics", "Couldn't map product order item characteristics to related product characteristics"),
    PRODUCT_ORDER_INVALID_EVENT("PRODUCT_ORDER_INVALID_EVENT", "Error while mapping product order Item to orchestration plan node with product order item id: {%s} | %s", "Product order Item not found"),
    ORCHESTRATION_PLAN_QUERY_PARAMETER_EMPTY_OR_NULL("INVALID_QUERY_PARAMETER_VALUE", "Query param [ Fields ] should not be empty or null", "if id is null or fields are empty"),
    INVALID_QUERY_STRING_PARAMETER("INVALID_QUERY_STRING_PARAMETER", "%s", "Invalid query-string parameter value."),
    INVALID_PRODUCTS_PARAMETERS("INVALID_PRODUCTS_PARAMETERS", "Invalid product parameters", ""),
    PRODUCT_ORDER_INVALID_EVENT_NO_PRODUCT_SPEC("PRODUCT_ORDER_INVALID_EVENT_NO_PRODUCT_SPEC", "Product specification id could not be found for product order item: {%s}", "Couldn't find product specification id in the product order when adding reliesOn relationship"),
    PRODUCT_SPEC_ID_NOT_FOUND("PRODUCT_SPEC_ID_NOT_FOUND", "Product specification id could not be found from catalog response for product order item id {%s}", "Couldn't find related product by product specification id in the product order when adding reliesOn relationship"),
    PRODUCT_SPEC_BY_ID_NOT_FOUND("PRODUCT_SPEC_BY_ID_NOT_FOUND", "Product specification id {%s} could not be found from catalog response for product order item id {%s}", "Couldn't find related product by product specification id in the product order when adding reliesOn relationship"),
    NODE_NOT_FOUND_BY_ITEM_ID("NODE_NOT_FOUND_BY_ITEM_ID", "Related orchestration plan node could not be found for the the related product order item id: %s", "Couldn't find related node for the related product by product specification id in the orchestration plan when adding deliversAfter relationship"),
    NODE_NOT_FOUND_BY_ID("NODE_NOT_FOUND_BY_ID", "Orchestration plan node could not be found by id: %s", "Couldn't find orchestration plan node by id"),
    NODE_NOT_FOUND_BY_ORDER_ITEM_ID("NODE_NOT_FOUND_BY_ITEM_ID", "Error while retrieve orchestration plan node with order item id {%s}", "Cannot found node by order item id"),
    NODE_RELATED_PRODUCT_NOT_FOUND("NODE_RELATED_PRODUCT_NOT_FOUND", "Related product not found for node id :{%s}", "Couldn't find related product for orchestration plan node"),
    ORCHESTRATION_PLAN_DUPLICATION("ORCHESTRATION_PLAN_DUPLICATION", "Orchestration plan with id {%s} already exists", "Orchestration plan already exists"),
    PRODUCT_ORDER_ITEM_ACTION_INVALID("PRODUCT_ORDER_ITEM_ACTION_INVALID", "Invalid product order Item Action {%s}", "Invalid product order action"),
    PRODUCT_ORDER_ITEM_INVALID("PRODUCT_ORDER_ITEM_INVALID", "Invalid product order Item/s: %s", "Invalid product order event other field field(s): order items list is null or empty, action, productSpecification id"),
    PRODUCT_ORDER_INVALID("PRODUCT_ORDER_INVALID", "Invalid product order: %s", "Invalid product order event field(s) on the order level: ORDER ID, party id, state = accepted"),
    PRODUCT_ORDER_INVALID_EVENT_NO_ITEMS("PRODUCT_ORDER_INVALID_EVENT_NO_ITEMS", "Product order with ID {%s} does not have product order items.", "No product order items found"),
    RELATED_PRODUCT_NOT_FOUND("RELATED_PRODUCT_NOT_FOUND", "Related product order item could not found for product specification id :{%s}", "Couldn't find related product by product specification id in the product order when adding deliversAfter relationship"),
    RELATED_PRODUCT_WITH_RELATION_TYPE_NOT_FOUND("RELATED_PRODUCT_NOT_FOUND", "Related product order item could not found for relation type :{%s}", "Couldn't find related product by related product relation type"),
    INSTALLED_PRODUCT_NOT_FOUND_EXCEPTION("INSTALLED_PRODUCT_NOT_FOUND_EXCEPTION", "Installed product could not found from CPIB response with order id :{%s} item id:{%s}, action :{%s}", "Couldn't find installed product by from cpib by order id and item ids."),
    PRODUCT_SPECIFICATION_ID_NOT_FOUND_BY_ORDER_ITEM("", "Cannot find productSpecificationId for the order item ID: %s", ""),
    CAN_NOT_DELIVERY_EMPTY_SHIPMENT("CAN_NOT_DELIVERY_EMPTY_SHIPMENT", "Unable to find a tangible product to be delivered through the requested shipment order item with id: {%s}", "A shipment must have at lease one tangible item to deliver"),
    PRODUCT_SPECIFICATION_NOT_RETRIEVED_BY_PRODUCT_ORDER_ID("PRODUCT_SPECIFICATION_NOT_RETRIEVED_BY_PRODUCT_ORDER_ID", "Error retrieving product specification with ProductOrderId %s and status %s", "Can't get product specification from product catalog"),
    INVALID_FALLOUT_EVENT_NO_ORCHESTRATION_PLAN("INVALID_FALLOUT_EVENT_NO_ORCHESTRATION_PLAN", "Event with Id: %s doesn't have orchestration plan id", "Can't find orchestration plan id."),
    INVALID_FALLOUT_EVENT_NO_ORCHESTRATION_PLAN_NODE("INVALID_FALLOUT_EVENT_NO_ORCHESTRATION_PLAN_NODE", "Event with Id: %s doesn't have orchestration plan node id [%d]", "Can't find orchestration plan node."),
    INVALID_FALLOUT_EVENT_NO_INITIATOR("INVALID_FALLOUT_EVENT_NO_INITIATOR", "Event with Id: %s doesn't have initiator related entity", "Can't find initiator related entity."),
    ORCHESTRATION_PLAN_NODE_NO_DELIVER_PRODUCT("ORCHESTRATION_PLAN_NODE_NO_DELIVER_PRODUCT", "orchestration plan node [%d] does not have deliver product", "can't find related product in the node."),
    ORCHESTRATION_PLAN_NOT_HELD("FALLOUT_ORCHESTRATION_PLAN_INVALID_STATE", "orchestration plan [%d] is not in held state", "orchestration plan is not in held state."),
    ORCHESTRATION_PLAN_NODE_NOT_HELD("FALLOUT_ORCHESTRATION_PLAN_NODE_INVALID_STATE", "orchestration plan node [%d] is not in held state", "orchestration plan node is not in held state."),
    HTTP_UNAUTHORIZED("HTTP_UNAUTHORIZED", "%s responded with a 401 Unauthorized and message %s", "The request requires user authentication or, if the request included authorization credentials, authorization has been refused for those credentials"),
    HTTP_FORBIDDEN("HTTP_FORBIDDEN", "%s responded with a 403 Forbidden and message %s", "The server understood the request, but it refuses to authorize it"),
    SERVICE_SPECIFICATION_CHARACTERISTICS_NOT_FOUND("SERVICE_SPEC_CHARACTERISTICS_NOT_FOUND", "Service specification characteristics {%s} were not found in the service catalog", "Service specification characteristics are needed for the service order request creation"),
    SHIPMENT_PRODUCT_NOT_FOUND("SHIPMENT_PRODUCT_NOT_FOUND", "Shipment product not found for node id {%s}", "shipment product not found"),
    SHIPMENT_RELATED_PRODUCT_ORDER_ITEM_NOT_FOUND("SHIPMENT_RELATED_PRODUCT_ORDER_ITEM_NOT_FOUND", "Shipment product order item not found for id {%s}", "shipment product order item not found"),
    PRODUCT_CHARACTERISTIC_NOT_FOUND("PRODUCT_CHARACTERISTIC_NOT_FOUND", "Product characteristic with name {%s} not found for related product id {%s}", "product characteristic not found"),
    SHIPPING_ORDER_ITEM_NOT_FOUND("SHIPPING_ORDER_ITEM_NOT_FOUND", "shipping order item not found for id {%s}", "shipping order item not found"),
    PRODUCT_SPECIFICATION_NOT_ADDED_FOR_RELATED_PRODUCT("PRODUCT_SPECIFICATION_NOT_ADDED_FOR_RELATED_PRODUCT", "Error retrieving product specification for related product in node with isInstallable is %s and RelatedProduct relation is %s", "Can't find product specification for related product"),
    PRODUCT_SPECIFICATION_PREREQUISITES_EXCEPTION("PREREQUISITES_PRODUCT_SPECIFICATION_NOT_SATISFIED", "the required relationship for product Specification with id = {%s} isn't realized by an active product", "All ProductSpecificationRelationships with relationship Type = reliesOn defined in the Product Catalog should be realized by a ProductRelationship on the installed product with relationship Type = reliesOn and the related product should be active"),
    PRODUCT_UNEXPECTED_STATE_EXCEPTION("PRODUCT_UNEXPECTED_STATE_EXCEPTION", "PRODUCT_STATE_NOT_EXPECTED", "Action required is {%s} and the Product to be delivered id {%s} and state {%s} should be {%s}"),
    PRODUCT_UNEXPECTED_OPERATIONAL_STATE_EXCEPTION("PRODUCT_UNEXPECTED_OPERATIONAL_STATE_EXCEPTION", "PRODUCT_OPERATIONAL_STATE_NOT_EXPECTED", "Action required is {%s} and the Product to be delivered id {%s} and operational state {%s} should be {%s}"),
    DELETION_DEPENDENT_PRODUCT_STATE_INVALID("DEPENDENT_PRODUCT_STATE_INVALID", "Dependent Products should have state = Terminated or Cancelled or Aborted", "Dependent Product with id = {%s}, state = {%s} should be Terminated or Cancelled or Aborted"),
    PRODUCT_PREREQUISITE_VALIDATION_EXCEPTION("INVALID_PREREQUISITE_PRODUCT_STATUS", "Prerequisite Products should have state = (Active, Sold)", "Prerequisite Product with id %s and state %s should be Active or Sold"),
    ORCHESTRATION_NODE_DELIVERY_HANDLER_NOT_FOUND_EXCEPTION("DELIVERY_HANDLER_NOT_FOUND_EXCEPTION", "No delivery handler found for OrchestrationPlanNode id: {%s}", "Failed to find a delivery handler"),
    EVENT_DELIVERY_HANDLER_NOT_FOUND_EXCEPTION("DELIVERY_HANDLER_NOT_FOUND_EXCEPTION", "No delivery handler found for event class: {%s}", "Failed to find a delivery handler"),
    INVALID_FALLOUT_INCIDENT_RESOLUTION_STATE("INVALID_FALLOUT_INCIDENT_RESOLUTION_STATE", "Invalid fallout incident resolution state for id {%s}", "Invalid fallout incident resolution state"),
    COOD_DECODING_EXCEPTION("COOD_DECODING_EXCEPTION", "Decoding error: %s", "Error occurred while decoding data"),
    MISSING_RELIES_ON_RELATIONSHIP_IN_MULTI_PURCHASE_SCENARIO("MISSING_RELIES_ON_RELATIONSHIP_IN_MULTI_PURCHASE_SCENARIO", "Order item %s has no relies on relationship", "Missing relationship in Multi-purchase scenario"),
    WRONG_RELIES_ON_RELATIONSHIP_IN_MULTI_PURCHASE_SCENARIO("WRONG_RELIES_ON_RELATIONSHIP_IN_MULTI_PURCHASE_SCENARIO", "Order item %s has wrong relies on relationship with order item %s", "Wrong relationship in Multi-purchase scenario"),
    MISSING_RELIES_ON_RELATIONSHIP_IN_MIGRATE_SCENARIO("MISSING_RELIES_ON_RELATIONSHIP_IN_MIGRATE_SCENARIO", "Order item %s has no relies on relationship", "Missing relationship in Migration scenario"),
    WRONG_RELIES_ON_RELATIONSHIP_IN_MIGRATE_SCENARIO("WRONG_RELIES_ON_RELATIONSHIP_IN_MIGRATE_SCENARIO", "Order item %s has wrong relies on relationship with order item %s", "Wrong relationship in Migrate scenario"),
    PLANS_CHUNK_STATE_CHANGE_EXCEPTION("PLANS_CHUNK_STATE_CHANGE_EXCEPTION", "PLANS_CHUNK_STATE_CHANGE_EXCEPTION", "PLANS_CHUNK_STATE_CHANGE_EXCEPTION"),
    INVALID_ORCHESTRATION_PLAN_NODE_STATE("INVALID_ORCHESTRATION_PLAN_NODE_STATE", "Invalid state for orchestration plan node: %s", "The orchestration plan node is in an invalid state"),
    INVALID_PRODUCT_ACTION("INVALID_PRODUCT_ACTION", "Invalid action [%s] for product with ID [%s]", "The provided product action is not valid"),
    ORCHESTRATION_PLAN_WITH_PRODUCT_ORDER_NOT_FOUND("COOD_ORCHESTRATION_PLAN_NOT_FOUND_EXCEPTION", "No orchestrationPlan found with product order id: %s", "orchestration plan doesn't exist"),
    ORCHESTRATION_PLAN_NODE_STATE_NOT_VALID("INVALID_NODE_STATE_TRANSITION", "Invalid node state transition from state: %s to state: %s", "The orchestration plan node transition is invalid"),
    MIGRATION_RELATED_PRODUCT_IS_MISSING("MIGRATION_RELATED_PRODUCT_IS_MISSING", "Migration related product is missing for node id: %s", "Migration related product is missing"),
    DELIVERY_STRATEGY_NOT_FOUND("DELIVERY_STRATEGY_NOT_FOUND", "No delivery strategy found for delivery mode: %s", "Delivery strategy missing"),
    UNSUPPORTED_DELIVERY_MODE("UNSUPPORTED_DELIVERY_MODE","Unsupported delivery mode %s found in product specification %s with support entity %s and order item id %s", "Unsupported delivery mode");

    private final String code;
    private final String messagePattern;
    private final String reason;

    ExceptionCode(String code, String messagePattern, String reason) {
        this.code = code;
        this.messagePattern = messagePattern;
        this.reason = reason;
    }

    public String getCode() {
        return code.toString();
    }

    public String getReason() {
        return reason;
    }

    public String getMessagePattern() {
        return messagePattern;
    }

    public String formatMessage(String... params) {
        return String.format(messagePattern, (Object[]) params);
    }
}

