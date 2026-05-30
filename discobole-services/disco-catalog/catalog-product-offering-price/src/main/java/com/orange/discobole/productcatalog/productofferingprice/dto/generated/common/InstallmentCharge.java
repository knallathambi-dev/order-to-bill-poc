/**  SPDX-FileCopyrightText: 2025 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt */

package com.orange.discobole.productcatalog.productofferingprice.dto.generated.common;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Objects;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@Schema(name = "InstallmentCharge", description = "  **Polymorphism**    Parent: ProductOfferingPrice    Discriminator: @type")
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-07-30T16:59:05.795+05:30")
public class InstallmentCharge extends  ProductOfferingPrice{

    @JsonProperty("interestRate")
    private Float interestRate = null;

    @NotBlank
    @JsonProperty("applicationDuration")
    private Quantity applicationDuration = null;

    @JsonProperty("downPayment")
    private Float downPayment = null;

    @NotBlank
    @JsonProperty("partner")
    private String partner = "Orange";

    @JsonProperty("externalId")
    private String externalId = null;

    @Valid
    @JsonProperty("popRelationship")
    private List<ProductOfferingPriceRelationship> popRelationship = null;


    @Schema(defaultValue = "Percentage interest rate (e.g., 5 for 5%)")
    public Float getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Float interestRate) {
        this.interestRate = interestRate;
    }

    public Quantity getApplicationDuration() {
        return applicationDuration;
    }

    public void setApplicationDuration(Quantity applicationDuration) {
        this.applicationDuration = applicationDuration;
    }

    @Schema(defaultValue = "Down payment amount in monetary value")
    public Float getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(Float downPayment) {
        this.downPayment = downPayment;
    }

    @Schema(defaultValue = "Partner name; defaults to \"Orange\" if null")
    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    @Schema(defaultValue = "External ID for mapping the payment plan")
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
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
        return "InstallmentCharge{" +
                "interestRate=" + interestRate +
                ", applicationDuration=" + applicationDuration +
                ", downPayment=" + downPayment +
                ", partner='" + partner + '\'' +
                ", externalId='" + externalId + '\'' +
                ", popRelationship=" + popRelationship +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InstallmentCharge that = (InstallmentCharge) o;
        return Objects.equals(interestRate, that.interestRate) && Objects.equals(applicationDuration, that.applicationDuration) && Objects.equals(downPayment, that.downPayment) && Objects.equals(partner, that.partner) && Objects.equals(externalId, that.externalId) && Objects.equals(popRelationship, that.popRelationship);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interestRate, applicationDuration, downPayment, partner, externalId, popRelationship);
    }

}
