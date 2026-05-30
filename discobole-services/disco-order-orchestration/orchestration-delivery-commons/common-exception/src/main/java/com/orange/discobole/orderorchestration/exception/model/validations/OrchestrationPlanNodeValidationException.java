// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.exception.model.validations;


import com.orange.discobole.orderorchestration.exception.model.CoodValidationException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;

public class OrchestrationPlanNodeValidationException extends CoodValidationException {

    public OrchestrationPlanNodeValidationException(ExceptionCode exceptionCode, Object... parameters) {
        super(exceptionCode, parameters);
    }

    @Override
    public String getMessage() {
        return new StringBuilder("COOD_PRODUCT_VALIDATION_EXCEPTION | code: {%s} | message: {%s} | reason: {%s}".formatted(coodError.code(), coodError.message(), coodError.reason())).toString();
    }
}