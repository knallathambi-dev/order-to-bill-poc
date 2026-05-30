// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.util;

import com.orange.discobole.ordermanagement.orderfollowup.constant.ExceptionMessage;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;

@Slf4j
public class ConstraintValidator {

    private static Validator fieldsValidator;

    static {
        initializeValidators();
    }

    private ConstraintValidator() {
        throw new IllegalStateException(ExceptionMessage.UTILITY_CLASS);
    }

    public static boolean isValid(Object data) {
        if (Objects.isNull(data)) {
            return false;
        }
        Set<ConstraintViolation<Object>> constraintsViolations = getConstraintsViolations(data);
        return constraintsViolations.isEmpty();
    }

    public static Set<ConstraintViolation<Object>> getConstraintsViolations(Object data) {
        return fieldsValidator.validate(data);
    }

    private static void initializeValidators() {
        fieldsValidator = Validation.buildDefaultValidatorFactory()
                .getValidator();
    }
}