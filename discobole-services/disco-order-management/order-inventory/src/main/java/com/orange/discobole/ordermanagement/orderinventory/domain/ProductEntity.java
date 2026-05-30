// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.domain;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductStatusType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@SuperBuilder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ProductEntity extends ProductRefOrValueEntity {
    private String id;
    private String atBaseType;
    private String href;
    private String description;
    private Boolean isBundle;
    private Boolean isCustomerVisible;
    private String name;
    private Instant orderDate;
    private String productSerialNumber;
    private Instant startDate;
    private Instant terminationDate;
    private List<ProductRefOrValueEntity> product;
    private List<CharacteristicEntity> productCharacteristic;
    private ProductOfferingRefEntity productOffering;
    private List<ProductRelationshipEntity> productRelationship;
    private ProductSpecificationRefEntity productSpecification;
    private List<ProductTermEntity> productTerm;
    private List<ResourceRefEntity> realizingResource;
    private List<ServiceRefEntity> realizingService;
    private ProductStatusType status;
    private URI atSchemaLocation;
    private BillingAccountRefEntity billingAccount;
    private String creationDate;
}