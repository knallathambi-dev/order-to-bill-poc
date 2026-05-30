// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.exception.model.notfounds;

import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.CoodNotFoundException;

public class OrchestrationPlanNotFoundException extends CoodNotFoundException {

    public OrchestrationPlanNotFoundException(ExceptionCode exceptionCode, Object... parameters) {
        super(exceptionCode, parameters);
    }

    @Override
    public String getMessage() {
        return "COOD_ORCHESTRATION_PLAN_NOT_FOUND_EXCEPTION | code: {%s} | reason: %s | message: {%s}".formatted(coodError.code(), coodError.reason(), coodError.message());
    }
}
