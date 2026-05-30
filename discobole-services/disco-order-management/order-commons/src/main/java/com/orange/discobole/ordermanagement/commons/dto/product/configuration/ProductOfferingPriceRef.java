// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOfferingPriceRef {
    private String id;
    private String href;
    private String name;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonProperty("@type")
    private String type;
    @JsonProperty("@referredType")
    private String referredType;
    private Boolean immediatePayment;
    private String version;
    private Integer recurringChargePeriodLength;
    private String recurringChargePeriodType;
    private Quantity applicationDuration;
    private Boolean isBundle;
    private String lastUpdate;
    private Float percentage;
    private Integer duration;
    private Integer priority;
    private PriceType priceType;
    private Money price;
    private Quantity unitOfMeasure;
    private String description;
    private List<ProductOfferingPriceRelationship> popRelationship;
    private List<TaxItem> tax;
    private Float interestRate;
    private Float downPayment;
    private String partner;
    private String externalId;
}