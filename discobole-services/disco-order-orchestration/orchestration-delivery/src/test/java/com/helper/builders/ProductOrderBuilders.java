// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.helper.builders;


import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ProductOrderBuilders {

    public static ProductOrder.ProductOrderBuilder productOrderBuilder() {
        return ProductOrder.builder()
                .id(UUID.randomUUID().toString())
                .creationDate(Instant.parse("2023-05-23T08:44:32.157581800Z"))
                .state(ProductOrderStateType.ACCEPTED)
                .requestedCompletionDate(Instant.parse("2023-05-23T08:44:32.157581800Z"))
                .relatedParty(List.of(RelatedPartyRefOrPartyRoleRef.builder()
                        .partyOrPartyRole(PartyRef.builder().id("231-mf4").name("Abir").atReferredType("individual").build()).build()))
                .productOrderItem(List.of(
                                ProductOrderItem.builder()
                                        .id("8")
                                        .quantity(1)
                                        .action(ItemActionType.ADD.ADD)
                                        .state(ProductOrderItemStateType.ACCEPTED)
                                        .product(Product.builder()
                                                .isBundle(false)
                                                .productSpecification(ProductSpecificationRef.builder()
                                                        .id("df32402e-ceb9-4467-aafd-fec0bbff3124")
                                                        .name("Mobile Line")
                                                        .atType("ProductSpecificationRef")
                                                        .build())
                                                .productCharacteristic(List.of(
                                                                StringCharacteristic.builder().name("MSISDN").value("4152797439").valueType("string").build()
                                                        )
                                                )
                                                .realizingResource(List.of(ResourceRef.builder().id("122").atType("Resource").build()))
                                                .build()
                                        )
                                        .productOrderItemRelationship(List.of(
                                                OrderItemRelationship.builder().id("3").relationshipType(RelationshipType.ISSOLD).build()
                                        ))
                                        .atType("ProductOrderItem")
                                        .build()
                        )

                );
    }
}
