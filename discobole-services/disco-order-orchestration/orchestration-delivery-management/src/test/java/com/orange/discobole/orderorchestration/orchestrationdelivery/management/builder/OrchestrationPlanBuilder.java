// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.builder;


import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType.DELIVERS;

public class OrchestrationPlanBuilder {
    public static OrchestrationPlan.OrchestrationPlanBuilder getOrchestrationPlanBuilder() {
        return OrchestrationPlan.builder()
                .id("id")
                .relatedProductOrder(RelatedProductOrder.builder().id("id").build())
                .relatedParty(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty("id")))
                .state(State.IN_PROGRESS)
                .receivedDate(LocalDateTime.of(2023, Month.JUNE, 21, 8, 16, 2).toInstant(ZoneOffset.UTC))
                .requestedDeliveryDate(LocalDateTime.of(2023, Month.JUNE, 21, 8, 16, 2).toInstant(ZoneOffset.UTC))
                .orchestrationPlanNodes(Set.of(
                        OrchestrationPlanNode.builder().id("id1")
                                .state(OrchestrationPlanNodeState.IN_PROGRESS)
                                .relatedServiceOrder(new RelatedServiceOrder())
                                .relatedProductOrderItem(List.of(new RelatedProductOrderItem("id1", "add", 1)))
                                        .relatedSupplyChainOrder(new RelatedSupplyChainOrder("id1", "id1"))
                                .relatedProduct(List.of(RelatedProduct.builder().isInstallable(true).productOrderItemId("id1").id("id1").type(RelatedProductType.CFS).relationshipType(DELIVERS).productSpecification(
                                ProductSpecification.builder().serviceSpecification(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "112", "name", "version"))).build()).build()))
                                .relatedOrchestrationPlanNode(List.of(new RelatedOrchestrationPlanNode("id2", RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER))).build(),
                        OrchestrationPlanNode.builder()
                                .id("id2")
                                .state(OrchestrationPlanNodeState.IN_PROGRESS)
                                .relatedServiceOrder(new RelatedServiceOrder())
                                .relatedProductOrderItem(List.of(
                                        new RelatedProductOrderItem("id2", "add", 1)
                                ))
                                .relatedSupplyChainOrder(new RelatedSupplyChainOrder("id2", "id2"))
                                .relatedProduct(List.of(
                                        RelatedProduct.builder()
                                                .isInstallable(true)
                                                .productOrderItemId("id2")
                                                .id("id2")
                                                .type(RelatedProductType.CFS)
                                                .relationshipType(RelatedProductRelationType.DELIVERS)
                                                .productSpecification(ProductSpecification.builder()
                                                        .serviceSpecification(List.of(
                                                                new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification(
                                                                        "baseType", "referredType", "schemaLocation", "type", "href", "id2", "name", "version")
                                                        ))
                                                        .build())
                                                .build(),
                                        RelatedProduct.builder()
                                                .id("id1")
                                                .isInstallable(true)
                                                .relationshipType(RelatedProductRelationType.RELIES_ON)
                                                .productOrderItemId("id2")
                                                .productSpecification(ProductSpecification.builder()
                                                        .id("id")
                                                        .build())
                                                .build()
                                ))
                                .relatedOrchestrationPlanNode(List.of())
                                .build(),
                        OrchestrationPlanNode.builder()
                                .id("id3")
                                .state(OrchestrationPlanNodeState.IN_PROGRESS)
                                .relatedServiceOrder(new RelatedServiceOrder())
                                .relatedProductOrderItem(List.of(
                                        new RelatedProductOrderItem("id3", "add", 1)
                                ))
                                .relatedSupplyChainOrder(new RelatedSupplyChainOrder("id3", "id3"))
                                .relatedProduct(List.of(
                                        RelatedProduct.builder()
                                                .isInstallable(true)
                                                .id("id3")
                                                .productOrderItemId("id3")
                                                .type(RelatedProductType.CFS)
                                                .relationshipType(RelatedProductRelationType.DELIVERS)
                                                .productSpecification(ProductSpecification.builder()
                                                        .serviceSpecification(List.of(
                                                                new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification(
                                                                        "baseType", "referredType", "schemaLocation", "type", "href", "id3", "name", "version")
                                                        ))
                                                        .build())
                                                .build()
                                ))
                                .build()
                ));

    }

}
