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
import lombok.Getter;

@Getter
public class PlanApiQueryParamException extends CoodValidationException {
    private String message;
    private String code;

    public PlanApiQueryParamException(ExceptionCode exceptionCode, Integer code, Object... parameters) {
        super(exceptionCode, parameters);
        this.message = exceptionCode.getMessagePattern().formatted(parameters);
        this.code = String.valueOf(code);
    }

}
