// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Objects;


/**
 * Provides all amounts (tax included, duty free, tax rate), used currency and percentage to apply for Price Alteration.
 **/
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PriceEntity {
    private Float percentage;
    private Float taxRate;
    private MoneyEntity dutyFreeAmount;
    private MoneyEntity taxIncludedAmount;
    private String baseType;
    private String schemaLocation;
    @JsonProperty("@type")
    @JsonAlias("atType")
    private String atType;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PriceEntity that)) {
            return false;
        }
        return Float.compare(that.percentage, percentage) == 0 && Float.compare(that.taxRate, taxRate) == 0 && Objects.equals(dutyFreeAmount, that.dutyFreeAmount) && Objects.equals(taxIncludedAmount, that.taxIncludedAmount);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
