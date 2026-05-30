// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.exception.model;

import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;

public class CoodDecodingException extends CoodException {

    public CoodDecodingException(ExceptionCode exceptionCode, Object... parameters) {
        super(CoodError.builder()
                .code(exceptionCode.getCode())
                .reason(exceptionCode.getReason())
                .message(exceptionCode.getMessagePattern().formatted(parameters)).build());
    }

    @Override
    public String getMessage() {
        return "COOD_DECODING_EXCEPTION | code: {%s} | message: {%s} | reason: {%s}".formatted(coodError.code(), coodError.message(), coodError.reason());
    }
}
