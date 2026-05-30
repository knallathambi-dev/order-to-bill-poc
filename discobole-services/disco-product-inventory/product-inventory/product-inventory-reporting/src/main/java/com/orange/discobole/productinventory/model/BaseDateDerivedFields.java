// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDate;

@Getter
@Setter
@FieldNameConstants
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseDateDerivedFields {

    private Integer dayOfMonth;

    private Integer isoDayOfWeek;

    private Integer dayOfYear;
    @Indexed
    private LocalDate date;

    /**
     * Computes derived fields based on the `date` field.
     */
    public void computeDerivedFields() {
        if (date != null) {
            this.dayOfMonth = date.getDayOfMonth();
            this.isoDayOfWeek = date.getDayOfWeek().getValue();
            this.dayOfYear = date.getDayOfYear();
        } else {
            this.dayOfMonth = null;
            this.isoDayOfWeek = null;
            this.dayOfYear = null;
        }
    }


}
