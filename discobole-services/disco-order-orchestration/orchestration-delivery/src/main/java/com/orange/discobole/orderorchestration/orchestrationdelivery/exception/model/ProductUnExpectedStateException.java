// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model;

import com.orange.discobole.orderorchestration.exception.model.CoodException;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductUnExpectedStateException extends CoodException {

    public ProductUnExpectedStateException(ExceptionCode exceptionCode, Object... parameters) {
        super(CoodError.builder().reason(exceptionCode.getReason().formatted(parameters)).message(exceptionCode.getMessagePattern()).code(exceptionCode.getCode()).build());
    }

    public String getMessage() {
        return "COOD_UNEXPECTED_STATE_EXCEPTION | code: {%s} | reason: {%s} | message: {%s}".formatted(this.coodError.code(), this.coodError.reason(), this.coodError.message());
    }
}
