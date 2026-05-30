// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RelationshipType {
    BUNDLES("bundles"),
    ISCHILD("isChild"),
    SELLS("sells"),
    ISSOLD("isSold"),
    RELIESON("reliesOn"),
    RELIESFROM("reliesFrom"),
    MIGRATETO("migrateTo"),
    MIGRATEFROM("migrateFrom"),
    REQUIRES("requires");

    private final String value;

    RelationshipType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @JsonCreator
    public static RelationshipType fromValue(String value) {
        for (RelationshipType b : values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}