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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Getter
@Slf4j
public abstract class CoodValidationException extends CoodException {

    protected CoodValidationException(ExceptionCode exceptionCode, Object... parameters) {
        super(CoodError.builder()
                .code(exceptionCode.getCode())
                .reason(exceptionCode.getReason())
                .message(exceptionCode.getMessagePattern().formatted(parameters)).build());
    }

    public String getMessage() {
        StringBuilder messageBuilder = new StringBuilder().append("Disco Validation Exception | code: {%s} | message: {%s})".formatted(coodError.code(), coodError.message()));
        if (Objects.nonNull(coodError.reason())) {
            messageBuilder.append(" | reason: {%s}".formatted(coodError.reason()));
        }
        return messageBuilder.toString();
    }
}
