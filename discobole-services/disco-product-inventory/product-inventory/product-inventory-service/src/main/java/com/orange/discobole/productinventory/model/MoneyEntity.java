// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model;

import lombok.*;

import java.util.Objects;

/**
 * A base / value business entity used to represent money
 **/
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyEntity {
    private String unit;
    private Float value;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MoneyEntity)) {
            return false;
        }
        MoneyEntity that = (MoneyEntity) o;
        return Float.compare(that.value, value) == 0 && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


