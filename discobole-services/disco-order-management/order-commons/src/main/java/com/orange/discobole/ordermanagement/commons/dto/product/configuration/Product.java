// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.configuration;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type",
        visible = true
)
@JsonSubTypes({@JsonSubTypes.Type(
        value = MobileLine.class,
        name = "MobileLine"
), @JsonSubTypes.Type(
        value = Offer.class,
        name = "Offer"
), @JsonSubTypes.Type(
        value = PhysicalProduct.class,
        name = "PhysicalProduct"
), @JsonSubTypes.Type(
        value = Service.class,
        name = "Service"
), @JsonSubTypes.Type(
        value = ShipmentProduct.class,
        name = "ShipmentProduct"
), @JsonSubTypes.Type(
        value = SimCard.class,
        name = "SimCard"
)})
public class Product extends ProductRefOrValue {
    private String id = null;
    private String href = null;
    private String description = null;
    private Boolean isBundle = null;
    private Boolean isCustomerVisible = null;
    private String name = null;
    private LocalDateTime orderDate = null;
    private String productSerialNumber = null;
    private LocalDateTime startDate = null;
    private LocalDateTime terminationDate = null;
    @Valid
    private List<AgreementItemRef> agreement = new ArrayList<>();
    @Valid
    private BillingAccountRef billingAccount = null;
    @Valid
    private List<RelatedPlaceRefOrValue> place = new ArrayList<>();
    private List<ProductCharacteristic> productCharacteristic = new ArrayList<>();
    @Valid
    private ProductOfferingRef productOffering = null;
    @Valid
    @NotEmpty(message = "The productOrderItem of product is required")
    private List<RelatedProductOrderItem> productOrderItem = new ArrayList<>();
    @Valid
    private List<ProductPrice> productPrice = new ArrayList<>();
    @Valid
    private List<ProductRelationship> productRelationship = new ArrayList<>();
    private List<StatusChange> statusChange = new ArrayList<>();
    private List<OperationalStatusChange> operationalStatusChange = new ArrayList<>();
    private List<ExternalIdentifier> externalIdentifier = new ArrayList<>();
    @Valid
    private ProductSpecificationRef productSpecification = null;
    private List<ProductTerm> productTerm = new ArrayList<>();
    @Valid
    private List<ResourceRef> realizingResource = new ArrayList<>();
    @Valid
    private List<ServiceRef> realizingService = new ArrayList<>();
    @Valid
    private List<RelatedPartyRefOrPartyRoleRef> relatedParty = new ArrayList<>();
    @NotNull(message = "The status of product is required")
    private ProductMainStatus status = null;
    private ProductOperationalStatus operationalStatus = null;
    private LocalDateTime creationDate = null;
    private LocalDateTime lastUpdateDate = null;
    @JsonProperty("@type")
    private String type = "Product";

}
