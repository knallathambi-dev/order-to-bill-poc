// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RelatedEntityRole {
    INITIATOR("initiator"),
    RELATED_ORCHESTRATION_PLAN("relatedOrchestrationPlan"),
    RELATED_PRODUCT_ORDER("relatedProductOrder");

    @JsonCreator
    public static RelatedEntityRole fromValue(String text) {
        for (RelatedEntityRole b : RelatedEntityRole.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    private final String value;

    @Override
    @JsonValue
    public String toString() {
        return this.value;
    }

    public String value() {
        return value;
    }
}
