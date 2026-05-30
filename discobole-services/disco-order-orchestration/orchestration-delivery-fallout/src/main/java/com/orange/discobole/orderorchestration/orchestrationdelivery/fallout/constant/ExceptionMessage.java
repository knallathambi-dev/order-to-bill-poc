// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.constant;


public class ExceptionMessage {

    public static final String ERROR_GETTING_RELATED_RESOURCES_ID_S = "Error getting related resources id(s)";
    public static final String CURRENT_THREAD_INTERRUPTED = "The current thread was interrupted";
    public static final String INVALID_PRODUCT_ORDER_ID = "Invalid productOrder id ";
    public static final String NAME_CANNOT_BE_NULL = "Name cannot be null";
    public static final String CHARACTERISTIC_CANNOT_BE_NULL_OR_EMPTY = "%s characteristic cannot be null or empty";
    public static final String ONE_CHARACTERISTIC_IS_MANDATORY = "At least one characteristic is mandatory";
    public static final String MIN_MAX_CARDINALITY = "Minimum %d and maximum %d %s characteristic can be defined";
    public static final String INVALID_CHARACTERISTICS_INPUT = "Invalid characteristics input";
    public static final String INVALID_INPUT = "Invalid  input";
    public static final String INVALID_SESSION_CONFIGURATION_SESSION_PARAMETERS = "Invalid session configuration parameters";
    public static final String INVALID_RESOURCE_PARAMETERS = "Invalid resource parameters";
    public static final String INVALID_PARTY_ROLE_PARAMETERS = "Invalid party role id parameters";
    public static final String INVALID_PRODUCT_ID = "Invalid product id";
    public static final String INVALID_PRODUCT_SPECIFICATION_PARAMETERS = "Invalid product specification parameters";
    public static final String UTILITY_CLASS = "Utility class";
    public static final String COULD_NOT_CHECK_PRODUCT_AVAILABILITY_IN_THE_INVENTORY = "Could not check product availability in the inventory";
    public static final String PRODUCT_ORDER_MAY_NOT_BE_NULL = "Product order may not be null";
    public static final String PRODUCT_ORDER_ITEM_MAY_NOT_BE_NULL = "Product order item may not be null";
    public static final String PRODUCT_ORDER_STATE_MAY_NOT_BE_NULL = "Product order state may not be null";
    public static final String PRODUCT_ORDER_ID_MAY_NOT_BE_NULL = "Product order id may not be null";
    public static final String EVENT_TYPE_MAY_NOT_BE_NULL = "Event type id may not be null";
    public static final String INVALID_PRODUCT_LIST = "Invalid product list";
    public static final String INVALID_PRODUCT_STOCK = "Invalid product stock";
    public static final String PARTY_ROLE_MAY_NOT_BE_NULL = "Party may not be null";
    public static final String PRODUCT_SPECIFICATION_CANNOT_BE_FOUND = "Product specification cannot be found";
    public static final String ERROR_PROCESSING_RELATED_RESOURCES_ID_S = "Error processing related resources id(s) [{}]:";

    private ExceptionMessage() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}