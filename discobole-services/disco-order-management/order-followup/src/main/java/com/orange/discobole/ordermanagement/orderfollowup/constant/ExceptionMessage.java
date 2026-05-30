// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.constant;

public class ExceptionMessage {
    public static final String RECEIVED_NULL_RESPONSE_BODY = "Received a null response body";
    public static final String UTILITY_CLASS = "Utility class";
    public static final String INVALID_INPUT = "Invalid  input";
    public static final String PRODUCT_ORDER_MAY_NOT_BE_NULL = "Product order may not be null";
    public static final String PRODUCT_ITEM_MAY_NOT_BE_NULL = "Product item may not be null";
    public static final String PROCESS_FLOW_MAY_NOT_BE_NULL = "ProcessFlow may not be null";
    public static final String NEXT_TASK_TO_BE_PERFORMED_MAY_NOT_BE_NULL = "Next task to be performed may not be null";
    public static final String PRODUCT_ORDER_ITEM_MAY_NOT_BE_NULL = "Product order item may not be null";
    public static final String PRODUCT_ORDER_ID_MAY_NOT_BE_NULL = "Product order id may not be null";
    public static final String NAME_CANNOT_BE_NULL = "Name cannot be null";
    public static final String CHARACTERISTIC_CANNOT_BE_NULL_OR_EMPTY = "%s characteristic cannot be null or empty";
    public static final String ONE_CHARACTERISTIC_IS_MANDATORY = "At least one characteristic is mandatory";
    public static final String MIN_MAX_CARDINALITY = "Minimum %d and maximum %d %s characteristic can be defined";
    public static final String INVALID_CHARACTERISTICS_INPUT = "Invalid characteristics input";
    public static final String INVALID_MESSAGE = "Invalid Message";
    public static final String INVALID_EVENT_TYPE = "Invalid event type";
    public static final String INVALID_EVENT_ID = "Invalid event id";
    public static final String INVALID_EVENT_TIME = "Invalid event time";
    public static final String INVALID_STATE_TYPE = "Invalid product state type";
    public static final String ERROR_UPDATING_PRODUCTS_IN_INVENTORY = "Error occurred while updating products in product inventory";
    public static final String PRODUCTS_UPDATE_FAILED_IN_INVENTORY = "Products update in product inventory cannot be performed";
    public static final String UNABLE_TO_UPDATE_PRODUCTS = "Unable to update products in product inventory";

    private ExceptionMessage() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}