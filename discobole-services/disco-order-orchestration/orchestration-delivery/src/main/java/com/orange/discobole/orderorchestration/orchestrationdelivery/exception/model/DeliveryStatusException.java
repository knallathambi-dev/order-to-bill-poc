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
import lombok.Getter;

@Getter
public class DeliveryStatusException extends CoodException {

    private DeliveryStatusException(CoodError error) {
        super(error);
    }

    @Override
    public String getMessage() {
        return "DeliveryStatusException | code: {%s} | message: {%s} | reason: {%s}"
                .formatted(coodError.code(), coodError.message(), coodError.reason());
    }

    public static DeliveryStatusException of(CoodError coodError) {
        if (coodError == null) {
            throw new IllegalArgumentException("CoodError cannot be null");
        }
        return new DeliveryStatusException(coodError);
    }
}
