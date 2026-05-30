// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.notfounds;

import com.orange.discobole.orderorchestration.exception.model.CoodNotFoundException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;

public class CPIBNotFoundException extends CoodNotFoundException {
    public CPIBNotFoundException(ExceptionCode exceptionCode, Object... parameters) {
        super(exceptionCode, parameters);
    }

    @Override
    public String getMessage() {
        return "CPIB_NOT_REACHABLE | code: {%s} | reason: {%s} | message: {%S}"
                .formatted(coodError.code(), coodError.reason(), coodError.message());
    }
}
