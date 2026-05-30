// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.exception.model;

import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import org.springframework.http.HttpStatus;

public class CoodAuthenticationException extends CoodException {
    private static final HttpStatus STATUS;

    static {
        STATUS = HttpStatus.UNAUTHORIZED;
    }

    public CoodAuthenticationException(String message) {
        super(CoodError.builder()
                .message(message)
                .reason(ExceptionCode.COOD_SECURITY_EXCEPTION.getReason())
                .code(ExceptionCode.COOD_SECURITY_EXCEPTION.getCode()).build());
    }

    public HttpStatus getStatus() {
        return STATUS;
    }

    @Override
    public String getMessage() {
        return "AUTHENTICATION EXCEPTION | code: {%s} | reason: {%s} | message: %s".formatted(coodError.code(), coodError.reason(), coodError.message());
    }
}