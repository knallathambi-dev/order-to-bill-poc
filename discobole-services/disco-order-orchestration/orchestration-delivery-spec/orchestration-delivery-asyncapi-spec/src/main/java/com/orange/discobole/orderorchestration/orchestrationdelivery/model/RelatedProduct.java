// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.specification.ProductSpecificationRelationship;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@JsonPropertyOrder({
        "id",
        "realisingService",
        "relationshipType",
        "productSpecification",
        "isInstallable",
        "@type",
        "productOrderItemId"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class RelatedProduct {

    @JsonProperty("id")
    private String id;
    @JsonProperty("realisingService")
    private List<RealisingService> realisingService = new ArrayList<>();
    @JsonProperty("relationshipType")
    private RelatedProductRelationType relationshipType;
    @JsonProperty("productSpecification")
    private ProductSpecification productSpecification;
    @JsonProperty("productCharacteristic")
    private Set<Characteristic> productCharacteristic;
    @JsonProperty("isInstallable")
    private Boolean isInstallable;
    @JsonProperty("@type")
    private RelatedProductType type;
    @JsonProperty("productOrderItemId")
    private String productOrderItemId;

    public RelatedProduct(String id) {
        this.id = id;
    }

    public static RelatedProductBuilder relatedProductWithProductSpecificationRelationshipReliesOnBuilder(ProductSpecificationRelationship productSpecificationRelationship, Boolean isInstallable) {
        return RelatedProduct.builder()
                .relationshipType(RelatedProductRelationType.RELIES_ON)
                .productSpecification(ProductSpecification.builder()
                        .id(productSpecificationRelationship.getId())
                        .name(productSpecificationRelationship.getName())
                        .href(productSpecificationRelationship.getHref()).build())
                .isInstallable(isInstallable);
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("realisingService")
    public List<RealisingService> getRealisingService() {
        return realisingService;
    }

    @JsonProperty("realisingService")
    public void setRealisingService(List<RealisingService> realisingService) {
        this.realisingService = realisingService;
    }

    @JsonProperty("relationshipType")
    public RelatedProductRelationType getRelationshipType() {
        return relationshipType;
    }

    @JsonProperty("relationshipType")
    public void setRelationshipType(RelatedProductRelationType relationshipType) {
        this.relationshipType = relationshipType;
    }

    @JsonProperty("productSpecification")
    public ProductSpecification getProductSpecification() {
        return this.productSpecification;
    }

    @JsonProperty("productSpecification")
    public void setProductSpecification(ProductSpecification productSpecification) {
        this.productSpecification = productSpecification;
    }

    @JsonProperty("productCharacteristic")
    public Set<Characteristic> getProductCharacteristic() {
        return productCharacteristic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RelatedProduct)) {
            return false;
        }

        RelatedProduct that = (RelatedProduct) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
