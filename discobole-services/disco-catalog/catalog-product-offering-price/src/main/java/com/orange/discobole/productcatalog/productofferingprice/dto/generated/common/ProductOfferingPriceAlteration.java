/** # SPDX-FileCopyrightText: 2025 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt */

package com.orange.discobole.productcatalog.productofferingprice.dto.generated.common;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProrationType;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.ApplicationDuration;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@Schema(name = "ProductOfferingPriceAlteration", description = "  **Polymorphism**    Parent: ProductOfferingPrice    Discriminator: @type")
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-07-30T16:59:05.795+05:30")
public class ProductOfferingPriceAlteration extends ProductOfferingPrice {
    @JsonProperty("prorationType")
    private ProrationType prorationType;

    @JsonProperty("percentage")
    private Float percentage = null;

    @JsonProperty("applicationDuration")
    private ApplicationDuration applicationDuration=null;

    @JsonProperty("priority")
    private Integer priority=null;

    @JsonProperty("priceType")
    private PriceType priceType = null;

    @JsonProperty("unitOfMeasure")
    private Quantity unitOfMeasure = null;

    @JsonProperty("applicationOffset")
    private Integer applicationOffset = null;

    public ProrationType getProrationType() {
        return prorationType;
    }

    public void setProrationType(ProrationType prorationType) {
        this.prorationType = prorationType;
    }

    @Schema(defaultValue = "Percentage to apply if this Product Offering Price is an Alteration (such as a Discount)")
    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    public ApplicationDuration getApplicationDuration() {
        return applicationDuration;
    }

    public void setApplicationDuration(ApplicationDuration applicationDuration) {
        this.applicationDuration = applicationDuration;
    }

    @Schema(defaultValue = "An amount in a given unit")
    public Quantity getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(Quantity unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    @Schema(defaultValue = "A category that describes the price, such as recurring, discount, allowance, penalty, and so forth")
    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }
    public ProductOfferingPriceAlteration priceType(PriceType priceType) {
        this.priceType = priceType;
        return this;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Schema(defaultValue = "Indicates the time period after which the discount is applied after # of recurring instances.")
    public Integer getApplicationOffset() {
        return applicationOffset;
    }

    public void setApplicationOffset(Integer applicationOffset) {
        this.applicationOffset = applicationOffset;
    }


    @Override
    public String toString() {
        return "ProductOfferingPriceAlteration{" +
                "prorationType=" + prorationType +
                ", percentage=" + percentage +
                ", applicationDuration=" + applicationDuration +
                ", priority=" + priority +
                ", priceType=" + priceType +
                ", unitOfMeasure=" + unitOfMeasure +
                ", applicationOffset=" + applicationOffset +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductOfferingPriceAlteration that = (ProductOfferingPriceAlteration) o;
        return prorationType == that.prorationType && Objects.equals(percentage, that.percentage) && Objects.equals(applicationDuration, that.applicationDuration) && Objects.equals(priority, that.priority) && priceType == that.priceType && Objects.equals(unitOfMeasure, that.unitOfMeasure) && Objects.equals(applicationOffset, that.applicationOffset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prorationType, percentage, applicationDuration, priority, priceType, unitOfMeasure, applicationOffset);
    }

}
