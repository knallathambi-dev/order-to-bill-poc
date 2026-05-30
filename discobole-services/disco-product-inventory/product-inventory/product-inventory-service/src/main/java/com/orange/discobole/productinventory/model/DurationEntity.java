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
 * An amount in a given unit
 **/
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DurationEntity {
    private Integer amount;
    private String units;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DurationEntity that)) {
            return false;
        }
        return Integer.compare(that.amount, amount) == 0 && Objects.equals(units, that.units);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


