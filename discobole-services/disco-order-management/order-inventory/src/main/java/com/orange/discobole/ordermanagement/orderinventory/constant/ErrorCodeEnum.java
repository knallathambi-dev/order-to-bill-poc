// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.orange.discobole.ordermanagement.orderinventory.exception.ProductOrderInventoryException;

public enum ErrorCodeEnum {
    INVALID_INPUT("Invalid body field", 24),
    MISSING_INPUT("Missing body field", 23),
    METHOD_NOT_ALLOWED("Method not allowed", 61),
    INVALID_URL_PARAMETER_VALUE("Invalid URL parameter value", 20),
    INTERNAL_ERROR("Internal error", 21),
    RESOURCE_NOT_FOUND("Resource not found", 60),
    INVALID_QUERY_STRING_PARAMETER("Invalid query-string parameter value", 28),
    MISSING_CREDENTIALS("Missing credentials", 40);

    private final String status;
    private final int code;

    ErrorCodeEnum(String status, int code) {
        this.status = status;
        this.code = code;
    }

    @JsonCreator
    public static ErrorCodeEnum fromStatus(String status) throws ProductOrderInventoryException {
        for (ErrorCodeEnum errorCodeEnum : ErrorCodeEnum.values()) {
            if (errorCodeEnum.status.equalsIgnoreCase(status)) {
                return errorCodeEnum;
            }
        }
        throw new ProductOrderInventoryException("Invalid ErrorCodeEnum status: " + status);
    }

    @JsonCreator
    public static ErrorCodeEnum fromCode(int code) throws ProductOrderInventoryException {
        for (ErrorCodeEnum errorCodeEnum : ErrorCodeEnum.values()) {
            if (errorCodeEnum.code == code) {
                return errorCodeEnum;
            }
        }
        throw new ProductOrderInventoryException("Invalid ErrorCodeEnum code: " + code);
    }

    public String getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }
}