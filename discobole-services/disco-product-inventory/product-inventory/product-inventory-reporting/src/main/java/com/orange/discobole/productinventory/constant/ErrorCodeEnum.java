// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.orange.discobole.productinventory.exception.ReportingException;

public enum ErrorCodeEnum {
    // ErrorCodes according to orange recommendations https://developer.orange.com/common-errors

    INVALID_URL_PARAMETER_VALUE("Invalid URL parameter value", 20), INTERNAL_ERROR("Internal error", 21), INVALID_BODY("Invalid body", 22),
    MISSING_INPUT("Missing body field", 23), INVALID_INPUT("Invalid body field", 24), MISSING_REQUEST_PARAMETER("Missing request parameter", 25), INVALID_HEADER_VALUE("Invalid header value", 26), MISSING_QUERY_STRING_PARAMETER("Missing query-string parameter", 27), INVALID_QUERY_STRING_PARAMETER("Invalid query-string parameter value", 28), MISSING_CREDENTIALS("Missing credentials", 40), ACCESS_DENIED("Access denied", 50), RESOURCE_NOT_FOUND("Resource not found", 60),
    METHOD_NOT_ALLOWED("Method not allowed", 61), CONFLICT("Conflict", 69);

    private final String status;
    private final int code;


    ErrorCodeEnum(String status, int code) {
        this.status = status;
        this.code = code;
    }

    @JsonCreator
    public static ErrorCodeEnum fromStatus(String status) throws ReportingException {
        for (ErrorCodeEnum errorCodeEnum : ErrorCodeEnum.values()) {
            if (errorCodeEnum.status.equalsIgnoreCase(status)) {
                return errorCodeEnum;
            }
        }
        throw new ReportingException("Invalid ErrorCodeEnum status: " + status);
    }


    @JsonCreator
    public static ErrorCodeEnum fromCode(int code) throws ReportingException {
        for (ErrorCodeEnum errorCodeEnum : ErrorCodeEnum.values()) {
            if (errorCodeEnum.code == code) {
                return errorCodeEnum;
            }
        }
        throw new ReportingException("Invalid ErrorCodeEnum code: " + code);
    }


    public String getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }
}



