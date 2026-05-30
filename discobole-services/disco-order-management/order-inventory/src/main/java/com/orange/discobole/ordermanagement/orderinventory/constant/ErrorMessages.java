// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.constant;

public class ErrorMessages {
    public static final String THE_PRODUCT_ORDER_ID_SHOULD_BE_NULL = "The id of product order should be null";
    public static final String MINIMUM_PRODUCT_ORDER_ITEM_REQUIRED = "There must be at least one product order item";
    public static final String INVALID_CANCELLATION_REASON_CANCELLATION_DATE_ORDER_DATE = "The product order must not have a cancellation date or a reason for cancellation";
    public static final String PRODUCT_ORDER_MAY_NOT_BE_NULL = "productOrder may not be null";
    public static final String PRODUCT_ORDER_ITEM_MAY_NOT_BE_NULL = "productOrderItem may not be null";
    public static final String PRODUCT_ORDER_ID_MAY_NOT_BE_NULL = "productOrder id may not be null";
    public static final String EVENT_TYPE_MAY_NOT_BE_NULL = "eventType id may not be null";
    public static final String YAML_NOT_FOUND = "The requested YAML file is not found";
    public static final String PROCESSING_ERROR = "Unable to process the YAML file";
    public static final String CANNOT_ACCESS_THIS_RESOURCE = "You are not authorized to access this resource (Invalid Related Party Id).";

    private ErrorMessages() {
    }
}