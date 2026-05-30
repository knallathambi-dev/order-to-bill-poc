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

public class CoodNoSessionFoundException extends CoodException {

    public CoodNoSessionFoundException() {

        super(CoodError.builder()
                .message(ExceptionCode.NO_SESSION_FOUND.getMessagePattern())
                .reason(ExceptionCode.NO_SESSION_FOUND.getReason())
                .code(ExceptionCode.NO_SESSION_FOUND.getCode())
                .build());
    }

    @Override
    public String getMessage() {
        return "COOD_NO_SESSION_FOUND_EXCEPTION | code: {%s} | reason: {%s} | message: {%s}".formatted(coodError.code(), coodError.reason(), coodError.message());
    }
}
