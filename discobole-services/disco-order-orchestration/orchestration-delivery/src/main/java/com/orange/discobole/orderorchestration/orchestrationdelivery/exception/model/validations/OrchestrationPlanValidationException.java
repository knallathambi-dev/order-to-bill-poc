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
import lombok.Getter;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class OrchestrationPlanValidationException extends CoodValidationException {

    private Set<ConstraintViolation<?>> violations = null;
    @Getter
    private Integer httpCode;

    public OrchestrationPlanValidationException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public OrchestrationPlanValidationException(ExceptionCode exceptionCode, Object... parameter) {
        super(exceptionCode, parameter);
    }

    public OrchestrationPlanValidationException(ExceptionCode exceptionCode, Integer code, Object... parameter) {
        super(exceptionCode, parameter);
        this.httpCode = code;
    }


    public <T> OrchestrationPlanValidationException(ExceptionCode exceptionCode, Set<ConstraintViolation<T>> violations, Object... parameter) {
        super(exceptionCode, parameter);
        this.violations = Collections.unmodifiableSet(violations);
    }

    @Override
    public String getMessage() {
        StringBuilder messageBuilder = new StringBuilder("COOD_ORCHESTRATION_PLAN_VALIDATION_EXCEPTION | code: {%s} | message: {%s} | reason: {%s}".formatted(
                coodError.code(), coodError.message(), coodError.reason()));
        if (Objects.nonNull(violations) && violations.isEmpty()) {
            messageBuilder.append(violations);
        }
        return messageBuilder.toString();
    }
}
