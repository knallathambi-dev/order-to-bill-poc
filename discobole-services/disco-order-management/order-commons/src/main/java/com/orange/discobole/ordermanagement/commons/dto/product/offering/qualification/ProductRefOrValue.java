// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.offering.qualification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRefOrValue {
    private String id;
    private String href;
    private String description;
    private Boolean isBundle;
    private Boolean isCustomerVisible;
    private String name;
    private Instant orderData;
    private String productSerialNumber;
    private Instant startDate;
    private Instant terminationDate;
    private ProductStatusType status;
    @JsonProperty("agreement")
    private List<AgreementItemRef> agreements;
    @JsonProperty("productCharacteristic")
    private List<Characteristic> productCharacteristics;
    @JsonProperty("relatedParty")
    private List<RelatedParty> relatedParties;
    @JsonProperty("relatedPlace")
    private List<RelatedPlaceRefOrValue> places;
    private ProductOfferingRef productOffering;
    @JsonProperty("product")
    private List<ProductRefOrValue> products;
    @JsonProperty("productPrice")
    private List<ProductPrice> productPrices;
    private BillingAccountRef billingAccount;
    private List<RelatedProductOrderItem> productOrderItems;
    private ProductSpecificationRef productSpecification;
    @JsonProperty("productTerm")
    private List<ProductTerm> productTerms;
    @JsonProperty("realizingResource")
    private List<ResourceRef> realizingResources;
    @JsonProperty("realizingService")
    private List<ServiceRef> realizingServices;
    private List<ProductRelationship> productRelationship;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonProperty("@type")
    private String type;
    @JsonProperty("@referredType")
    private String referredType;
}