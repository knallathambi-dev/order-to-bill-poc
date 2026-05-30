// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.exception;

import org.springframework.http.HttpStatus;

public class DiscoException extends RuntimeException {
    private static final HttpStatus STATUS;
    private static final Integer CODE;

    static {
        STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
        CODE = 1;
    }

    private final String reason;

    public DiscoException() {
        this.reason = "unknown";
    }

    public DiscoException(String reason) {
        super(reason);
        this.reason = reason;
    }

    public DiscoException(String reason, Throwable cause) {
        super(reason, cause);
        this.reason = reason;
    }

    public HttpStatus getStatus() {
        return STATUS;
    }

    public Integer getCode() {
        return CODE;
    }

    public String getReason() {
        return this.reason;
    }
}