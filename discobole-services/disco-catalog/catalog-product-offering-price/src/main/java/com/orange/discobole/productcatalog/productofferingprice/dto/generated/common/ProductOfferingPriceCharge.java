/** # SPDX-FileCopyrightText: 2025 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt */

package com.orange.discobole.productcatalog.productofferingprice.dto.generated.common;


import com.fasterxml.jackson.annotation.JsonProperty;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ChargeCycle;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProrationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Objects;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@Schema(name = "ProductOfferingPriceCharge", description = "  **Polymorphism**    Parent: ProductOfferingPrice    Discriminator: @type")
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-07-30T16:59:05.795+05:30")
public class ProductOfferingPriceCharge  extends ProductOfferingPrice {

    @JsonProperty("priceType")
    private PriceType priceType = null;

    @JsonProperty("immediatePayment")
    private Boolean immediatePayment = null;

    @JsonProperty("recurringChargePeriodLength")
    private Integer recurringChargePeriodLength = null;

    @JsonProperty("recurringChargePeriodType")
    private String recurringChargePeriodType = null;

    @Valid
    @JsonProperty("unitOfMeasure")
    private Quantity unitOfMeasure = null;

    @JsonProperty("prorationType")
    private ProrationType prorationType;

    @JsonProperty("chargeCycle")
    private ChargeCycle chargeCycle = null;

    @Valid
    @JsonProperty("popRelationship")
    private List<ProductOfferingPriceRelationship> popRelationship = null;

    @Schema(defaultValue = "A category that describes the price, such as recurring, discount, allowance, penalty, and so forth")
    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    @Schema(defaultValue = "")
    public Boolean getImmediatePayment() {
        return immediatePayment;
    }

    public void setImmediatePayment(Boolean immediatePayment) {
        this.immediatePayment = immediatePayment;
    }

    @Schema(defaultValue = "the period of the recurring charge: 1, 2, ... .It sets to zero if it is not applicable")
    public Integer getRecurringChargePeriodLength() {
        return recurringChargePeriodLength;
    }

    public void setRecurringChargePeriodLength(Integer recurringChargePeriodLength) {
        this.recurringChargePeriodLength = recurringChargePeriodLength;
    }

    @Schema(defaultValue = "The period to repeat the application of the price Could be month, week...")
    public String getRecurringChargePeriodType() {
        return recurringChargePeriodType;
    }

    public void setRecurringChargePeriodType(String recurringChargePeriodType) {
        this.recurringChargePeriodType = recurringChargePeriodType;
    }

    @Schema(defaultValue = "An amount in a given unit")
    public Quantity getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(Quantity unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public ProrationType getProrationType() {
        return prorationType;
    }

    public void setProrationType(ProrationType prorationType) {
        this.prorationType = prorationType;
    }

    public ChargeCycle getChargeCycle() {
        return chargeCycle;
    }

    public void setChargeCycle(ChargeCycle chargeCycle) {
        this.chargeCycle = chargeCycle;
    }

    @Schema(defaultValue = "Product Offering Prices related to this Product Offering Price, for example a price alteration such as allowance or discount")
    public List<ProductOfferingPriceRelationship> getPopRelationship() {
        return popRelationship;
    }

    public void setPopRelationship(List<ProductOfferingPriceRelationship> popRelationship) {
        this.popRelationship = popRelationship;
    }

    @Override
    public String toString() {
        return "ProductOfferingPriceCharge{" +
                "priceType=" + priceType +
                ", immediatePayment=" + immediatePayment +
                ", recurringChargePeriodLength=" + recurringChargePeriodLength +
                ", recurringChargePeriodType='" + recurringChargePeriodType + '\'' +
                ", unitOfMeasure=" + unitOfMeasure +
                ", prorationType=" + prorationType +
                ", chargeCycle=" + chargeCycle +
                ", popRelationship=" + popRelationship +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductOfferingPriceCharge that = (ProductOfferingPriceCharge) o;
        return priceType == that.priceType && Objects.equals(immediatePayment, that.immediatePayment) && Objects.equals(recurringChargePeriodLength, that.recurringChargePeriodLength) && Objects.equals(recurringChargePeriodType, that.recurringChargePeriodType) && Objects.equals(unitOfMeasure, that.unitOfMeasure) && prorationType == that.prorationType && chargeCycle == that.chargeCycle && Objects.equals(popRelationship, that.popRelationship);
    }

    @Override
    public int hashCode() {
        return Objects.hash(priceType, immediatePayment, recurringChargePeriodLength, recurringChargePeriodType, unitOfMeasure, prorationType, chargeCycle, popRelationship);
    }


}
