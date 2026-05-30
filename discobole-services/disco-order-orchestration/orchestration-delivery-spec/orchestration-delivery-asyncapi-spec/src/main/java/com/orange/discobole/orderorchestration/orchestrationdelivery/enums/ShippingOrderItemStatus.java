// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ShippingOrderItemStatus {
    COMPLETED("completed"),
    HELD("held"),
    FAILED("failed");

    @Getter
    private final String value;

    public static ShippingOrderItemStatus fromValue(String value) {
        for (ShippingOrderItemStatus status : ShippingOrderItemStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException(value);
    }
}
