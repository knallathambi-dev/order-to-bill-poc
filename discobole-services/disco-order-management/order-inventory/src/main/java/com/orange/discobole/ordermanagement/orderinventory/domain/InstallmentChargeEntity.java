// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.domain;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PriceType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.net.URI;
import java.util.List;

@SuperBuilder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class InstallmentChargeEntity extends ProductOfferingPriceRefOrValueEntity {
    private String id;
    private String href;
    private String name;
    private String atBaseType;
    private URI atSchemaLocation;
    private String atReferredType;
    private Boolean immediatePayment;
    private String version;
    private Integer recurringChargePeriodLength;
    private String recurringChargePeriodType;
    private QuantityEntity applicationDuration;
    private Boolean isBundle;
    private String lastUpdate;
    private Float percentage;
    private Integer duration;
    private Integer priority;
    private PriceType priceType;
    private MoneyEntity price;
    private QuantityEntity unitOfMeasure;
    private String description;
    private TimePeriodEntity validFor;
    private ProductOfferingPriceLifecycleEntity lifecycleStatus;
    private List<ProductOfferingPriceRelationshipEntity> popRelationship;
    private List<TaxItemEntity> tax;
    private float interestRate;
    private float downPayment;
    private String partner;
    private String externalId;
}
