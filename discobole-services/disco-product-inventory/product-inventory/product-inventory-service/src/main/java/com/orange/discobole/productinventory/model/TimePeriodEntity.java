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

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * A period of time, either as a deadline (endDateTime only) a startDateTime only, or both
 **/

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimePeriodEntity {
    private OffsetDateTime endDateTime;
    private OffsetDateTime startDateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimePeriodEntity)) {
            return false;
        }
        TimePeriodEntity that = (TimePeriodEntity) o;
        return Objects.equals(endDateTime, that.endDateTime) && Objects.equals(startDateTime, that.startDateTime);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


