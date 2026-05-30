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

public class CoodTechnicalException extends CoodException {

    public CoodTechnicalException(String message) {
        super(message);
        this.coodError = CoodError.builder()
                .reason(ExceptionCode.COOD_TECHNICAL_EXCEPTION.getReason())
                .message(ExceptionCode.COOD_TECHNICAL_EXCEPTION.getMessagePattern().formatted(message))
                .code(ExceptionCode.COOD_TECHNICAL_EXCEPTION.getCode()).build();
    }

    public CoodTechnicalException(ExceptionCode exceptionCode, Object... parameters) {
        super(CoodError.builder()
                .reason(exceptionCode.getReason())
                .message(exceptionCode.getMessagePattern().formatted(parameters))
                .code(exceptionCode.getCode()).build());

    }

    public String getMessage() {
        return "COOD_TECHNICAL_EXCEPTION | code: {%s} | message: {%s} | reason: {%s}".formatted(coodError.code(), coodError.message(), coodError.reason());
    }
}
