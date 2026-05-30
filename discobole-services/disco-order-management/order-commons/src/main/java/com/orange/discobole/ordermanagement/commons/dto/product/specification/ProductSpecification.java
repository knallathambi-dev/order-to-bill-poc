// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.specification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpecification {
    private String id;
    private String brand;
    private String description;
    private Boolean isBundle;
    private String lastUpdate;
    private String lifecycleStatus;
    private String name;
    private String productNumber;
    private String version;
    private String supportEntity;
    private List<ProductConfigurationSpec> productConfiguration;
    private List<ProductSpecCharacteristic> productSpecCharacteristic;
    private List<ProductSpecificationRelationship> productSpecificationRelationship;
    private List<RelatedParty> relatedParty;
    private StockItemType stockItemType;
    private List<ServiceSpecification> serviceSpecification;
    private ValidFor validFor;
    private List<PolicyRuleRef> policyRuleRef;
    private List<RelatedResource> relatedResource;
    private List<OperationSpecification> operationSpecification;
    @JsonProperty("@type")
    private String type;
    private List<UsageSpecification> usageSpecification;
    private String href;
    @JsonProperty("@schemaLocation")
    private String schemaLocation;
    @JsonProperty("@baseType")
    private String baseType;
    @JsonProperty("@referredType")
    private String referredType;
}