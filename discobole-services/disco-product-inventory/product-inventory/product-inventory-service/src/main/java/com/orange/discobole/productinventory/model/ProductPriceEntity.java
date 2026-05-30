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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceEntity {
    private String description;
    private String name;
    private String priceType;
    private MeasuredValue recurringChargePeriod;
    private String unitOfMeasure;
    private TimePeriodEntity validFor;
    private BillingAccountRefEntity billingAccount;
    private PriceEntity price = null;
    private ProductOfferingPriceEntity productOfferingPrice;
    private List<PriceAlterationEntity> productPriceAlteration = new ArrayList<>();
    private Quantity applicationDuration;
    //TODO referredType or baseType should only be "BillingAccount"
    private String baseType;
    @JsonProperty("@type")
    @JsonAlias("atType")
    private String atType;
    private Float interestRate;
    private Float downPayment;
    private String partner;
    private String externalId;
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductPriceEntity that)) {
            return false;
        }
        return Objects.equals(priceType, that.priceType);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
