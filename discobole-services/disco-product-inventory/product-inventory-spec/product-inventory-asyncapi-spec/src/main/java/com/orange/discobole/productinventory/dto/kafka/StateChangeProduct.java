// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.dto.kafka;

import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@lombok.experimental.Accessors(chain = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.experimental.SuperBuilder
@lombok.experimental.FieldNameConstants
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class StateChangeProduct extends Product {
    private ProductStatusType oldStatus;

    public static StateChangeProduct fromProduct(Product product, ProductStatusType oldStatus) {
        return StateChangeProduct.builder() //NOSONAR
                .id(product.getId())
                .href(product.getHref())
                .description(product.getDescription())
                .isBundle(product.getIsBundle())
                .isCustomerVisible(product.getIsCustomerVisible())
                .name(product.getName())
                .orderDate(product.getOrderDate())
                .productSerialNumber(product.getProductSerialNumber())
                .startDate(product.getStartDate())
                .billingAccount(product.getBillingAccount())
                .agreement(product.getAgreement())
                .terminationDate(product.getTerminationDate())
                .place(product.getPlace())
                .productCharacteristic(product.getProductCharacteristic())
                .productOffering(product.getProductOffering())
                .productOrderItem(product.getProductOrderItem())
                .productPrice(product.getProductPrice())
                .productRelationship(product.getProductRelationship())
                .productSpecification(product.getProductSpecification())
                .productTerm(product.getProductTerm())
                .realizingResource(product.getRealizingResource())
                .realizingService(product.getRealizingService())
                .relatedParty(product.getRelatedParty())
                .status(product.getStatus())
                .operationalStatus(product.getOperationalStatus())
                .creationDate(product.getCreationDate())
                .lastUpdateDate(product.getLastUpdateDate())
                .atType("StateChangeProduct")
                .statusChange(product.getStatusChange())
                .operationalStatusChange(product.getOperationalStatusChange())
                .externalIdentifier(product.getExternalIdentifier())
                .oldStatus(oldStatus) // Set the additional field from `StateChangeProduct`
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StateChangeProduct that = (StateChangeProduct) o;
        return oldStatus == that.oldStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), oldStatus);
    }

}
