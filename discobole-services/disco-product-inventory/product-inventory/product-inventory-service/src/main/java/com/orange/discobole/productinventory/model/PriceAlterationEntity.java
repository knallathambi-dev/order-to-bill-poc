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
import com.orange.discobole.productinventory.dto.v1.MeasuredValue;
import com.orange.discobole.productinventory.dto.v1.Quantity;
import lombok.*;

import java.util.Objects;


/**
 * Is an amount, usually of money, that modifies the price charged for an order item.
 **/

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PriceAlterationEntity {
    private Integer applicationOffset;

    private Quantity applicationDuration;
    private String description;
    private String name;
    private String priceType;
    private Integer priority;
    private MeasuredValue recurringChargePeriod;
    private String unitOfMeasure;
    private PriceEntity price;
    private ProductOfferingPriceEntity productOfferingPrice;
    private TimePeriodEntity validFor;
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
        if (!(o instanceof PriceAlterationEntity that)) {
            return false;
        }
        return Objects.equals(name, that.name) && Objects.equals(priceType, that.priceType) && Objects.equals(priority, that.priority) && Objects.equals(unitOfMeasure, that.unitOfMeasure) && Objects.equals(price, that.price) && Objects.equals(productOfferingPrice, that.productOfferingPrice);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


