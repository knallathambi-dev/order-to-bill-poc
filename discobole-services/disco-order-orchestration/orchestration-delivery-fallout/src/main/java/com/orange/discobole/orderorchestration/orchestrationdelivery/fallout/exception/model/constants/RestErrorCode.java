// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum RestErrorCode {

    FALLOUT_NOT_FOUND_REST_EXCEPTION(60, "Resource not found", HttpStatus.NOT_FOUND),
    FALLOUT_INTERNAL_SERVER_EXCEPTION(1, "Internal error", HttpStatus.INTERNAL_SERVER_ERROR),
    FALLOUT_SECURITY_EXCEPTION_MISSING_CREDENTIALS(40, "Missing credentials", HttpStatus.UNAUTHORIZED),
    FALLOUT_SECURITY_EXCEPTION_INVALID_CREDENTIALS(41, "Invalid credentials", HttpStatus.UNAUTHORIZED),
    FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION(28, "Invalid query-string parameter value", HttpStatus.BAD_REQUEST);
    @Getter
    private final int errorCode;
    @Getter
    private final String message;
    @Getter
    private final HttpStatus httpStatus;

    RestErrorCode(int errorCode, String message, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.message = message;
        this.httpStatus = httpStatus;
    }


    @Override
    public String toString() {
        return String.valueOf(errorCode);
    }
}
