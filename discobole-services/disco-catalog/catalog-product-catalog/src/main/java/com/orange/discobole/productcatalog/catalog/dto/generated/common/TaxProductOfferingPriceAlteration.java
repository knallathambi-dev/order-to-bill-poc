// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.dto.generated.common;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.TypeAlias;



@TypeAlias("TaxProductOfferingPriceAlteration")
@JsonTypeName("TaxProductOfferingPriceAlteration")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "TaxProductOfferingPriceAlteration",
        description = "**Polymorphism** Parent: ProductOfferingPrice Discriminator: @type"
)


public class TaxProductOfferingPriceAlteration extends ProductOfferingPrice {
    @JsonProperty("percentage")
    private Float percentage = null;

    @JsonProperty("unitOfMeasure")
    private Quantity unitOfMeasure = null;

    @Schema(defaultValue = "Percentage to apply if this Product Offering Price is an Alteration (such as a Discount)")
    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }


    public TaxProductOfferingPriceAlteration percentage(Float percentage) {
        this.percentage = percentage;
        return this;
    }

    @Schema(defaultValue = "An amount in a given unit")
    public Quantity getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(Quantity unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    @Override
    public String toString() {
        return "TaxProductOfferingPriceAlteration{" +
                "percentage=" + percentage +
                ", unitOfMeasure=" + unitOfMeasure +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TaxProductOfferingPriceAlteration that = (TaxProductOfferingPriceAlteration) o;
        return Objects.equals(percentage, that.percentage) && Objects.equals(unitOfMeasure, that.unitOfMeasure) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(percentage, unitOfMeasure);
    }
}
