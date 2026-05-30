// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.offering.price;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOfferingPrice {
    private String id;
    private String href;
    private String description;
    private Boolean isBundle;
    private OffsetDateTime lastUpdate;
    private ProductOfferingPriceLifecycle lifecycleStatus;
    private String name;
    private Float percentage;
    private ApplicationDuration applicationDuration;
    private Integer priority;
    private PriceType priceType;
    private Boolean immediatePayment;
    private Integer recurringChargePeriodLength;
    private String recurringChargePeriodType;
    private String version;
    private List<ProductOfferingPriceRelationship> popRelationship;
    private Money price;
    private List<TaxItem> tax;
    private Quantity unitOfMeasure;
    private TimePeriod validFor;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonProperty("@type")
    private ProductOfferingPriceType type;
}