// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations;

import com.orange.discobole.orderorchestration.exception.model.CoodValidationException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import jakarta.validation.ConstraintViolation;

import java.util.Objects;
import java.util.Set;

public class ProductOrderItemValidationException extends CoodValidationException {
    private Set<ConstraintViolation<?>> violations = null;

    public ProductOrderItemValidationException(ExceptionCode exceptionCode, Object... parameters) {
        super(exceptionCode, parameters);
    }

    public ProductOrderItemValidationException(ExceptionCode exceptionCode, Set<ConstraintViolation<?>> violations, Object... parameters) {
        super(exceptionCode, parameters);
        this.violations = violations;
    }

    @Override
    public String getMessage() {
        StringBuilder messageBuilder = new StringBuilder("COOD_PRODUCT_ORDER_VALIDATION_EXCEPTION | code: {%s} | message: {%s} | reason: {%s}".formatted(coodError.code(), coodError.message(), coodError.reason()));
        if (Objects.nonNull(violations) && violations.isEmpty()) {
            messageBuilder.append(violations);
        }
        return messageBuilder.toString();
    }
}
