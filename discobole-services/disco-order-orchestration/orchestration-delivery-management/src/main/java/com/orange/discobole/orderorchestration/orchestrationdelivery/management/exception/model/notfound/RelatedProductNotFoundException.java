// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.exception.model.notfound;

import com.orange.discobole.orderorchestration.exception.model.CoodNotFoundException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.management.constant.ExceptionMessage.CODE_REASON_MESSAGE_FORMATING;

public class RelatedProductNotFoundException extends CoodNotFoundException {
    public RelatedProductNotFoundException(ExceptionCode exceptionCode, Object... parameters) {
        super(exceptionCode, parameters);
    }

    @Override
    public String getMessage() {
        return "RELATED_PRODUCT_NOT_FOUND_EXCEPTION".concat(CODE_REASON_MESSAGE_FORMATING)
                .formatted(coodError.code(), coodError.reason(), coodError.message());
    }
}
