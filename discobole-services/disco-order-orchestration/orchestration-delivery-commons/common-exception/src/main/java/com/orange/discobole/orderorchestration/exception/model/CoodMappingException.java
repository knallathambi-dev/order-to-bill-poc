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

public class CoodMappingException extends CoodException {

    protected final Class<?> entityName;
    protected final Class<?> targetEntityName;

    public CoodMappingException(ExceptionCode exceptionCode, Class<?> entityName, Class<?> targetEntityName, Object... parameters) {
        super(CoodError.builder()
                .code(exceptionCode.getCode())
                .message(exceptionCode.getMessagePattern().formatted(parameters)).reason(exceptionCode.getReason()).build());
        this.entityName = entityName;
        this.targetEntityName = targetEntityName;
    }

    public String getMessage() {
        return "COOD_MAPPING_EXCEPTION | entityName: {%s} | targetEntityName: {%s} | message: {%s} | reason: {%s} | code: {%s}".formatted(entityName.getName(), targetEntityName.getName(),
                super.coodError.message(), super.coodError.reason(), super.coodError.code());
    }
}
