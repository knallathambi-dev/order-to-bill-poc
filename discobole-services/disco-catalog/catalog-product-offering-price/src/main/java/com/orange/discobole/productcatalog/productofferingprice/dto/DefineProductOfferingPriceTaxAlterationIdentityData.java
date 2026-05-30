// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.dto;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Money;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Quantity;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ChargeCycle;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceAlterationType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProrationType;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.ApplicationDuration;

public class DefineProductOfferingPriceTaxAlterationIdentityData {
    private String name;
    private String description;
    private Float percentage;
    private Money price;
    private PriceAlterationType priceAlterationType;

    public DefineProductOfferingPriceTaxAlterationIdentityData name(String name) {
        this.name = name;
        return this;
    }

    public DefineProductOfferingPriceTaxAlterationIdentityData description(String description) {
        this.description = description;
        return this;
    }


    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    public DefineProductOfferingPriceTaxAlterationIdentityData percentage(Float percentage) {
        this.percentage = percentage;
        return this;
    }

    public DefineProductOfferingPriceTaxAlterationIdentityData price(Money price) {
        this.price = price;
        return this;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public PriceAlterationType getPriceAlterationType() {
        return priceAlterationType;
    }

    public void setPriceAlterationType(PriceAlterationType priceAlterationType) {
        this.priceAlterationType = priceAlterationType;
    }

    public DefineProductOfferingPriceTaxAlterationIdentityData priceAlterationType(
            PriceAlterationType priceAlterationType) {
        this.priceAlterationType = priceAlterationType;
        return this;
    }


    @Override
    public String toString() {
        return "DefineProductOfferingPriceAlterationIdentityData [name=" + name + ", description=" + description
                + ", percentage=" + percentage + ", price=" + price
                + ", priceAlterationType=" + priceAlterationType + "]";
    }

}
