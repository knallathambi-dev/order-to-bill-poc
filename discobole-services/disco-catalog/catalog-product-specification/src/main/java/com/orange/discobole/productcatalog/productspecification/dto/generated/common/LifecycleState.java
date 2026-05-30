// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.dto.generated.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LifecycleState {

    INSTUDY("inStudy"),

    INDESIGN("inDesign"),

    REJECTED("rejected"),

    INTEST("inTest"),

    ACTIVE("active"),

    LAUNCHED("launched"),

    UNAVAILABLE("unavailable"),

    RETIRED("retired"),

    OBSOLETE("obsolete");

    private final String value;

    LifecycleState(String value) {
        this.value = value;
    }

    @JsonCreator
    public static LifecycleState fromValue(String text) {
        for (LifecycleState b : LifecycleState.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
