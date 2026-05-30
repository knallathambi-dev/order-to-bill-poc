// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.constant;

public class ExceptionMessage {
    public static final String ERROR_GETTING_RELATED_RESOURCES_ID_S = "Error getting related resources id(s)";
    public static final String CURRENT_THREAD_INTERRUPTED = "The current thread was interrupted";
    public static final String INVALID_PRODUCT_ORDER_ID = "Invalid product order id ";
    public static final String NAME_CANNOT_BE_NULL = "Name cannot be null";
    public static final String CHARACTERISTIC_CANNOT_BE_NULL_OR_EMPTY = "%s characteristic cannot be null or empty";
    public static final String ONE_CHARACTERISTIC_IS_MANDATORY = "At least one characteristic is mandatory";
    public static final String MIN_MAX_CARDINALITY = "Minimum %d and maximum %d %s characteristic can be defined";
    public static final String INVALID_CHARACTERISTICS_INPUT = "Invalid characteristics input";
    public static final String INVALID_INPUT = "Invalid  input";
    public static final String INVALID_PRODUCT_CONFIGURATION_ID = "Invalid product configuration ID";
    public static final String INVALID_RESOURCE_PARAMETERS = "Invalid resource parameters";
    public static final String INVALID_PARTY_ROLE_PARAMETERS = "Invalid party role id parameters";
    public static final String INVALID_PRODUCT_ID = "Invalid product id";
    public static final String INVALID_PRODUCT_OFFERING_ID = "Invalid product offering id";
    public static final String INVALID_PRODUCT_SPECIFICATION_PARAMETERS = "Invalid product specification parameters";
    public static final String INVALID_PRODUCT_OFFERING_PRICE_PARAMETERS = "Invalid product offering price parameters";
    public static final String INVALID_PRODUCT_ID_PARAMETERS = "Invalid product id parameters";
    public static final String UTILITY_CLASS = "Utility class";
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
    public static final String ERROR_CONFIRMING_PRODUCTS = "Error confirming products";
    public static final String ERROR_CANCELLING_PRODUCTS = "Error cancelling products";
    public static final String ERROR_ABORTING_PRODUCTS = "Error aborting products";
    public static final String ERROR_TERMINATING_PRODUCTS = "Error terminating products";
    public static final String ERROR_UPDATING_PRODUCTS = "Error updating products";
    public static final String PARTY_ROLES_CANNOT_BE_FOUND = "Party roles cannot be found";
    public static final String PRODUCT_OFFERING_PRICES_NOT_FOUND = "Product offering prices cannot be found";
    public static final String ERROR_WHILE_ROLLING_BACK_RESERVED_RESOURCE = "Error while rolling back reserved resource";
    public static final String INVALID_PAYMENT_ID = "Invalid payment id";
    public static final String INVALID_BILLING_ACCOUNT_ID = "Invalid billing account id";
    public static final String INVALID_RELATED_PARTY_ID = "Invalid related party id";
    public static final String PRODUCT_INVENTORY_SERVICE_UNREACHABLE = "The product inventory service is currently unreachable, please try later";
    public static final String SERVICE_QUALIFICATION_IS_NULL = "Service qualification is null";
    public static final String INVALID_APPOINTMENT_ID = "Invalid appointment id";
    public static final String UNABLE_TO_UPDATE_PRODUCTS = "Unable to update products in product inventory";
    public static final String INVALID_CURRENCY_CODE = "Invalid Currency code";


    private ExceptionMessage() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}