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
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DuplicateOrchestrationPlanException extends CoodException {

    public DuplicateOrchestrationPlanException(ExceptionCode exceptionCode, String planId) {
        super(CoodError.builder()
                .message(exceptionCode.getMessagePattern().formatted(planId))
                .reason(exceptionCode.getReason())
                .code(exceptionCode.getCode()).build());
    }

    @Override
    public String getMessage() {
        return coodError.message();
    }
}
