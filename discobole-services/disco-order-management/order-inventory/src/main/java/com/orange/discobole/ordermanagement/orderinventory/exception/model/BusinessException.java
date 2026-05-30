// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.exception.model;

public class BusinessException extends RuntimeException {
    public static final String NOT_INCLUDED_IN_PRODUCT_ORDER_FIELDS = "%s not included in product order fields";
    public static final String SUPPLEMENT_SPACES_CANNOT_BE_INCLUDED_ON_FIELDS = "Supplement spaces cannot be included on fields";
    public static final String OFFSET_AND_LIMIT_SHOULD_NOT_BE_NEGATIVE = "offset and limit should not be negative";
    public static final String OFFSET_SHOULD_NOT_BE_NEGATIVE = "offset should not be negative";
    public static final String LIMIT_SHOULD_NOT_BE_NEGATIVE = "limit should not be negative";
    public static final String ERROR_IN_OFFSET = "Offset should not be greater than the total count of product orders in result";
    public static final String VALUE_IS_NOT_A_VALID_INTEGER = "%s value is not a valid integer";
    public static final String THE_VALUE_OF_THE_KEY_SHOULD_NOT_BE_NULL = "The value of the key %s should not be null";
    public static final String CANNOT_PARSE_FIELD_VALUE_TO_TYPE = "Cannot parse field value: %s to type: %s";
    public static final String DUPLICATE_SORT_PARAMETER = "Duplicate sort parameter could not be included in the sorting parameter list";
    public static final String INVALID_DATE_FORMAT = "Invalid date format for key %s : %s";
    public static final String NOT_BE_EMPTY = " must not be empty";
    public static final String UNSUPPORTED_FILTER = "Unsupported filter parameter: ";

    private BusinessException() {
    }
}