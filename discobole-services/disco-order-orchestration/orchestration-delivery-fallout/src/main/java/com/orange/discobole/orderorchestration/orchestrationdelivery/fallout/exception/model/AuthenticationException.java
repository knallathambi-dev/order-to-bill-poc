// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants.ExceptionCode;
import org.springframework.http.HttpStatus;

public class AuthenticationException extends FalloutException {
    private static final HttpStatus STATUS;

    static {
        STATUS = HttpStatus.UNAUTHORIZED;
    }

    public AuthenticationException(String message) {
        super(message);
        this.code = ExceptionCode.FALLOUT_SECURITY_EXCEPTION;
    }

    public HttpStatus getStatus() {
        return STATUS;
    }

    @Override
    public String getLogMessage() {
        return "AUTHENTICATION EXCEPTION | message: %s".formatted(getMessage());
    }
}