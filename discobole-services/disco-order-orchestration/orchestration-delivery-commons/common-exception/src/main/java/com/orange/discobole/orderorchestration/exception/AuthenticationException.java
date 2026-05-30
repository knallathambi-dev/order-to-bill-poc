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

public class AuthenticationException extends DiscoException {
    private static final HttpStatus STATUS;
    private static final Integer CODE;

    static {
        STATUS = HttpStatus.UNAUTHORIZED;
        CODE = 40;
    }

    public AuthenticationException(String reason) {
        super(reason);
    }

    @Override
    public HttpStatus getStatus() {
        return STATUS;
    }

    @Override
    public Integer getCode() {
        return CODE;
    }
}